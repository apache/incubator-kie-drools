package org.drools.verifier.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Multimap<K,V> {

    private final Map<K, Collection<V>> map = new HashMap<>();
    
    public Collection<V> get(K key) {
        return map.getOrDefault(key, Collections.emptyList());
    }

    public void put(K key, V value) {
        map.computeIfAbsent(key, K -> new ArrayList<>()).add(value);
    }

    public void remove(K key, V value) {
        Collection<V> values = map.get(key);
        if (values != null) {
            values.remove(value);
        }
    }

    public Iterable<V> values() {
        return map.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Set<K> keySet() {
        return map.keySet();
    }
}
