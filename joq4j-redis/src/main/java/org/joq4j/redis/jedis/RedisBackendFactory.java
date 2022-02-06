package org.joq4j.redis.jedis;

import com.google.common.base.Preconditions;
import org.joq4j.backend.BackendFactory;
import org.joq4j.backend.BackendFactoryRegistry;
import org.joq4j.backend.StorageBackend;
import org.joq4j.common.utils.Strings;
import org.joq4j.config.Config;
import org.joq4j.redis.backend.RedisBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisBackendFactory implements BackendFactory {

    @Override
    public StorageBackend createBackend(Config config, String url) {
        Preconditions.checkArgument(Strings.isNonEmpty(url));
        Preconditions.checkArgument(url.startsWith("redis"));
        RedisConfig jedisConfig = new RedisConfig(config, url);

        return new RedisBackend(RedisConnections.getDefault(jedisConfig));
    }

    static {
        BackendFactoryRegistry.addRegistry("redis", RedisBackendFactory.class);
        BackendFactoryRegistry.addRegistry("rediss", RedisBackendFactory.class);
        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).debug("BackendFactory registry added scheme redis");
    }
}
