package org.joq4j.backend;

import org.joq4j.Joq4jException;
import org.joq4j.common.net.ConnectionUrl;
import org.joq4j.common.utils.Strings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BackendFactory {

    private static final Map<String, Class<? extends StorageBackend>> backendRegistry = new HashMap<>();

    public static StorageBackend fromUrl(String url) {
        ConnectionUrl connectionUrl = ConnectionUrl.parseFromString(url);
        Class<? extends StorageBackend> clazz = backendRegistry.get(connectionUrl.scheme());
        if (clazz == null) {
            throw new IllegalArgumentException(
                    Strings.format("Scheme %s not found", connectionUrl.scheme()));
        }

        try {
            Constructor<? extends StorageBackend> constructor = clazz.getConstructor(String.class);
            return constructor.newInstance(url);
        } catch (NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new Joq4jException(e);
        }
    }

    public static void addRegistry(String scheme, Class<? extends StorageBackend> clazz) {
        backendRegistry.put(scheme, clazz);
    }
}
