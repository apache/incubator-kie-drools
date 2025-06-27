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
package org.kie.kogito.persistence.mongodb.client;

import org.bson.Document;
import org.kie.kogito.persistence.mongodb.index.MongoConfig;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MongoClientManager {

    //TODO private static final String DATABASE_PROPERTY = "quarkus.mongodb.database";
    //ConfigProvider.getConfig().getValue(DATABASE_PROPERTY, String.class);

    @Inject
    MongoClient mongoClient;

    @Inject
    MongoConfig mongoConfig;

    public <E> MongoCollection<E> getCollection(String collection, Class<E> type) {
        return getMongoDatabase().getCollection(collection, type);
    }

    public MongoCollection<Document> getCollection(String collection) {
        return getMongoDatabase().getCollection(collection);
    }

    private MongoDatabase getMongoDatabase() {
        return mongoClient.getDatabase(mongoConfig.database());
    }
}
