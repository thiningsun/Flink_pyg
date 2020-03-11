package cn.itcast.util

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor, TableName}

/**
  * @Date 2019/7/28
  */
object HbaseUtil {

  private val configuration: Configuration = HBaseConfiguration.create()

  //获取连接
  private val connection: Connection = ConnectionFactory.createConnection(configuration)
  //获取客户端实例对象
  private val admin: Admin = connection.getAdmin


  /**
    * 初始化表
    *
    */
  def initTable(tableName: String, columnFamily: String): Table = {

    val tblName: TableName = TableName.valueOf(tableName)
    //判断表是否存在
    if (!admin.tableExists(tblName)) {

      //构建表描述器
      val tblNameDescriptor = new HTableDescriptor(tblName)
      //构建列族描述器
      val hColumnDescriptor = new HColumnDescriptor(columnFamily)
      tblNameDescriptor.addFamily(hColumnDescriptor)

      admin.createTable(tblNameDescriptor)
    }

    val table: Table = connection.getTable(tblName)
    table
  }

  /**
    * 通过rowkey查询数据（指定列族和指定列）
    */
  def getDataByRowkey(tableName: String, family: String, columnName: String, rowkey: String): String = {

    val table: Table = initTable(tableName, family)
    var str: String = ""
    try {
      val get: Get = new Get(rowkey.getBytes())
      val result: Result = table.get(get)
      val bytes: Array[Byte] = result.getValue(family.getBytes(), columnName.getBytes())
      if (bytes != null && bytes.length > 0) {
        str = new String(bytes)
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      table.close()
    }
    str
  }

  /**
    * 根据rowkey插入数据（指定列族和指定列）
    */
  def putDataByRowkey(tableName: String, family: String, columnName: String, columnValue: String, rowkey: String): Unit = {

    val table: Table = initTable(tableName, family)
    try {
      val put = new Put(rowkey.getBytes())
      put.addColumn(family.getBytes(), columnName.getBytes(), columnValue.getBytes())
      table.put(put)
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      table.close()
    }
  }

  /**
    * 根据rowkey插入多列数据
    * 用map封装数据： key作为列名， value：作为列值
    */
  def putMapDataByRowkey(tableName: String, family: String, map: Map[String, Any], rowkey: String): Unit = {

    val table: Table = initTable(tableName, family)
    try {
      val put: Put = new Put(rowkey.getBytes())
      for ((key, value) <- map) {
        put.addColumn(family.getBytes(), key.getBytes(), value.toString.getBytes())
      }
      table.put(put)
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      table.close()
    }
  }

  /**
    * 根据rowkey删除数据
    */
  def delByRowkey(tableName: String, family: String, rowkey: String): Unit = {
    val table: Table = initTable(tableName, family)
    try {
      val delete = new Delete(rowkey.getBytes())
      delete.addFamily(family.getBytes())
      table.delete(delete)
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      table.close()
    }
  }


  /**
    * 测试
     */
  def main(args: Array[String]): Unit = {

    //单列数据插入
    //putDataByRowkey("test-0729","info","name","zhangsan","001")

    //数据查询，rowkey
    val str: String = getDataByRowkey("test-0729","info","name","001")
    //println("value:"+ str)

    //插入map数据
    putMapDataByRowkey("test-0729","info",Map("k1"->1,"k2"->2),"002")

    //数据删除
    //delByRowkey("test-0728","info","002")

  }

}
