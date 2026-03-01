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
package org.drools.reactive.quarkus;

import java.time.Duration;
import java.util.Optional;

import org.drools.reactive.api.ConnectorException;
import org.drools.reactive.api.FiringStrategy;
import org.drools.reactive.kafka.KafkaConnectorConfig;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReactiveConnectorProducerTest {

    private final ReactiveConnectorProducer producer = new ReactiveConnectorProducer();

    @Test
    void shouldBuildKafkaConfigFromInstanceConfig() {
        ReactiveConnectorConfig.ConnectorInstanceConfig config = new TestInstanceConfig(
                "kafka", "topic-a,topic-b", "broker:9092", "my-group",
                "micro-batch", 50, Duration.ofMillis(500));

        KafkaConnectorConfig result = producer.buildKafkaConfig(config);

        assertThat(result.getBootstrapServers()).isEqualTo("broker:9092");
        assertThat(result.getTopics()).containsExactly("topic-a", "topic-b");
        assertThat(result.getGroupId()).isEqualTo("my-group");
        assertThat(result.getFiringStrategy()).isEqualTo(FiringStrategy.MICRO_BATCH);
        assertThat(result.getBatchSize()).isEqualTo(50);
    }

    @Test
    void shouldThrowWhenNoTopics() {
        ReactiveConnectorConfig.ConnectorInstanceConfig config = new TestInstanceConfig(
                "kafka", null, "broker:9092", "grp",
                "per-message", 100, Duration.ofSeconds(1));

        assertThatThrownBy(() -> producer.buildKafkaConfig(config))
                .isInstanceOf(ConnectorException.class)
                .hasMessageContaining("No topics configured");
    }

    @Test
    void shouldParseFiringStrategies() {
        assertThat(ReactiveConnectorProducer.parseFiringStrategy("per-message"))
                .isEqualTo(FiringStrategy.PER_MESSAGE);
        assertThat(ReactiveConnectorProducer.parseFiringStrategy("micro-batch"))
                .isEqualTo(FiringStrategy.MICRO_BATCH);
        assertThat(ReactiveConnectorProducer.parseFiringStrategy("external"))
                .isEqualTo(FiringStrategy.EXTERNAL);
        assertThat(ReactiveConnectorProducer.parseFiringStrategy("PER_MESSAGE"))
                .isEqualTo(FiringStrategy.PER_MESSAGE);
        assertThat(ReactiveConnectorProducer.parseFiringStrategy("unknown"))
                .isEqualTo(FiringStrategy.PER_MESSAGE);
        assertThat(ReactiveConnectorProducer.parseFiringStrategy(null))
                .isEqualTo(FiringStrategy.PER_MESSAGE);
    }

    /**
     * Simple implementation of ConnectorInstanceConfig for testing
     * without a running Quarkus container.
     */
    static class TestInstanceConfig implements ReactiveConnectorConfig.ConnectorInstanceConfig {
        private final String type;
        private final String topics;
        private final String bootstrapServers;
        private final String groupId;
        private final String firingStrategy;
        private final int batchSize;
        private final Duration pollTimeout;

        TestInstanceConfig(String type, String topics, String bootstrapServers,
                           String groupId, String firingStrategy, int batchSize,
                           Duration pollTimeout) {
            this.type = type;
            this.topics = topics;
            this.bootstrapServers = bootstrapServers;
            this.groupId = groupId;
            this.firingStrategy = firingStrategy;
            this.batchSize = batchSize;
            this.pollTimeout = pollTimeout;
        }

        @Override public String type() { return type; }
        @Override public Optional<String> topics() { return Optional.ofNullable(topics); }
        @Override public String bootstrapServers() { return bootstrapServers; }
        @Override public String groupId() { return groupId; }
        @Override public String firingStrategy() { return firingStrategy; }
        @Override public int batchSize() { return batchSize; }
        @Override public Duration pollTimeout() { return pollTimeout; }
        @Override public String subscriptionType() { return "exclusive"; }
        @Override public Optional<String> connectorClass() { return Optional.empty(); }
    }
}
