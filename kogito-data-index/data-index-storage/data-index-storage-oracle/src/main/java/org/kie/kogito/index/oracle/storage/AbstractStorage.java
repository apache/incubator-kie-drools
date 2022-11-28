/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.oracle.storage;

import java.util.Map;
import java.util.function.Function;

import javax.transaction.Transactional;

import org.kie.kogito.index.oracle.model.AbstractEntity;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Multi;

import static java.util.stream.Collectors.toMap;

public abstract class AbstractStorage<E extends AbstractEntity, V> implements Storage<String, V> {

    private static final String LISTENER_NOT_AVAILABLE_IN_ORACLE_SQL = "Listener not available in OracleSQL";

    private PanacheRepositoryBase<E, String> repository;
    private Class<V> modelClass;
    private Class<E> entityClass;
    private Function<E, V> mapToModel;
    private Function<V, E> mapToEntity;

    protected AbstractStorage() {
    }

    protected AbstractStorage(PanacheRepositoryBase<E, String> repository, Class<V> modelClass, Class<E> entityClass, Function<E, V> mapToModel,
            Function<V, E> mapToEntity) {
        this.repository = repository;
        this.modelClass = modelClass;
        this.mapToModel = mapToModel;
        this.mapToEntity = mapToEntity;
        this.entityClass = entityClass;
    }

    @Override
    public Multi<V> objectCreatedListener() {
        throw new UnsupportedOperationException(LISTENER_NOT_AVAILABLE_IN_ORACLE_SQL);
    }

    @Override
    public Multi<V> objectUpdatedListener() {
        throw new UnsupportedOperationException(LISTENER_NOT_AVAILABLE_IN_ORACLE_SQL);
    }

    @Override
    public Multi<String> objectRemovedListener() {
        throw new UnsupportedOperationException(LISTENER_NOT_AVAILABLE_IN_ORACLE_SQL);
    }

    @Override
    public Query<V> query() {
        return new OracleQuery<>(repository, mapToModel, entityClass);
    }

    @Override
    @Transactional
    public V get(String key) {
        return repository.findByIdOptional(key).map(mapToModel).orElse(null);
    }

    @Override
    @Transactional
    public V put(String key, V value) {
        repository.deleteById(key);
        repository.persist(mapToEntity.apply(value));
        return value;
    }

    @Override
    @Transactional
    public V remove(String key) {
        V value = get(key);
        if (value != null) {
            repository.deleteById(key);
        }
        return value;
    }

    @Override
    public boolean containsKey(String key) {
        return repository.count("id = ?1", key) == 1;
    }

    @Override
    public Map<String, V> entries() {
        return repository.streamAll().collect(toMap(AbstractEntity::getId, mapToModel));
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
}
