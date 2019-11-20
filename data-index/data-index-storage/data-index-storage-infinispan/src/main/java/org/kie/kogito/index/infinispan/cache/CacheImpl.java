/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.index.infinispan.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.infinispan.client.hotrod.RemoteCache;
import org.kie.kogito.index.cache.Cache;
import org.kie.kogito.index.infinispan.listener.CacheObjectCreatedListener;
import org.kie.kogito.index.infinispan.listener.CacheObjectRemovedListener;
import org.kie.kogito.index.infinispan.listener.CacheObjectUpdatedListener;
import org.kie.kogito.index.infinispan.query.InfinispanQuery;
import org.kie.kogito.index.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheImpl<K, V> implements Cache<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheImpl.class);

    private RemoteCache<K, V> delegate;
    private String rootType;

    public CacheImpl(RemoteCache<K, V> delegate, String rootType) {
        this.delegate = delegate;
        this.rootType = rootType;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        delegate.forEach(action);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public V replace(K key, V value) {
        return delegate.replace(key, value);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        delegate.replaceAll(function);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return delegate.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return delegate.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return delegate.merge(key, value, remappingFunction);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public void addObjectCreatedListener(Consumer<V> consumer) {
        LOGGER.debug("Adding new object created listener into Cache: {}", delegate.getName());
        delegate.addClientListener(new CacheObjectCreatedListener(delegate, consumer));
    }

    @Override
    public void addObjectUpdatedListener(Consumer<V> consumer) {
        LOGGER.debug("Adding new object updated listener into Cache: {}", delegate.getName());
        delegate.addClientListener(new CacheObjectUpdatedListener(delegate, consumer));
    }

    @Override
    public void addObjectRemovedListener(Consumer<K> consumer) {
        LOGGER.debug("Adding new object removed listener into Cache: {}", delegate.getName());
        delegate.addClientListener(new CacheObjectRemovedListener(consumer));
    }

    public RemoteCache<K, V> getDelegate() {
        return delegate;
    }

    @Override
    public String getRootType() {
        return rootType;
    }

    @Override
    public Query<V> query() {
        return new InfinispanQuery<>(delegate, rootType);
    }
}
