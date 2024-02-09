/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.reliability.h2mvstore;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.core.common.Storage;
import org.h2.mvstore.MVMap;

public class H2MVStoreStorage<K, V> implements Storage<K, V> {

    private MVMap<K, V> mvMap;

    public static <K1, V1> Storage<K1, V1> fromMVMap(MVMap<K1, V1> mvMap) {
        return new H2MVStoreStorage<>(mvMap);
    }

    private H2MVStoreStorage(MVMap<K, V> mvMap) {
        this.mvMap = mvMap;
    }

    @Override
    public V get(K key) {
        return mvMap.get(key);
    }

    @Override
    public V getOrDefault(K key, V value) {
        return mvMap.getOrDefault(key, value);
    }

    @Override
    public V put(K key, V value) {
        V previousValue = mvMap.put(key, value);
        mvMap.store.commit();
        return previousValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> otherMap) {
        mvMap.putAll(otherMap);
        mvMap.store.commit();
    }

    @Override
    public boolean containsKey(K key) {
        return mvMap.containsKey(key);
    }

    @Override
    public V remove(K key) {
        V previousValue = mvMap.remove(key);
        mvMap.store.commit();
        return previousValue;
    }

    @Override
    public void clear() {
        mvMap.clear();
        mvMap.store.commit();
    }

    @Override
    public Collection<V> values() {
        return mvMap.values();
    }

    @Override
    public Set<K> keySet() {
        return mvMap.keySet();
    }

    @Override
    public int size() {
        return mvMap.size();
    }

    @Override
    public boolean isEmpty() {
        return mvMap.isEmpty();
    }
}
