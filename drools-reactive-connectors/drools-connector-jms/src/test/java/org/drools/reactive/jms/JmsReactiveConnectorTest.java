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

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;

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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JmsReactiveConnectorTest {

    private JmsReactiveConnector<TestEvent> connector;

    @AfterEach
    void tearDown() {
        if (connector != null && connector.getState() != ConnectorState.STOPPED) {
            connector.close();
        }
    }

    @Test
    void shouldTransitionThroughLifecycleStates() throws Exception {
        connector = createQueueConnector(nullReceiveConsumer(), FiringStrategy.EXTERNAL);

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
    void shouldDeserializeTextMessagesAndAppendFacts() throws Exception {
        MessageConsumer jmsConsumer = finiteTextConsumer(
                "{\"name\":\"event1\",\"value\":42}",
                "{\"name\":\"event2\",\"value\":99}");

        connector = createQueueConnector(jmsConsumer, FiringStrategy.EXTERNAL);
        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        connector.start(stream);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(2));

        assertThat(stream.getCollected().get(0).getName()).isEqualTo("event1");
        assertThat(stream.getCollected().get(0).getValue()).isEqualTo(42);
        assertThat(stream.getCollected().get(1).getName()).isEqualTo("event2");
    }

    @Test
    void shouldFireRulesPerMessage() throws Exception {
        MessageConsumer jmsConsumer = finiteTextConsumer(
                "{\"name\":\"e1\",\"value\":1}",
                "{\"name\":\"e2\",\"value\":2}");

        connector = createQueueConnector(jmsConsumer, FiringStrategy.PER_MESSAGE);
        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        RuleUnitInstance<?> mockRuleUnit = mock(RuleUnitInstance.class);

        connector.start(stream, mockRuleUnit);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(2));

        verify(mockRuleUnit, atLeastOnce()).fire();
    }

    @Test
    void shouldNotFireWhenExternal() throws Exception {
        MessageConsumer jmsConsumer = finiteTextConsumer("{\"name\":\"e1\",\"value\":1}");

        connector = createQueueConnector(jmsConsumer, FiringStrategy.EXTERNAL);
        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        RuleUnitInstance<?> mockRuleUnit = mock(RuleUnitInstance.class);

        connector.start(stream, mockRuleUnit);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(1));

        verify(mockRuleUnit, never()).fire();
    }

    @Test
    void shouldTrackHealthMetrics() throws Exception {
        MessageConsumer jmsConsumer = finiteTextConsumer(
                "{\"name\":\"e1\",\"value\":1}",
                "{\"name\":\"e2\",\"value\":2}",
                "{\"name\":\"e3\",\"value\":3}");

        connector = createQueueConnector(jmsConsumer, FiringStrategy.EXTERNAL);
        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        connector.start(stream);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    ConnectorHealth health = connector.health();
                    assertThat(health.getMessagesReceived()).isEqualTo(3);
                    assertThat(health.getMessagesProcessed()).isEqualTo(3);
                    assertThat(health.getMessagesFailed()).isZero();
                });
    }

    @Test
    void shouldSupportTopicDestination() throws Exception {
        MessageConsumer jmsConsumer = finiteTextConsumer("{\"name\":\"e1\",\"value\":1}");

        JmsConnectorConfig config = JmsConnectorConfig.builder()
                .destinationName("events.topic")
                .destinationType(JmsDestinationType.TOPIC)
                .firingStrategy(FiringStrategy.EXTERNAL)
                .pollTimeout(Duration.ofMillis(100))
                .build();

        ConnectionFactory factory = mockConnectionFactory(jmsConsumer, config);
        FactDeserializer<TestEvent> deser = createDeserializer();

        connector = new JmsReactiveConnector<>(config, deser, factory);
        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        connector.start(stream);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(1));
    }

    @Test
    void shouldProcessMicroBatch() throws Exception {
        MessageConsumer jmsConsumer = finiteTextConsumer(
                "{\"name\":\"e1\",\"value\":1}",
                "{\"name\":\"e2\",\"value\":2}");

        connector = createQueueConnector(jmsConsumer, FiringStrategy.MICRO_BATCH);
        CollectingDataStream<TestEvent> stream = new CollectingDataStream<>();
        RuleUnitInstance<?> mockRuleUnit = mock(RuleUnitInstance.class);

        connector.start(stream, mockRuleUnit);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(stream.getCollected()).hasSize(2));

        verify(mockRuleUnit, atLeastOnce()).fire();
    }

    // --- Helpers ---

    private JmsReactiveConnector<TestEvent> createQueueConnector(
            MessageConsumer jmsConsumer, FiringStrategy strategy) throws Exception {
        JmsConnectorConfig config = JmsConnectorConfig.builder()
                .destinationName("test.queue")
                .destinationType(JmsDestinationType.QUEUE)
                .firingStrategy(strategy)
                .pollTimeout(Duration.ofMillis(100))
                .build();

        ConnectionFactory factory = mockConnectionFactory(jmsConsumer, config);
        FactDeserializer<TestEvent> deser = createDeserializer();
        return new JmsReactiveConnector<>(config, deser, factory);
    }

    private ConnectionFactory mockConnectionFactory(
            MessageConsumer jmsConsumer, JmsConnectorConfig config) throws Exception {
        ConnectionFactory factory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);

        when(factory.createConnection()).thenReturn(connection);
        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);

        if (config.getDestinationType() == JmsDestinationType.TOPIC) {
            Topic topic = mock(Topic.class);
            when(session.createTopic(anyString())).thenReturn(topic);
            when(session.createConsumer(any(), isNull())).thenReturn(jmsConsumer);
        } else {
            Queue queue = mock(Queue.class);
            when(session.createQueue(anyString())).thenReturn(queue);
            when(session.createConsumer(any(), isNull())).thenReturn(jmsConsumer);
        }

        return factory;
    }

    private MessageConsumer nullReceiveConsumer() throws Exception {
        MessageConsumer consumer = mock(MessageConsumer.class);
        when(consumer.receive(anyLong())).thenReturn(null);
        return consumer;
    }

    private MessageConsumer finiteTextConsumer(String... jsonValues) throws Exception {
        MessageConsumer consumer = mock(MessageConsumer.class);
        AtomicInteger idx = new AtomicInteger(0);

        when(consumer.receive(anyLong())).thenAnswer(inv -> {
            int i = idx.getAndIncrement();
            if (i < jsonValues.length) {
                TextMessage msg = mock(TextMessage.class);
                when(msg.getText()).thenReturn(jsonValues[i]);
                when(msg.getJMSMessageID()).thenReturn("msg-" + i);
                return msg;
            }
            Thread.sleep(50);
            return null;
        });

        return consumer;
    }

    private FactDeserializer<TestEvent> createDeserializer() {
        com.fasterxml.jackson.databind.ObjectMapper mapper =
                new com.fasterxml.jackson.databind.ObjectMapper();
        return (topic, data) -> {
            if (data == null || data.length == 0) return null;
            try {
                return mapper.readValue(data, TestEvent.class);
            } catch (Exception e) {
                throw new org.drools.reactive.api.ConnectorException("deser fail", e);
            }
        };
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
