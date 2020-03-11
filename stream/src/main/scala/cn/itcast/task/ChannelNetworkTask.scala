package cn.itcast.task

import cn.itcast.`trait`.ProcessData
import cn.itcast.bean.Message
import cn.itcast.map.ChannelNetworkMap
import cn.itcast.reduce.ChannelNetworkReduce
import cn.itcast.sink.ChannelNetworkSink
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

/**
  * @Date 2019/7/29
  */
object ChannelNetworkTask extends ProcessData {
  override def process(waterData: DataStream[Message]): Unit = {

    /**
      * 1.数据转换，转换成ChannelNetwork
      * 2.数据分组
      * 3.设置时间窗口
      * 4.数据聚合
      * 5.数据落地
      */
    waterData
      //1.数据转换，转换成ChannelNetwork
      .map(new ChannelNetworkMap)
      //2.数据分组
      .keyBy(line=>line.getChannelId+line.getDataField)
      //3.设置时间窗口
      .timeWindow(Time.seconds(3))
      //4.数据聚合
      .reduce(new ChannelNetworkReduce)
      //5.数据落地
      .addSink(new ChannelNetworkSink)

  }
}
