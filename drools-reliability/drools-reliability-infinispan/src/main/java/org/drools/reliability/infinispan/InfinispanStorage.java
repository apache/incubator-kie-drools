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

package org.drools.reliability.infinispan;

import org.drools.reliability.core.Storage;
import org.infinispan.commons.api.BasicCache;

import java.util.Collection;

public class InfinispanStorage<K, V> implements Storage<K, V> {

    BasicCache<K, V> cache;

    public static <K1, V1> InfinispanStorage<K1, V1> fromCache(BasicCache<K1, V1> cache) {
        return new InfinispanStorage<>(cache);
    }

    private InfinispanStorage(BasicCache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public V getOrDefault(K key, V value) {
        return cache.getOrDefault(key, value);
    }

    @Override
    public V put(K key, V value) {
        return cache.put(key, value);
    }

    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    @Override
    public V remove(K key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Collection<V> values() {
        return cache.values();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }
}
