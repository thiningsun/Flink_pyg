package cn.itcast.map

import cn.itcast.bean.{ChannelPvuv, Message, UserState}
import cn.itcast.util.TimeUtil
import org.apache.flink.api.common.functions.RichMapFunction

/**
  * @Date 2019/7/28
  */
class ChannelPVUVMap extends RichMapFunction[Message,ChannelPvuv]{

  val hour:String ="yyyyMMddHH"
  val day:String="yyyyMMdd"
  val month:String = "yyyyMM"
  override def map(value: Message): ChannelPvuv = {

    val userBrowse = value.userBrowse
    val userId: Long = userBrowse.userID
    val timestamp: Long = userBrowse.timestamp

    val channelID:Long = userBrowse.channelID

    val state: UserState = UserState.getUserState(userId,timestamp)
    val isNew = state.isNew
    val firstDay = state.isFirstDay
    val firstHour = state.isFirstHour
    val firstMonth = state.isFirstMonth

    val channelPvuv = new ChannelPvuv
    channelPvuv.setPv(1L)
    channelPvuv.setChannelID(channelID)

    //日期格式化
    val hourTime = TimeUtil.getTime(timestamp, hour)
    val dayTime = TimeUtil.getTime(timestamp,day)
    val monthTime = TimeUtil.getTime(timestamp, month)

    if(isNew == true){
      channelPvuv.setUv(1L)
    }

    //小时维度的判断
    if(firstHour == true){
      channelPvuv.setUv(1L)
      channelPvuv.setDataField(hourTime)
    }else{
      channelPvuv.setUv(0L)
      channelPvuv.setDataField(hourTime)
    }

    //天维度的判断
    if(firstDay == true){
      channelPvuv.setUv(1L)
      channelPvuv.setDataField(dayTime)
    }else{
      channelPvuv.setUv(0L)
      channelPvuv.setDataField(dayTime)
    }

    //月维度的判断
    if(firstMonth == true){
      channelPvuv.setUv(1L)
      channelPvuv.setDataField(monthTime)
    }else{
      channelPvuv.setUv(0L)
      channelPvuv.setDataField(monthTime)
    }
    channelPvuv
  }
}
