package cn.itcast.map

import cn.itcast.bean.{ChannelRegion, Message, UserState}
import cn.itcast.util.TimeUtil
import org.apache.flink.api.common.functions.RichMapFunction

/**
  * @Date 2019/7/29
  */
class ChannelRegionMap extends RichMapFunction[Message,ChannelRegion]{


  //定义日期格式化模板
  val hour :String = "yyyyMMddHH"
  val day:String ="yyyyMMdd"
  val month:String="yyyyMM"
  override def map(value: Message): ChannelRegion = {

    //从value中取需要的值
    val userBrowse = value.userBrowse
    val userID = userBrowse.userID
    val timestamp = userBrowse.timestamp
    val channelID = userBrowse.channelID
    val country = userBrowse.country
    val city = userBrowse.city
    val province = userBrowse.province

    //新建返回对象，封装数据
    val channelRegion = new ChannelRegion
    channelRegion.setCountry(country)
    channelRegion.setCity(city)
    channelRegion.setChannelID(channelID)
    channelRegion.setProvice(province)
    channelRegion.setPv(1L)

    //日期格式化
    val houtTime = TimeUtil.getTime(timestamp,hour)
    val dayTime = TimeUtil.getTime(timestamp,day)
    val monthTime = TimeUtil.getTime(timestamp,month)

    //获取用户状态
    val userState = UserState.getUserState(userID,timestamp)
    val isNew:Boolean = userState.isNew
    val firstHour:Boolean = userState.isFirstHour
    val firstDay: Boolean = userState.isFirstDay
    val firstMonth = userState.isFirstMonth

    //根据用户状态封装数据
    isNew match {
      case true=>
        channelRegion.setUv(1L)
        channelRegion.setNewCount(1L)
      case false=>
        channelRegion.setUv(0L)
        channelRegion.setNewCount(0L)
    }

    channelRegion.setUv(0L)
    //小时维度的判断
    firstHour match {
      case true=>
        channelRegion.setNewCount(1L)
        channelRegion.setUv(1L)
        channelRegion.setDataField(houtTime)
      case false=>
        channelRegion.setOldCount(1L)
        channelRegion.setDataField(houtTime)
    }
    //天维度的判断
    firstDay match {
      case true=>
        channelRegion.setNewCount(1L)
        channelRegion.setUv(1L)
        channelRegion.setDataField(dayTime)
      case false=>
        channelRegion.setOldCount(1L)
        channelRegion.setDataField(dayTime)
    }

    //月维度的判断
    firstMonth match {
      case true=>
        channelRegion.setNewCount(1L)
        channelRegion.setUv(1L)
        channelRegion.setDataField(monthTime)
      case false=>
        channelRegion.setOldCount(1L)
        channelRegion.setDataField(monthTime)
    }
    channelRegion
  }
}
