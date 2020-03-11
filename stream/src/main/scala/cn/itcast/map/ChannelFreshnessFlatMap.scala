package cn.itcast.map

import cn.itcast.bean.{ChannelFreshness, Message, UserState}
import cn.itcast.util.TimeUtil
import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.util.Collector

/**
  * @Date 2019/7/29
  */
class ChannelFreshnessFlatMap extends RichFlatMapFunction[Message,ChannelFreshness]{

  //定义格式化的模板
  val hour:String ="yyyyMMddHH"
  val day:String ="yyyyMMdd"
  val month:String = "yyyyMM"
  override def flatMap(in: Message, out: Collector[ChannelFreshness]): Unit = {

    val userBrowse = in.userBrowse
    val channelId: Long = userBrowse.channelID
    val timestamp = userBrowse.timestamp
    val userID = userBrowse.userID

    //根据时间和用户id查询用户在时，天，月的状态
    val state: UserState = UserState.getUserState(userID,timestamp)
    //获取用户状态
    val isNew: Boolean = state.isNew
    val firstHour = state.isFirstHour
    val firstDay = state.isFirstDay
    val firstMonth = state.isFirstMonth

    //时间戳的格式化
    val hourTime = TimeUtil.getTime(timestamp,hour)
    val dayTime = TimeUtil.getTime(timestamp,day)
    val monthTime = TimeUtil.getTime(timestamp,month)

    //封装数据到ChannelFreshness
    val freshness = new ChannelFreshness
    freshness.setChannelID(channelId)

    //根据用户状态封装指定数据
    isNew match {
      case true =>
        freshness.setNewCount(1L)
      case false =>
        freshness.setNewCount(0L)
    }

    //小时维度的判断
    firstHour match {
      case true=>
        freshness.setNewCount(1L)
        freshness.setDataField(hourTime)
      case false=>
        freshness.setOldCount(1L)
        freshness.setDataField(hourTime)
    }

    //天维度的判断
    firstDay match {
      case true=>
        freshness.setNewCount(1L)
        freshness.setDataField(dayTime)
      case false =>
        freshness.setOldCount(1L)
        freshness.setDataField(dayTime)
    }

    //月维度的判断
    firstMonth match {
      case true =>
        freshness.setNewCount(1L)
        freshness.setDataField(monthTime)
      case false=>
        freshness.setOldCount(1L)
        freshness.setDataField(monthTime)
    }
    out.collect(freshness)
  }
}
