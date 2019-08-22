package org.joq4j.backend;

import java.io.Closeable;
import java.util.Map;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface StorageBackend extends Closeable {

    String get(String key);

    void set(String key, String value);

    String remove(String key);

    void putMap(String key, Map<String, String> fieldMap);

    void putToMap(String key, String field, String value);

    String getFromMap(String key, String field);

    Map<String, String> getMap(String key);

    String removeFromMap(String key, String field);

    Map<String, String> removeMap(String key);
}
