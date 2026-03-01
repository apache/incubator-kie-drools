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
package org.drools.reactive.debezium;

import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.drools.reactive.api.ConnectorException;

/**
 * Converts Debezium JSON change event values into typed {@link ChangeEvent} objects.
 * Extracts the operation type and the {@code after} (or {@code before} for deletes)
 * payload from the Debezium envelope.
 *
 * @param <T> the target fact type
 */
public class ChangeEventDeserializer<T> {

    private final Class<T> targetType;
    private ObjectMapper objectMapper;

    public ChangeEventDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    public ChangeEventDeserializer(Class<T> targetType, ObjectMapper objectMapper) {
        this.targetType = targetType;
        this.objectMapper = objectMapper;
    }

    public void configure(Map<String, Object> config) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }

    /**
     * Parse a Debezium JSON envelope into a {@link ChangeEvent}.
     *
     * @param valueJson the JSON string from the Debezium change event value
     * @return the parsed ChangeEvent, or {@code null} if the value is null/empty
     */
    public ChangeEvent<T> deserialize(String valueJson) {
        if (valueJson == null || valueJson.isEmpty()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(valueJson);
            JsonNode payload = root.has("payload") ? root.get("payload") : root;

            ChangeEventOperation op = parseOperation(payload);
            String source = extractSource(payload);
            String table = extractTable(payload);
            long ts = payload.has("ts_ms") ? payload.get("ts_ms").asLong() : System.currentTimeMillis();

            T value = null;
            if (op == ChangeEventOperation.DELETE) {
                JsonNode before = payload.get("before");
                if (before != null && !before.isNull()) {
                    value = objectMapper.treeToValue(before, targetType);
                }
            } else {
                JsonNode after = payload.get("after");
                if (after != null && !after.isNull()) {
                    value = objectMapper.treeToValue(after, targetType);
                }
            }

            return new ChangeEvent<>(op, value, source, table, ts);
        } catch (Exception e) {
            throw new ConnectorException("Failed to deserialize Debezium change event", e);
        }
    }

    private static ChangeEventOperation parseOperation(JsonNode payload) {
        if (!payload.has("op")) {
            return ChangeEventOperation.UNKNOWN;
        }
        String op = payload.get("op").asText();
        switch (op) {
            case "c":
                return ChangeEventOperation.CREATE;
            case "u":
                return ChangeEventOperation.UPDATE;
            case "d":
                return ChangeEventOperation.DELETE;
            case "r":
                return ChangeEventOperation.READ;
            default:
                return ChangeEventOperation.UNKNOWN;
        }
    }

    private static String extractSource(JsonNode payload) {
        if (payload.has("source") && payload.get("source").has("name")) {
            return payload.get("source").get("name").asText();
        }
        return null;
    }

    private static String extractTable(JsonNode payload) {
        if (payload.has("source") && payload.get("source").has("table")) {
            return payload.get("source").get("table").asText();
        }
        return null;
    }

    public void close() {
    }
}
