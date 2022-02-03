package org.joq4j.broker;

import org.joq4j.Joq4jException;
import org.joq4j.common.net.ConnectionUrl;
import org.joq4j.common.utils.Strings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BrokerFactory {

    private static final Map<String, Class<? extends Broker>> brokerRegistry = new HashMap<>();

    public static Broker fromUrl(String url) {
        ConnectionUrl connectionUrl = ConnectionUrl.parseFromString(url);
        Class<? extends Broker> clazz = brokerRegistry.get(connectionUrl.scheme());
        if (clazz == null) {
            throw new IllegalArgumentException(
                    Strings.format("Scheme %s not found", connectionUrl.scheme()));
        }

        try {
            Constructor<? extends Broker> constructor = clazz.getConstructor(String.class);
            return constructor.newInstance(url);
        } catch (NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new Joq4jException(e);
        }
    }

    public static void addRegistry(String scheme, Class<? extends Broker> clazz) {
        brokerRegistry.put(scheme, clazz);
    }
}
