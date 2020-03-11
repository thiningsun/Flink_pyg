package cn.itcast.task

import cn.itcast.`trait`.ProcessData
import cn.itcast.bean.Message
import cn.itcast.map.ChannelFreshnessFlatMap
import cn.itcast.reduce.ChannelFreshnessReduce
import cn.itcast.sink.ChannelFreshnessSink
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
/**
  * @Date 2019/7/29
  */
object ChannelFreshnessTask extends ProcessData{
  override def process(waterData: DataStream[Message]): Unit = {

    /**
      * 1.数据转换，Message转换成ChannelFreshness
      * 2.分组（keyby）:channelID+dataField
      * 3.设置时间窗口：不重叠的时间窗口
      * 4.数据聚合
      * 5.数据落地
      */

    waterData
      //1.数据转换，Message转换成ChannelFreshness
      .flatMap(new ChannelFreshnessFlatMap)
      //2.分组（keyby）:channelID+dataField
      .keyBy(line=>line.getChannelID+line.getDataField)
      //3.设置时间窗口：不重叠的时间窗口
      .timeWindow(Time.seconds(3))
      //4.数据聚合
      .reduce(new ChannelFreshnessReduce)
      //5.数据落地
      .addSink(new ChannelFreshnessSink)


  }
}
