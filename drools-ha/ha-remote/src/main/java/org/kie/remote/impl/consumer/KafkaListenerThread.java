/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.remote.impl.consumer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.kie.remote.CommonConfig;
import org.kie.remote.TopicsConfig;
import org.kie.remote.message.ResultMessage;
import org.kie.remote.util.KafkaRemoteUtil;
import org.kie.remote.util.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaListenerThread implements ListenerThread {

    private static Logger logger = LoggerFactory.getLogger(KafkaListenerThread.class);
    private TopicsConfig topicsConfig;
    private Map<String, CompletableFuture<Object>> requestsStore;
    private KafkaConsumer consumer;

    private volatile boolean running = true;

    public KafkaListenerThread(Properties configuration, TopicsConfig config) {
        this.topicsConfig = config;
        consumer = KafkaRemoteUtil.getConsumer(topicsConfig.getKieSessionInfosTopicName(), configuration);
    }

    public void init(Map<String, CompletableFuture<Object>> requestsStore) {
        this.requestsStore = requestsStore;
    }

    @Override
    public void run() {
        if (requestsStore == null) {
            throw new IllegalStateException("Request store not initialized, init method must be called before run the thread");
        }
        try {
            while (running) {
                ConsumerRecords records = consumer.poll(Duration.of(CommonConfig.DEFAULT_POLL_TIMEOUT_MS,
                                                                    ChronoUnit.MILLIS));
                Iterator<ConsumerRecord<String, byte[]>> iterator = records.iterator();
                while (iterator.hasNext()) {
                    ConsumerRecord<String, byte[]> record = iterator.next();
                    Object msg = SerializationUtil.deserialize(record.value());
                    if (msg instanceof ResultMessage) {
                        complete(requestsStore,
                                 (ResultMessage) msg,
                                 logger);
                    } else if (msg != null) {
                        throw new IllegalStateException("Wrong type of response message: found " +
                                                                msg.getClass().getCanonicalName() +
                                                                " instead of " +
                                                                ResultMessage.class.getCanonicalName());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            consumer.close();
        }
    }

    public void complete(Map<String, CompletableFuture<Object>> requestsStore, ResultMessage message, Logger logger) {
        CompletableFuture<Object> completableFuture = requestsStore.get(message.getId());
        if (completableFuture != null) {
            completableFuture.complete(message.getResult());
            if (logger.isDebugEnabled()) {
                logger.debug("completed msg with key {}", message.getId());
            }
        }
    }

    @Override
    public void stop() {
        running = false;
    }
}
