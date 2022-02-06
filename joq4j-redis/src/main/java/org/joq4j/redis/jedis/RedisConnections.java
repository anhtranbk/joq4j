package org.joq4j.redis.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RedisConnections {
    private static final Map<String, JedisPool> clients = new HashMap<>();

    public static synchronized JedisPool getOrCreate(String name, RedisConfig jedisConfig) {
        return clients.computeIfAbsent(name, k -> init(jedisConfig));
    }

    public static synchronized JedisPool getDefault(RedisConfig jedisConfig) {
        return getOrCreate("default", jedisConfig);
    }

    private static JedisPool init(RedisConfig jedisConfig) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(jedisConfig.poolSize());
        jedisPoolConfig.setMaxIdle(jedisConfig.maxIdle());
        jedisPoolConfig.setMinIdle(jedisConfig.minIdle());
        jedisPoolConfig.setTestOnBorrow(jedisConfig.testOnBorrow());
        jedisPoolConfig.setMinEvictableIdleTimeMillis(jedisConfig.minEvictableIdleMillis());
        jedisPoolConfig.setBlockWhenExhausted(jedisConfig.blockWhenExhausted());

        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).info(
                "Init JedisPool from url " + jedisConfig.url(false));
        return new JedisPool(jedisPoolConfig, URI.create(jedisConfig.url(true)));
    }

    public static void shutdownPools() {
        clients.forEach((key, client) -> client.close());
    }
}
