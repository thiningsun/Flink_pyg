package cn.itcast.sink

import cn.itcast.bean.ChannelPvuv
import cn.itcast.util.HbaseUtil
import org.apache.commons.lang3.StringUtils
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction

/**
  * @Date 2019/7/29
  */
class ChannelPvuvSink extends RichSinkFunction[ChannelPvuv] {

  override def invoke(value: ChannelPvuv): Unit = {

    /**
      * 步骤
      * 1.设置hbase的表，rowkey，列族，列名
      * 2.查询hbase，hbase如果有数据需要合并，没有数据直接插入
      * 3.插入操作
      */
    //1.设置hbase的表，rowkey，列族，列名
    val tableName: String = "channel"
    val family: String = "info"
    val pvCol: String = "pv"
    val uvCol: String = "uv"
    val rowkey: String = value.getChannedID + value.getDataField

    //2.查询hbase，hbase如果有数据需要合并，没有数据直接插入
    var pv: Long = value.getPv
    var uv: Long = value.getUv
    val pvData: String = HbaseUtil.getDataByRowkey(tableName, family, pvCol, rowkey)
    val uvData: String = HbaseUtil.getDataByRowkey(tableName, family, uvCol, rowkey)

    //pv uv非空判断
    if (StringUtils.isNotBlank(pvData)) {
      pv = pv + pvData.toLong
    }

    if (StringUtils.isNotBlank(uvData)) {
      uv = uv + uvData.toLong
    }

    //3.插入数据到hbase
    var map = Map[String, Any]()
    map += (pvCol -> pv)
    map += (uvCol -> uv)
    map += ("channelId" -> value.getChannedID)
    map += ("time" -> value.getDataField)

    HbaseUtil.putMapDataByRowkey(tableName, family, map, rowkey)
  }

}
