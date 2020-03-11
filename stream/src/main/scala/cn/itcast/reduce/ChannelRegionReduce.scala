package cn.itcast.reduce

import cn.itcast.bean.ChannelRegion
import org.apache.flink.api.common.functions.ReduceFunction

/**
  * @Date 2019/7/29
  */
class ChannelRegionReduce extends ReduceFunction[ChannelRegion]{
  override def reduce(in1: ChannelRegion, in2: ChannelRegion): ChannelRegion = {
    val channelRegion = new ChannelRegion
    channelRegion.setOldCount(in1.getOldCount+in2.getOldCount)
    channelRegion.setDataField(in1.getDataField)
    channelRegion.setUv(in1.getUv+in2.getUv)
    channelRegion.setNewCount(in1.getNewCount+in2.getNewCount)
    channelRegion.setPv(in1.getPv+in2.getPv)
    channelRegion.setProvice(in1.getProvince)
    channelRegion.setCity(in1.getCity)
    channelRegion.setCountry(in1.getCountry)
    channelRegion.setChannelID(in1.getChannelID)
    channelRegion
  }

}
