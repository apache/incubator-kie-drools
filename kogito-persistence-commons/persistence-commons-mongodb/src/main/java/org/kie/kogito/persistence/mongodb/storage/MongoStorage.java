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
package org.kie.kogito.persistence.mongodb.storage;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.bson.Document;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;
import org.kie.kogito.persistence.mongodb.query.MongoQuery;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;

import io.smallrye.mutiny.Multi;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static java.util.Arrays.asList;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;
import static org.kie.kogito.persistence.mongodb.storage.StorageUtils.watchCollectionEntries;
import static org.kie.kogito.persistence.mongodb.storage.StorageUtils.watchCollectionKeys;

public class MongoStorage<V, E> implements Storage<String, V> {

    static final String OPERATION_TYPE = "operationType";

    MongoEntityMapper<V, E> mapper;

    MongoCollection<E> mongoCollection;

    String rootType;

    public MongoStorage(MongoCollection<E> mongoCollection, String rootType, MongoEntityMapper<V, E> mapper) {
        this.mongoCollection = mongoCollection;
        this.rootType = rootType;
        this.mapper = mapper;
    }

    @Override
    public Multi<V> objectCreatedListener() {
        return watchCollectionEntries(this.mongoCollection, eq(OPERATION_TYPE, "insert"), this.mapper);
    }

    @Override
    public Multi<V> objectUpdatedListener() {
        return watchCollectionEntries(this.mongoCollection, in(OPERATION_TYPE, asList("update", "replace")), this.mapper);
    }

    @Override
    public Multi<String> objectRemovedListener() {
        return watchCollectionKeys(this.mongoCollection, eq(OPERATION_TYPE, "delete"));
    }

    @Override
    public Query<V> query() {
        return new MongoQuery<>(this.mongoCollection, this.mapper);
    }

    @Override
    public boolean containsKey(String o) {
        return this.mongoCollection.find(new Document(MONGO_ID, o)).iterator().hasNext();
    }

    @Override
    public Map<String, V> entries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(String o) {
        return Optional.ofNullable(this.mongoCollection.find(new Document(MONGO_ID, o)).first()).map(e -> mapper.mapToModel(e)).orElse(null);
    }

    @Override
    public V put(String s, V v) {
        V oldValue = this.get(s);
        Optional.ofNullable(oldValue).ifPresentOrElse(
                o -> Optional.ofNullable(v).map(n -> mapper.mapToEntity(s, n)).ifPresent(
                        e -> this.mongoCollection.replaceOne(
                                new Document(MONGO_ID, s),
                                e, new ReplaceOptions().upsert(true))),
                () -> Optional.ofNullable(v).map(n -> mapper.mapToEntity(s, n)).ifPresent(
                        e -> this.mongoCollection.insertOne(e)));
        return Objects.nonNull(v) ? oldValue : null;
    }

    @Override
    public void clear() {
        this.mongoCollection.deleteMany(new Document());
    }

    @Override
    public String getRootType() {
        return this.rootType;
    }

    @Override
    public V remove(String o) {
        V oldValue = this.get(o);
        Optional.ofNullable(oldValue).ifPresent(i -> this.mongoCollection.deleteOne(new Document(MONGO_ID, o)));
        return oldValue;
    }
}
