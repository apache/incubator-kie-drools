package org.drools.reliability.infinispan;

import org.drools.core.common.Storage;
import org.infinispan.commons.api.BasicCache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class InfinispanStorage<K, V> implements Storage<K, V> {

    private BasicCache<K, V> cache;

    public static <K1, V1> Storage<K1, V1> fromCache(BasicCache<K1, V1> cache) {
        return new InfinispanStorage<>(cache);
    }

    private InfinispanStorage(BasicCache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public V getOrDefault(K key, V value) {
        return cache.getOrDefault(key, value);
    }

    @Override
    public V put(K key, V value) {
        return cache.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> otherMap) {
        cache.putAll(otherMap);
    }

    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    @Override
    public V remove(K key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Collection<V> values() {
        return cache.values();
    }

    @Override
    public Set<K> keySet() {
        return cache.keySet();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }
}
