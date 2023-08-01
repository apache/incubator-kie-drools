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
package com.myspace.demo;

import java.io.IOException;
import java.util.Optional;

import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.event.impl.StringEventMarshaller;
import org.kie.kogito.event.process.ProcessDataEvent;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageProducer extends org.kie.kogito.event.impl.AbstractMessageProducer<java.lang.String>{


    Optional<Boolean> useCloudEvents = Optional.of(true);

    EventMarshaller<String> marshaller = new StringEventMarshaller(new ObjectMapper());

    public void configure() {

    }

    public void produce(KogitoProcessInstance pi, $Type$ eventData) {

    }

    private String marshall(KogitoProcessInstance pi, $Type$ eventData) throws IOException {
        return marshaller.marshall(useCloudEvents.orElse(true) ? new ProcessDataEvent<>(
                "",
                "",
                eventData,
                pi.getStringId(),
                pi.getProcess().getVersion(),
                pi.getParentProcessInstanceId(),
                pi.getRootProcessInstanceId(),
                pi.getProcessId(),
                pi.getRootProcessId(),
                String.valueOf(pi.getState()),
                null,
                pi.getProcess().getType(),
                pi.getReferenceId() == null || pi.getReferenceId().trim().isEmpty() ? null : pi.getReferenceId(),
                null) : eventData);
    }
}