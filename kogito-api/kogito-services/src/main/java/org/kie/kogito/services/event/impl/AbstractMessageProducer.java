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

import org.kie.kogito.event.CloudEventEmitter;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.EventMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageProducer<D, T extends AbstractProcessDataEvent<D>> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageProducer.class);

    private Optional<Boolean> useCloudEvents;
    private EventMarshaller marshaller;
    private String trigger;
    private CloudEventEmitter emitter;

    // in general we should favor the non-empty constructor
    // but there is an issue with Quarkus https://github.com/quarkusio/quarkus/issues/2949#issuecomment-513017781
    // use this in conjuction with setParams()
    public AbstractMessageProducer() {
    }

    public AbstractMessageProducer(
            CloudEventEmitter emitter,
            EventMarshaller marshaller,
            String trigger,
            Optional<Boolean> useCloudEvents) {
        this.emitter = emitter;
        this.marshaller = marshaller;
        this.trigger = trigger;
        this.useCloudEvents = useCloudEvents;
    }

    protected void setParams(
            CloudEventEmitter emitter,
            EventMarshaller marshaller,
            String trigger,
            Optional<Boolean> useCloudEvents) {
        this.emitter = emitter;
        this.marshaller = marshaller;
        this.trigger = trigger;
        this.useCloudEvents = useCloudEvents;
    }

    public void produce(KogitoProcessInstance pi, D eventData) {
        emitter.emit(this.marshall(pi, eventData))
                .exceptionally(ex -> {
                    logger.error("An error was caught while process " + pi.getProcessId() + " produced message " + eventData, ex);
                    return null;
                });
    }

    protected String marshall(KogitoProcessInstance pi, D eventData) {
        return marshaller.marshall(eventData,
                                   e -> dataEventTypeConstructor(e, pi, trigger),
                                   useCloudEvents);
    }

    protected abstract T dataEventTypeConstructor(D e, KogitoProcessInstance pi, String trigger);
}