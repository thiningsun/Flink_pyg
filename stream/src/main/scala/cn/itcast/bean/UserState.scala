package cn.itcast.bean

import cn.itcast.util.{HbaseUtil, TimeUtil}
import org.apache.commons.lang3.StringUtils

/**
  * @Date 2019/7/28
  */

case class UserState(
                      var isNew: Boolean = false,
                      var isFirstHour: Boolean = false,
                      var isFirstDay: Boolean = false,
                      var isFirstMonth: Boolean = false
                    )


object UserState {

  val hour: String = "yyyyMMddHH"
  val day: String = "yyyyMMdd"
  val month: String = "yyyyMM"

  def getUserState(userID: Long, timestamp: Long): UserState = {

    //格式化日期
    val hourTime = TimeUtil.getTime(timestamp, hour)
    val dayTime = TimeUtil.getTime(timestamp, day)
    val monthTime = TimeUtil.getTime(timestamp, month)

    var isNew: Boolean = false
    var isFirstHour: Boolean = false
    var isFirstDay: Boolean = false
    var isFirstMonth: Boolean = false

    //设置hbase的user状态的表名，列族名和rowkey
    val rowkey = userID.toString
    val tableName = "userState"
    val family = "info"
    //设置列名
    val visitFirstTimeColumn: String = "visitFirst"
    val visitLastTimeColumn: String = "visitLast"

    //首先查询是否有首次访问数据
    val str = HbaseUtil.getDataByRowkey(tableName, family, visitFirstTimeColumn, rowkey)
    if (StringUtils.isBlank(str)) {

      //将时间日期数据保存到habse，其中首次访问时间和最后一次访问时间是同一个值
      HbaseUtil.putDataByRowkey(tableName,family,visitFirstTimeColumn,timestamp.toString,rowkey)
      HbaseUtil.putDataByRowkey(tableName,family,visitLastTimeColumn,timestamp.toString,rowkey)

      isNew = true
      isFirstHour = true
      isFirstDay = true
      isFirstMonth = true
      UserState(isNew, isFirstHour, isFirstDay, isFirstMonth)
    }else{
      val strLastData = HbaseUtil.getDataByRowkey(tableName, family, visitLastTimeColumn, rowkey)

      //小时维度判断是否是首次访问
      //2019-07-28 18  >  2019-07-28 17
      if(hourTime.toLong > TimeUtil.getTime(strLastData.toLong,hour).toLong){
        isFirstHour = true
      }

      //天维度的判断是否是首次访问
      //2019-07-29  >  2019-07-28
      if(dayTime.toLong > TimeUtil.getTime(strLastData.toLong,day).toLong){
        isFirstDay = true
      }

      //月维度的判断是否是首次访问
      //2019-08  >  2019-07
      if(monthTime.toLong > TimeUtil.getTime(strLastData.toLong,month).toLong){
        isFirstMonth = true
      }

      HbaseUtil.putDataByRowkey(tableName,family,visitLastTimeColumn,timestamp.toString,rowkey)
      UserState(isNew,isFirstHour,isFirstDay,isFirstMonth)
    }
  }

}
