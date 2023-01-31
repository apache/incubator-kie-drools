/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.test.quarkus.kafka;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumerLoop<T> implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerLoop.class);

    private final KafkaConsumer<String, T> consumer;
    private final Collection<String> topics;
    private final Consumer<T> callback;
    private final UnaryOperator<Void> onSubscribe;
    private final CountDownLatch shutdownLatch;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public KafkaConsumerLoop(KafkaConsumer<String, T> consumer, Collection<String> topics, Consumer<T> callback, UnaryOperator<Void> onSubscribe) {
        this.consumer = consumer;
        this.topics = topics;
        this.callback = callback;
        this.onSubscribe = onSubscribe;
        this.shutdownLatch = new CountDownLatch(1);
    }

    private boolean doCommitSync() {
        try {
            consumer.commitSync();
            return true;
        } catch (CommitFailedException e) {
            LOGGER.debug("Kafka commit failed", e);
            return false;
        }
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(topics, new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    LOGGER.debug("Kafka consumer partitions revoked: {}", partitions);
                    doCommitSync();
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    LOGGER.debug("Kafka consumer partitions assigned: {}", partitions);
                }
            });
            LOGGER.debug("Kafka consumer subscribed to topic(s): {}", topics);
            onSubscribe.apply(null);

            while (running.get()) {
                ConsumerRecords<String, T> records = consumer.poll(Duration.ofSeconds(1));
                LOGGER.debug("Kafka consumer received records: {}", records);
                if (doCommitSync()) {
                    records.forEach(record -> callback.accept(record.value()));
                } else {
                    LOGGER.warn("Kafka records ignored: {}", records);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
        } finally {
            try {
                consumer.close();
                shutdownLatch.countDown();
                LOGGER.debug("Kafka consumer closed");
            } catch (Exception ex) {
                LOGGER.error("Error while closing Kafka consumer", ex);
            }
        }
    }

    public void shutdown() {
        running.set(false);
        try {
            shutdownLatch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
