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

package org.kie.kogito.persistence.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.persistence.redis.index.RedisIndexManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.redisearch.Client;
import io.redisearch.Document;
import io.smallrye.mutiny.Multi;

import static org.kie.kogito.persistence.redis.Constants.INDEX_NAME_FIELD;
import static org.kie.kogito.persistence.redis.Constants.RAW_OBJECT_FIELD;

public class RedisStorage<V> implements Storage<String, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisStorage.class);

    private final Client redisClient;
    private final RedisIndexManager redisIndexManager;
    private final String indexName;
    private final Class<V> type;

    public RedisStorage(Client redisClient, RedisIndexManager redisIndexManager, String indexName, Class<V> type) {
        this.redisClient = redisClient;
        this.redisIndexManager = redisIndexManager;
        this.indexName = indexName;
        this.type = type;
    }

    @Override
    public Multi<V> objectCreatedListener() {
        throw new UnsupportedOperationException("addObjectCreatedListener operation is not supported for Redis yet.");
    }

    @Override
    public Multi<V> objectUpdatedListener() {
        throw new UnsupportedOperationException("addObjectUpdatedListener operation is not supported for Redis yet.");
    }

    @Override
    public Multi<String> objectRemovedListener() {
        throw new UnsupportedOperationException("addObjectRemovedListener operation is not supported for Redis yet.");
    }

    @Override
    public Query<V> query() {
        return new RedisQuery<>(redisClient, indexName, type);
    }

    @Override
    public V get(String key) {
        Document document = redisClient.getDocument(key);
        try {
            return document == null ? null : JsonUtils.getMapper().readValue((String) document.get(RAW_OBJECT_FIELD), type);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Could not deserialize the requested object.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public V put(String key, V value) {
        Map<String, Object> document = new HashMap<>();
        List<String> indexedFields = redisIndexManager.getSchema(indexName);
        if (!indexedFields.isEmpty()) { // Add into the payload only the indexed fields, if there are any
            Map<String, Object> mappedValue = JsonUtils.getMapper().convertValue(value, Map.class);
            for (String fieldName : indexedFields) {
                if (mappedValue.get(fieldName) != null) { // If a field is indexed, its value can not be null: it has to be filtered out
                    // Indexed values have to be escaped according to https://github.com/RediSearch/RediSearch/issues/1148
                    document.put(fieldName, Sanitizer.sanitize(mappedValue.get(fieldName)));
                }
            }
        }

        document.put(INDEX_NAME_FIELD, indexName);

        try {
            document.put(RAW_OBJECT_FIELD, JsonUtils.getMapper().writeValueAsString(value));
        } catch (JsonProcessingException e) {
            LOGGER.warn("Could not serialize the object.", e);
            throw new RuntimeException(e);
        }
        redisClient.addDocument(key, document);
        return value;
    }

    @Override
    public V remove(String key) {
        V value = get(key);
        redisClient.deleteDocument(key);
        return value;
    }

    @Override
    public boolean containsKey(String key) {
        return redisClient.getDocument(key) != null;
    }

    @Override
    public Map<String, V> entries() {
        throw new UnsupportedOperationException("entrySet operation not supported for Redis.");
    }

    @Override
    public void clear() {
        List<Document> documents = redisClient.search(new io.redisearch.Query(String.format("@%s:%s", INDEX_NAME_FIELD, indexName))).docs;
        for (Document doc : documents) {
            redisClient.deleteDocument(doc.getId());
        }
    }

    @Override
    public String getRootType() {
        return type.getSimpleName();
    }
}
