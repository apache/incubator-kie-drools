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
