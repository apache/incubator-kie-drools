package utils.shim;

import java.util.Collections;
import java.util.HashMap;

public class Map {
    public static <K, V> java.util.Map<K, V> of(K key, V value) {
        java.util.Map<K, V> result = new HashMap<>();
        result.put(key, value);
        return Collections.unmodifiableMap(result);
    }
    public static <K, V> java.util.Map<K, V> of(K key1, V value1, K key2, V value2) {
        java.util.Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        return Collections.unmodifiableMap(result);
    }
    public static <K, V> java.util.Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3) {
        java.util.Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        return Collections.unmodifiableMap(result);
    }
}