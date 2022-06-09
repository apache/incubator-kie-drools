package org.optaplanner.core.impl.domain.solution.cloner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A thread-safe memoization that caches a calculation.
 *
 * @param <K> the parameter of the calculation
 * @param <V> the result of the calculation
 */
final class ConcurrentMemoization<K, V> extends ConcurrentHashMap<K, V> {

    /**
     * An overridden implementation that heavily favors read access over write access speed.
     * This is thread-safe.
     *
     * {@inheritDoc}
     */
    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        V value = get(key);
        if (value != null) {
            return value;
        }
        return super.computeIfAbsent(key, mappingFunction);
    }

}
