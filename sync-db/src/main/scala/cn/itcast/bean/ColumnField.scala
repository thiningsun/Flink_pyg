package cn.itcast.bean

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}

import scala.collection.mutable.ArrayBuffer

/**
  * @Date 2019/7/29
  */
case class ColumnField(
                        var columnName: String,
                        var columnValue: String,
                        var isValid: Boolean
                      )

object ColumnField {


  def getColumnField(str: String): ArrayBuffer[ColumnField] = {

    val array: JSONArray = JSON.parseArray(str)
    val fields = new ArrayBuffer[ColumnField]()
    for (line <- 0 until array.size()) {
      val json: JSONObject = array.getJSONObject(line)
      fields+=ColumnField(
        json.getString("columnName"),
        json.getString("columnValue"),
        json.getBoolean("isValid")
      )
    }
    fields
  }

}

