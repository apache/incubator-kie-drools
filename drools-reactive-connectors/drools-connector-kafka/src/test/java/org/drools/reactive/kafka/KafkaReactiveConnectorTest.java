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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.drools.reactive.api.ConnectorException;
import org.drools.reactive.api.ConnectorHealth;
import org.drools.reactive.api.ConnectorState;
import org.drools.reactive.api.FactDeserializer;
import org.drools.reactive.api.FiringStrategy;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitInstance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaReactiveConnectorTest {

    private KafkaReactiveConnector<TestEvent> connector;

    @AfterEach
    void tearDown() {
        if (connector != null && connector.getState() != ConnectorState.STOPPED) {
            connector.close();
        }
    }

    @Test
    void shouldTransitionThroughLifecycleStates() {
        connector = createConnector(emptyPollConsumer(), FiringStrategy.EXTERNAL);

        assertThat(connector.getState()).isEqualTo(ConnectorState.CREATED);

        connector.start(new CollectingDataStream<>());
        assertThat(connector.getState()).isEqualTo(ConnectorState.RUNNING);

        connector.pause();
        assertThat(connector.getState()).isEqualTo(ConnectorState.PAUSED);

        connector.resume();
        assertThat(connector.getState()).isEqualTo(ConnectorState.RUNNING);

        connector.close();
        assertThat(connector.getState()).isEqualTo(ConnectorState.STOPPED);
    }

    @Test
    void shouldRejectDoubleStart() {
        connector = createConnector(emptyPollConsumer(), FiringStrategy.EXTERNAL);
        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();

        connector.start(stream);

        assertThatThrownBy(() -> connector.start(stream))
                .isInstanceOf(ConnectorException.class)
                .hasMessageContaining("cannot be started");
    }

    @Test
    void shouldDeserializeAndAppendFacts() throws Exception {
        List<ConsumerRecords<String, byte[]>> recordBatches = new ArrayList<>();
        recordBatches.add(createRecords("test-topic",
                "{\"name\":\"event1\",\"value\":42}",
                "{\"name\":\"event2\",\"value\":99}"));

        connector = createConnector(
                finitePollConsumer(recordBatches),
                FiringStrategy.EXTERNAL);

        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        connector.start(stream);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(2));

        assertThat(stream.getCollected().get(0).getName()).isEqualTo("event1");
        assertThat(stream.getCollected().get(0).getValue()).isEqualTo(42);
        assertThat(stream.getCollected().get(1).getName()).isEqualTo("event2");
        assertThat(stream.getCollected().get(1).getValue()).isEqualTo(99);
    }

    @Test
    void shouldFireRulesPerMessageWhenConfigured() throws Exception {
        List<ConsumerRecords<String, byte[]>> recordBatches = new ArrayList<>();
        recordBatches.add(createRecords("test-topic",
                "{\"name\":\"e1\",\"value\":1}",
                "{\"name\":\"e2\",\"value\":2}"));

        connector = createConnector(
                finitePollConsumer(recordBatches),
                FiringStrategy.PER_MESSAGE);

        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        RuleUnitInstance<?> mockRuleUnit = mock(RuleUnitInstance.class);

        connector.start(stream, mockRuleUnit);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(2));

        verify(mockRuleUnit, atLeastOnce()).fire();
    }

    @Test
    void shouldFireRulesOncePerBatchWhenMicroBatchConfigured() throws Exception {
        List<ConsumerRecords<String, byte[]>> recordBatches = new ArrayList<>();
        recordBatches.add(createRecords("test-topic",
                "{\"name\":\"e1\",\"value\":1}",
                "{\"name\":\"e2\",\"value\":2}",
                "{\"name\":\"e3\",\"value\":3}"));

        connector = createConnector(
                finitePollConsumer(recordBatches),
                FiringStrategy.MICRO_BATCH);

        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        RuleUnitInstance<?> mockRuleUnit = mock(RuleUnitInstance.class);

        connector.start(stream, mockRuleUnit);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(3));

        verify(mockRuleUnit, atLeastOnce()).fire();
    }

    @Test
    void shouldNotFireRulesWhenExternalStrategy() throws Exception {
        List<ConsumerRecords<String, byte[]>> recordBatches = new ArrayList<>();
        recordBatches.add(createRecords("test-topic",
                "{\"name\":\"e1\",\"value\":1}"));

        connector = createConnector(
                finitePollConsumer(recordBatches),
                FiringStrategy.EXTERNAL);

        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        RuleUnitInstance<?> mockRuleUnit = mock(RuleUnitInstance.class);

        connector.start(stream, mockRuleUnit);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(1));

        verify(mockRuleUnit, org.mockito.Mockito.never()).fire();
    }

    @Test
    void shouldTrackHealthMetrics() throws Exception {
        List<ConsumerRecords<String, byte[]>> recordBatches = new ArrayList<>();
        recordBatches.add(createRecords("test-topic",
                "{\"name\":\"e1\",\"value\":1}",
                "{\"name\":\"e2\",\"value\":2}"));

        connector = createConnector(
                finitePollConsumer(recordBatches),
                FiringStrategy.EXTERNAL);

        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        connector.start(stream);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    ConnectorHealth health = connector.health();
                    assertThat(health.getMessagesReceived()).isEqualTo(2);
                    assertThat(health.getMessagesProcessed()).isEqualTo(2);
                    assertThat(health.getMessagesFailed()).isZero();
                });
    }

    @Test
    void shouldRecordFailedMessagesOnDeserializationError() throws Exception {
        List<ConsumerRecords<String, byte[]>> recordBatches = new ArrayList<>();
        recordBatches.add(createRecords("test-topic",
                "not-valid-json",
                "{\"name\":\"good\",\"value\":1}"));

        connector = createConnector(
                finitePollConsumer(recordBatches),
                FiringStrategy.EXTERNAL);

        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        connector.start(stream);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    ConnectorHealth health = connector.health();
                    assertThat(health.getMessagesReceived()).isEqualTo(2);
                    assertThat(health.getMessagesProcessed()).isEqualTo(1);
                    assertThat(health.getMessagesFailed()).isEqualTo(1);
                    assertThat(health.getLastError()).isNotNull();
                });
    }

    @Test
    void shouldSkipNullDeserializationResults() throws Exception {
        FactDeserializer<TestEvent> nullReturningDeserializer = new FactDeserializer<>() {
            @Override
            public TestEvent deserialize(String topic, byte[] data) {
                return null;
            }
        };

        List<ConsumerRecords<String, byte[]>> recordBatches = new ArrayList<>();
        recordBatches.add(createRecords("test-topic", "{\"name\":\"e1\",\"value\":1}"));

        connector = createConnectorWithDeserializer(
                finitePollConsumer(recordBatches),
                nullReturningDeserializer,
                FiringStrategy.EXTERNAL);

        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        connector.start(stream);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    ConnectorHealth health = connector.health();
                    assertThat(health.getMessagesReceived()).isEqualTo(1);
                    assertThat(health.getMessagesProcessed()).isZero();
                });

        assertThat(stream.getCollected()).isEmpty();
    }

    // --- Helper methods ---

    private KafkaReactiveConnector<TestEvent> createConnector(
            KafkaConsumer<String, byte[]> mockConsumer,
            FiringStrategy strategy) {
        return createConnectorWithDeserializer(
                mockConsumer,
                new JsonFactDeserializer<>(TestEvent.class),
                strategy);
    }

    private KafkaReactiveConnector<TestEvent> createConnectorWithDeserializer(
            KafkaConsumer<String, byte[]> mockConsumer,
            FactDeserializer<TestEvent> deserializer,
            FiringStrategy strategy) {
        KafkaConnectorConfig config = KafkaConnectorConfig.builder()
                .bootstrapServers("localhost:9092")
                .topics("test-topic")
                .groupId("test-group")
                .firingStrategy(strategy)
                .pollTimeout(Duration.ofMillis(100))
                .build();

        return new KafkaReactiveConnector<>(config, deserializer, props -> mockConsumer);
    }

    @SuppressWarnings("unchecked")
    private KafkaConsumer<String, byte[]> emptyPollConsumer() {
        KafkaConsumer<String, byte[]> consumer = mock(KafkaConsumer.class);
        doAnswer(inv -> ConsumerRecords.empty())
                .when(consumer).poll(any(Duration.class));
        return consumer;
    }

    @SuppressWarnings("unchecked")
    private KafkaConsumer<String, byte[]> finitePollConsumer(
            List<ConsumerRecords<String, byte[]>> batches) {
        KafkaConsumer<String, byte[]> consumer = mock(KafkaConsumer.class);
        AtomicInteger pollCount = new AtomicInteger(0);

        doAnswer(inv -> {
            int index = pollCount.getAndIncrement();
            if (index < batches.size()) {
                return batches.get(index);
            }
            Thread.sleep(50);
            return ConsumerRecords.empty();
        }).when(consumer).poll(any(Duration.class));

        return consumer;
    }

    private ConsumerRecords<String, byte[]> createRecords(String topic, String... jsonValues) {
        TopicPartition tp = new TopicPartition(topic, 0);
        List<ConsumerRecord<String, byte[]>> records = new ArrayList<>();
        for (int i = 0; i < jsonValues.length; i++) {
            records.add(new ConsumerRecord<>(topic, 0, i, null, jsonValues[i].getBytes()));
        }
        Map<TopicPartition, List<ConsumerRecord<String, byte[]>>> recordMap = new HashMap<>();
        recordMap.put(tp, records);
        return new ConsumerRecords<>(recordMap);
    }

    /**
     * A simple DataStream that collects all appended facts for test assertions.
     */
    static class CollectingDataStream<T> implements DataStream<T> {

        private final List<T> collected = new CopyOnWriteArrayList<>();

        @Override
        public void append(T value) {
            collected.add(value);
        }

        @Override
        public void subscribe(DataProcessor<T> subscriber) {
        }

        public List<T> getCollected() {
            return collected;
        }
    }
}
