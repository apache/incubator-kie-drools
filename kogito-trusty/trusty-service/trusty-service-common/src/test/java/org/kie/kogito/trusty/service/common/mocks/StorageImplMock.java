/*
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
package org.kie.kogito.trusty.service.common.mocks;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.NotFoundException;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;

import io.smallrye.mutiny.Multi;

public class StorageImplMock<K, V> implements Storage<K, V> {

    private Map<K, V> storage = new HashMap<>();

    private Class<K> rootType;

    public StorageImplMock(Class<K> type) {
        rootType = type;
    }

    @Override
    public Multi<V> objectCreatedListener() {
        return Multi.createFrom().empty();
    }

    @Override
    public Multi<V> objectUpdatedListener() {
        return Multi.createFrom().empty();
    }

    @Override
    public Multi<K> objectRemovedListener() {
        return Multi.createFrom().empty();
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
        storage.put(key, value);
        return value;
    }

    @Override
    public V remove(K key) {
        V element = storage.get(key);
        storage.remove(key);
        return element;
    }

    @Override
    public boolean containsKey(K key) {
        return storage.containsKey(key);
    }

    @Override
    public Map<K, V> entries() {
        return storage;
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
