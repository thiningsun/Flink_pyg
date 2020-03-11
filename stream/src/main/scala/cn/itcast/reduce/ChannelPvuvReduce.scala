package cn.itcast.reduce

import cn.itcast.bean.ChannelPvuv
import org.apache.flink.api.common.functions.ReduceFunction

/**
  * @Date 2019/7/29
  */
class ChannelPvuvReduce extends ReduceFunction[ChannelPvuv]{
  override def reduce(value1: ChannelPvuv, value2: ChannelPvuv): ChannelPvuv = {

    val pvuv = new ChannelPvuv
    pvuv.setDataField(value1.getDataField)
    pvuv.setChannelID(value1.getChannedID)
    pvuv.setUv(value1.getUv+value2.getUv)
    pvuv.setPv(value1.getPv+value2.getPv)
    pvuv
  }
}
