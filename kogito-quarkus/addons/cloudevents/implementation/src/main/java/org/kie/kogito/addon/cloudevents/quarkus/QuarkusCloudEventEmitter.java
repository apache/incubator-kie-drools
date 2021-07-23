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
package org.kie.kogito.addon.cloudevents.quarkus;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.addon.cloudevents.quarkus.decorators.MessageDecorator;
import org.kie.kogito.addon.cloudevents.quarkus.decorators.MessageDecoratorFactory;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.services.event.impl.DefaultEventMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * the quarkus implementation just delegates to a real emitter,
 * since smallrye reactive messaging handles different transports
 *
 */
@ApplicationScoped
public class QuarkusCloudEventEmitter implements EventEmitter {

    private MessageDecorator messageDecorator;

    private static final Logger logger = LoggerFactory.getLogger(QuarkusCloudEventEmitter.class);

    @Inject
    @Channel(KogitoEventStreams.OUTGOING)
    Emitter<String> emitter;

    @Inject
    ConfigBean configBean;

    @Inject
    Instance<EventMarshaller> marshallerInstance;
    EventMarshaller marshaller;

    @PostConstruct
    private void init() {
        marshaller = marshallerInstance.isResolvable() ? marshallerInstance.get() : new DefaultEventMarshaller();
        messageDecorator = MessageDecoratorFactory.newInstance(configBean.useCloudEvents());
    }

    @Override
    public <T> CompletionStage<Void> emit(T e, String type, Optional<Function<T, Object>> processDecorator) {
        logger.debug("publishing event {} for type {}", e, type);
        final Message<String> message = this.messageDecorator.decorate(marshaller.marshall(
                configBean.useCloudEvents() ? processDecorator.map(d -> d.apply(e)).orElse(e) : e));
        emitter.send(message);
        return message.getAck().get();
    }
}
