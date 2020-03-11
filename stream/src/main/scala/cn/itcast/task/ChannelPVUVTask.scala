package cn.itcast.task

import cn.itcast.`trait`.ProcessData
import cn.itcast.bean.Message
import cn.itcast.map.ChannelPVUVMap
import cn.itcast.reduce.ChannelPvuvReduce
import cn.itcast.sink.ChannelPvuvSink
import org.apache.flink.streaming.api.scala.{DataStream, _}
import org.apache.flink.streaming.api.windowing.time.Time

/**
  *
  * @Date 2019/7/28
  */
object ChannelPVUVTask extends ProcessData {

  override def process(waterData: DataStream[Message]): Unit = {

    /**
      * 1.数据转换
      * 2.分组
      * 3.划分时间窗口
      * 4.数据聚合
      * 5.数据落地
      */

    waterData
      // 1.数据转换
      .map(new ChannelPVUVMap)
      //分组
      .keyBy(line => line.getChannedID + line.getDataField)
      //划分时间窗口
      .timeWindow(Time.seconds(3))
      //数据聚合
      .reduce(new ChannelPvuvReduce)
      //数据落地
      .addSink(new ChannelPvuvSink)

  }
}
