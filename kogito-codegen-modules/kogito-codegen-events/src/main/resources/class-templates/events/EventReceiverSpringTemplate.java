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
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.event.CloudEventUnmarshallerFactory;
import org.kie.kogito.event.Converter;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.EventUnmarshaller;
import org.kie.kogito.event.Subscription;
import org.kie.kogito.event.impl.CloudEventConverter;
import org.kie.kogito.event.impl.DataEventConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.kie.kogito.addon.cloudevents.spring.KogitoMessaging;

import jakarta.annotation.PostConstruct;

@Component("Receiver-$ChannelName$")
public class $ClassName$ implements EventReceiver {

    private static final Logger log = LoggerFactory.getLogger($ClassName$.class);
    private Collection<Subscription<DataEvent<?>, $Type$>> consumers;

    @PostConstruct
    private void initialize() {
        consumers = new CopyOnWriteArrayList<>();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> void subscribe(Consumer<DataEvent<T>> consumer, Class<T> clazz) {
        Subscription subscription = new Subscription<>(consumer, toTopicType(clazz));
        consumers.add(subscription);
    }

    private <T> Converter<$Type$, DataEvent<T>> toTopicTypeCloud(Class<T> clazz) {
        return new CloudEventConverter<>(clazz, ceUnmarshaller);
    }

    private <T> Converter<$Type$, DataEvent<T>> toTopicTypeEvent(Class<T> clazz) {
        return new DataEventConverter<>(clazz, eventDataUnmarshaller);
    }

    @KafkaListener(topics = { "$Topic$" })
    public void receive(ConsumerRecord<String, $Type$> message, Acknowledgment ack) throws InterruptedException {
        log.debug("Receive message with key {} for topic {}", message.key(), message.topic());
        for (Subscription<DataEvent<?>, $Type$> subscription : consumers) {
            try {
                DataEvent<?> object = subscription.getConverter().convert(message.value());
                subscription.getConsumer().accept(object);
                ack.acknowledge();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

        }
    }

}
