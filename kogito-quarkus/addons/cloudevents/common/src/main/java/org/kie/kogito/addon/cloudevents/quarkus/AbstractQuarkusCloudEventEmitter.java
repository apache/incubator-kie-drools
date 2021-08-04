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
package org.kie.kogito.addon.cloudevents.quarkus;

import java.util.Optional;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.addon.cloudevents.quarkus.message.MessageFactory;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.services.event.impl.DefaultEventMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractQuarkusCloudEventEmitter implements EventEmitter {

    @Inject
    ConfigBean configBean;

    @Inject
    Instance<EventMarshaller> marshallers;
    private EventMarshaller marshaller;

    @Inject
    ObjectMapper mapper;

    private MessageFactory messageFactory;

    @PostConstruct
    private void init() {
        messageFactory = new MessageFactory(configBean.useCloudEvents());
        marshaller = marshallers.isUnsatisfied() ? new DefaultEventMarshaller(mapper) : marshallers.get();
    }

    protected <T> Message<String> processMessage(T e, Optional<Function<T, Object>> processDecorator) {
        return this.messageFactory.build(marshaller.marshall(
                configBean.useCloudEvents() ? processDecorator.map(d -> d.apply(e)).orElse(e) : e));
    }

}
