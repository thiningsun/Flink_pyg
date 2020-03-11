package cn.itcast.util

import java.util.Date

import org.apache.commons.lang3.time.FastDateFormat

/**
  * @Date 2019/7/28
  */
object TimeUtil {

  def getTime(timestamp:Long, format:String): String ={

    val time: Long = new Date(timestamp).getTime
    val fastDateFormat: FastDateFormat = FastDateFormat.getInstance(format)
    val str: String = fastDateFormat.format(time)
    str
  }


}
