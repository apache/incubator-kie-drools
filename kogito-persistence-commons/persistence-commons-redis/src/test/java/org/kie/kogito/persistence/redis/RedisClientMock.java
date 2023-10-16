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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.redisearch.AggregationResult;
import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.Schema;
import io.redisearch.SearchResult;
import io.redisearch.Suggestion;
import io.redisearch.aggregation.AggregationBuilder;
import io.redisearch.aggregation.AggregationRequest;
import io.redisearch.client.AddOptions;
import io.redisearch.client.Client;
import io.redisearch.client.ConfigOption;
import io.redisearch.client.SuggestionOptions;

import redis.clients.jedis.Jedis;

public class RedisClientMock implements io.redisearch.Client {

    private final Map<String, Map<String, Object>> storage = new HashMap<>();

    private final List<Schema> schemas = new ArrayList<>();

    public Map<String, Map<String, Object>> getStorage() {
        return storage;
    }

    public List<Schema> getSchemas() {
        return schemas;
    }

    @Override
    public boolean createIndex(Schema schema, Client.IndexOptions indexOptions) {
        schemas.add(schema);
        return true;
    }

    @Override
    public SearchResult search(Query query) {
        return null;
    }

    @Override
    public boolean addDocument(String s, Map<String, Object> map) {
        // If an indexed value is null, redis throws an exception.
        for (Object value : map.values()) {
            if (value == null) {
                throw new IllegalArgumentException("indexed field can not be null");
            }
        }

        storage.put(s, map);
        return true;
    }

    @Override
    public boolean deleteDocument(String s) {
        storage.remove(s);
        return true;
    }

    @Override
    public boolean dropIndex() {
        return true;
    }

    @Override
    public Document getDocument(String key) {
        if (!storage.containsKey(key)) {
            return null;
        }
        return new Document(key, storage.get(key));
    }

    @Override
    public SearchResult[] searchBatch(Query... queries) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public SearchResult search(Query query, boolean b) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public AggregationResult aggregate(AggregationRequest aggregationRequest) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public AggregationResult aggregate(AggregationBuilder aggregationBuilder) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean cursorDelete(long l) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public AggregationResult cursorRead(long l, int i) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public String explain(Query query) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean addDocument(Document document, AddOptions addOptions) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean addDocument(String s, double v, Map<String, Object> map, boolean b, boolean b1, byte[] bytes) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean addDocument(Document document) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean[] addDocuments(Document... documents) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean[] addDocuments(AddOptions addOptions, Document... documents) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean addDocument(String s, double v, Map<String, Object> map) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean replaceDocument(String s, double v, Map<String, Object> map) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean replaceDocument(String s, double v, Map<String, Object> map, String s1) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean updateDocument(String s, double v, Map<String, Object> map) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean updateDocument(String s, double v, Map<String, Object> map, String s1) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean addHash(String s, double v, boolean b) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public Map<String, Object> getInfo() {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean[] deleteDocuments(boolean b, String... strings) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean deleteDocument(String s, boolean b) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public Document getDocument(String s, boolean b) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public List<Document> getDocuments(String... strings) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public List<Document> getDocuments(boolean b, String... strings) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean dropIndex(boolean b) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public Long addSuggestion(Suggestion suggestion, boolean b) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public List<Suggestion> getSuggestion(String s, SuggestionOptions suggestionOptions) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public Long deleteSuggestion(String s) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public Long getSuggestionLength() {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean alterIndex(Schema.Field... fields) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean setConfig(ConfigOption configOption, String s) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public String getConfig(ConfigOption configOption) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public Map<String, String> getAllConfig() {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean addAlias(String s) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean updateAlias(String s) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean deleteAlias(String s) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public long addSynonym(String... strings) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean updateSynonym(long l, String... strings) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public boolean updateSynonym(String s, String... strings) {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public Map<String, List<String>> dumpSynonym() {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public Jedis connection() {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Mock does not support this operation.");
    }
}
