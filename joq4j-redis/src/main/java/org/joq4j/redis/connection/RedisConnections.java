package org.joq4j.redis.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

public class RedisConnections {
    private static final Logger logger = LoggerFactory.getLogger(RedisConnections.class);
    private static final Map<String, JedisPool> clients = new TreeMap<>();

    public static synchronized Jedis getOrCreate(String name, RedisConf redisConf) {
        JedisPool jedisPool = clients.computeIfAbsent(name, k -> init(redisConf));
        return jedisPool.getResource();
    }

    public static synchronized Jedis getDefault(RedisConf redisConf) {
        return getOrCreate("default", redisConf);
    }

    private static JedisPool init(RedisConf redisConf) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisConf.poolSize());
        jedisPoolConfig.setMaxIdle(redisConf.maxIdle());
        jedisPoolConfig.setMinIdle(redisConf.minIdle());
        jedisPoolConfig.setTestOnBorrow(redisConf.testOnBorrow());
        jedisPoolConfig.setMinEvictableIdleTimeMillis(redisConf.minEvictableIdleMillis());
        jedisPoolConfig.setBlockWhenExhausted(redisConf.blockWhenExhausted());

        logger.info("Init JedisPool from url " + redisConf.url(false));
        return new JedisPool(jedisPoolConfig, URI.create(redisConf.url(true)));
    }

    public static void shutdownPools() {
        clients.forEach((key, client) -> client.close());
    }
}
