package org.joq4j.kafka.jafka;

import org.joq4j.broker.Broker;
import org.joq4j.broker.BrokerFactory;
import org.joq4j.broker.BrokerFactoryRegistry;
import org.joq4j.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaBrokerFactory implements BrokerFactory {
    @Override
    public Broker createBroker(Config config, String url) {
        return null;
    }

    static {
        BrokerFactoryRegistry.addRegistry("kafka", KafkaBrokerFactory.class);
        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).debug("BrokerFactory registry added scheme kafka");
    }
}
