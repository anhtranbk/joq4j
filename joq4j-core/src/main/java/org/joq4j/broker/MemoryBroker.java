package org.joq4j.broker;

import org.joq4j.Broker;
import org.joq4j.backend.StorageBackend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Only for testing, do not use this broker for production !
 */
public class MemoryBroker implements Broker {

    private Map<String, List<String>> listMap = new HashMap<>();
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
    public void appendToList(String key, String... values) {
        for (String value : values) {
            listMap.computeIfAbsent(key, k -> new LinkedList<>()).add(value);
        }
    }

    @Override
    public String popFromList(String key) {
        try {
            return listMap.get(key).remove(0);
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    @Override
    public void removeFromList(String key, String value) {
        try {
            listMap.get(key).remove(value);
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {
        }
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
    public void close() throws IOException {
    }
}
