package cn.itcast.reduce

import cn.itcast.bean.ChannelFreshness
import org.apache.flink.api.common.functions.ReduceFunction

/**
  * @Date 2019/7/29
  */
class ChannelFreshnessReduce extends ReduceFunction[ChannelFreshness]{
  override def reduce(in1: ChannelFreshness, in2: ChannelFreshness): ChannelFreshness = {

    val channelFreshness = new ChannelFreshness
    channelFreshness.setDataField(in1.getDataField)
    channelFreshness.setChannelID(in1.getChannelID)
    channelFreshness.setOldCount(in1.getOldCount+in2.getOldCount)
    channelFreshness.setNewCount(in1.getNewCount+in2.getNewCount)
    channelFreshness
  }

}
