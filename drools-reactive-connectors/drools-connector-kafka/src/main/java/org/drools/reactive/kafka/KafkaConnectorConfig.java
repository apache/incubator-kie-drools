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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.drools.reactive.api.ConnectorConfig;

/**
 * Kafka-specific configuration extending the base {@link ConnectorConfig}.
 */
public class KafkaConnectorConfig extends ConnectorConfig {

    private final String bootstrapServers;
    private final List<String> topics;
    private final String groupId;
    private final boolean autoCommit;

    private KafkaConnectorConfig(Builder builder) {
        super(builder);
        this.bootstrapServers = builder.bootstrapServers;
        this.topics = Collections.unmodifiableList(builder.topics);
        this.groupId = builder.groupId;
        this.autoCommit = builder.autoCommit;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getGroupId() {
        return groupId;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ConnectorConfig.Builder<Builder> {

        private String bootstrapServers = "localhost:9092";
        private List<String> topics = Collections.emptyList();
        private String groupId = "drools-reactive";
        private boolean autoCommit = false;

        public Builder bootstrapServers(String servers) {
            this.bootstrapServers = Objects.requireNonNull(servers, "bootstrapServers must not be null");
            return this;
        }

        public Builder topics(String... topics) {
            this.topics = Arrays.asList(topics);
            return this;
        }

        public Builder topics(List<String> topics) {
            this.topics = Objects.requireNonNull(topics, "topics must not be null");
            return this;
        }

        public Builder groupId(String groupId) {
            this.groupId = Objects.requireNonNull(groupId, "groupId must not be null");
            return this;
        }

        public Builder autoCommit(boolean autoCommit) {
            this.autoCommit = autoCommit;
            return this;
        }

        @Override
        public KafkaConnectorConfig build() {
            if (topics.isEmpty()) {
                throw new IllegalArgumentException("At least one topic must be specified");
            }
            return new KafkaConnectorConfig(this);
        }
    }
}
