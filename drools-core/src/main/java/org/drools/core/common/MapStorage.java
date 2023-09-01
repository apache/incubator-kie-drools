package org.drools.core.common;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapStorage<K, V> implements Storage<K, V> {

    private final Map<K, V> map;

    public MapStorage(Map<K, V> map) {
        this.map = map;
    }
    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public V getOrDefault(K key, V value) {
        return map.getOrDefault(key, value);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> otherMap) {
        map.putAll(otherMap);
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public V remove(K key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
}
