package cn.itcast.bean

/**
  * @Date 2019/7/28
  */
class ChannelPvuv {

  var pv:Long = 0L
  var uv:Long=0L
  var channelID:Long=0L
  var dataField:String=null

  //get
  def getPv = pv
  def getUv = uv
  def getChannedID= channelID
  def getDataField =dataField

  //set
  def setPv(value:Long) = {pv = value}
  def setUv(value:Long) ={uv = value}
  def setChannelID(value:Long) = {channelID = value}
  def setDataField(value:String)={dataField = value}
}
