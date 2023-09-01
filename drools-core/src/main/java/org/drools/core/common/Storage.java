package org.drools.core.common;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This interface represents the minimal abstraction to store data. It might be merged into ObjectStore in the long run.
 * See drools-reliability module for alternative implementations
 * @param <K>
 * @param <V>
 */
public interface Storage<K, V> {

    V get(K key);

    V put(K key, V value);

    void putAll(Map<? extends K, ? extends V> otherMap);

    boolean containsKey(K key);

    V remove(K key);

    Set<K> keySet();

    Collection<V> values();

    void clear();

    int size();

    boolean isEmpty();

    V getOrDefault(K key, V value);

    default boolean requiresFlush() {
        return false;
    }

    default void flush() {
        throw new UnsupportedOperationException();
    }

    static <K, V> Storage<K,V> fromMap(Map<K, V> input) {
        return new MapStorage<>(input);
    }
}
