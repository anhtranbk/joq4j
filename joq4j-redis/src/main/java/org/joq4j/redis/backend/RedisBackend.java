package org.joq4j.redis.backend;

import com.google.common.base.Preconditions;
import org.joq4j.backend.KeyValueBackend;
import org.joq4j.common.utils.Strings;
import org.joq4j.config.Config;
import org.joq4j.redis.connection.RedisConf;
import org.joq4j.redis.connection.RedisConnections;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Map;

public class RedisBackend implements KeyValueBackend {
    private final Jedis jedis;

    public RedisBackend(Config config) {
        jedis = RedisConnections.getDefault(new RedisConf(config));
    }

    public RedisBackend(String url) {
        Preconditions.checkArgument(Strings.isNonEmpty(url));
        Preconditions.checkArgument(url.startsWith("redis://"));
        jedis = RedisConnections.getDefault(new RedisConf(url));
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
    public String encodeResult(Object result) {
        return null;
    }

    @Override
    public void close() throws IOException {
        jedis.close();
    }
}
