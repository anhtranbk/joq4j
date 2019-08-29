package org.joq4j;

import org.joq4j.broker.Subscriber;

import java.io.Closeable;
import java.util.List;

public interface Broker extends Closeable {

    void subscribe(Subscriber subscriber, String... channels);

    void unsubscribe(Subscriber subscriber, String... channels);

    void publish(String channel, String message);

    void appendToList(String key, String... values);

    String popFromList(String key);

    void removeFromList(String key, String value);
    
    List<String> getList(String key);

    List<String> removeList(String key);
}
