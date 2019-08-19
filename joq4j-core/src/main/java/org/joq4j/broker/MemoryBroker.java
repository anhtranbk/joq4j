package org.joq4j.broker;

import org.joq4j.Broker;
import org.joq4j.internal.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MemoryBroker implements Broker {

    private Map<String, List<String>> listMap = new HashMap<>();
    private Map<String, Map<String, String>> mapMap = new HashMap<>();
    private Map<String, String> map = new HashMap<>();
    private Map<String, List<Subscriber>> subscriberMap = new HashMap<>();

    @Override
    public void subscribe(Subscriber subscriber, String... channels) {
        for (String channel : channels) {
            subscriberMap.computeIfAbsent(channel, k -> new ArrayList<>()).add(subscriber);
            subscriber.onSubscribe(channel);
        }
    }

    @Override
    public void unsubscribe(Subscriber subscriber, String... channels) {
        for (String channel : channels) {
            subscriberMap.get(channel).remove(subscriber);
            subscriber.onUnsubscribe(channel);
        }
    }

    @Override
    public void publish(String channel, String message) {
        for (Subscriber subscriber : subscriberMap.get(channel)) {
            subscriber.onMessage(channel, message);
        }
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public void set(String key, String value) {
        map.put(key, value);
    }

    @Override
    public String remove(String key) {
        return map.remove(key);
    }

    @Override
    public void appendToList(String key, String... values) {
        for (String value : values) {
            listMap.computeIfAbsent(key, k -> new LinkedList<>()).add(value);
        }
    }

    @Override
    public String popFromList(String key) {
        return listMap.get(key).remove(0);
    }

    @Override
    public List<String> getList(String key) {
        return listMap.get(key);
    }

    @Override
    public List<String> removeList(String key) {
        return listMap.remove(key);
    }

    @Override
    public void putMap(String key, Map<String, String> fieldMap) {
        mapMap.put(key, fieldMap);
    }

    @Override
    public void putToMap(String key, String field, String value) {
        mapMap.computeIfAbsent(key, k -> new HashMap<>()).put(field, value);
    }

    @Override
    public String getFromMap(String key, String field) {
        return mapMap.get(key).get(field);
    }

    @Override
    public Map<String, String> getMap(String key) {
        return mapMap.get(key);
    }

    @Override
    public String removeFromMap(String key, String field) {
        return mapMap.get(key).remove(field);
    }

    @Override
    public Map<String, String> removeMap(String key) {
        return mapMap.remove(key);
    }

    @Override
    public void close() throws IOException {
    }
}
