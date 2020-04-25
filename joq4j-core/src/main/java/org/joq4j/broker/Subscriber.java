package org.joq4j.broker;

public interface Subscriber {

    void onSubscribe(String channel);

    void onMessage(String channel, String message);

    void onUnsubscribe(String channel);
}
