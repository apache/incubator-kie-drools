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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.jms.BytesMessage;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;

import org.drools.reactive.api.AbstractReactiveConnector;
import org.drools.reactive.api.ConnectorException;
import org.drools.reactive.api.ConnectorState;
import org.drools.reactive.api.FactDeserializer;
import org.drools.reactive.api.FiringStrategy;

/**
 * A {@link org.drools.reactive.api.ReactiveConnector} that consumes messages
 * from JMS queues or topics and feeds deserialized facts into a Drools
 * {@link org.drools.ruleunits.api.DataStream}.
 *
 * <p>Works with any JMS-compliant broker: ActiveMQ, IBM MQ, RabbitMQ (via JMS),
 * Artemis, etc. The {@link ConnectionFactory} is injected at construction time
 * (typically via CDI or JNDI).
 *
 * <p>Supports:
 * <ul>
 *   <li>Queue (point-to-point) and Topic (pub/sub) destinations</li>
 *   <li>JMS message selectors for filtering</li>
 *   <li>Durable topic subscriptions</li>
 *   <li>Transacted sessions with commit-after-fire</li>
 *   <li>Both TextMessage and BytesMessage payloads</li>
 * </ul>
 *
 * @param <T> the fact type produced by this connector
 */
public class JmsReactiveConnector<T> extends AbstractReactiveConnector<T> {

    private final JmsConnectorConfig jmsConfig;
    private final ConnectionFactory connectionFactory;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    private Thread pollingThread;
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    public JmsReactiveConnector(JmsConnectorConfig config,
                                FactDeserializer<T> deserializer,
                                ConnectionFactory connectionFactory) {
        super(config, deserializer);
        this.jmsConfig = config;
        this.connectionFactory = connectionFactory;
    }

    @Override
    protected void doStart() {
        try {
            connection = connectionFactory.createConnection();
            if (jmsConfig.getClientId() != null) {
                connection.setClientID(jmsConfig.getClientId());
            }

            int ackMode = jmsConfig.isSessionTransacted()
                    ? Session.SESSION_TRANSACTED
                    : Session.AUTO_ACKNOWLEDGE;
            session = connection.createSession(jmsConfig.isSessionTransacted(), ackMode);

            Destination destination = createDestination(session);
            consumer = createConsumer(session, destination);

            connection.start();
        } catch (JMSException e) {
            closeQuietly();
            throw new ConnectorException("Failed to start JMS connector for destination '"
                    + jmsConfig.getDestinationName() + "'", e);
        }

        pollingThread = new Thread(this::pollLoop,
                "drools-jms-connector-" + jmsConfig.getDestinationName());
        pollingThread.setDaemon(true);
        pollingThread.start();

        logger.info("JMS connector started: destination={}, type={}, selector={}",
                jmsConfig.getDestinationName(), jmsConfig.getDestinationType(),
                jmsConfig.getMessageSelector());
    }

    @Override
    protected void doPause() {
        paused.set(true);
    }

    @Override
    protected void doResume() {
        paused.set(false);
    }

    @Override
    protected void doStop() {
        try {
            if (!shutdownLatch.await(10, TimeUnit.SECONDS)) {
                logger.warn("JMS polling thread did not shut down within 10 seconds");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        closeQuietly();
    }

    private void pollLoop() {
        try {
            long receiveTimeoutMs = jmsConfig.getPollTimeout().toMillis();
            while (getState() != ConnectorState.STOPPING && getState() != ConnectorState.STOPPED) {
                if (paused.get()) {
                    sleepQuietly(100);
                    continue;
                }

                if (jmsConfig.getFiringStrategy() == FiringStrategy.MICRO_BATCH) {
                    pollMicroBatch(receiveTimeoutMs);
                } else {
                    pollSingle(receiveTimeoutMs);
                }
            }
        } catch (Exception e) {
            if (getState() != ConnectorState.STOPPING && getState() != ConnectorState.STOPPED) {
                logger.error("Error in JMS polling loop", e);
                recordFailed(e);
            }
        } finally {
            shutdownLatch.countDown();
        }
    }

    private void pollSingle(long receiveTimeoutMs) throws JMSException {
        Message message = consumer.receive(receiveTimeoutMs);
        if (message == null) {
            return;
        }
        recordReceived();
        try {
            byte[] payload = extractPayload(message);
            T fact = deserializer.deserialize(jmsConfig.getDestinationName(), payload);
            if (fact != null) {
                processSingle(fact);
                recordProcessed();
            }
            commitIfTransacted();
        } catch (Exception e) {
            logger.warn("Failed to process JMS message: id={}", safeMessageId(message), e);
            recordFailed(e);
            rollbackIfTransacted();
        }
    }

    private void pollMicroBatch(long receiveTimeoutMs) throws JMSException {
        List<T> batch = new ArrayList<>();
        int batchSize = jmsConfig.getBatchSize();

        for (int i = 0; i < batchSize; i++) {
            long timeout = (i == 0) ? receiveTimeoutMs : 0;
            Message message = consumer.receive(timeout);
            if (message == null) {
                break;
            }
            recordReceived();
            try {
                byte[] payload = extractPayload(message);
                T fact = deserializer.deserialize(jmsConfig.getDestinationName(), payload);
                if (fact != null) {
                    batch.add(fact);
                    recordProcessed();
                }
            } catch (Exception e) {
                logger.warn("Failed to deserialize JMS message: id={}", safeMessageId(message), e);
                recordFailed(e);
            }
        }
        if (!batch.isEmpty()) {
            processBatch(batch);
            commitIfTransacted();
        }
    }

    private byte[] extractPayload(Message message) throws JMSException {
        if (message instanceof TextMessage) {
            String text = ((TextMessage) message).getText();
            return text != null ? text.getBytes(java.nio.charset.StandardCharsets.UTF_8) : new byte[0];
        } else if (message instanceof BytesMessage) {
            BytesMessage bytesMsg = (BytesMessage) message;
            byte[] data = new byte[(int) bytesMsg.getBodyLength()];
            bytesMsg.readBytes(data);
            return data;
        } else {
            throw new ConnectorException("Unsupported JMS message type: " + message.getClass().getName()
                    + ". Only TextMessage and BytesMessage are supported.");
        }
    }

    private Destination createDestination(Session session) throws JMSException {
        if (jmsConfig.getDestinationType() == JmsDestinationType.TOPIC) {
            return session.createTopic(jmsConfig.getDestinationName());
        }
        return session.createQueue(jmsConfig.getDestinationName());
    }

    private MessageConsumer createConsumer(Session session, Destination destination) throws JMSException {
        if (jmsConfig.getDurableSubscriptionName() != null && destination instanceof Topic) {
            return session.createDurableSubscriber(
                    (Topic) destination,
                    jmsConfig.getDurableSubscriptionName(),
                    jmsConfig.getMessageSelector(),
                    false);
        }
        return session.createConsumer(destination, jmsConfig.getMessageSelector());
    }

    private void commitIfTransacted() {
        if (jmsConfig.isSessionTransacted() && session != null) {
            try {
                session.commit();
            } catch (JMSException e) {
                logger.warn("Failed to commit JMS session", e);
            }
        }
    }

    private void rollbackIfTransacted() {
        if (jmsConfig.isSessionTransacted() && session != null) {
            try {
                session.rollback();
            } catch (JMSException e) {
                logger.warn("Failed to rollback JMS session", e);
            }
        }
    }

    private void closeQuietly() {
        try {
            if (consumer != null) consumer.close();
        } catch (JMSException e) {
            logger.debug("Error closing JMS consumer", e);
        }
        try {
            if (session != null) session.close();
        } catch (JMSException e) {
            logger.debug("Error closing JMS session", e);
        }
        try {
            if (connection != null) connection.close();
        } catch (JMSException e) {
            logger.debug("Error closing JMS connection", e);
        }
    }

    private static String safeMessageId(Message message) {
        try {
            return message.getJMSMessageID();
        } catch (JMSException e) {
            return "<unknown>";
        }
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
