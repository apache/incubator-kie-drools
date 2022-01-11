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
package org.kie.kogito.addon.quarkus.messaging.common;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.addon.quarkus.messaging.common.message.MessageFactory;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractQuarkusCloudEventEmitter<M> implements EventEmitter {

    private static final Logger logger = LoggerFactory.getLogger(AbstractQuarkusCloudEventEmitter.class);

    @Inject
    ConfigBean configBean;

    @Inject
    EventMarshaller<M> marshaller;

    @Inject
    ObjectMapper mapper;

    private MessageFactory messageFactory;

    @PostConstruct
    void init() {
        messageFactory = new MessageFactory(configBean.useCloudEvents());
    }

    @Override
    public <T> CompletionStage<Void> emit(T e, String type, Optional<Function<T, Object>> processDecorator) {
        logger.debug("publishing event {} for type {}", e, type);
        final Message<M> message = this.messageFactory.getMessageDecorator().decorate(marshaller.marshall(
                configBean.useCloudEvents() ? processDecorator.map(d -> d.apply(e)).orElse(e) : e));
        emit(message);
        return message.getAck().get();
    }

    protected abstract void emit(Message<M> message);
}
