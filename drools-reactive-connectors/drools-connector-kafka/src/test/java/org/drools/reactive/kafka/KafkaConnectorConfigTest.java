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

import java.time.Duration;

import org.drools.reactive.api.FiringStrategy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KafkaConnectorConfigTest {

    @Test
    void shouldBuildWithDefaults() {
        KafkaConnectorConfig config = KafkaConnectorConfig.builder()
                .topics("my-topic")
                .build();

        assertThat(config.getBootstrapServers()).isEqualTo("localhost:9092");
        assertThat(config.getTopics()).containsExactly("my-topic");
        assertThat(config.getGroupId()).isEqualTo("drools-reactive");
        assertThat(config.isAutoCommit()).isFalse();
        assertThat(config.getFiringStrategy()).isEqualTo(FiringStrategy.PER_MESSAGE);
    }

    @Test
    void shouldBuildWithCustomValues() {
        KafkaConnectorConfig config = KafkaConnectorConfig.builder()
                .bootstrapServers("broker1:9092,broker2:9092")
                .topics("topic-a", "topic-b")
                .groupId("my-group")
                .autoCommit(true)
                .firingStrategy(FiringStrategy.MICRO_BATCH)
                .batchSize(200)
                .batchWindow(Duration.ofSeconds(1))
                .pollTimeout(Duration.ofMillis(500))
                .property("custom.key", "custom.value")
                .build();

        assertThat(config.getBootstrapServers()).isEqualTo("broker1:9092,broker2:9092");
        assertThat(config.getTopics()).containsExactly("topic-a", "topic-b");
        assertThat(config.getGroupId()).isEqualTo("my-group");
        assertThat(config.isAutoCommit()).isTrue();
        assertThat(config.getFiringStrategy()).isEqualTo(FiringStrategy.MICRO_BATCH);
        assertThat(config.getBatchSize()).isEqualTo(200);
        assertThat(config.getBatchWindow()).isEqualTo(Duration.ofSeconds(1));
        assertThat(config.getPollTimeout()).isEqualTo(Duration.ofMillis(500));
        assertThat(config.getProperties()).containsEntry("custom.key", "custom.value");
    }

    @Test
    void shouldRejectEmptyTopics() {
        assertThatThrownBy(() -> KafkaConnectorConfig.builder().build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("topic");
    }

    @Test
    void shouldRejectInvalidBatchSize() {
        assertThatThrownBy(() -> KafkaConnectorConfig.builder()
                .topics("t")
                .batchSize(0)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("batchSize");
    }
}
