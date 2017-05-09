package org.kie.dmn.core.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynamicTypeUtils {
    
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> toMap() {
        return Collectors.toMap(x -> x.getKey(), x -> x.getValue());
    }
    
    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(Map.Entry<K, V>... attributes) {
        return prototype(attributes);
    }
    
    @SafeVarargs
    public static <K, V> Map<K, V> prototype(Map.Entry<K, V>... attributes) {
        // as Stream.of(attributes).collect(toMap()); might fail due to some value=null, because toMap() uses java.util.HashMap.merge(HashMap.java:1224)
        // need avoid Stream API
        Map<K, V> result = new HashMap<>();
        for ( Entry<K, V> entry : attributes ) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}

