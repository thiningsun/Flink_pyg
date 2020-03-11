package cn.itcast

import java.util.Properties

import cn.itcast.bean.{CanalMysql, HbaseOperation}
import cn.itcast.config.GlobalConfig
import cn.itcast.util.HbaseUtil
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09
import org.apache.flink.streaming.api.scala._

/**
  * @Date 2019/7/29
  */
object SyncDbApp {

  def main(args: Array[String]): Unit = {
    /**
      * 步骤：
      * 1.获取流处理执行环境
      * 2.添加checkpoint
      * 3.整合kafka
      * 4.加载数据源
      * 5.具体task执行任务
      * 8.触发执行
      */
    //1.获取流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //2.添加checkpoint
    env.setStateBackend(new FsStateBackend("hdfs://node01:8020/flink-checkpoint"))
    //周期性的发送检查点
    env.enableCheckpointing(6000)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    env.getCheckpointConfig.setCheckpointInterval(6000)
    env.getCheckpointConfig.setFailOnCheckpointingErrors(false)
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    //需要手动删除检查点
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)

    //设置事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime) //消息源本身的时间

    //3.整合kafka
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", GlobalConfig.kakfaServers)
    properties.setProperty("zookeeper.connect", GlobalConfig.kakfaZk)
    properties.setProperty("group.id", GlobalConfig.kakfaGroupID)

    val kafkaSource: FlinkKafkaConsumer09[String] = new FlinkKafkaConsumer09[String](GlobalConfig.kakfaTopic, new SimpleStringSchema(), properties)

    //4.加载数据源
    val source: DataStream[String] = env.addSource(kafkaSource)
    val canalMysqls: DataStream[CanalMysql] = source.map(line => {

      val canalMysql: CanalMysql = CanalMysql.getCanalMysql(line)
      canalMysql
    })

    //5.task执行任务
    val hbaseOperations: DataStream[HbaseOperation] = ProcessData.process(canalMysqls)

    //6.hbase交互操作，执行增删改
    hbaseOperations.map(line=>{
      line.eventType match {
        case "DELETE" =>
          HbaseUtil.delByRowkey(line.tableName,line.family,line.rowkey)
        case _=>
          HbaseUtil.putDataByRowkey(line.tableName,line.family,line.colName,line.colValue,line.rowkey)
      }
    })

    //7.触发执行
    env.execute("sync mysql job")

  }

}
