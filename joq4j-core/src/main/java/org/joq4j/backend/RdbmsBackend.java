package org.joq4j.backend;

import java.io.IOException;
import java.util.Map;

public class RdbmsBackend implements StorageBackend {
    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public void set(String key, String value) {

    }

    @Override
    public String remove(String key) {
        return null;
    }

    @Override
    public void putMap(String key, Map<String, String> fieldMap) {

    }

    @Override
    public void putToMap(String key, String field, String value) {

    }

    @Override
    public String getFromMap(String key, String field) {
        return null;
    }

    @Override
    public Map<String, String> getMap(String key) {
        return null;
    }

    @Override
    public String removeFromMap(String key, String field) {
        return null;
    }

    @Override
    public Map<String, String> removeMap(String key) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
