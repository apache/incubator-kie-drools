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
package org.kie.kogito.test.quarkus.kafka;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.util.Collections.singleton;

/**
 * Kafka client for Kogito Example tests.
 */
public class KafkaTypedTestClient<T, S extends Serializer<T>, D extends Deserializer<T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTypedTestClient.class);
    private static final int TIMEOUT = 10;

    private ExecutorService executorService;
    private KafkaConsumerLoop<T> consumer;
    private Class<S> serializer;
    private Class<D> deserializer;

    private String hosts;
    private Properties additionalConfig;

    public KafkaTypedTestClient(String hosts, Class<S> serializer, Class<D> deserializer) {
        this.hosts = hosts;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public KafkaTypedTestClient(String hosts, Properties additionalConfig, Class<S> serializer, Class<D> deserializer) {
        this(hosts, serializer, deserializer);
        this.additionalConfig = additionalConfig;
    }

    private Properties createDefaultConfig(String hosts) {
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getName());
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaTypedTestClient.class.getName() + "Consumer");
        return consumerConfig;
    }

    private KafkaConsumer<String, T> createDefaultConsumer(String hosts) {
        Properties resultConfig = createDefaultConfig(hosts);
        if (additionalConfig != null) {
            resultConfig.putAll(additionalConfig);
        }
        return new KafkaConsumer<>(resultConfig);
    }

    private KafkaProducer<String, T> createDefaultProducer(String hosts) {
        Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaTypedTestClient.class.getName() + "Producer");
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer.getName());
        return new KafkaProducer<>(producerConfig);
    }

    public void consume(Set<String> topics, Consumer<T> callback) {
        if (consumer != null) {
            shutdown();
        }

        executorService = Executors.newSingleThreadExecutor();

        CountDownLatch awaitSubscribe = new CountDownLatch(1);
        consumer = new KafkaConsumerLoop<>(createDefaultConsumer(hosts), topics, callback, v -> {
            awaitSubscribe.countDown();
            return null;
        });

        executorService.execute(consumer);
        try {
            if (!awaitSubscribe.await(TIMEOUT, TimeUnit.SECONDS)) {
                throw new IllegalStateException(format("Timeout while waiting for KafkaTestClient to subscribe to topics: %s", topics));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void consume(String topic, Consumer<T> callback) {
        consume(singleton(topic), callback);
    }

    public void produce(T data, String topic) {
        try (KafkaProducer<String, T> producer = createDefaultProducer(hosts)) {
            LOGGER.info("Publishing event with data {} for topic {}", data, topic);
            ProducerRecord<String, T> record = new ProducerRecord<>(topic, data);
            waitForCompletion(producer.send(record));
        }
    }

    public void shutdown() {
        if (consumer != null) {
            consumer.shutdown();
            consumer = null;
        }
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                executorService.shutdownNow();
            }
            executorService = null;
        }
    }

    public void waitForCompletion(Future<RecordMetadata> future) {
        try {
            future.get(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new KafkaException(e.getCause());
            }
        }
    }

}
