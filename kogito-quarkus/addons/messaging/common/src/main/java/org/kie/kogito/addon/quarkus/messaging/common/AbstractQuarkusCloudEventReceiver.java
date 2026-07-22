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
package org.kie.kogito.addon.quarkus.messaging.common;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

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

    protected EventUnmarshaller<I> getEventUnmarshaller() {
        return null;
    }

    protected CloudEventUnmarshallerFactory<I> getCloudEventUnmarshallerFactory() {
        return null;
    }

    protected void produce(Message<I> message) {
        LOGGER.trace("Received message {}", message.getPayload());
        for (Subscription<DataEvent<?>, Message<I>> subscription : consumers) {
            try {
                DataEvent<?> object = subscription.getConverter().convert(message);
                subscription.getConsumer().accept(object);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> void subscribe(Consumer<DataEvent<T>> consumer, Class<T> objectClass) {
        Subscription subscription = new Subscription<>(consumer, getConverter(objectClass));
        consumers.add(subscription);
    }

    private <T> Converter<Message<I>, DataEvent<T>> getConverter(Class<T> objectClass) {
        if (getCloudEventUnmarshallerFactory() != null) {
            return new QuarkusCloudEventConverter<>(getCloudEventUnmarshallerFactory().unmarshaller(objectClass));
        } else {
            return new QuarkusDataEventConverter<>(objectClass, getEventUnmarshaller());
        }
    }
}
