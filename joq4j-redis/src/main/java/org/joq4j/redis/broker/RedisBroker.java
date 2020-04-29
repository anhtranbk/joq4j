package org.joq4j.redis.broker;

import org.joq4j.broker.Broker;
import org.joq4j.broker.Subscriber;
import org.joq4j.redis.RedisConf;
import org.joq4j.redis.connection.RedisConnectionProviders;
import redis.clients.jedis.Jedis;

public class RedisBroker implements Broker {
    private Jedis jedis;

    public RedisBroker() {
        this.jedis = RedisConnectionProviders.getDefault(new RedisConf());
    }

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
        jedis.rpush(queue, values);
    }

    @Override
    public String pop(String queue) {
        return jedis.rpop(queue);
    }

    @Override
    public void close() {

    }
}
