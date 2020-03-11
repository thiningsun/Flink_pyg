package cn.itcast.config

import com.typesafe.config.{Config, ConfigFactory}

/**
  * @Date 2019/7/28
  */
object GlobalConfig {

  private val config: Config = ConfigFactory.load()
  //kafka的配置
  val kakfaServers: String = config.getString("bootstrap.servers")
  val kakfaZk: String = config.getString("zookeeper.connect")
  val kakfaTopic: String = config.getString("input.topic")
  val kakfaGroupID: String = config.getString("group.id")


}
