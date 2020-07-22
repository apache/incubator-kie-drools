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

package org.kie.kogito.persistence.mongodb.storage;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;
import org.kie.kogito.persistence.mongodb.query.MongoQuery;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static java.util.Arrays.asList;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;
import static org.kie.kogito.persistence.mongodb.storage.StorageUtils.watchCollection;

public class MongoStorage<V, E> implements Storage<String, V> {

    static final String OPERATION_TYPE = "operationType";

    MongoEntityMapper<V, E> mongoEntityMapper;

    MongoCollection<E> mongoCollection;

    com.mongodb.reactivestreams.client.MongoCollection<E> reactiveMongoCollection;

    String rootType;

    public MongoStorage(MongoCollection<E> mongoCollection, com.mongodb.reactivestreams.client.MongoCollection<E> reactiveMongoCollection,
                        String rootType, MongoEntityMapper<V, E> mongoEntityMapper) {
        this.mongoCollection = mongoCollection;
        this.rootType = rootType;
        this.mongoEntityMapper = mongoEntityMapper;
        this.reactiveMongoCollection = reactiveMongoCollection;
    }

    @Override
    public void addObjectCreatedListener(Consumer<V> consumer) {
        watchCollection(this.reactiveMongoCollection, eq(OPERATION_TYPE, "insert"), (k, v) -> consumer.accept(v), this.mongoEntityMapper);
    }

    @Override
    public void addObjectUpdatedListener(Consumer<V> consumer) {
        watchCollection(this.reactiveMongoCollection, in(OPERATION_TYPE, asList("update", "replace")), (k, v) -> consumer.accept(v), this.mongoEntityMapper);
    }

    @Override
    public void addObjectRemovedListener(Consumer<String> consumer) {
        watchCollection(this.reactiveMongoCollection, eq(OPERATION_TYPE, "delete"), (k, v) -> consumer.accept(k), this.mongoEntityMapper);
    }

    @Override
    public Query<V> query() {
        return new MongoQuery<>(this.mongoCollection, this.mongoEntityMapper);
    }

    @Override
    public boolean containsKey(String o) {
        return this.mongoCollection.find(new Document(MONGO_ID, o)).iterator().hasNext();
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(String o) {
        return Optional.ofNullable(this.mongoCollection.find(new Document(MONGO_ID, o)).first()).map(e -> mongoEntityMapper.mapToModel(e)).orElse(null);
    }

    @Override
    public V put(String s, V v) {
        V oldValue = this.get(s);
        Optional.ofNullable(oldValue).ifPresentOrElse(
                o -> Optional.ofNullable(v).map(n -> mongoEntityMapper.mapToEntity(s, n)).ifPresent(
                        e -> this.mongoCollection.replaceOne(
                                new Document(MONGO_ID, s),
                                e, new ReplaceOptions().upsert(true))),
                () -> Optional.ofNullable(v).map(n -> mongoEntityMapper.mapToEntity(s, n)).ifPresent(
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
