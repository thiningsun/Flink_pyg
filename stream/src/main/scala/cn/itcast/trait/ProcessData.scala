package cn.itcast.`trait`

import cn.itcast.bean.Message
import org.apache.flink.streaming.api.scala.DataStream

/**
  * @Date 2019/7/28
  */
trait ProcessData {

  def process(waterData: DataStream[Message])

}
