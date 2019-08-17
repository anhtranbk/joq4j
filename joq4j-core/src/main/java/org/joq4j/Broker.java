package org.joq4j;

import org.joq4j.core.Subscriber;

import java.io.Closeable;
import java.util.List;

public interface Broker extends Closeable {

    void subscribe(Subscriber subscriber, String... channels);

    void unsubscribe(Subscriber subscriber, String... channels);

    void publish(String channel, String message);

    String get(String key);

    void set(String key, String value);

    void remove(String key);

    void appendToList(String key, String... values);

    String popFromList(String key);
    
    List<String> getList(String key);

    void removeList(String key);

    void putToMap(String key, String field, String value);

    String getFromMap(String key, String field);

    String removeFromMap(String key, String field);

    void removeMap(String key);
}
