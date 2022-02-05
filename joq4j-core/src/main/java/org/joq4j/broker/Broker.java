package org.joq4j.broker;

import java.io.Closeable;

public interface Broker extends Closeable {

    void subscribe(Subscriber subscriber, String... channels);

    void unsubscribe(String... channels);

    void publish(String channel, String message);

    void push(String queue, String... values);

    String pop(String queue);

    void close();
}
