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

import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.process.ProcessDataEvent;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageProducer<D> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageProducer.class);

    private String trigger;
    private EventEmitter emitter;

    // in general, we should favor the non-empty constructor
    // but there is an issue with Quarkus https://github.com/quarkusio/quarkus/issues/2949#issuecomment-513017781
    // use this in conjunction with setParams()
    public AbstractMessageProducer() {
    }

    public AbstractMessageProducer(EventEmitter emitter, String trigger) {
        setParams(emitter, trigger);
    }

    protected void setParams(EventEmitter emitter, String trigger) {
        this.emitter = emitter;
        this.trigger = trigger;
    }

    public void produce(KogitoProcessInstance pi, D eventData) {
        emitter.emit(eventData, trigger, Optional.of(e -> dataEventTypeConstructor(e, pi, trigger)))
                .exceptionally(ex -> {
                    logger.error("An error was caught while process " + pi.getProcessId() + " produced message " + eventData, ex);
                    return null;
                });
    }

    public ProcessDataEvent<D> dataEventTypeConstructor(D eventPayload, KogitoProcessInstance pi, String trigger) {
        return new ProcessDataEvent<>(trigger,
                "",
                eventPayload,
                pi.getStringId(),
                pi.getParentProcessInstanceId(),
                pi.getRootProcessInstanceId(),
                pi.getProcessId(),
                pi.getRootProcessId(),
                String.valueOf(pi.getState()),
                null,
                pi.getReferenceId());
    }
}