package org.joq4j.redis.jedis;

import com.google.common.base.Preconditions;
import org.joq4j.broker.Broker;
import org.joq4j.broker.BrokerFactory;
import org.joq4j.broker.BrokerFactoryRegistry;
import org.joq4j.common.utils.Strings;
import org.joq4j.config.Config;
import org.joq4j.redis.broker.RedisBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisBrokerFactory implements BrokerFactory {
    @Override
    public Broker createBroker(Config config, String url) {
        Preconditions.checkArgument(Strings.isNonEmpty(url));
        Preconditions.checkArgument(url.startsWith("redis"));
        RedisConfig redisConfig = new RedisConfig(config, url);

        return new RedisBroker(RedisConnections.getDefault(redisConfig));
    }

    static {
        BrokerFactoryRegistry.addRegistry("redis", RedisBrokerFactory.class);
        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).debug("BrokerFactory registry added scheme redis");
    }
}
