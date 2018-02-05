package org.optaplanner.core.impl.domain.solution.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Thread-safe basic cache implementation.
 *
 * @param <K> the type of keys for the cache
 * @param <V> the type of values stored in the cache
 * */
public final class ConcurrentCache<K, V> extends ConcurrentHashMap<K, V> {

    /**
     * {@link ConcurrentHashMap#computeIfAbsent(Object, Function)} should not be used to directly retrieve cached values
     * as it incorporates locking even when the value is present and doesn't have to be computed. Thus, the value is
     * retrieved by {@link ConcurrentHashMap#get(Object)} and if and only if the value is null,
     * {@link ConcurrentHashMap#computeIfAbsent(Object, Function)} is used to ensure thread-safety.
     *
     * {@inheritDoc}
     * */
    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        V value = get(key);
        if (value == null) {
            value = super.computeIfAbsent(key, mappingFunction);
        }
        return value;
    }

}
