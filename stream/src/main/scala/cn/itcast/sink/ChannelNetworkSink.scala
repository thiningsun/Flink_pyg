package cn.itcast.sink

import cn.itcast.bean.ChannelNetwork
import cn.itcast.util.HbaseUtil
import org.apache.commons.lang3.StringUtils
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction

/**
  * @Date 2019/7/29
  */
class ChannelNetworkSink extends RichSinkFunction[ChannelNetwork] {

  override def invoke(value: ChannelNetwork): Unit = {

    //1.设置habse的表名，列族，列，rowkey
    val tableName = "network"
    val family = "info"
    val rowkey = value.getChannelId + value.getDataField
    val newCountCol = "newCount"
    val oldCountCol = "oldCount"

    //查询hbase，如果hbase中有数据（newCount oldCount），需要进行合并操作
    val newCountData = HbaseUtil.getDataByRowkey(tableName, family, newCountCol, rowkey)
    val oldCountData = HbaseUtil.getDataByRowkey(tableName, family, oldCountCol, rowkey)

    //取流数据中的新老用户数据
    var newCount: Long = value.getNewCount
    var oldCount: Long = value.getOldCount

    //对非空数据进行合并
    if (StringUtils.isNotBlank(newCountData)) {

      newCount = newCount + newCountData.toLong
    }

    if (StringUtils.isNotBlank(oldCountData)) {

      oldCount = oldCount + oldCountData.toLong
    }

    //封装map数据
    var map = Map[String, Any]()
    map += (newCountCol -> newCount)
    map += (oldCountCol -> oldCount)
    map += ("channelId" -> value.getChannelId)
    map += ("network" -> value.getNetWork)
    map += ("time" -> value.getDataField)

    //数据插入操作
    HbaseUtil.putMapDataByRowkey(tableName, family, map, rowkey)

  }

}
