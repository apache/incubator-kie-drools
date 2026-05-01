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
package org.drools.grpc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.grpc.proto.Fact;

/**
 * Converts between gRPC {@link Fact} messages (typed JSON) and Java objects.
 */
public class FactConverter {

    private final ObjectMapper objectMapper;
    private final ClassLoader classLoader;

    public FactConverter() {
        this(new ObjectMapper(), Thread.currentThread().getContextClassLoader());
    }

    public FactConverter(ObjectMapper objectMapper, ClassLoader classLoader) {
        this.objectMapper = objectMapper;
        this.classLoader = classLoader;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Deserializes a {@link Fact} proto message to a Java object.
     *
     * @param fact the fact message containing type name and JSON payload
     * @return the deserialized Java object
     * @throws FactConversionException if the type cannot be found or JSON is invalid
     */
    public Object toObject(Fact fact) {
        try {
            Class<?> clazz = classLoader.loadClass(fact.getType());
            return objectMapper.readValue(fact.getJson(), clazz);
        } catch (ClassNotFoundException e) {
            throw new FactConversionException("Unknown fact type: " + fact.getType(), e);
        } catch (Exception e) {
            throw new FactConversionException(
                    "Failed to deserialize fact of type " + fact.getType() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Serializes a Java object to a {@link Fact} proto message.
     *
     * @param object the Java object to serialize
     * @return the Fact message with type and JSON payload
     * @throws FactConversionException if serialization fails
     */
    public Fact toFact(Object object) {
        try {
            return Fact.newBuilder()
                    .setType(object.getClass().getName())
                    .setJson(objectMapper.writeValueAsString(object))
                    .build();
        } catch (Exception e) {
            throw new FactConversionException(
                    "Failed to serialize fact of type " + object.getClass().getName() + ": " + e.getMessage(), e);
        }
    }

    public static class FactConversionException extends RuntimeException {
        public FactConversionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
