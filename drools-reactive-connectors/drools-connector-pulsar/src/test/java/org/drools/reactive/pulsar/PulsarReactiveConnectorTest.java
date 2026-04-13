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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionType;

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
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PulsarReactiveConnectorTest {

    private PulsarReactiveConnector<TestEvent> connector;

    @AfterEach
    void tearDown() {
        if (connector != null && connector.getState() != ConnectorState.STOPPED) {
            connector.close();
        }
    }

    @Test
    void shouldTransitionThroughLifecycleStates() throws Exception {
        connector = createConnector(emptyBatchConsumer(), FiringStrategy.EXTERNAL);

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
    void shouldDeserializeAndAppendFacts() throws Exception {
        List<Messages<byte[]>> batches = new ArrayList<>();
        batches.add(createMessages(
                "{\"name\":\"event1\",\"value\":42}",
                "{\"name\":\"event2\",\"value\":99}"));

        connector = createConnector(finiteBatchConsumer(batches), FiringStrategy.EXTERNAL);

        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        connector.start(stream);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(2));

        assertThat(stream.getCollected().get(0).getName()).isEqualTo("event1");
        assertThat(stream.getCollected().get(1).getValue()).isEqualTo(99);
    }

    @Test
    void shouldFirePerMessage() throws Exception {
        List<Messages<byte[]>> batches = new ArrayList<>();
        batches.add(createMessages("{\"name\":\"e1\",\"value\":1}"));

        connector = createConnector(finiteBatchConsumer(batches), FiringStrategy.PER_MESSAGE);
        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        RuleUnitInstance<?> mockRuleUnit = mock(RuleUnitInstance.class);

        connector.start(stream, mockRuleUnit);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(1));

        verify(mockRuleUnit, atLeastOnce()).fire();
    }

    @Test
    void shouldNotFireWhenExternal() throws Exception {
        List<Messages<byte[]>> batches = new ArrayList<>();
        batches.add(createMessages("{\"name\":\"e1\",\"value\":1}"));

        connector = createConnector(finiteBatchConsumer(batches), FiringStrategy.EXTERNAL);
        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        RuleUnitInstance<?> mockRuleUnit = mock(RuleUnitInstance.class);

        connector.start(stream, mockRuleUnit);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(1));

        verify(mockRuleUnit, never()).fire();
    }

    @Test
    void shouldTrackHealthMetrics() throws Exception {
        List<Messages<byte[]>> batches = new ArrayList<>();
        batches.add(createMessages(
                "{\"name\":\"e1\",\"value\":1}",
                "{\"name\":\"e2\",\"value\":2}"));

        connector = createConnector(finiteBatchConsumer(batches), FiringStrategy.EXTERNAL);
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

    // --- Helpers ---

    @SuppressWarnings("unchecked")
    private PulsarReactiveConnector<TestEvent> createConnector(
            Consumer<byte[]> mockConsumer, FiringStrategy strategy) throws Exception {
        PulsarConnectorConfig config = PulsarConnectorConfig.builder()
                .serviceUrl("pulsar://localhost:6650")
                .topics("test-topic")
                .subscriptionName("test-sub")
                .firingStrategy(strategy)
                .pollTimeout(Duration.ofMillis(100))
                .build();

        PulsarClient mockClient = mock(PulsarClient.class);
        ConsumerBuilder<byte[]> mockBuilder = mock(ConsumerBuilder.class);
        when(mockClient.newConsumer(any(Schema.class))).thenReturn(mockBuilder);
        when(mockBuilder.topics(anyList())).thenReturn(mockBuilder);
        when(mockBuilder.subscriptionName(any())).thenReturn(mockBuilder);
        when(mockBuilder.subscriptionType(any(SubscriptionType.class))).thenReturn(mockBuilder);
        when(mockBuilder.subscribe()).thenReturn(mockConsumer);

        FactDeserializer<TestEvent> deserializer = (topic, data) -> {
            if (data == null || data.length == 0) {
                return null;
            }
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper =
                        new com.fasterxml.jackson.databind.ObjectMapper();
                return mapper.readValue(data, TestEvent.class);
            } catch (Exception e) {
                throw new org.drools.reactive.api.ConnectorException("deser fail", e);
            }
        };

        return new PulsarReactiveConnector<>(config, deserializer, url -> mockClient);
    }

    @SuppressWarnings("unchecked")
    private Consumer<byte[]> emptyBatchConsumer() throws Exception {
        Consumer<byte[]> consumer = mock(Consumer.class);
        Messages<byte[]> empty = mock(Messages.class);
        when(empty.size()).thenReturn(0);
        when(empty.iterator()).thenReturn(java.util.Collections.emptyIterator());
        when(consumer.batchReceive()).thenReturn(empty);
        return consumer;
    }

    @SuppressWarnings("unchecked")
    private Consumer<byte[]> finiteBatchConsumer(List<Messages<byte[]>> batches) throws Exception {
        Consumer<byte[]> consumer = mock(Consumer.class);
        AtomicInteger idx = new AtomicInteger(0);

        Messages<byte[]> empty = mock(Messages.class);
        when(empty.size()).thenReturn(0);
        when(empty.iterator()).thenReturn(java.util.Collections.emptyIterator());

        when(consumer.batchReceive()).thenAnswer(inv -> {
            int i = idx.getAndIncrement();
            if (i < batches.size()) {
                return batches.get(i);
            }
            Thread.sleep(50);
            return empty;
        });

        return consumer;
    }

    @SuppressWarnings("unchecked")
    private Messages<byte[]> createMessages(String... jsonValues) {
        List<Message<byte[]>> msgList = new ArrayList<>();
        for (String json : jsonValues) {
            Message<byte[]> msg = mock(Message.class);
            when(msg.getData()).thenReturn(json.getBytes());
            when(msg.getTopicName()).thenReturn("test-topic");
            when(msg.getMessageId()).thenReturn(MessageId.latest);
            msgList.add(msg);
        }

        Messages<byte[]> messages = mock(Messages.class);
        when(messages.size()).thenReturn(msgList.size());
        when(messages.iterator()).thenReturn(msgList.iterator());
        return messages;
    }

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
