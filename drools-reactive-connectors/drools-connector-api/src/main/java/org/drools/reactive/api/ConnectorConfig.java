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

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Base configuration shared by all {@link ReactiveConnector} implementations.
 * Connector-specific subclasses add additional properties (e.g., Kafka bootstrap servers).
 */
public class ConnectorConfig {

    private final FiringStrategy firingStrategy;
    private final int batchSize;
    private final Duration batchWindow;
    private final Duration pollTimeout;
    private final Map<String, Object> properties;

    protected ConnectorConfig(Builder<?> builder) {
        this.firingStrategy = builder.firingStrategy;
        this.batchSize = builder.batchSize;
        this.batchWindow = builder.batchWindow;
        this.pollTimeout = builder.pollTimeout;
        this.properties = Collections.unmodifiableMap(new HashMap<>(builder.properties));
    }

    public FiringStrategy getFiringStrategy() {
        return firingStrategy;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public Duration getBatchWindow() {
        return batchWindow;
    }

    public Duration getPollTimeout() {
        return pollTimeout;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @SuppressWarnings("unchecked")
    public static class Builder<B extends Builder<B>> {

        private FiringStrategy firingStrategy = FiringStrategy.PER_MESSAGE;
        private int batchSize = 100;
        private Duration batchWindow = Duration.ofMillis(500);
        private Duration pollTimeout = Duration.ofMillis(1000);
        private final Map<String, Object> properties = new HashMap<>();

        public B firingStrategy(FiringStrategy strategy) {
            this.firingStrategy = Objects.requireNonNull(strategy, "firingStrategy must not be null");
            return (B) this;
        }

        public B batchSize(int size) {
            if (size < 1) {
                throw new IllegalArgumentException("batchSize must be >= 1");
            }
            this.batchSize = size;
            return (B) this;
        }

        public B batchWindow(Duration window) {
            this.batchWindow = Objects.requireNonNull(window, "batchWindow must not be null");
            return (B) this;
        }

        public B pollTimeout(Duration timeout) {
            this.pollTimeout = Objects.requireNonNull(timeout, "pollTimeout must not be null");
            return (B) this;
        }

        public B property(String key, Object value) {
            this.properties.put(key, value);
            return (B) this;
        }

        public ConnectorConfig build() {
            return new ConnectorConfig(this);
        }
    }
}
