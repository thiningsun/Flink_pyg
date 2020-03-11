package cn.itcast.sink

import cn.itcast.bean.ChannelRegion
import cn.itcast.util.HbaseUtil
import org.apache.commons.lang3.StringUtils
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction

/**
  * @Date 2019/7/29
  */
class ChannelRegionSink extends RichSinkFunction[ChannelRegion] {

  override def invoke(value: ChannelRegion): Unit = {

    /**
      * 1.定义habse的表名，列族，列，rowkey
      * 2.查询habsse
      * 3.合并hbase数据
      * 4.封装数据到map
      * 5.插入数据
      */
    //1.定义habse的表名，列族，列，rowkey
    val tableName = "region"
    val family = "info"
    val pvCol = "pv"
    val uvCol = "uv"
    val newCountCol = "newCount"
    val oldCountCol = "oldCount"

    val rowkey = value.getChannelID + value.getDataField

    //2.查询habsse
    val pvData: String = HbaseUtil.getDataByRowkey(tableName, family, pvCol, rowkey)
    val uvData: String = HbaseUtil.getDataByRowkey(tableName, family, uvCol, rowkey)
    val newCountData: String = HbaseUtil.getDataByRowkey(tableName, family, newCountCol, rowkey)
    val oldCountData: String = HbaseUtil.getDataByRowkey(tableName, family, oldCountCol, rowkey)

    //获取流value中的需与hbase合并的数据
    var pv = value.getPv
    var uv = value.getUv
    var newCount = value.getNewCount
    var oldCount = value.getOldCount

    //3.合并hbase数据
    if (StringUtils.isNotBlank(pvData)) {
      pv = pv + pvData.toLong
    }
    if (StringUtils.isNotBlank(uvData)) {
      uv = uv + uvData.toLong
    }
    if (StringUtils.isNotBlank(newCountData)) {
      newCount = newCount + newCountData.toLong
    }
    if (StringUtils.isNotBlank(oldCountData)) {
      oldCount = oldCount + oldCountData.toLong
    }

    //4.封装数据到map
    var map = Map[String, Any]()
    map += (pvCol -> pv)
    map += (uvCol -> uv)
    map += (newCountCol -> newCount)
    map += (oldCountCol -> oldCount)
    map += ("channelId" -> value.getChannelID)
    map += ("country" -> value.getCountry)
    map += ("province" -> value.getProvince)
    map += ("city" -> value.getCity)
    map += ("time" -> value.getDataField)

    //5.插入数据
    HbaseUtil.putMapDataByRowkey(tableName, family, map, rowkey)

  }
}
