package org.joq4j.broker;

import org.joq4j.config.Config;

public interface BrokerFactory {

    Broker createBroker(Config config, String url);
}
