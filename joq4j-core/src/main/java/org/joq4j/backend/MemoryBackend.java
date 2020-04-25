package org.joq4j.backend;

import org.joq4j.Job;
import org.joq4j.encoding.TaskSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MemoryBackend implements KeyValueBackend {

    private Map<String, Map<String, String>> mapMap = new HashMap<>();

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
        return mapMap.computeIfAbsent(key, k -> new HashMap<>()).get(field);
    }

    @Override
    public Map<String, String> getMap(String key) {
        return mapMap.get(key);
    }

    @Override
    public String removeFromMap(String key, String field) {
        return mapMap.computeIfAbsent(key, k -> new HashMap<>()).remove(field);
    }

    @Override
    public Map<String, String> removeMap(String key) {
        return mapMap.remove(key);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public TaskSerializer getTaskSerializer() {
        return null;
    }

    @Override
    public Job getJob(String jobId) {
        return null;
    }
}
