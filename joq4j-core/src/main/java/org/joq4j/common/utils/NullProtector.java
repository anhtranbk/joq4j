package org.joq4j.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@SuppressWarnings("unchecked")
public class NullProtector {

    /**
     * Split một chuỗi và trả về giá trị nằm ở một thứ tự xác định trong mảng
     * là kết quả của quá trình split
     *
     * @param source chuỗi ban đầu
     * @param regex  dùng để split chuỗi
     * @param index  thứ tự của gía trị cần lấy về trong mảng là kết quả của
     *               quá trình split
     * @return giá trị cần lấy
     */
    public static Optional<String> split(String source, String regex, int index) {
        try {
            return get(source.split(regex), index);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    /**
     * Lấy giá trị từ một List theo index an toàn, tránh bị IndexOutOfBoundsException
     *
     * @param list  List chứa giá trị cần lấy
     * @param index index nơi chứa giá trị cần lấy trong List
     * @param <T>   Kiểu của các giá trị trong List
     * @return giá trị cần lấy
     */
    @SuppressWarnings("SameParameterValue")
    public static <T> Optional<T> get(List<T> list, int index) {
        try {
            return Optional.of(list.get(index));
        } catch (IndexOutOfBoundsException | NullPointerException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Lấy giá trị từ một mảng theo index an toàn, tránh bị IndexOutOfBoundsException
     *
     * @param arr   mảng chứa giá trị cần lấy
     * @param index index nơi chứa giá trị cần lấy trong mảng
     * @param <T>   Kiểu của các giá trị trong mảng
     * @return giá trị cần lấy
     */
    public static <T> Optional<T> get(T[] arr, int index) {
        try {
            return Optional.of(arr[index]);
        } catch (IndexOutOfBoundsException | NullPointerException ignored) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> first(Collection<T> coll) {
        return coll.isEmpty() ? Optional.empty() : Optional.ofNullable(coll.iterator().next());
    }

    public static <K, V> Optional<V> get(Map<K, ?> map, K key) {
        try {
            return Optional.of((V) map.get(key));
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    public static <K, V> V getOrNull(Map<K, ?> map, K key) {
        Object value = map.get(key);
        return value != null ? (V) value : null;
    }
}
