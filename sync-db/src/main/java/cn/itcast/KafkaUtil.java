package cn.itcast;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

import java.util.Properties;

/**
 * @Date 2019/7/29
 */
public class KafkaUtil {

    public static Producer getProducer() {

        Properties prop = new Properties();
        prop.put("zookeeper.connect", "node01:2181,node02:2181,node03:2181");
        prop.put("metadata.broker.list", "node01:9092,node02:9092,node03:9092");
        prop.put("serializer.class", StringEncoder.class.getName());

        ProducerConfig producerConfig = new ProducerConfig(prop);
        Producer<String, String> producer = new Producer<String, String>(producerConfig);
        return producer;
    }

    public static void senMsg(String topic, String key, String value) {
        //获取Producer生产者对象
        Producer producer = getProducer();
        producer.send(new KeyedMessage(topic, key, value));
    }


}
