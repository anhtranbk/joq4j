package org.joq4j.kafka.broker;

import org.joq4j.broker.Broker;
import org.joq4j.broker.Subscriber;

public class KafkaBroker implements Broker {
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
