package org.joq4j.redis;

import org.joq4j.broker.Broker;
import org.joq4j.broker.Subscriber;
import redis.clients.jedis.Jedis;

public class RedisBroker implements Broker {
    private Jedis jedis;
    @Override
    public void subscribe(Subscriber subscriber, String... channels) {

    }

    @Override
    public void unsubscribe(Subscriber subscriber, String... channels) {

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
