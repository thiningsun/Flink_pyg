package cn.itcast

import cn.itcast.bean.{CanalMysql, ColumnField, HbaseOperation}
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.scala._

import scala.collection.mutable.ArrayBuffer

/**
  * @Date 2019/7/29
  */
object ProcessData {


  def process(canalMysqls: DataStream[CanalMysql]): DataStream[HbaseOperation] = {

    val hbaseOperations: DataStream[HbaseOperation] = canalMysqls.flatMap {
      line =>
        val columnValueList: String = line.columnValueList
        val dbName = line.dbName
        val tableName = line.tableName
        val eventType = line.eventType

        //解析columnValueList
        val fields: ArrayBuffer[ColumnField] = ColumnField.getColumnField(columnValueList)

        //设置habse表名，rowkey，列族
        val hbaseTableName: String = dbName + "-" + tableName
        val family: String = "info"
        var rowkey: String = fields(0).columnValue

        eventType match {

          case "INSERT" =>
            fields.map(line => HbaseOperation(hbaseTableName, family, line.columnName, line.columnValue, rowkey, eventType))
          case "UPDATE" =>
            fields.filter(_.isValid).map(line => HbaseOperation(hbaseTableName, family, line.columnName, line.columnValue, rowkey, eventType))
          case _ =>
            List(HbaseOperation(hbaseTableName, family, null, null, rowkey, eventType))
        }

    }
    hbaseOperations
  }
}
