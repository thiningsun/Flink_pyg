package cn.itcast.task

import cn.itcast.`trait`.ProcessData
import cn.itcast.bean.{ChannelHot, Message}
import cn.itcast.sink.ChannelHotSink
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
/**
  * @Date 2019/7/28
  */
object ChannelHotTask extends ProcessData{

  override def process(waterData: DataStream[Message]): Unit = {

    /**
      * 1.数据转换
      * 2.分组
      * 3.划分时间窗口
      * 4.数据聚合
      * 5.数据落地
      */
    waterData
      //1.数据转换
      .map(line=>ChannelHot(line.count,line.timestamp,line.userBrowse.channelID))
      //2.分组
      .keyBy(_.channelID)
      //3.划分时间窗口
      .timeWindow(Time.seconds(3))
      //4.数据聚合
      .reduce((x,y)=>ChannelHot(x.count+y.count,x.timestamp,x.channelID))
      //5.数据落地
      .addSink(new ChannelHotSink)


  }
}
