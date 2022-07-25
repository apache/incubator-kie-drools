package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Uses {@link LinkedHashMap} as tuple storage unless it is the first and only entry, in which case it uses fields.
 * This helps avoid the overhead of creating and accessing a hash map if we only have 1 entry in the index.
 * This situation occurs often enough that this optimization is beneficial.
 */
final class NoneIndexerStorage<Key_, Value_> {

    private Key_ singletonKey;
    private Value_ singletonValue;
    private Map<Key_, Value_> map;
    private int size = 0;

    public Value_ put(Key_ key, Value_ value) {
        Objects.requireNonNull(value);
        if (map == null) { // We have not yet created the map.
            if (size == 0) { // Use the fields instead of the map.
                singletonKey = key;
                singletonValue = value;
                size = 1;
                return null;
            } else if (size == 1) { // Switch from the fields to the map.
                map = new LinkedHashMap<>();
                map.put(singletonKey, singletonValue);
                singletonKey = null;
                singletonValue = null;
            } else {
                throw new IllegalStateException("Impossible state: storage size (" + size + ") > 1 yet no map used.");
            }
        }
        Value_ oldValue = map.put(key, value);
        if (oldValue == null) {
            size += 1;
        }
        return oldValue;
    }

    public Value_ remove(Key_ key) {
        if (map == null) { // We're using the fields.
            if (!Objects.equals(singletonKey, key)) {
                return null; // Key was not found.
            }
            Value_ oldValue = singletonValue;
            singletonKey = null;
            singletonValue = null;
            size = 0;
            return oldValue;
        }
        Value_ value = map.remove(key);
        if (value != null) {
            size -= 1;
        }
        return value;
    }

    public Value_ get(Key_ key) {
        if (map == null) { // We're using the fields.
            if (!Objects.equals(singletonKey, key)) {
                return null; // Key was not found.
            }
            return singletonValue;
        }
        return map.get(key);
    }

    public void visit(BiConsumer<Key_, Value_> keyValueVisitor) {
        if (size == 0) {
            return;
        } else if (map == null) {
            keyValueVisitor.accept(singletonKey, singletonValue);
            return;
        }
        map.forEach(keyValueVisitor);
    }

    public boolean isEmpty() {
        return size == 0;
    }

}
