package cn.itcast.sink

import cn.itcast.bean.ChannelFreshness
import cn.itcast.util.HbaseUtil
import org.apache.commons.lang3.StringUtils
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction

/**
  * @Date 2019/7/29
  */
class ChannelFreshnessSink extends RichSinkFunction[ChannelFreshness] {

  override def invoke(value: ChannelFreshness): Unit = {

    /**
      * 1.设置hbase表名，rowkey，列族，列
      * 2.查询hbase，hbase如果数据（newCount,oldCount），需要合并
      * 3.封装map数据
      * 4.执行插入操作
      */

    //1.设置hbase表名，rowkey，列族，列
    val tableName = "channel"
    val family = "info"
    val newCountCol = "newCount"
    val oldCountCol = "oldCount"
    val rowkey = value.getChannelID + value.getDataField

    //取值newCount oldCount
    var newCount: Long = value.getNewCount
    var oldCount: Long = value.getOldCount

    //2.查询hbase，hbase如果数据（newCount,oldCount），需要合并
    val newCountData: String = HbaseUtil.getDataByRowkey(tableName, family, newCountCol, rowkey)
    val oldCountData: String = HbaseUtil.getDataByRowkey(tableName, family, oldCountCol, rowkey)
    //非空判断
    if (StringUtils.isNotEmpty(newCountData)) {

      newCount = newCount + newCountData.toLong
    }
    if (StringUtils.isNotBlank(oldCountData)) {
      oldCount = oldCount + oldCountData.toLong
    }

    //3.封装map数据
    var map = Map[String, Any]()
    map += (oldCountCol -> oldCount)
    map += (newCountCol -> newCount)
    map += ("channelId" -> value.getChannelID)
    map += ("time" -> value.getDataField)

    //数据插入hhbase
    HbaseUtil.putMapDataByRowkey(tableName, family, map, rowkey)
  }

}
