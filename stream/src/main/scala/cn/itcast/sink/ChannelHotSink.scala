package cn.itcast.sink

import cn.itcast.bean.ChannelHot
import cn.itcast.util.HbaseUtil
import org.apache.commons.lang3.StringUtils
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction

/**
  * @Date 2019/7/28
  */
class ChannelHotSink extends RichSinkFunction[ChannelHot] {

  override def invoke(value: ChannelHot): Unit = {

    //设置habse参数，表，列族，列名
    val tableName:String = "channel"
    val columnFamily = "info"
    val countColumn = "count"
    val rowkey:String = value.channelID.toString
    var count: Int = value.count

    //先查询hbase，如果hbase中有数据，需要进行累加操作
    val str: String = HbaseUtil.getDataByRowkey(tableName,columnFamily,countColumn,rowkey)

    //非空判断
    if(StringUtils.isNotBlank(str)){
      count = count+str.toInt
    }

    //数据插入
    HbaseUtil.putDataByRowkey(tableName,columnFamily,countColumn,count.toString ,rowkey)
  }

}
