package org.joq4j.broker;

import org.joq4j.Broker;
import org.joq4j.internal.Subscriber;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.SetParams;

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
    public String remove(String key) {
        Jedis jedis = jedisPool.getResource();
        String val = jedis.get(key);
        jedis.del(key);
        return val;
    }

    @Override
    public void appendToList(String key, String... values) {
        jedisPool.getResource().rpush(key, values);
    }

    @Override
    public String popFromList(String key) {
        return jedisPool.getResource().lpop(key);
    }

    @Override
    public List<String> getList(String key) {
        Jedis jedis = jedisPool.getResource();
        long len = jedis.llen(key);
        return jedis.lrange(key, 0, len);
    }

    @Override
    public List<String> removeList(String key) {
        Jedis jedis = jedisPool.getResource();
        long len = jedis.llen(key);
        List<String> list = jedis.lrange(key, 0, len);
        jedis.del(key);
        return list;
    }

    @Override
    public void putMap(String key, Map<String, String> fieldMap) {
        jedisPool.getResource().hset(key, fieldMap);
    }

    @Override
    public void putToMap(String key, String field, String value) {
        jedisPool.getResource().hset(key, field, value);
    }

    @Override
    public String getFromMap(String key, String field) {
        return jedisPool.getResource().hget(key, field);
    }

    @Override
    public Map<String, String> getMap(String key) {
        return jedisPool.getResource().hgetAll(key);
    }

    @Override
    public String removeFromMap(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        String val = jedis.hget(key, field);
        jedis.hdel(key, field);
        return val;
    }

    @Override
    public Map<String, String> removeMap(String key) {
        Map<String, String> map = getMap(key);
        jedisPool.getResource().del(key);
        return map;
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
            subscriber.onMessage(channel, message);
        }

        @Override
        public void onUnsubscribe(String channel, int subscribedChannels) {
            subscriber.onUnsubscribe(channel);
        }
    }
}
