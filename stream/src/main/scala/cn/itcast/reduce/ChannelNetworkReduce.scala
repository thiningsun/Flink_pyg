package cn.itcast.reduce

import cn.itcast.bean.ChannelNetwork
import org.apache.flink.api.common.functions.ReduceFunction

/**
  * @Date 2019/7/29
  */
class ChannelNetworkReduce extends ReduceFunction[ChannelNetwork] {
  override def reduce(in1: ChannelNetwork, in2: ChannelNetwork): ChannelNetwork = {

    val channelNetwork = new ChannelNetwork
    channelNetwork.setOldCount(in1.getOldCount + in2.getOldCount)
    channelNetwork.setDataField(in1.getDataField)
    channelNetwork.setNewCount(in1.getNewCount + in2.getNewCount)
    channelNetwork.setNewWork(in1.getNetWork)
    channelNetwork.setChannelId(in1.getChannelId)
    channelNetwork
  }
}
