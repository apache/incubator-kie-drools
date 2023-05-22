/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.impl;

import java.util.Collection;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventEmitterFactory;
import org.kie.kogito.event.EventFactory;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.EventReceiverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventFactoryUtils {

    private static final Logger logger = LoggerFactory.getLogger(EventFactoryUtils.class);
    private static Collection<EventReceiverFactory> receivers = getSortedLoaders(EventReceiverFactory.class);
    private static Collection<EventEmitterFactory> emitters = getSortedLoaders(EventEmitterFactory.class);

    private static <V extends EventFactory<?>> Collection<V> getSortedLoaders(Class<V> clazz) {
        return ServiceLoader.load(clazz).stream().map(Provider::get).sorted().collect(Collectors.toList());
    }

    public static EventReceiver getEventReceiver(String trigger) {
        return getInstance(trigger, receivers, () -> new EventReceiver() {
            @Override
            public <T> void subscribe(Function<DataEvent<T>, CompletionStage<?>> consumer, Class<T> dataClass) {
                // default receiver does nothing
            }
        });
    }

    private static <T extends EventFactory<?>> void ready(Iterable<T> factories) {
        factories.forEach(EventFactory::ready);
    }

    private static <T extends AutoCloseable> void cleanUp(Iterable<T> closeables) {
        for (AutoCloseable closeable : closeables) {
            try {
                closeable.close();
            } catch (Exception ex) {
                logger.error("Error closing factory", ex);
            }
        }
    }

    public static EventEmitter getEventEmitter(String trigger) {
        return getInstance(trigger, emitters, () -> new EventEmitter() {
            @Override
            public CompletionStage<Void> emit(DataEvent<?> dataEvent) {
                // default emitter does nothing
                return CompletableFuture.completedStage(null);
            }
        });
    }

    private static <T, V extends Function<String, T>> T getInstance(String trigger, Collection<V> services, Supplier<T> defaultValue) {
        return services.stream().map(s -> s.apply(trigger)).filter(Objects::nonNull).findFirst().orElseGet(defaultValue);
    }

    public static void cleanUp() {
        cleanUp(receivers);
        cleanUp(emitters);
    }

    public static void ready() {
        ready(receivers);
        ready(emitters);
    }

    private EventFactoryUtils() {
    }
}
