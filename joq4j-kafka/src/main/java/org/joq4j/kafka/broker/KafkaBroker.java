package org.joq4j.kafka.broker;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.internals.KafkaConsumerMetrics;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.joq4j.broker.Broker;
import org.joq4j.broker.Subscriber;
import org.joq4j.config.Config;
import org.joq4j.kafka.jafka.KafkaConfig;

public class KafkaBroker implements Broker {
    private KafkaProducer<String, byte[]> kafkaProducer;
    private KafkaConsumer<String, byte[]> kafkaConsumer;
    public KafkaBroker(Config conf) {
        KafkaConfig kafkaConfig = new KafkaConfig(conf);
        kafkaProducer = new KafkaProducer<>(kafkaConfig.getProducerProps());
        kafkaConsumer = new KafkaConsumer<>(kafkaConfig.getConsumerProps());
        kafkaConsumer.subscribe(kafkaConfig.Topics);
    }
    @Override
    public void subscribe(Subscriber subscriber, String... channels) {

    }

    @Override
    public void unsubscribe(String... channels) {

    }

    @Override
    public void publish(String channel, String message) {

    }

    @Override
    public void push(String queue, String... values) {

    }

    @Override
    public String pop(String queue) {
        return null;
    }

    @Override
    public void close() {

    }
}
