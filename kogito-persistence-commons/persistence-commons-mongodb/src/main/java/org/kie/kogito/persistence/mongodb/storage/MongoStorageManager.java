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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;
import org.kie.kogito.persistence.mongodb.client.MongoClientManager;

import io.quarkus.arc.properties.IfBuildProperty;

import static org.kie.kogito.persistence.api.factory.Constants.PERSISTENCE_TYPE_PROPERTY;
import static org.kie.kogito.persistence.mongodb.Constants.MONGODB_STORAGE;

@ApplicationScoped
@IfBuildProperty(name = PERSISTENCE_TYPE_PROPERTY, stringValue = MONGODB_STORAGE)
public class MongoStorageManager implements StorageService {

    @Inject
    MongoClientManager mongoClientManager;

    @Inject
    MongoModelService mongoModelService;

    @Override
    public Storage<String, String> getCache(String name) {
        return new MongoStorage<>(
                mongoClientManager.getCollection(name, mongoModelService.getEntityMapper(name).getEntityClass()),
                String.class.getName(), mongoModelService.getEntityMapper(name));
    }

    @Override
    public <T> Storage<String, T> getCache(String name, Class<T> type) {
        return new MongoStorage<>(
                mongoClientManager.getCollection(name, mongoModelService.getEntityMapper(name).getEntityClass()),
                type.getName(), mongoModelService.getEntityMapper(name));
    }

    @Override
    public <T> Storage<String, T> getCache(String name, Class<T> type, String rootType) {
        return new MongoStorage<>(
                mongoClientManager.getCollection(name, mongoModelService.getEntityMapper(name).getEntityClass()),
                rootType, mongoModelService.getEntityMapper(name));
    }
}
