package org.joq4j.rabbitmq.amqp;

import com.google.common.base.Preconditions;
import org.joq4j.broker.Broker;
import org.joq4j.broker.BrokerFactory;
import org.joq4j.broker.BrokerFactoryRegistry;
import org.joq4j.common.utils.Strings;
import org.joq4j.config.Config;
import org.joq4j.rabbitmq.broker.RabMQBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabMQBrokerFactory implements BrokerFactory {
    @Override
    public Broker createBroker(Config config, String url) {
        Preconditions.checkArgument(Strings.isNonEmpty(url));
        Preconditions.checkArgument(url.startsWith("amqp"));
        RabMQConfig rabMQConfig = new RabMQConfig(config, url);

        return new RabMQBroker(rabMQConfig);
    }

    static {
        BrokerFactoryRegistry.addRegistry("amqp", RabMQBrokerFactory.class);
        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).debug("Broker registry added schema rabbitmq");
    }
}
