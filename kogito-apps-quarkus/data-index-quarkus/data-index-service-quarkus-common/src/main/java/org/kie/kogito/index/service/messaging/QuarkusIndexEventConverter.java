/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.service.messaging;

import java.lang.reflect.Type;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.http.vertx.VertxMessageFactory;
import io.quarkus.reactivemessaging.http.runtime.IncomingHttpMetadata;
import io.smallrye.reactive.messaging.MessageConverter;
import io.vertx.core.buffer.Buffer;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Converts the message payload into an indexable object. The conversion takes into account that the
 * message can be coded in the structured or binary format.
 */
@ApplicationScoped
public class QuarkusIndexEventConverter implements MessageConverter {

    @Inject
    QuarkusIndexEventConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public int getPriority() {
        return CONVERTER_DEFAULT_PRIORITY - 2;
    }

    private ObjectMapper objectMapper;
    private IndexEventConverterHelper converter;

    @PostConstruct
    void init() {
        converter = new IndexEventConverterHelper(objectMapper);
    }

    @Override
    public boolean canConvert(Message<?> message, Type type) {
        return message.getPayload() instanceof Buffer && converter.isIndexable(type);
    }

    @Override
    public Message<?> convert(Message<?> message, Type type) {
        return message.withPayload(converter.convert(VertxMessageFactory.createReader(message.getMetadata(IncomingHttpMetadata.class)
                .orElseThrow(() -> new IllegalStateException("No IncomingHttpMetadata metadata was found current message.")).getHeaders(), (Buffer) message.getPayload()).toEvent(), type));
    }

}
