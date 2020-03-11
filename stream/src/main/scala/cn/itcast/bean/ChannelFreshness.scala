package cn.itcast.bean

/**
  * @Date 2019/7/29
  */
class ChannelFreshness {

  var channelID:Long = 0L
  var dataField:String =null //扩展字段
  var newCount:Long = 0L  //新用户
  var oldCount:Long = 0L  //老用户

  //get
  def getChannelID = channelID
  def getDataField= dataField
  def getNewCount = newCount
  def getOldCount = oldCount

  //set
  def setChannelID(value:Long) = {channelID = value}
  def setDataField(value:String) ={dataField = value}
  def setNewCount(value:Long) = {newCount = value}
  def setOldCount(value:Long) ={oldCount = value}



}
