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
package org.drools.reactive.jms;

import java.time.Duration;

import org.drools.reactive.api.FiringStrategy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JmsConnectorConfigTest {

    @Test
    void shouldBuildQueueConfigWithDefaults() {
        JmsConnectorConfig config = JmsConnectorConfig.builder()
                .destinationName("order.queue")
                .build();

        assertThat(config.getDestinationName()).isEqualTo("order.queue");
        assertThat(config.getDestinationType()).isEqualTo(JmsDestinationType.QUEUE);
        assertThat(config.getMessageSelector()).isNull();
        assertThat(config.isSessionTransacted()).isFalse();
        assertThat(config.getDurableSubscriptionName()).isNull();
        assertThat(config.getFiringStrategy()).isEqualTo(FiringStrategy.PER_MESSAGE);
    }

    @Test
    void shouldBuildTopicConfigWithSelector() {
        JmsConnectorConfig config = JmsConnectorConfig.builder()
                .destinationName("events.topic")
                .destinationType(JmsDestinationType.TOPIC)
                .messageSelector("priority > 5")
                .firingStrategy(FiringStrategy.MICRO_BATCH)
                .batchSize(50)
                .pollTimeout(Duration.ofMillis(200))
                .build();

        assertThat(config.getDestinationName()).isEqualTo("events.topic");
        assertThat(config.getDestinationType()).isEqualTo(JmsDestinationType.TOPIC);
        assertThat(config.getMessageSelector()).isEqualTo("priority > 5");
        assertThat(config.getFiringStrategy()).isEqualTo(FiringStrategy.MICRO_BATCH);
        assertThat(config.getBatchSize()).isEqualTo(50);
    }

    @Test
    void shouldBuildDurableSubscriptionConfig() {
        JmsConnectorConfig config = JmsConnectorConfig.builder()
                .destinationName("notifications")
                .destinationType(JmsDestinationType.TOPIC)
                .durableSubscription("my-sub", "client-1")
                .sessionTransacted(true)
                .build();

        assertThat(config.getDurableSubscriptionName()).isEqualTo("my-sub");
        assertThat(config.getClientId()).isEqualTo("client-1");
        assertThat(config.isSessionTransacted()).isTrue();
    }

    @Test
    void shouldRejectMissingDestinationName() {
        assertThatThrownBy(() -> JmsConnectorConfig.builder().build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("destinationName");
    }

    @Test
    void shouldRejectDurableSubscriptionOnQueue() {
        assertThatThrownBy(() -> JmsConnectorConfig.builder()
                .destinationName("my-queue")
                .destinationType(JmsDestinationType.QUEUE)
                .durableSubscription("sub", "client")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Durable subscriptions");
    }
}
