package cn.itcast.config;

import cn.itcast.bean.RoundRobinPartition;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2019/7/28
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${kafka.producer.servers}")
    private String servers;

    @Value("${kafka.producer.retries}")
    private int retries;

    @Value("${kafka.producer.batch.size}")
    private int batchSize;

    @Value("${kafka.producer.linger}")
    private int linger;

    @Value("${kafka.producer.buffer.memory}")
    private int memory;

    @Bean
    public KafkaTemplate getkafkaTemplate() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        map.put(ProducerConfig.RETRIES_CONFIG, retries);
        map.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        map.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        map.put(ProducerConfig.BUFFER_MEMORY_CONFIG, memory);
        //序列化配置
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        //设置数据倾斜,避免数据倾斜采用算法-roundBin
        map.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartition.class);

        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<String, String>(map);

        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<String, String>(producerFactory);
        return kafkaTemplate;
    }


}
