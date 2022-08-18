package org.optaplanner.core.impl.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Uses a given {@link Map} as storage unless it is the first and only entry, in which case it uses fields.
 * This helps avoid the overhead of creating and accessing a hash map if we only have 1 entry.
 * This implementation is not thread-safe, regardless of the underlying {@link Map}.
 * Iteration order of elements is specified by the underlying {@link Map}.
 */
public final class FieldBasedScalingMap<K, V> implements Map<K, V> {

    private final Supplier<Map<K, V>> mapSupplier;
    private K singletonKey;
    private V singletonValue;
    private Map<K, V> map;
    private int size = 0;

    public FieldBasedScalingMap(Supplier<Map<K, V>> mapSupplier) {
        this.mapSupplier = Objects.requireNonNull(mapSupplier);
    }

    @Override
    public V put(K key, V value) {
        if (map == null) { // We have not yet created the map.
            if (size == 0) { // Use the fields instead of the map.
                singletonKey = key;
                singletonValue = value;
                size = 1;
                return null;
            } else if (size == 1) { // Switch from the fields to the map.
                map = mapSupplier.get();
                map.put(singletonKey, singletonValue);
                singletonKey = null;
                singletonValue = null;
            } else {
                throw new IllegalStateException("Impossible state: size (" + size + ") > 1 yet no map used.");
            }
        }
        V oldValue = map.put(key, value);
        if (oldValue == null) {
            size += 1;
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            put(key, value);
        }
    }

    @Override
    public void clear() {
        if (map == null) {
            singletonKey = null;
            singletonValue = null;
        } else {
            map.clear();
        }
        size = 0;
    }

    /**
     * As defined by {@link Map#keySet()}.
     * May throw an exception if the iterator is used to remove an element while the map is based on a field.
     *
     * @return never null
     */
    @Override
    public Set<K> keySet() {
        if (size == 0) {
            return Collections.emptySet();
        } else if (map == null) {
            return Collections.singleton(singletonKey);
        } else {
            return map.keySet();
        }
    }

    /**
     * As defined by {@link Map#values()}.
     * May throw an exception if the iterator is used to remove an element while the map is based on a field.
     *
     * @return never null
     */
    @Override
    public Collection<V> values() {
        if (size == 0) {
            return Collections.emptyList();
        } else if (map == null) {
            return Collections.singletonList(singletonValue);
        } else {
            return map.values();
        }
    }

    /**
     * As defined by {@link Map#entrySet()}.
     * May throw an exception if the iterator is used to remove an element while the map is based on a field.
     *
     * @return never null
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        if (size == 0) {
            return Collections.emptySet();
        } else if (map == null) {
            return Collections.singleton(Map.entry(singletonKey, singletonValue));
        } else {
            return map.entrySet();
        }
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) { // To avoid unnecessary entry sets.
        if (size == 0) {
            return;
        }
        if (map == null) {
            action.accept(singletonKey, singletonValue);
        } else {
            map.forEach(action); // The entry set creation is only necessary now.
        }
    }

    @Override
    public V remove(Object key) {
        if (map == null) { // We're using the fields.
            if (!Objects.equals(singletonKey, key)) {
                return null; // Key was not found.
            }
            V oldValue = singletonValue;
            singletonKey = null;
            singletonValue = null;
            size = 0;
            return oldValue;
        }
        V value = map.remove(key);
        if (value != null) {
            size -= 1;
        }
        return value;
    }

    @Override
    public V get(Object key) {
        if (map == null) { // We're using the fields.
            if (!Objects.equals(singletonKey, key)) {
                return null; // Key was not found.
            }
            return singletonValue;
        }
        return map.get(key);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (size == 0) {
            return false;
        } else if (map == null) {
            return Objects.equals(key, singletonKey);
        } else {
            return map.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (size == 0) {
            return false;
        } else if (map == null) {
            return Objects.equals(value, singletonValue);
        } else {
            return map.containsValue(singletonValue);
        }
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "{}";
        } else if (map == null) {
            return "{" + singletonKey + "=" + singletonValue + "}";
        } else {
            return map.toString();
        }
    }
}
