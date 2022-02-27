package org.joq4j.rabbitmq.broker;

import com.rabbitmq.client.Channel;
import org.joq4j.broker.Broker;
import org.joq4j.broker.Subscriber;
import org.joq4j.rabbitmq.amqp.RabMQConfig;
import org.joq4j.rabbitmq.amqp.RabMQConnections;

public class RabMQBroker implements Broker {
    private Channel channel;
    private RabMQConfig config;
    public RabMQBroker(RabMQConfig config){
        this.config = config;
        this.channel = RabMQConnections.getChannel(config);
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
