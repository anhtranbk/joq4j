package org.joq4j.backend;

import org.joq4j.common.net.ConnectionUrl;
import org.joq4j.common.utils.Reflects;
import org.joq4j.common.utils.Strings;

import java.util.HashMap;
import java.util.Map;

public class BackendFactoryRegistry {

    private static final Map<String, Class<? extends BackendFactory>> factoryRegistry = new HashMap<>();

    public static BackendFactory findFactory(String url) {
        ConnectionUrl connectionUrl = ConnectionUrl.parseFromString(url);
        Class<? extends BackendFactory> clazz = factoryRegistry.get(connectionUrl.scheme());
        if (clazz == null) {
            throw new IllegalArgumentException(
                    Strings.format("Scheme %s not found", connectionUrl.scheme()));
        }

        return Reflects.newInstance(clazz);
    }

    public static void addRegistry(String scheme, Class<? extends BackendFactory> clazz) {
        factoryRegistry.put(scheme, clazz);
    }
}
