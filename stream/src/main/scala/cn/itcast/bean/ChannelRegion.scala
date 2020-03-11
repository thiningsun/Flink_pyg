package cn.itcast.bean

/**
  * @Date 2019/7/29
  */
class ChannelRegion {

  var pv:Long = 0L
  var uv:Long = 0L
  var newCount:Long = 0L
  var oldCount:Long =0L
  var dataField:String =null  //时间扩展字段
  var channelID:Long=0L    //频道ID
  var country:String = null
  var province:String = null
  var city:String = null

  //get
  def getPv = pv
  def getUv = uv
  def getNewCount = newCount
  def getOldCount = oldCount
  def getDataField = dataField
  def getChannelID = channelID
  def getCountry = country
  def getProvince = province
  def getCity = city

  //set
  def setPv(value:Long) = {pv = value}
  def setUv(value:Long) ={uv=value}
  def setNewCount(value:Long)={newCount = value}
  def setOldCount (value:Long)={oldCount= value}
  def setDataField(value:String)={dataField=value}
  def setChannelID(value:Long) ={channelID=value}
  def setCountry(value:String)={country=value}
  def setProvice(value:String)={province=value}
  def setCity(value:String)={city=value}


}
