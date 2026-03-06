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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionType;

import org.drools.reactive.api.AbstractReactiveConnector;
import org.drools.reactive.api.ConnectorException;
import org.drools.reactive.api.ConnectorState;
import org.drools.reactive.api.FactDeserializer;
import org.drools.reactive.api.FiringStrategy;

/**
 * A {@link org.drools.reactive.api.ReactiveConnector} that consumes messages
 * from Apache Pulsar topics and feeds deserialized facts into a Drools
 * {@link org.drools.ruleunits.api.DataStream}.
 *
 * <p>The connector creates a Pulsar {@link Consumer} that receives raw bytes,
 * passes them through the configured {@link FactDeserializer}, and appends the
 * resulting facts to the target DataStream.
 *
 * @param <T> the fact type produced by this connector
 */
public class PulsarReactiveConnector<T> extends AbstractReactiveConnector<T> {

    private final PulsarConnectorConfig pulsarConfig;
    private final PulsarClientFactory clientFactory;

    private PulsarClient client;
    private Consumer<byte[]> consumer;
    private Thread pollingThread;
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    public PulsarReactiveConnector(PulsarConnectorConfig config, FactDeserializer<T> deserializer) {
        this(config, deserializer, PulsarReactiveConnector::createDefaultClient);
    }

    /**
     * Constructor accepting a factory for the Pulsar client, enabling
     * injection of mock clients in tests.
     */
    PulsarReactiveConnector(PulsarConnectorConfig config,
                            FactDeserializer<T> deserializer,
                            PulsarClientFactory clientFactory) {
        super(config, deserializer);
        this.pulsarConfig = config;
        this.clientFactory = clientFactory;
    }

    @Override
    protected void doStart() {
        try {
            client = clientFactory.create(pulsarConfig.getServiceUrl());
            consumer = client.newConsumer(Schema.BYTES)
                    .topics(pulsarConfig.getTopics())
                    .subscriptionName(pulsarConfig.getSubscriptionName())
                    .subscriptionType(mapSubscriptionType(pulsarConfig.getSubscriptionType()))
                    .subscribe();
        } catch (PulsarClientException e) {
            throw new ConnectorException("Failed to create Pulsar consumer", e);
        }

        pollingThread = new Thread(this::pollLoop,
                "drools-pulsar-connector-" + pulsarConfig.getSubscriptionName());
        pollingThread.setDaemon(true);
        pollingThread.start();

        logger.info("Pulsar connector started: topics={}, subscription={}, type={}",
                pulsarConfig.getTopics(), pulsarConfig.getSubscriptionName(),
                pulsarConfig.getSubscriptionType());
    }

    @Override
    protected void doPause() {
        paused.set(true);
        try {
            consumer.pause();
        } catch (Exception e) {
            logger.warn("Error pausing Pulsar consumer", e);
        }
    }

    @Override
    protected void doResume() {
        paused.set(false);
        try {
            consumer.resume();
        } catch (Exception e) {
            logger.warn("Error resuming Pulsar consumer", e);
        }
    }

    @Override
    protected void doStop() {
        try {
            if (!shutdownLatch.await(10, TimeUnit.SECONDS)) {
                logger.warn("Pulsar polling thread did not shut down within 10 seconds");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        closeQuietly(consumer, "consumer");
        closeQuietly(client, "client");
    }

    private void pollLoop() {
        try {
            int batchTimeoutMs = (int) pulsarConfig.getPollTimeout().toMillis();
            while (getState() != ConnectorState.STOPPING && getState() != ConnectorState.STOPPED) {
                if (paused.get()) {
                    sleepQuietly(100);
                    continue;
                }

                Messages<byte[]> messages = consumer.batchReceive();
                if (messages.size() == 0) {
                    continue;
                }

                if (pulsarConfig.getFiringStrategy() == FiringStrategy.MICRO_BATCH) {
                    processMicroBatch(messages);
                } else {
                    processPerMessage(messages);
                }

                consumer.acknowledge(messages);
            }
        } catch (Exception e) {
            if (getState() != ConnectorState.STOPPING && getState() != ConnectorState.STOPPED) {
                logger.error("Error in Pulsar polling loop", e);
                recordFailed(e);
            }
        } finally {
            shutdownLatch.countDown();
        }
    }

    private void processPerMessage(Messages<byte[]> messages) {
        for (Message<byte[]> msg : messages) {
            recordReceived();
            try {
                T fact = deserializer.deserialize(msg.getTopicName(), msg.getData());
                if (fact != null) {
                    processSingle(fact);
                    recordProcessed();
                }
            } catch (Exception e) {
                logger.warn("Failed to process Pulsar message from topic={}, messageId={}",
                        msg.getTopicName(), msg.getMessageId(), e);
                recordFailed(e);
            }
        }
    }

    private void processMicroBatch(Messages<byte[]> messages) {
        List<T> batch = new ArrayList<>(messages.size());
        for (Message<byte[]> msg : messages) {
            recordReceived();
            try {
                T fact = deserializer.deserialize(msg.getTopicName(), msg.getData());
                if (fact != null) {
                    batch.add(fact);
                    recordProcessed();
                }
            } catch (Exception e) {
                logger.warn("Failed to deserialize Pulsar message from topic={}, messageId={}",
                        msg.getTopicName(), msg.getMessageId(), e);
                recordFailed(e);
            }
        }
        if (!batch.isEmpty()) {
            processBatch(batch);
        }
    }

    private static SubscriptionType mapSubscriptionType(PulsarSubscriptionType type) {
        switch (type) {
            case SHARED:
                return SubscriptionType.Shared;
            case FAILOVER:
                return SubscriptionType.Failover;
            case KEY_SHARED:
                return SubscriptionType.Key_Shared;
            case EXCLUSIVE:
            default:
                return SubscriptionType.Exclusive;
        }
    }

    private static PulsarClient createDefaultClient(String serviceUrl) {
        try {
            return PulsarClient.builder().serviceUrl(serviceUrl).build();
        } catch (PulsarClientException e) {
            throw new ConnectorException("Failed to create Pulsar client for " + serviceUrl, e);
        }
    }

    private void closeQuietly(AutoCloseable closeable, String name) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.warn("Error closing Pulsar {}", name, e);
            }
        }
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    interface PulsarClientFactory {
        PulsarClient create(String serviceUrl);
    }
}
