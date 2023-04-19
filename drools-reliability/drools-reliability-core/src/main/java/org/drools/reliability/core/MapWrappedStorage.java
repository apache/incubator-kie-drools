/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper class that implements Map interface.
 * It is used by FullReliableObjectStore and SimpleReliableObjectStore
 * NOTE: only a subset of methods is implemented, check javadoc for more information
 * @param <K>
 * @param <V>
 */
public class MapWrappedStorage<K, V> implements Map<K, V> {

    Storage<K, V> storage;

    public MapWrappedStorage(Storage<K, V> storage) {
        this.storage = storage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        return storage.get((K) key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V getOrDefault(Object key, V value) {
        return storage.getOrDefault((K) key, value);
    }

    @Override
    public V put(K key, V value) {
        return storage.put(key, value);
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsKey(Object key) {
        return storage.containsKey((K) key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object key) {
        return storage.remove((K) key);
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public Collection<V> values() {
        return storage.values();
    }

    /**
     * Not supported
     * @throws UnsupportedOperationException
     */
    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     * @throws UnsupportedOperationException
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     * @throws UnsupportedOperationException
     */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     * @throws UnsupportedOperationException
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

}
