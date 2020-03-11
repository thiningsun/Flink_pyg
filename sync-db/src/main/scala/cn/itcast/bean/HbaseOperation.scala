package cn.itcast.bean

/**
  * @Date 2019/7/29
  */
case class HbaseOperation(
                         var tableName:String,
                         var family:String,
                         var colName:String,
                         var colValue:String,
                         var rowkey:String,
                         var eventType:String
                         )