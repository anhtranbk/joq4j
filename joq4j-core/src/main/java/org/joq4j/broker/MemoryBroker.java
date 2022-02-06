package org.joq4j.broker;

import com.google.common.base.Preconditions;
import org.joq4j.common.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Use only for testing
 */
public class MemoryBroker implements Broker {

    private final Map<String, List<String>> listMap = new HashMap<>();
    private final Map<String, List<Subscriber>> subscriberMap = new HashMap<>();

    public MemoryBroker() {
    }

    public MemoryBroker(String url) {
        Preconditions.checkArgument(Strings.isNonEmpty(url));
        Preconditions.checkArgument(url.startsWith("mem://"));
    }

    @Override
    public void subscribe(Subscriber subscriber, String... channels) {
        for (String channel : channels) {
            subscriberMap.computeIfAbsent(channel, k -> new ArrayList<>()).add(subscriber);
            subscriber.onSubscribe(channel);
        }
    }

    @Override
    public void unsubscribe(String... channels) {
        for (String channel : channels) {
            subscriberMap.get(channel).forEach(subscriber -> subscriber.onUnsubscribe(channel));
            subscriberMap.remove(channel);
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
