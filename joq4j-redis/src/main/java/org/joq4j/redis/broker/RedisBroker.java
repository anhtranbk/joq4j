package org.joq4j.redis.broker;

import org.joq4j.broker.Broker;
import org.joq4j.broker.Subscriber;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisBroker implements Broker {
    private final Jedis jedis;
    private final JedisPool pool;

    public RedisBroker(JedisPool pool) {
        this.jedis = pool.getResource();
        this.pool = pool;
    }

    @Override
    public synchronized void subscribe(Subscriber subscriber, String... channels) {
        jedis.subscribe(new MyJedisPubSub(subscriber), channels);
    }

    @Override
    public synchronized void unsubscribe(String... channels) {
        this.jedis.getClient().unsubscribe(channels);
    }

    @Override
    public synchronized void publish(String channel, String message) {
        jedis.publish(channel, message);
    }

    @Override
    public synchronized void push(String queue, String... values) {
        jedis.lpush(queue, values);
    }

    @Override
    public synchronized String pop(String queue) {
        return jedis.rpop(queue);
    }

    @Override
    public void close() {
        this.jedis.close();
        this.pool.close();
    }

    private static class MyJedisPubSub extends JedisPubSub {
        private final Subscriber subscriber;

        MyJedisPubSub(Subscriber subscriber) {
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
