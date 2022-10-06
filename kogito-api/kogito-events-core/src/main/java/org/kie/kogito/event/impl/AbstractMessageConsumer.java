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
package org.kie.kogito.event.impl;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventDispatcher;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageConsumer<M extends Model, D> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractMessageConsumer.class);

    private String trigger;
    private EventDispatcher<M, D> eventDispatcher;

    protected void init(Application application,
            Process<M> process,
            String trigger,
            EventReceiver eventReceiver,
            Class<D> dataClass,
            ProcessService processService,
            ExecutorService executorService,
            Set<String> correlations) {
        this.trigger = trigger;
        this.eventDispatcher = new ProcessEventDispatcher<>(process, getModelConverter(), processService, executorService, correlations, getDataResolver());
        eventReceiver.subscribe(this::consume, dataClass);
        logger.info("Consumer for {} started", trigger);
    }

    // this will be overriden by serverless workflow
    protected Function<DataEvent<D>, D> getDataResolver() {
        return this::justData;
    }

    protected final D justData(DataEvent<D> dataEvent) {
        return dataEvent.getData();
    }

    private CompletionStage<?> consume(DataEvent<D> payload) {
        logger.trace("Received {} for trigger {}", payload, trigger);
        return eventDispatcher.dispatch(trigger, payload)
                .thenAccept(v -> logger.trace("Consume completed {} for trigger {}", payload, trigger));
    }

    protected Optional<Function<D, M>> getModelConverter() {
        return Optional.empty();
    }
}
