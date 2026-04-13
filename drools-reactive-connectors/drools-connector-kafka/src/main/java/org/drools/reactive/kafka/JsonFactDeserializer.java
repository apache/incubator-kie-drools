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
package org.drools.reactive.kafka;

import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.drools.reactive.api.ConnectorException;
import org.drools.reactive.api.FactDeserializer;

/**
 * A {@link FactDeserializer} that converts JSON byte arrays into typed
 * fact objects using Jackson.
 *
 * @param <T> the target fact type
 */
public class JsonFactDeserializer<T> implements FactDeserializer<T> {

    private final Class<T> targetType;
    private ObjectMapper objectMapper;

    public JsonFactDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    public JsonFactDeserializer(Class<T> targetType, ObjectMapper objectMapper) {
        this.targetType = targetType;
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure(Map<String, Object> config) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(data, targetType);
        } catch (Exception e) {
            throw new ConnectorException(
                    "Failed to deserialize JSON from topic '" + topic + "' into " + targetType.getName(), e);
        }
    }
}
