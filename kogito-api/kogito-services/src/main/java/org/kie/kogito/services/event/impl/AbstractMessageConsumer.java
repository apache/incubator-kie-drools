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

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.EventConsumerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageConsumer<M extends Model, D, T extends AbstractProcessDataEvent<D>> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageConsumer.class);

    private Process<M> process;
    private Application application;
    private EventConsumerFactory eventConsumerFactory;
    private Optional<Boolean> useCloudEvents;
    private String trigger;
    private Class<D> dataEventClass;
    private Class<T> cloudEventClass;

    // in general we should favor the non-empty constructor
    // but there is an issue with Quarkus https://github.com/quarkusio/quarkus/issues/2949#issuecomment-513017781
    // use this in conjuction with setParams()
    public AbstractMessageConsumer() {
    }

    public AbstractMessageConsumer(
            Application application,
            Process<M> process,
            Class<D> dataEventClass,
            Class<T> cloudEventClass,
            String trigger,
            EventConsumerFactory eventConsumerFactory,
            Optional<Boolean> useCloudEvents) {
        setParams(application,
                process,
                dataEventClass,
                cloudEventClass,
                trigger,
                eventConsumerFactory,
                useCloudEvents);
    }

    public void setParams(
            Application application,
            Process<M> process,
            Class<D> dataEventClass,
            Class<T> cloudEventClass,
            String trigger,
            EventConsumerFactory eventConsumerFactory,
            Optional<Boolean> useCloudEvents) {
        this.process = process;
        this.application = application;
        this.dataEventClass = dataEventClass;
        this.cloudEventClass = cloudEventClass;
        this.eventConsumerFactory = eventConsumerFactory;
        this.trigger = trigger;
        this.useCloudEvents = useCloudEvents;

        logger.info("Consumer for {} started.", dataEventClass);
    }

    public void consume(String payload) {
        logger.debug("Received: {} on thread {}", payload, Thread.currentThread().getName());
        eventConsumerFactory.get(this::eventToModel, dataEventClass, cloudEventClass, useCloudEvents)
                .consume(application, (Process<Model>) process, payload, trigger);
    }

    protected abstract Model eventToModel(D event);
}