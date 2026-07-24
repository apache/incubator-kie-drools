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
package org.kie.kogito.persistence.redis.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.kogito.persistence.redis.RedisClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.redisearch.Client;
import io.redisearch.Schema;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import redis.clients.jedis.exceptions.JedisDataException;

import static org.kie.kogito.persistence.redis.Constants.INDEX_NAME_FIELD;

@ApplicationScoped
public class RedisIndexManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisIndexManager.class);

    private final RedisClientManager redisClientManager;
    private Map<String, List<String>> indexes = new HashMap<>();

    @Inject
    public RedisIndexManager(RedisClientManager redisClientManager) {
        this.redisClientManager = redisClientManager;
    }

    public void createIndex(RedisCreateIndexEvent event) {
        Client client = redisClientManager.getClient(event.getIndexName());
        indexes.put(event.getIndexName(), event.getFields().stream().map(field -> field.name).collect(Collectors.toList()));

        Schema schema = new Schema();
        event.getFields().forEach(schema::addField);
        schema.addField(new Schema.Field(INDEX_NAME_FIELD, Schema.FieldType.FullText, false));

        try {
            client.createIndex(schema, io.redisearch.client.Client.IndexOptions.defaultOptions());
        } catch (JedisDataException ignored) {
            LOGGER.info(String.format("Could not add redis index %s, it probably already exists.", event.getIndexName()));
        }
    }

    public List<String> getSchema(String indexName) {
        return indexes.get(indexName);
    }
}
