/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.services.event.impl;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.EventUnmarshaller;
import org.kie.kogito.event.SubscriptionInfo;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.EventConsumer;
import org.kie.kogito.services.event.EventConsumerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageConsumer<M extends Model, D, T extends AbstractProcessDataEvent<D>> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractMessageConsumer.class);

    private Process<M> process;
    private Application application;
    private String trigger;
    private EventConsumer<M> eventConsumer;

    // in general we should favor the non-empty constructor
    // but there is an issue with Quarkus https://github.com/quarkusio/quarkus/issues/2949#issuecomment-513017781
    // use this in conjuction with setParams()
    public AbstractMessageConsumer() {
    }

    public AbstractMessageConsumer(Application application,
            Process<M> process,
            String trigger,
            EventConsumerFactory eventConsumerFactory,
            EventReceiver eventReceiver,
            Class<D> dataEventConverter,
            Class<T> cloudEventConverter,
            boolean useCloudEvents,
            ProcessService processService,
            ExecutorService executorService,
            EventUnmarshaller<Object> eventUnmarshaller) {
        init(application, process, trigger, eventConsumerFactory, eventReceiver, dataEventConverter, cloudEventConverter, useCloudEvents, processService, executorService, eventUnmarshaller);
    }

    public void init(Application application,
            Process<M> process,
            String trigger,
            EventConsumerFactory eventConsumerFactory,
            EventReceiver eventReceiver,
            Class<D> dataEventClass,
            Class<T> cloudEventClass,
            boolean useCloudEvents,
            ProcessService processService,
            ExecutorService executorService,
            EventUnmarshaller<Object> eventUnmarshaller) {
        this.process = process;
        this.application = application;
        this.trigger = trigger;
        this.eventConsumer = eventConsumerFactory.get(processService, executorService, getModelConverter(), useCloudEvents);
        if (useCloudEvents) {
            eventReceiver.subscribe(this::consumeCloud, new SubscriptionInfo<>(eventUnmarshaller, cloudEventClass, Optional.of(trigger)));
        } else {
            eventReceiver.subscribe(this::consumeNotCloud, new SubscriptionInfo<>(eventUnmarshaller, dataEventClass, Optional.of(trigger)));
        }
        logger.info("Consumer for {} started", trigger);
    }

    protected CompletionStage<?> consumeCloud(T payload) {
        return consume(payload);
    }

    protected CompletionStage<?> consumeNotCloud(D payload) {
        return consume(payload);
    }

    private CompletionStage<?> consume(Object payload) {
        logger.trace("Received {} for trigger {}", payload, trigger);
        CompletionStage<?> result = eventConsumer.consume(application, process, payload, trigger);
        if (logger.isTraceEnabled()) {
            result = result.thenAccept(v -> logger.trace("Completed {} for trigger {}", payload, trigger));
        }
        logger.trace("Dispatched {} for trigger {}", payload, trigger);
        return result;
    }

    protected Optional<Function<D, M>> getModelConverter() {
        return Optional.empty();
    }
}
