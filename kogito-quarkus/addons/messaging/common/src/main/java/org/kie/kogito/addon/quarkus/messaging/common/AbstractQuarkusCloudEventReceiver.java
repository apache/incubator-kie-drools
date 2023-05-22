/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addon.quarkus.messaging.common;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.CloudEventUnmarshallerFactory;
import org.kie.kogito.event.Converter;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.EventUnmarshaller;
import org.kie.kogito.event.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractQuarkusCloudEventReceiver<I> implements EventReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQuarkusCloudEventReceiver.class);

    private Collection<Subscription<DataEvent<?>, Message<I>>> consumers = new CopyOnWriteArrayList<>();

    private EventUnmarshaller<I> eventDataUnmarshaller;

    private CloudEventUnmarshallerFactory<I> cloudEventUnmarshaller;

    protected void setEventDataUnmarshaller(EventUnmarshaller<I> eventDataUnmarshaller) {
        this.eventDataUnmarshaller = eventDataUnmarshaller;
    }

    protected void setCloudEventUnmarshaller(CloudEventUnmarshallerFactory<I> cloudEventUnmarshaller) {
        this.cloudEventUnmarshaller = cloudEventUnmarshaller;
    }

    protected CompletionStage<?> produce(final Message<I> message) {
        LOGGER.debug("Received message {}", message);
        return produce(message, (v, e) -> {
            LOGGER.debug("Acking message {}", message);
            message.ack();
            if (e != null) {
                LOGGER.error("Error processing message {}", message.getPayload(), e);
            }
        });
    }

    private CompletionStage<?> produce(final Message<I> message, BiConsumer<Object, Throwable> callback) {
        CompletionStage<?> result = CompletableFuture.completedFuture(null);
        CompletionStage<?> future = result;
        for (Subscription<DataEvent<?>, Message<I>> subscription : consumers) {
            try {
                DataEvent<?> object = subscription.getConverter().convert(message);
                future = future.thenCompose(f -> subscription.getConsumer().apply(object));
            } catch (IOException e) {
                LOGGER.info("Error converting event. Exception message is {}", e.getMessage());
            }
        }
        if (callback != null) {
            future.whenComplete(callback);
        }
        return result;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> void subscribe(Function<DataEvent<T>, CompletionStage<?>> consumer, Class<T> objectClass) {
        Subscription subscription = new Subscription<>(consumer, getConverter(objectClass));
        consumers.add(subscription);
    }

    private <T> Converter<Message<I>, DataEvent<T>> getConverter(Class<T> objectClass) {
        if (cloudEventUnmarshaller != null) {
            return new QuarkusCloudEventConverter<>(cloudEventUnmarshaller.unmarshaller(objectClass));
        } else if (eventDataUnmarshaller != null) {
            return new QuarkusDataEventConverter<>(objectClass, eventDataUnmarshaller);
        } else {
            throw new IllegalStateException("No unmarshaller set for receiver " + this);
        }
    }
}
