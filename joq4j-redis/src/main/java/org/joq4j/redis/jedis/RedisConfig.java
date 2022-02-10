package org.joq4j.redis.jedis;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.joq4j.common.net.ConnectionUrl;
import org.joq4j.common.utils.Strings;
import org.joq4j.config.Config;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.ConfigHelper;
import org.joq4j.config.Configurable;

@Accessors(chain = true, fluent = true)
@Data
@Configurable
public class RedisConfig {

    @ConfigDescriptor(name = "redis.url")
    @Getter(AccessLevel.NONE)
    private String url;

    @ConfigDescriptor(name = "redis.host", defaultValue = "localhost")
    private String host;

    @ConfigDescriptor(name = "redis.port", defaultValue = "6379")
    private int port;

    @ConfigDescriptor(name = "redis.database", defaultValue = "0")
    private String database;

    @ConfigDescriptor(name = "redis.password")
    private String password;

    @ConfigDescriptor(name = "jedis.pool.maxSize", defaultValue = "10")
    private Integer poolSize;

    @ConfigDescriptor(name = "jedis.pool.minIdle", defaultValue = "1")
    private Integer minIdle;

    @ConfigDescriptor(name = "jedis.pool.maxIdle", defaultValue = "5")
    private Integer maxIdle;

    @ConfigDescriptor(name = "jedis.pool.testOnBorrow", defaultValue = "true")
    private Boolean testOnBorrow;

    @ConfigDescriptor(name = "jedis.pool.minEvictableIdleMillis", defaultValue = "30000")
    private Long minEvictableIdleMillis;

    @ConfigDescriptor(name = "jedis.pool.blockWhenExhausted", defaultValue = "true")
    private Boolean blockWhenExhausted;

    public RedisConfig(Config conf, String url) {
        this(conf);
        this.url = url;
    }

    public RedisConfig(Config conf) {
        ConfigHelper.injectFields(this, conf);
    }

    public String url(boolean withCredentials) {
        if (Strings.isNonEmpty(this.url)) {
            return this.url;
        }
        ConnectionUrl connectionUrl = new ConnectionUrl()
                .scheme("redis")
                .host(this.host)
                .port(this.port)
                .password(this.password)
                .database(this.database);
        return connectionUrl.asString(withCredentials);
    }
}
