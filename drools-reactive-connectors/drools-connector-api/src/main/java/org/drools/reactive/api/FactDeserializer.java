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
package org.drools.reactive.api;

/**
 * Converts raw bytes from a messaging system into typed fact objects
 * that can be inserted into a Drools {@link org.drools.ruleunits.api.DataStream}.
 *
 * @param <T> the target fact type
 */
public interface FactDeserializer<T> {

    /**
     * Deserialize raw bytes into a fact object.
     *
     * @param topic the source topic or channel name
     * @param data  the raw message bytes
     * @return the deserialized fact, or {@code null} to skip the message
     * @throws ConnectorException if deserialization fails irrecoverably
     */
    T deserialize(String topic, byte[] data);

    /**
     * Called once when the connector starts, allowing initialization
     * of any internal state (e.g., schema registry clients).
     */
    default void configure(java.util.Map<String, Object> config) {
    }

    /**
     * Called when the connector is closed, allowing cleanup.
     */
    default void close() {
    }
}
