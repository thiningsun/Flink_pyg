package cn.itcast.bean

/**
  * @Date 2019/7/29
  */
class ChannelNetwork {


  var channelId:Long = 0L
  var network:String = null
  var dataField:String=null //格式化日期的扩展字段
  var newCount:Long = 0L
  var oldCount:Long = 0L

  //get
  def getChannelId = channelId
  def getNetWork=network
  def getDataField = dataField
  def getNewCount = newCount
  def getOldCount = oldCount

  //set
  def setChannelId(value:Long)={channelId=value}
  def setNewWork(value:String)={network= value}
  def setDataField(value:String )={dataField=value}
  def setNewCount(value:Long)={newCount=value}
  def setOldCount(value:Long)={oldCount=value}


}
