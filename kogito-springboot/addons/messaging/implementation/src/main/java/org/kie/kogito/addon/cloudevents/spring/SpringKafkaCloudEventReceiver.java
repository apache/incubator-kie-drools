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
package org.kie.kogito.addon.cloudevents.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.event.CloudEventUnmarshallerFactory;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.EventUnmarshaller;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.event.Subscription;
import org.kie.kogito.event.impl.CloudEventConverter;
import org.kie.kogito.event.impl.DataEventConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class SpringKafkaCloudEventReceiver implements EventReceiver {

    private static final Logger log = LoggerFactory.getLogger(SpringKafkaCloudEventReceiver.class);
    private Collection<Subscription<Object, String>> consumers;

    @Autowired
    EventUnmarshaller<Object> eventDataUnmarshaller;

    @Autowired
    CloudEventUnmarshallerFactory<Object> cloudEventUnmarshaller;

    @Autowired
    ConfigBean configBean;

    @PostConstruct
    private void init() {
        consumers = new CopyOnWriteArrayList<>();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> void subscribe(Function<DataEvent<T>, CompletionStage<?>> consumer, Class<T> clazz) {

        consumers.add(
                new Subscription(consumer, configBean.useCloudEvents() ? new CloudEventConverter<>(clazz, cloudEventUnmarshaller)
                        : new DataEventConverter<>(clazz, eventDataUnmarshaller)));
    }

    @KafkaListener(topics = "${kogito.addon.cloudevents.kafka." + KogitoEventStreams.INCOMING + ":" + KogitoEventStreams.INCOMING + "}")
    public void receive(@Payload Collection<String> messages) throws InterruptedException {
        log.debug("Received {} events", messages.size());
        Collection<CompletionStage<?>> futures = new ArrayList<>();
        for (String message : messages) {
            for (Subscription<Object, String> consumer : consumers) {
                try {
                    futures.add(consumer.getConsumer().apply(consumer.getConverter().convert(message)));
                } catch (IOException e) {
                    log.info("Cannot convert event to the proper type {}", e.getMessage());
                }
            }
        }
        // wait for this batch to complete
        log.debug("Waiting for all operations in batch to complete");
        for (CompletionStage<?> future : futures) {
            try {
                future.toCompletableFuture().get();
            } catch (ExecutionException ex) {
                log.error("Error executing consumer", ex.getCause());
            }
        }
        log.debug("All operations in batch completed");
    }
}
