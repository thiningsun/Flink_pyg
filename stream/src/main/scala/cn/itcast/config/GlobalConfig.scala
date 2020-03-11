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

  //hbase的配置
  val hbaseMaster: String = config.getString("hbase.master")
  val hbaseZk: String = config.getString("hbase.zookeeper.quorum")
  val hbaseRpcTimeout: String = config.getString("hbase.rpc.timeout")
  val hbaseOperationTimeout: String = config.getString("hbase.client.operation.timeout")
  val hbaseScanTimeout: String = config.getString("hbase.client.scanner.timeout.period")


}
