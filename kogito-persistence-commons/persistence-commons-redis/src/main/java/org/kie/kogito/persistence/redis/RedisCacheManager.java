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
package org.kie.kogito.persistence.redis;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;
import org.kie.kogito.persistence.redis.index.RedisIndexManager;

import io.quarkus.arc.properties.IfBuildProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.kie.kogito.persistence.api.factory.Constants.PERSISTENCE_TYPE_PROPERTY;
import static org.kie.kogito.persistence.redis.Constants.REDIS_STORAGE;

@ApplicationScoped
@IfBuildProperty(name = PERSISTENCE_TYPE_PROPERTY, stringValue = REDIS_STORAGE)
public class RedisCacheManager implements StorageService {

    @Inject
    RedisClientManager redisClientManager;

    @Inject
    RedisIndexManager redisIndexManager;

    @Override
    public Storage<String, String> getCache(String name) {
        return new RedisStorage<>(redisClientManager.getClient(name), redisIndexManager, name, String.class);
    }

    @Override
    public <T> Storage<String, T> getCache(String name, Class<T> type) {
        return new RedisStorage<>(redisClientManager.getClient(name), redisIndexManager, name, type);
    }

    @Override
    public <T> Storage<String, T> getCache(String name, Class<T> type, String rootType) {
        return new RedisStorage<>(redisClientManager.getClient(name), redisIndexManager, name, type);
    }
}
