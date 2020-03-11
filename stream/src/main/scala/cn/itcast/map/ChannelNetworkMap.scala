package cn.itcast.map

import cn.itcast.bean.{ChannelNetwork, Message, UserState}
import cn.itcast.util.TimeUtil
import org.apache.flink.api.common.functions.RichMapFunction

/**
  * @Date 2019/7/29
  */
class ChannelNetworkMap extends RichMapFunction[Message,ChannelNetwork]{


  //设置日期格式化模板
  val hour = "yyyyMMddHH"
  val day="yyyyMMdd"
  val month="yyyyMM"
  override def map(value: Message): ChannelNetwork = {

    val userBrowse = value.userBrowse
    val userID = userBrowse.userID
    val timestamp = userBrowse.timestamp
    val network = userBrowse.network
    val channelID = userBrowse.channelID

    //格式化日期
    val hourTime = TimeUtil.getTime(timestamp,hour)
    val dayTime = TimeUtil.getTime(timestamp,day)
    val monthTime = TimeUtil.getTime(timestamp,month)

    //封装数据
    val channelNetwork = new ChannelNetwork
    channelNetwork.setChannelId(channelID)
    channelNetwork.setNewWork(network)

    //根据时间和用户ID获取状态
    val userState = UserState.getUserState(userID,timestamp)
    val isNew:Boolean = userState.isNew
    val firstHour = userState.isFirstHour
    val firstDay = userState.isFirstDay
    val firstMonth = userState.isFirstMonth

    //根据时间维度判断用户状态
    isNew match {
      case true=>
        channelNetwork.setNewCount(1L)
      case false=>
        channelNetwork.setNewCount(0L)
    }
    //小时维度的用户状态判断
    firstHour match {
      case true=>
        channelNetwork.setNewCount(1L)
        channelNetwork.setDataField(hourTime)
      case false=>
        channelNetwork.setOldCount(1L)
        channelNetwork.setDataField(hourTime)
    }
    //天维度的用户状态判断
    firstDay match {
      case true=>
        channelNetwork.setNewCount(1L)
        channelNetwork.setDataField(dayTime)
      case false=>
        channelNetwork.setOldCount(1L)
        channelNetwork.setDataField(dayTime)
    }
    //月维度的用户状态判断
    firstMonth match {
      case true=>
        channelNetwork.setNewCount(1L)
        channelNetwork.setDataField(monthTime)
      case false=>
        channelNetwork.setOldCount(1L)
        channelNetwork.setDataField(monthTime)
    }

    channelNetwork
  }
}
