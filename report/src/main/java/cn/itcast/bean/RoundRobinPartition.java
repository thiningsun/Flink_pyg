package cn.itcast.bean;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date 2019/7/28
 */
public class RoundRobinPartition implements Partitioner {

    AtomicInteger atomicInteger = new AtomicInteger(0);
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        Integer cnt = cluster.partitionCountForTopic(topic);
        int i = atomicInteger.incrementAndGet() % cnt;
        if(atomicInteger.get()> 1000){
            atomicInteger.set(0);
        }
        return i;
    }

    public void close() {

    }

    public void configure(Map<String, ?> configs) {

    }
}
