package org.joq4j.worker;

import org.joq4j.backend.BackendFactory;
import org.joq4j.backend.StorageBackend;
import org.joq4j.broker.Broker;
import org.joq4j.broker.BrokerFactory;
import org.joq4j.broker.Subscriber;
import org.joq4j.common.lifecycle.AbstractLifeCycle;
import org.joq4j.common.utils.Reflects;
import org.joq4j.common.utils.Strings;
import org.joq4j.config.Config;

public class Worker extends AbstractLifeCycle implements Subscriber {

    private final WorkerOptions options;
    private final Broker broker;
    private final StorageBackend backend;

    public Worker(Config conf, WorkerOptions options) {
        this.options = options;
        BrokerFactory brokerFactory = Reflects.newInstance(options.getBrokerFactoryClass());
        this.broker = brokerFactory.createBroker(conf, options.getBrokerUrl());

        BackendFactory backendFactory = Reflects.newInstance(options.getBackendFactoryClass());
        this.backend = backendFactory.createBackend(conf, options.getBackendUrl());
    }

    public Worker(WorkerOptions options, Broker broker, StorageBackend backend) {
        this.options = options;
        this.broker = broker;
        this.backend = backend;
    }

    @Override
    protected void onStart() {
        super.onStart();
        logger.info("Launching a worker with following settings:");
        logger.info("Broker: " + options.getBrokerUrl());
        logger.info("StorageBackend: " + options.getBackendUrl());
        logger.info("Queues: " + options.getQueues());

        this.broker.subscribe(this, Strings.join(options.getQueues(), ","));

        logger.info("Waiting for messages...To exit press Ctrl + C");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSubscribe(String channel) {

    }

    @Override
    public void onMessage(String channel, String message) {

    }

    @Override
    public void onUnsubscribe(String channel) {
        logger.info("Worker unsubscribed from channel: " + channel);
    }
}
