package cn.itcast.bean

import com.alibaba.fastjson.{JSON, JSONObject}

/**
  * @Date 2019/7/29
  */
case class CanalMysql(
                       var columnValueList: String,
                       var dbName: String,
                       var emptyCount: Long,
                       var eventType: String,
                       var logFileName: String,
                       var logFileOffset: Long,
                       var tableName: String,
                       var timestamp: Long
                     )


object CanalMysql{

  def getCanalMysql(str:String): CanalMysql ={

    val json: JSONObject = JSON.parseObject(str)
    CanalMysql(
      json.getString("columnValueList"),
      json.getString("dbName") ,
      json.getLong("emptyCount"),
      json.getString("eventType"),
      json.getString("logFileName"),
      json.getLong("logFileOffset"),
      json.getString("tableName"),
      json.getLong("timestamp")
    )
  }


}