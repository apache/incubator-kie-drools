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
package org.kie.kogito.trusty.storage.postgresql;

import java.util.Map;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.postgresql.PostgresStorage;
import org.kie.kogito.persistence.postgresql.model.CacheEntityRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Multi;

import jakarta.transaction.Transactional;

public abstract class BaseTransactionalStorage<T> implements Storage<String, T> {

    private PostgresStorage<T> delegate;

    BaseTransactionalStorage() {
        //CDI proxy
    }

    protected BaseTransactionalStorage(String name, CacheEntityRepository repository, ObjectMapper mapper, Class<T> type) {
        this(new PostgresStorage<>(name, repository, mapper, type));
    }

    //For Unit Tests to check delegation
    BaseTransactionalStorage(final PostgresStorage<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Multi<T> objectCreatedListener() {
        return delegate.objectCreatedListener();
    }

    @Override
    public Multi<T> objectUpdatedListener() {
        return delegate.objectUpdatedListener();
    }

    @Override
    public Multi<String> objectRemovedListener() {
        return delegate.objectRemovedListener();
    }

    @Override
    @Transactional
    public Query<T> query() {
        return delegate.query();
    }

    @Override
    @Transactional
    public T get(String key) {
        return delegate.get(key);
    }

    @Override
    @Transactional
    public T put(String key, T value) {
        return delegate.put(key, value);
    }

    @Override
    @Transactional
    public T remove(String key) {
        return delegate.remove(key);
    }

    @Override
    @Transactional
    public boolean containsKey(String key) {
        return delegate.containsKey(key);
    }

    @Override
    @Transactional
    public Map<String, T> entries() {
        return delegate.entries();
    }

    @Override
    @Transactional
    public void clear() {
        delegate.clear();
    }

    @Override
    public String getRootType() {
        return delegate.getRootType();
    }
}
