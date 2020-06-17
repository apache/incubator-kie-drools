/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.infinispan.cache;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.infinispan.client.hotrod.RemoteCache;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.infinispan.listener.CacheObjectCreatedListener;
import org.kie.kogito.persistence.infinispan.listener.CacheObjectRemovedListener;
import org.kie.kogito.persistence.infinispan.listener.CacheObjectUpdatedListener;
import org.kie.kogito.persistence.infinispan.query.InfinispanQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageImpl<K, V> implements Storage<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageImpl.class);

    private RemoteCache<K, V> delegate;
    private String rootType;

    public StorageImpl(RemoteCache<K, V> delegate, String rootType) {
        this.delegate = delegate;
        this.rootType = rootType;
    }

    public V get(Object key) {
        return delegate.get(key);
    }

    public void clear() {
        delegate.clear();
    }

    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return delegate.containsKey(key);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public void addObjectCreatedListener(Consumer<V> consumer) {
        LOGGER.debug("Adding new object created listener into Cache: {}", delegate.getName());
        delegate.addClientListener(new CacheObjectCreatedListener<>(delegate, consumer));
    }

    @Override
    public void addObjectUpdatedListener(Consumer<V> consumer) {
        LOGGER.debug("Adding new object updated listener into Cache: {}", delegate.getName());
        delegate.addClientListener(new CacheObjectUpdatedListener<>(delegate, consumer));
    }

    @Override
    public void addObjectRemovedListener(Consumer<K> consumer) {
        LOGGER.debug("Adding new object removed listener into Cache: {}", delegate.getName());
        delegate.addClientListener(new CacheObjectRemovedListener<>(consumer));
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
