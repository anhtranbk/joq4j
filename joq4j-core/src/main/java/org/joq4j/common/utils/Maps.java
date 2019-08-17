package org.joq4j.common.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper methods for working with Map
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@SuppressWarnings("unchecked")
public class Maps {

    public static <K, V> V getAndRemove(Map<K, ?> map, K key) {
        Object value = map.get(key);
        map.remove(key);
        return (V) value;
    }

    /**
     * Lấy giá trị từ Map theo một key và ép về kiểu mong muốn hoặc null nếu
     * không có giá trị tương ứng với key
     *
     * @param map Map cần lấy giá trị
     * @param key key của giá trị cần lấy
     * @param <K> kiểu của key
     * @param <V> kiểu của value cần trả về
     * @return giá trị từ Map đã được đưa về kiểu cần trả về hoặc null nếu không
     * có giá trị tương ứng với key
     */
    public static <K, V> V getOrNull(Map<K, ?> map, K key) {
        Object value = map.get(key);
        return value != null ? (V) value : null;
    }

    /**
     * Khởi tạo Map từ danh sách các key values
     *
     * @param keyValues Danh sách các key value cần đưa vào Map theo thứ tự
     *                  <i>key_1</i>, <i>value_1</i>, <i>key_2</i>, <i>value_2</i>...
     * @return Map được khởi tạo
     */
    public static Map<String, Object> initFromKeyValues(Object... keyValues) {
        assert keyValues.length % 2 == 0;
        Map<String, Object> map = new TreeMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(keyValues[i].toString(), keyValues[i + 1]);
        }
        return map;
    }

    /**
     * Put giá trị vào Map nếu value khác null và không empty
     *
     * @param map   Map cần put giá trị
     * @param key   key cần put
     * @param value giá trị cần put
     */
    public static void putIfNotNullOrEmpty(Map<String, Object> map, String key, Object value) {
        if (!Strings.isNullOrStringEmpty(value)) {
            map.put(key, value);
        }
    }

    /**
     * Convert một Map với các values bất kì sang String
     *
     * @param input Map đầu vào
     * @return Map với các giá trị đã được convert sang String, bỏ qua các giá trị null
     */
    public static Map<String, String> convertToTextMap(Map<String, ?> input) {
        Map<String, String> output = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : input.entrySet()) {
            if (entry.getValue() == null) continue;
            output.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return output;
    }
}
