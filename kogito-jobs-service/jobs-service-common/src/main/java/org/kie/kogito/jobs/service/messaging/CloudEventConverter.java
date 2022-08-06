/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.messaging;

import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.quarkus.reactivemessaging.http.runtime.IncomingHttpMetadata;
import io.smallrye.reactive.messaging.MessageConverter;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;

/**
 * Converts the message payload into a io.cloudevents.CloudEvent object. The conversion takes into account that the
 * message can be coded in the structured or binary format.
 */
@ApplicationScoped
public class CloudEventConverter implements MessageConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventConverter.class);

    @Override
    public boolean canConvert(Message<?> message, Type type) {
        return message.getMetadata(IncomingHttpMetadata.class).isPresent() &&
                message.getPayload() instanceof Buffer && type == CloudEvent.class;
    }

    @Override
    public Message<?> convert(Message<?> message, Type type) {
        LOGGER.debug("convert: message, {}, type: {}, metadata: {}, payload: {}", message, type,
                message.getMetadata(), message.getPayload());
        IncomingHttpMetadata httpMetadata = message.getMetadata(IncomingHttpMetadata.class)
                .orElseThrow(() -> new IllegalStateException("No http metadata"));
        MultiMap httpHeaders = httpMetadata.getHeaders();
        LOGGER.debug("httpHeaders: {}", httpHeaders);
        Buffer buffer = (Buffer) message.getPayload();
        MessageReader messageReader = VertxMessageFactory.createReader(httpHeaders, buffer);
        CloudEvent cloudEvent = messageReader.toEvent();
        return message.withPayload(cloudEvent);
    }
}
