package org.drools.tms.util;

import java.util.HashMap;
import java.util.function.Function;

public class CustomKeyTransformerHashMap<K, V> extends HashMap<K, V> {

    private final Function<Object, Object> keyTransformer;

    public CustomKeyTransformerHashMap(Function<Object, Object> keyTransformer) {
        this.keyTransformer = keyTransformer;
    }

    @Override
    public V get(Object key) {
        return super.get(keyTransformer.apply(key));
    }
}
