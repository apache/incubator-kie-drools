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
package org.drools.reactive.pulsar;

import java.time.Duration;

import org.drools.reactive.api.FiringStrategy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PulsarConnectorConfigTest {

    @Test
    void shouldBuildWithDefaults() {
        PulsarConnectorConfig config = PulsarConnectorConfig.builder()
                .topics("my-topic")
                .build();

        assertThat(config.getServiceUrl()).isEqualTo("pulsar://localhost:6650");
        assertThat(config.getTopics()).containsExactly("my-topic");
        assertThat(config.getSubscriptionName()).isEqualTo("drools-reactive");
        assertThat(config.getSubscriptionType()).isEqualTo(PulsarSubscriptionType.EXCLUSIVE);
        assertThat(config.getFiringStrategy()).isEqualTo(FiringStrategy.PER_MESSAGE);
    }

    @Test
    void shouldBuildWithCustomValues() {
        PulsarConnectorConfig config = PulsarConnectorConfig.builder()
                .serviceUrl("pulsar://broker:6650")
                .topics("topic-a", "topic-b")
                .subscriptionName("my-sub")
                .subscriptionType(PulsarSubscriptionType.SHARED)
                .firingStrategy(FiringStrategy.MICRO_BATCH)
                .batchSize(50)
                .pollTimeout(Duration.ofMillis(200))
                .build();

        assertThat(config.getServiceUrl()).isEqualTo("pulsar://broker:6650");
        assertThat(config.getTopics()).containsExactly("topic-a", "topic-b");
        assertThat(config.getSubscriptionName()).isEqualTo("my-sub");
        assertThat(config.getSubscriptionType()).isEqualTo(PulsarSubscriptionType.SHARED);
        assertThat(config.getFiringStrategy()).isEqualTo(FiringStrategy.MICRO_BATCH);
        assertThat(config.getBatchSize()).isEqualTo(50);
    }

    @Test
    void shouldRejectEmptyTopics() {
        assertThatThrownBy(() -> PulsarConnectorConfig.builder().build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("topic");
    }

    @Test
    void shouldSupportAllSubscriptionTypes() {
        for (PulsarSubscriptionType type : PulsarSubscriptionType.values()) {
            PulsarConnectorConfig config = PulsarConnectorConfig.builder()
                    .topics("t")
                    .subscriptionType(type)
                    .build();
            assertThat(config.getSubscriptionType()).isEqualTo(type);
        }
    }
}
