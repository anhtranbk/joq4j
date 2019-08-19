package org.joq4j.internal;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Subscriber {

    void onSubscribe(String channel);

    void onMessage(String channel, String message);

    void onUnsubscribe(String channel);
}
