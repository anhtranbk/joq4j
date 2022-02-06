package org.joq4j.redis.backend;

import org.joq4j.backend.KeyValueBackend;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Map;

public class RedisBackend implements KeyValueBackend {
    private final Jedis jedis;
    private final JedisPool pool;

    public RedisBackend(JedisPool pool) {
        this.jedis = pool.getResource();
        this.pool = pool;
    }

    @Override
    public void put(String key, Map<String, String> fieldMap) {
        jedis.hset(key, fieldMap);
    }

    @Override
    public void putOne(String key, String field, String value) {
        jedis.hset(key, field, value);
    }

    @Override
    public String getOne(String key, String field) {
        return jedis.hget(key, field);
    }

    @Override
    public Map<String, String> get(String key) {
        return jedis.hgetAll(key);
    }

    @Override
    public String removeOne(String key, String field) {
        String value = getOne(key, field);
        jedis.hdel(key, field);
        return value;
    }

    @Override
    public Map<String, String> remove(String key) {
        Map<String, String> value = jedis.hgetAll(key);
        jedis.del(key);
        return value;
    }

    @Override
    public void close() throws IOException {
        this.jedis.close();
        this.pool.close();
    }
}
