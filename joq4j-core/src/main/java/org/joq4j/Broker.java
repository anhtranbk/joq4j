package org.joq4j;

import org.joq4j.internal.Subscriber;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

public interface Broker extends Closeable {

    void subscribe(Subscriber subscriber, String... channels);

    void unsubscribe(Subscriber subscriber, String... channels);

    void publish(String channel, String message);

    String get(String key);

    void set(String key, String value);

    String remove(String key);

    void appendToList(String key, String... values);

    String popFromList(String key);
    
    List<String> getList(String key);

    List<String> removeList(String key);

    void putMap(String key, Map<String, String> fieldMap);

    void putToMap(String key, String field, String value);

    String getFromMap(String key, String field);

    Map<String, String> getMap(String key);

    String removeFromMap(String key, String field);

    Map<String, String> removeMap(String key);
}
