package org.joq4j.broker;

import org.joq4j.Broker;
import org.joq4j.core.Subscriber;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisBroker implements Broker {

    private static final int DEFAULT_PORT = 6379;

    private final JedisPool jedisPool;
    private final Map<Subscriber, JedisSubscriber> subscriberMap = new HashMap<>();

    public RedisBroker(String host, int port) {
        this(new JedisPool(host, port));
    }

    public RedisBroker(String host) {
        this(host, DEFAULT_PORT);
    }

    public RedisBroker() {
        this("localhost");
    }

    private RedisBroker(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public static Jedis fromUri(String redisUri) {
        return new Jedis(URI.create(redisUri));
    }

    @Override
    public void subscribe(Subscriber subscriber, String... channels) {
        JedisSubscriber jedisSubscriber = new JedisSubscriber(subscriber);
        subscriberMap.put(subscriber, jedisSubscriber);

        Jedis jedis = jedisPool.getResource();
        jedis.subscribe(jedisSubscriber, channels);
    }

    @Override
    public void unsubscribe(Subscriber subscriber, String... channels) {
        JedisSubscriber jedisSubscriber = subscriberMap.get(subscriber);
        if (jedisSubscriber == null) {
            throw new IllegalArgumentException("Subscriber is not subscribed to a Redis channel");
        }
        jedisSubscriber.unsubscribe(channels);
        subscriberMap.remove(subscriber);
    }

    @Override
    public void publish(String channel, String data) {
        Jedis jedis = jedisPool.getResource();
        jedis.publish(channel, data);
    }

    @Override
    public String get(String key) {
        return jedisPool.getResource().get(key);
    }

    @Override
    public void set(String key, String value) {
        jedisPool.getResource().set(key, value);
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public void appendToList(String key, String... values) {

    }

    @Override
    public String popFromList(String key) {
        return null;
    }

    @Override
    public List<String> getList(String key) {
        return null;
    }

    @Override
    public void removeList(String key) {

    }

    @Override
    public void putToMap(String key, String field, String value) {
    }

    @Override
    public String getFromMap(String key, String field) {
        return null;
    }

    @Override
    public String removeFromMap(String key, String field) {
        return null;
    }

    @Override
    public void removeMap(String key) {

    }


    @Override
    public void close() throws IOException {
        jedisPool.close();
    }

    static class JedisSubscriber extends JedisPubSub {

        Subscriber subscriber;

        JedisSubscriber(Subscriber subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            subscriber.onSubscribe(channel);
        }

        @Override
        public void onMessage(String channel, String message) {
            subscriber.onMessage(channel, message.getBytes());
        }

        @Override
        public void onUnsubscribe(String channel, int subscribedChannels) {
            subscriber.onUnsubscribe(channel);
        }
    }
}
