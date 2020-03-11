package cn.itcast

import java.lang
import java.util.Properties

import cn.itcast.bean.{Message, UserBrowse}
import cn.itcast.config.GlobalConfig
import cn.itcast.task._
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.watermark.Watermark

/**
  * @Date 2019/7/28
  */
object App {
  def main(args: Array[String]): Unit = {

    /**
      * 步骤：
      * 1.获取流处理执行环境
      * 2.添加checkpoint
      * 3.整合kafka
      * 4.加载数据源
      * 5.数据转换
      * 6.添加水位线
      * 7.具体task执行任务
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
    kafkaSource.setStartFromEarliest()

    //4.加载数据源
    val source: DataStream[String] = env.addSource(kafkaSource)

    //5.数据转换
    val messages: DataStream[Message] = source.map(line => {

      val json: JSONObject = JSON.parseObject(line)
      val count: Int = json.getIntValue("count")
      val message: String = json.getString("message")
      val timestamp: Long = json.getLong("timestamp")
      val userBrowse: UserBrowse = UserBrowse.getUserBrowse(message)
      Message(count, timestamp, userBrowse)
    })

    //6.添加水位线
    val waterData: DataStream[Message] = messages.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks[Message] {
      val delayTime: Long = 2000L
      var currentTimestamp: Long = 0

      override def getCurrentWatermark: Watermark = {
        new Watermark(currentTimestamp - delayTime)
      }

      override def extractTimestamp(element: Message, previousElementTimestamp: Long): Long = {
        val timestamp: Long = element.userBrowse.timestamp
        currentTimestamp = Math.max(timestamp, currentTimestamp)
        currentTimestamp
      }
    })

    //7.具体task执行任务
    /**
      * 1.实时频道的热点统计分析
      * 2.实时频道PVUV统计分析
      * 3.实时频道的用户新鲜度统计分析
      * 4.实时频道的地域统计分析
      * 5.实时频道的运营商业务统计分析
      * 6.实时频道的浏览器类型统计分析
      */
    //1.实时频道的热点统计分析
    //ChannelHotTask.process(waterData)
    //2.实时频道PVUV统计分析
    //ChannelPVUVTask.process(waterData)
    //3.实时频道的用户新鲜度统计分析
    ChannelFreshnessTask.process(waterData)
    //4.实时频道的地域统计分析
    //ChannelRegionTask.process(waterData)
    //5.实时频道的运营商业务统计分析
    ChannelNetworkTask.process(waterData)

    //8.触发执行
    env.execute()
  }

}
