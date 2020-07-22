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

package org.kie.kogito.persistence.mongodb.client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import org.bson.Document;
import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
public class MongoClientManager {

    private static final String DATABASE_PROPERTY = "quarkus.mongodb.database";

    String database;

    @Inject
    MongoClient mongoClient;

    @Inject
    ReactiveMongoClient reactiveMongoClient;

    public MongoClientManager() {
        database = ConfigProvider.getConfig().getValue(DATABASE_PROPERTY, String.class);
    }

    public <E> MongoCollection<E> getCollection(String collection, Class<E> type) {
        return getMongoDatabase().getCollection(collection, type);
    }

    public MongoCollection<Document> getCollection(String collection) {
        return getMongoDatabase().getCollection(collection);
    }

    public <E> com.mongodb.reactivestreams.client.MongoCollection<E> getReactiveCollection(String collection, Class<E> type) {
        return getReactiveMongoDatabase().getCollection(collection, type);
    }

    public com.mongodb.reactivestreams.client.MongoCollection<Document> getReactiveCollection(String collection) {
        return getReactiveMongoDatabase().getCollection(collection);
    }

    private MongoDatabase getMongoDatabase() {
        return mongoClient.getDatabase(database);
    }

    private com.mongodb.reactivestreams.client.MongoDatabase getReactiveMongoDatabase() {
        return reactiveMongoClient.unwrap().getDatabase(database);
    }
}
