package org.joq4j.broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public void push(String queue, String... values) {
        for (String value : values) {
            listMap.computeIfAbsent(queue, k -> new LinkedList<>()).add(value);
        }
    }

    @Override
    public String pop(String queue) {
        try {
            return listMap.get(queue).remove(0);
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    @Override
    public void close() {

    }
}
