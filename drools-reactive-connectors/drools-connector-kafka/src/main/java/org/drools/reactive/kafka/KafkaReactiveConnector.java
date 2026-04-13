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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.drools.reactive.api.AbstractReactiveConnector;
import org.drools.reactive.api.ConnectorState;
import org.drools.reactive.api.FactDeserializer;
import org.drools.reactive.api.FiringStrategy;

/**
 * A {@link org.drools.reactive.api.ReactiveConnector} that consumes messages
 * from Apache Kafka topics and feeds deserialized facts into a Drools
 * {@link org.drools.ruleunits.api.DataStream}.
 *
 * <p>The connector runs a single polling thread that:
 * <ol>
 *   <li>Polls Kafka for new records</li>
 *   <li>Deserializes each record's value via the configured {@link FactDeserializer}</li>
 *   <li>Appends facts to the target DataStream</li>
 *   <li>Fires rules according to the {@link FiringStrategy}</li>
 *   <li>Commits offsets (unless auto-commit is enabled)</li>
 * </ol>
 *
 * @param <T> the fact type produced by this connector
 */
public class KafkaReactiveConnector<T> extends AbstractReactiveConnector<T> {

    private final KafkaConnectorConfig kafkaConfig;
    private final Function<Map<String, Object>, KafkaConsumer<String, byte[]>> consumerFactory;

    private KafkaConsumer<String, byte[]> consumer;
    private Thread pollingThread;
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    public KafkaReactiveConnector(KafkaConnectorConfig config, FactDeserializer<T> deserializer) {
        this(config, deserializer, KafkaConsumer::new);
    }

    /**
     * Constructor accepting a factory for the Kafka consumer, enabling
     * injection of mock consumers in tests.
     */
    KafkaReactiveConnector(KafkaConnectorConfig config,
                           FactDeserializer<T> deserializer,
                           Function<Map<String, Object>, KafkaConsumer<String, byte[]>> consumerFactory) {
        super(config, deserializer);
        this.kafkaConfig = config;
        this.consumerFactory = consumerFactory;
    }

    @Override
    protected void doStart() {
        Map<String, Object> props = buildConsumerProperties();
        consumer = consumerFactory.apply(props);
        consumer.subscribe(kafkaConfig.getTopics());

        pollingThread = new Thread(this::pollLoop, "drools-kafka-connector-" + kafkaConfig.getGroupId());
        pollingThread.setDaemon(true);
        pollingThread.start();

        logger.info("Kafka connector started: topics={}, groupId={}, firingStrategy={}",
                kafkaConfig.getTopics(), kafkaConfig.getGroupId(), kafkaConfig.getFiringStrategy());
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
            consumer.wakeup();
        } catch (Exception e) {
            logger.debug("Error waking up Kafka consumer during shutdown", e);
        }
        try {
            if (!shutdownLatch.await(10, TimeUnit.SECONDS)) {
                logger.warn("Kafka polling thread did not shut down within 10 seconds");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void pollLoop() {
        try {
            Duration pollTimeout = kafkaConfig.getPollTimeout();
            while (getState() != ConnectorState.STOPPING && getState() != ConnectorState.STOPPED) {
                if (paused.get()) {
                    sleepQuietly(100);
                    continue;
                }

                ConsumerRecords<String, byte[]> records = consumer.poll(pollTimeout);
                if (records.isEmpty()) {
                    continue;
                }

                if (kafkaConfig.getFiringStrategy() == FiringStrategy.MICRO_BATCH) {
                    processMicroBatch(records);
                } else {
                    processPerMessage(records);
                }

                if (!kafkaConfig.isAutoCommit()) {
                    consumer.commitSync();
                }
            }
        } catch (WakeupException e) {
            if (getState() != ConnectorState.STOPPING && getState() != ConnectorState.STOPPED) {
                logger.error("Unexpected wakeup in Kafka polling loop", e);
                recordFailed(e);
            }
        } catch (Exception e) {
            logger.error("Error in Kafka polling loop", e);
            recordFailed(e);
        } finally {
            try {
                consumer.close();
            } catch (Exception e) {
                logger.warn("Error closing Kafka consumer", e);
            }
            shutdownLatch.countDown();
        }
    }

    private void processPerMessage(ConsumerRecords<String, byte[]> records) {
        for (ConsumerRecord<String, byte[]> record : records) {
            recordReceived();
            try {
                T fact = deserializer.deserialize(record.topic(), record.value());
                if (fact != null) {
                    processSingle(fact);
                    recordProcessed();
                }
            } catch (Exception e) {
                logger.warn("Failed to process record from topic={}, partition={}, offset={}",
                        record.topic(), record.partition(), record.offset(), e);
                recordFailed(e);
            }
        }
    }

    private void processMicroBatch(ConsumerRecords<String, byte[]> records) {
        List<T> batch = new ArrayList<>(records.count());
        for (ConsumerRecord<String, byte[]> record : records) {
            recordReceived();
            try {
                T fact = deserializer.deserialize(record.topic(), record.value());
                if (fact != null) {
                    batch.add(fact);
                    recordProcessed();
                }
            } catch (Exception e) {
                logger.warn("Failed to deserialize record from topic={}, partition={}, offset={}",
                        record.topic(), record.partition(), record.offset(), e);
                recordFailed(e);
            }
        }
        if (!batch.isEmpty()) {
            processBatch(batch);
        }
    }

    private Map<String, Object> buildConsumerProperties() {
        Map<String, Object> props = new HashMap<>(kafkaConfig.getProperties());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(kafkaConfig.isAutoCommit()));
        if (!props.containsKey(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG)) {
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        }
        return props;
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
