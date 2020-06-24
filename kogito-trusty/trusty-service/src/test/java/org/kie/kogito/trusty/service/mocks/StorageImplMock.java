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

package org.kie.kogito.trusty.service.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.ws.rs.NotFoundException;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;

public class StorageImplMock<K, V> implements Storage<K, V> {

    private Map<K, V> storage = new HashMap<>();
    private List<Consumer<V>> createdListeners = new ArrayList<>();
    private List<Consumer<V>> updateListeners = new ArrayList<>();
    private List<Consumer<K>> removedListeners = new ArrayList<>();

    private Class<K> rootType;

    public StorageImplMock(Class<K> type) {
        rootType = type;
    }

    @Override
    public void addObjectCreatedListener(Consumer<V> consumer) {
        createdListeners.add(consumer);
    }

    @Override
    public void addObjectUpdatedListener(Consumer<V> consumer) {
        updateListeners.add(consumer);
    }

    @Override
    public void addObjectRemovedListener(Consumer<K> consumer) {
        removedListeners.add(consumer);
    }

    @Override
    public Query<V> query() {
        throw new RuntimeException("This mock does not provide query support.");
    }

    @Override
    public V get(K key) {
        if (storage.containsKey(key)) {
            return storage.get(key);
        }
        throw new NotFoundException("Element not found");
    }

    @Override
    public V put(K key, V value) {
        createdListeners.forEach(x -> x.accept(value));
        storage.put(key, value);
        return value;
    }

    @Override
    public V remove(K key) {
        removedListeners.forEach(x -> x.accept(key));
        V element = storage.get(key);
        storage.remove(key);
        return element;
    }

    @Override
    public boolean containsKey(K key) {
        return storage.containsKey(key);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return storage.entrySet();
    }

    @Override
    public void clear() {
        storage = new HashMap<>();
    }

    @Override
    public String getRootType() {
        return rootType.getName();
    }
}