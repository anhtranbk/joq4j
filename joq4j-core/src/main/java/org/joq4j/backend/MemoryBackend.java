package org.joq4j.backend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Use only for testing
 */
public class MemoryBackend implements KeyValueBackend {

    private final Map<String, Map<String, String>> mapMap = new HashMap<>();

    @Override
    public void put(String key, Map<String, String> fieldMap) {
        mapMap.put(key, fieldMap);
    }

    @Override
    public void putOne(String key, String field, String value) {
        mapMap.computeIfAbsent(key, k -> new HashMap<>()).put(field, value);
    }

    @Override
    public String getOne(String key, String field) {
        return mapMap.computeIfAbsent(key, k -> new HashMap<>()).get(field);
    }

    @Override
    public Map<String, String> get(String key) {
        return mapMap.get(key);
    }

    @Override
    public String removeOne(String key, String field) {
        return mapMap.computeIfAbsent(key, k -> new HashMap<>()).remove(field);
    }

    @Override
    public Map<String, String> remove(String key) {
        return mapMap.remove(key);
    }

    @Override
    public void close() throws IOException {
    }
}
