package org.optaplanner.examples.common.experimental.impl;

import java.util.Iterator;
import java.util.Map;

final class MapValuesIterable<Key_, Value_> implements Iterable<Value_> {
    private final Map<Key_, ? extends Value_> sourceMap;

    MapValuesIterable(Map<Key_, ? extends Value_> sourceMap) {
        this.sourceMap = sourceMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Value_> iterator() {
        return (Iterator<Value_>) sourceMap.values().iterator();
    }

    @Override
    public String toString() {
        return sourceMap.values().toString();
    }
}
