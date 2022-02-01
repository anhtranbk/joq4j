package org.joq4j.redis.connection;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.joq4j.common.net.ConnectionUrl;
import org.joq4j.common.utils.Strings;
import org.joq4j.config.Config;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.Configurable;

@Accessors(chain = true, fluent = true)
public @Data
class RedisConf implements Configurable {

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

    @ConfigDescriptor(name = "redis.pool.maxSize", defaultValue = "10")
    private Integer poolSize;

    @ConfigDescriptor(name = "redis.pool.minIdle", defaultValue = "1")
    private Integer minIdle;

    @ConfigDescriptor(name = "redis.pool.maxIdle", defaultValue = "5")
    private Integer maxIdle;

    @ConfigDescriptor(name = "redis.pool.testOnBorrow", defaultValue = "true")
    private Boolean testOnBorrow;

    @ConfigDescriptor(name = "redis.pool.minEvictableIdleMillis", defaultValue = "30000")
    private Long minEvictableIdleMillis;

    @ConfigDescriptor(name = "redis.pool.blockWhenExhausted", defaultValue = "true")
    private Boolean blockWhenExhausted;

    public RedisConf() {
        this.configure(new Config());
    }

    public RedisConf(String url) {
        this.configure(new Config());
        this.url = url;
    }

    public RedisConf(Config conf) {
        this.configure(conf);
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
