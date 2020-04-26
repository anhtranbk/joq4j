package org.joq4j.redis;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joq4j.common.net.ConnectionUrl;
import org.joq4j.common.utils.Strings;
import org.joq4j.config.Config;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.Configurable;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@Accessors(chain = true, fluent = true)
public @Data class RedisConf implements Configurable {

    @ConfigDescriptor(name = "redis.url")
    private String url;

    @ConfigDescriptor(name = "redis.host", defaultValue = "localhost")
    private String host;

    @ConfigDescriptor(name = "redis.port", defaultValue = "3306")
    private int port;

    @ConfigDescriptor(name = "redis.database")
    private String database;

    @ConfigDescriptor(name = "redis.username")
    private String username;

    @ConfigDescriptor(name = "redis.password")
    private String password;

    public RedisConf() {
        super();
    }

    public RedisConf(Config conf) {
        this.configure(conf);
    }

    public String url() {
        return url(false);
    }

    public String url(boolean withCredentials) {
        if (Strings.isNonEmpty(this.url)) {
            return this.url;
        }
        ConnectionUrl connectionUrl = new ConnectionUrl()
                .scheme("redis")
                .host(this.host)
                .port(this.port)
                .username(this.username)
                .password(this.password)
                .database(this.database);
        return connectionUrl.asString(withCredentials);
    }
}
