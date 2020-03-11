package cn.itcast.task

import cn.itcast.`trait`.ProcessData
import cn.itcast.bean.Message
import cn.itcast.map.ChannelRegionMap
import cn.itcast.reduce.ChannelRegionReduce
import cn.itcast.sink.ChannelRegionSink
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
/**
  * @Date 2019/7/29
  */
object ChannelRegionTask extends ProcessData{
  override def process(waterData: DataStream[Message]): Unit = {

    /**
      * 1.数据转换，转换成ChannelRegion
      * 2.数据分组，channelID+ dataField
      * 3.划分时间窗口
      * 4.数据聚合（pv uv newCount oldCount）
      * 5.数据落地，将数据插入hbase
      */
    waterData
      //1.数据转换，转换成ChannelRegion
      .map(new ChannelRegionMap)
      //2.数据分组，channelID+ dataField
      .keyBy(line=>line.getChannelID+line.getDataField)
      //3.划分时间窗口
      .timeWindow(Time.seconds(3))
      //4.数据聚合（pv uv newCount oldCount）
      .reduce(new ChannelRegionReduce)
      //5.数据落地，将数据插入hbase
      .addSink(new ChannelRegionSink)

  }
}
