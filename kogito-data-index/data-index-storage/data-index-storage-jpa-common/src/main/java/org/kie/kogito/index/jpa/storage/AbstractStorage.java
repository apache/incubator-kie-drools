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
package org.kie.kogito.index.jpa.storage;

import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.index.jpa.model.AbstractEntity;
import org.kie.kogito.persistence.api.Storage;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import jakarta.transaction.Transactional;

import static java.util.stream.Collectors.toMap;

public abstract class AbstractStorage<K, E extends AbstractEntity, V> extends AbstractJPAStorageFetcher<K, E, V> implements Storage<K, V> {

    private Class<V> modelClass;

    private Function<V, E> mapToEntity;
    private Function<E, K> mapEntityToKey;

    protected AbstractStorage() {
    }

    protected AbstractStorage(PanacheRepositoryBase<E, K> repository, Class<V> modelClass, Class<E> entityClass, Function<E, V> mapToModel,
            Function<V, E> mapToEntity, Function<E, K> mapEntityToKey) {
        super(repository, entityClass, mapToModel);
        this.modelClass = modelClass;
        this.mapToEntity = mapToEntity;
        this.mapEntityToKey = mapEntityToKey;
    }

    @Override
    @Transactional
    public V put(K key, V value) {
        repository.getEntityManager().merge(mapToEntity.apply(value));
        return value;
    }

    @Override
    @Transactional
    public V remove(K key) {
        V value = get(key);
        if (value != null) {
            repository.deleteById(key);
        }
        return value;
    }

    @Transactional
    @Override
    public boolean containsKey(K key) {
        return repository.count("id = ?1", key) == 1;
    }

    @Override
    public Map<K, V> entries() {
        return repository.streamAll().collect(toMap(mapEntityToKey, mapToModel));
    }

    @Override
    @Transactional
    public void clear() {
        repository.deleteAll();
    }

    @Override
    public String getRootType() {
        return modelClass.getCanonicalName();
    }

    protected PanacheRepositoryBase<E, K> getRepository() {
        return repository;
    }
}
