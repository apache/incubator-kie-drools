/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.kie.kogito.services.event.impl;

import java.util.Optional;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.CloudEventEmitter;
import org.kie.kogito.services.event.EventMarshaller;

public abstract class AbstractMessageProducer<D, T extends AbstractProcessDataEvent<D>> {

    private Optional<Boolean> useCloudEvents;
    private EventMarshaller marshaller;
    private CloudEventEmitter emitter;

    // in general we should favor the non-empty constructor
    // but there is an issue with Quarkus https://github.com/quarkusio/quarkus/issues/2949#issuecomment-513017781
    // use this in conjuction with setParams()
    public AbstractMessageProducer() {
    }

    public AbstractMessageProducer(
            CloudEventEmitter emitter,
            EventMarshaller marshaller,
            Optional<Boolean> useCloudEvents) {
        this.emitter = emitter;
        this.marshaller = marshaller;
        this.useCloudEvents = useCloudEvents;
    }

    protected void setParams(
            CloudEventEmitter emitter,
            EventMarshaller marshaller,
            Optional<Boolean> useCloudEvents) {
        this.emitter = emitter;
        this.marshaller = marshaller;
        this.useCloudEvents = useCloudEvents;
    }

    public void produce(ProcessInstance pi, D eventData) {
        emitter.emit(this.marshall(pi, eventData));
    }

    protected String marshall(ProcessInstance pi, D eventData) {
        return marshaller.marshall(eventData,
                                   e -> dataEventTypeConstructor(e, pi),
                                   useCloudEvents);
    }

    protected abstract T dataEventTypeConstructor(D e, ProcessInstance pi);
}