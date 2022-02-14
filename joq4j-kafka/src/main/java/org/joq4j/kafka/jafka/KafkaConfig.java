package org.joq4j.kafka.jafka;

import org.joq4j.config.Config;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.ConfigHelper;

import java.util.List;
import java.util.Properties;

public class KafkaConfig {
    // kafka broker configurations
    @ConfigDescriptor(name = "kafka.boostrap.servers", defaultValue = "localhost:9092")
    private String bootStrapServers;
    @ConfigDescriptor(name="kafka.topics", defaultValue = "")
    public List<String> Topics;
    // producer configurations
    @ConfigDescriptor(name = "kafka.key.serializer",
            defaultValue = "org.apache.kafka.common.serialization.StringSerializer")
    private String keySerializer;
    @ConfigDescriptor(name = "kafka.value.serializer",
            defaultValue = "org.apache.kafka.common.serialization.StringSerializer")
    private String valueSerializer;
    @ConfigDescriptor(name="kafka.acks", defaultValue = "1")
    private int ACKs;
    @ConfigDescriptor(name="kafka.buffer.memory", defaultValue = "33554432")
    private int bufferMemory;
    @ConfigDescriptor(name="kafka.batch.size", defaultValue = "16384")
    private int batchSize;
    @ConfigDescriptor(name="kafka.compression.type", defaultValue = "snappy")
    private String compressType;
    @ConfigDescriptor(name="kafka.delivery.timeout.ms", defaultValue = "120000")
    private int deliveryTimeoutMs;
    // Consumer configurations
    @ConfigDescriptor(name = "kafka.key.deserializer",
            defaultValue = "org.apache.kafka.common.serialization.StringDeserializer")
    private String keyDeserializer;
    @ConfigDescriptor(name = "kafka.value.deserializer",
            defaultValue = "org.apache.kafka.common.serialization.StringDeserializer")
    private String valueDeserializer;
    @ConfigDescriptor(name = "kafka.group.id", defaultValue = "jq4j-group")
    private String groupId;
    @ConfigDescriptor(name="kafka.enable.auto.commit", defaultValue = "false")
    private int enableAutoCommit;
    @ConfigDescriptor(name="kafka.num.consumers", defaultValue = "1")
    private int  numOfConsumers;
    @ConfigDescriptor(name="kafka.fetch.min.bytes", defaultValue = "1048576")
    private int fetchMinBytes;
    @ConfigDescriptor(name="kafka.auto.offset.reset", defaultValue= "latest")
    private String autoOffsetReset;

    public KafkaConfig(Config conf){
        ConfigHelper.injectFields(this, conf);
    }

    public Properties getProducerProps(){
        Properties props = new Properties();
        props.put("key.serializer", keySerializer);
        props.put("value.serializer", valueDeserializer);
        props.put("acks", ACKs);
        props.put("buffer.memory", bufferMemory);
        props.put("batch.size", batchSize);
        props.put("compress.type", compressType);
        props.put("delivery.timeout.ms", deliveryTimeoutMs);

        return props;
    }
    public Properties getConsumerProps(){
        Properties props = new Properties();
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", enableAutoCommit);
        props.put("fetch.min.bytes", fetchMinBytes);
        props.put("auto.offset.reset", autoOffsetReset);

        return props;
    }
}
