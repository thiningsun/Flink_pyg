# Flink—电商实时分析系统

# 项目背景



通过对用户的流浏览记录进行统计分析，来达到提高公司业绩，提升市场占有率的目的。

# 项目架构介绍

![1564298748249](photo/1564298748249.png)

# 技术选型

kafka,hbase,flink,canal

开发语言：

java,scala

# 课程目标

```
1）：掌握 HBASE 的搭建和基本运维操作
2）：掌握 flink 基本语法 
3）：掌握 kafka 的搭建和基本运维操作 
4）：掌握 canal 的使⽤ 
5）：能够独⽴开发出上报服务 （springboot）
6）：能够使⽤flink：处理实时热点数据及数据落地 Hbase 
7）：能够使⽤flink：处理频道的 PV、UV 及数据落地 Hbase 
8）：能够使⽤flink：处理新鲜度 
9）：能够使⽤flink：处理频道地域分布 
10）：能够使⽤flink：处理运营商平台数据 
11）：能够使⽤flink：处理浏览器类型 
12）：能够使⽤java代码对接 canal，并将数据同步到 kafka 
13）：能够使⽤flink(scala) 同步数据到 hbase
```

# 构建工程

![1564278824444](photo/1564278824444.png)

## 上报服务模块

使用springBoot开发web项目：

 优点：

​	内部集成tomcat

​        使用注解配置（省略了xml的配置）

​        开发速度快 



#### 1.导入依赖：

```java
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
</properties>

<!-- 阿里云 maven-->
<repositories>
    <repository>
        <id>alimaven</id>
        <name>alimaven</name>
        <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <version>1.5.13.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <version>1.5.13.RELEASE</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>1.5.13.RELEASE</version>
    </dependency>

    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.47</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
        <version>1.0.6.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-autoconfigure</artifactId>
        <version>1.5.13.RELEASE</version>
    </dependency>


</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

#### 2.导入application.properties

```java
server.port= 8090
#spring.application.name=report

#===kafka的生产配置
kafka.producer.servers=node01:9092,node02:9092,node03:9092
kafka.producer.retries=0
kafka.producer.batch.size=4096
kafka.producer.linger=1
kafka.producer.buffer.memory=40960
```

#### 3.Spring Boot启动入口

```
@SpringBootApplication
public class ReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class,args);
    }
}
```



#### 4.测试类

```
public class HttpTest {


    public static void main(String[] args) throws Exception {

        String address="http://localhost:8090/report/put";
        sendData(address,"test==info===");
    }

    private static void sendData(String address, String info) throws Exception {
        //获取连接
        URL url = new URL(address);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        //设置参数
        urlConnection.setAllowUserInteraction(true);
        urlConnection.setConnectTimeout(60000);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        //设置请求头
        urlConnection.setRequestProperty("Content-Type","application/json");
        //设置代理
        urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.108 Safari/537.36");

        //输出数据
        OutputStream outputStream = urlConnection.getOutputStream();
        BufferedOutputStream out = new BufferedOutputStream(outputStream);
        out.write(info.getBytes());
        out.flush();
        out.close();

        //接收响应数据
        InputStream inputStream = urlConnection.getInputStream();
        byte[] bytes = new byte[1024];
        String str = "";
        while (inputStream.read(bytes,0,1024)>-1){
            str = new String(bytes);
        }
        System.out.println("<<rsp value:"+str);
        System.out.println("rsp code:"+urlConnection.getResponseCode());

    }
}
```



## 实时流处理项目

#### 1.导入依赖

```java
<properties>
    <scala.version>2.11</scala.version>
    <flink.version>1.6.0</flink.version>
    <hadoop.version>2.6.0</hadoop.version>
    <hbase.version>1.2.0</hbase.version>
    <cdh.version>cdh5.14.0</cdh.version>
</properties>

<!--很多包都是CDH版本得到，如果不指定CDH的下载源，那么是下载不下来的-->
<repositories>
    <repository>
        <id>cloudera</id>
        <url>https://repository.cloudera.com/artifactory/cloudera-repos</url>
    </repository>
</repositories>

<dependencies>

    <dependency>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka_2.11</artifactId>
        <version>0.9.0.1</version>
    </dependency>
    <!--flink对接kafka：导入flink使用kafka的依赖-->
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-connector-kafka-0.9_${scala.version}</artifactId>
        <version>${flink.version}</version>
    </dependency>
    <!--导入scala的依赖-->
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-scala_${scala.version}</artifactId>
        <version>${flink.version}</version>
    </dependency>
    <!--模块二 流处理-->
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-streaming-scala_${scala.version}</artifactId>
        <version>${flink.version}</version>
    </dependency>
    <!--数据落地flink和hbase的集成依赖-->
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-hbase_${scala.version}</artifactId>
        <version>${flink.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-client</artifactId>
        <version>${hbase.version}-${cdh.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-server</artifactId>
        <version>${hbase.version}-${cdh.version}</version>
    </dependency>
    <!--hbase依赖于hadoop-->
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-common</artifactId>
        <version>${hadoop.version}-${cdh.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-hdfs</artifactId>
        <version>${hadoop.version}-${cdh.version}</version>
        <!--xml.parser冲突 flink hdfs-->
        <exclusions>
            <exclusion>
                <groupId>xml-apis</groupId>
                <artifactId>xml-apis</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <!--对象和json 互相转换的-->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.44</version>
    </dependency>

</dependencies>

<!--开发环境-->

<profiles>
    <!--开发环境-->
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
            <property>
                <name>env</name>
                <value>Env</value>
            </property>
        </activation>
        <build>
            <resources>
                <resource>
                    <directory>src/main/resources/dev</directory>
                </resource>
            </resources>
        </build>
    </profile>

    <!--生产环境-->
    <profile>
        <id>pro</id>
        <activation>
            <activeByDefault>true</activeByDefault>
            <property>
                <name>pro</name>
                <value>Pro</value>
            </property>
        </activation>
        <build>
            <resources>
                <resource>
                    <directory>src/main/resources/pro</directory>
                </resource>
            </resources>
        </build>
    </profile>

    <!--测试环境-->
    <profile>
        <id>test</id>
        <activation>
            <activeByDefault>true</activeByDefault>
            <property>
                <name>test</name>
                <value>Test</value>
            </property>
        </activation>
        <build>
            <resources>
                <resource>
                    <directory>src/main/resources/test</directory>
                </resource>
            </resources>
        </build>
    </profile>
</profiles>
```

#### 2.配置文件

![1564286993513](photo/1564286993513.png)



#### 3.代码开发

（1）flink整合kafka （source）

（2）flink整合hbase (sink)

业务内容：

```
实时分析频道热点 
实时分析频道PV/UV 
实时分析频道新鲜度 
实时分析频道地域分布 
实时分析运营商平台 
实时分析浏览器类型
```

##### hbaseUtil

```java
object HbaseUtil {

  private val configuration: Configuration = HBaseConfiguration.create()

  configuration.set("hbase.master", GlobalConfig.hbaseMaster)
  configuration.set("hbase.zookeeper.quorum", GlobalConfig.hbaseZk)
  configuration.set("hbase.rpc.timeout", GlobalConfig.hbaseRpcTimeout)
  configuration.set("hbase.client.operation.timeout", GlobalConfig.hbaseOperationTimeout)
  configuration.set("hbase.client.scanner.timeout.period", GlobalConfig.hbaseScanTimeout)

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
    //putDataByRowkey("test-0728","info","name","zhangsan","001")

    //数据查询，rowkey
    //val str: String = getDataByRowkey("test-0728","info","name","001")
    //println("value:"+ str)

    //插入map数据
    //putMapDataByRowkey("test-0728","info",Map("k1"->1,"k2"->2),"002")

    //数据删除
    delByRowkey("test-0728","info","002")

  }

}
```

##### App主类

```java
object App {

  def main(args: Array[String]): Unit = {

    /**
      * 步骤：
      * 1.获取流处理执行环境
      * 2.添加checkpoint
      * 3.整合kafka
      * 4.加载数据源
      * 5.数据转换
      * 6.添加水位线
      * 7.具体task执行任务
      * 8.触发执行
      */
    //1.获取流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //2.添加checkpoint
    env.setStateBackend(new FsStateBackend("hdfs://node01:8020/flink-checkpoint"))
    //周期性的发送检查点
    env.enableCheckpointing(6000)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    env.getCheckpointConfig.setCheckpointInterval(6000)
    env.getCheckpointConfig.setFailOnCheckpointingErrors(false)
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    //需要手动删除检查点
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)

    //设置事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime) //消息源本身的时间

    //3.整合kafka
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", GlobalConfig.kakfaServers)
    properties.setProperty("zookeeper.connect", GlobalConfig.kakfaZk)
    properties.setProperty("group.id", GlobalConfig.kakfaGroupID)

    val kafkaSource: FlinkKafkaConsumer09[String] = new FlinkKafkaConsumer09[String](GlobalConfig.kakfaTopic, new SimpleStringSchema(), properties)
    kafkaSource.setStartFromEarliest()

    //4.加载数据源
    val source: DataStream[String] = env.addSource(kafkaSource)

    //5.数据转换
    val messages: DataStream[Message] = source.map(line => {

      val json: JSONObject = JSON.parseObject(line)
      val count: Int = json.getIntValue("count")
      val message: String = json.getString("message")
      val timestamp: Long = json.getLong("timestamp")
      val userBrowse: UserBrowse = UserBrowse.getUserBrowse(message)
      Message(count, timestamp, userBrowse)
    })

    //6.添加水位线
    val waterData: DataStream[Message] = messages.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks[Message] {
      val delayTime: Long = 2000L
      var currentTimestamp: Long = 0

      override def getCurrentWatermark: Watermark = {
        new Watermark(currentTimestamp - delayTime)
      }

      override def extractTimestamp(element: Message, previousElementTimestamp: Long): Long = {
        val timestamp: Long = element.userBrowse.timestamp
        currentTimestamp = Math.max(timestamp, currentTimestamp)
        currentTimestamp
      }
    })
    waterData.print()
    //7.具体task执行任务



    //8.触发执行
    env.execute()
  }

}
```





























































































































































































































































































































































