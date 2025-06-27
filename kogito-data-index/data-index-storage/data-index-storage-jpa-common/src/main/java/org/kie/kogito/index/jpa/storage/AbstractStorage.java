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
import java.util.Optional;
import java.util.function.Function;

import org.kie.kogito.index.jpa.model.AbstractEntity;
import org.kie.kogito.persistence.api.Storage;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

public abstract class AbstractStorage<K, E extends AbstractEntity, V> extends AbstractJPAStorageFetcher<K, E, V> implements Storage<K, V> {

    private Class<V> modelClass;

    private Function<V, E> mapToEntity;

    protected AbstractStorage() {
    }

    protected AbstractStorage(EntityManager em, Class<V> modelClass, Class<E> entityClass, Function<E, V> mapToModel,
            Function<V, E> mapToEntity, Function<E, K> mapEntityToKey) {
        this(em, modelClass, entityClass, mapToModel, mapToEntity, mapEntityToKey, Optional.empty());
    }

    protected AbstractStorage(EntityManager em, Class<V> modelClass, Class<E> entityClass, Function<E, V> mapToModel,
            Function<V, E> mapToEntity, Function<E, K> mapEntityToKey, Optional<JsonPredicateBuilder> jsonPredicateBuilder) {
        super(em, entityClass, mapToModel, jsonPredicateBuilder);
        this.modelClass = modelClass;
        this.mapToEntity = mapToEntity;
    }

    @Override
    @Transactional
    public V put(K key, V value) {
        em.merge(mapToEntity.apply(value));
        return value;
    }

    @Override
    @Transactional
    public V remove(K key) {
        E value = em.find(entityClass, key);
        if (value != null) {
            em.remove(value);
            return mapToModel.apply(value);
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public boolean containsKey(K key) {
        return em.find(entityClass, key) != null;
    }

    @Override
    public Map<K, V> entries() {
        throw new UnsupportedOperationException("We should not iterate over all entries");
    }

    @Override
    public String getRootType() {
        return modelClass.getCanonicalName();
    }
}
