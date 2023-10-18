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

import java.io.IOException;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.service.DataIndexServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.reactive.messaging.MessageConverter;
import io.vertx.core.buffer.Buffer;

/**
 * Converts the message payload into an indexable object. The conversion takes into account that the
 * message can be coded in the structured.
 */
@ApplicationScoped
public class KogitoIndexEventConverter implements MessageConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoIndexEventConverter.class);

    ObjectMapper objectMapper;

    @Override
    public boolean canConvert(Message<?> message, Type type) {
        return isIndexable(type) &&
                (message.getPayload() instanceof Buffer);
    }

    private boolean isIndexable(Type type) {
        return type == ProcessInstanceDataEvent.class
                || type == UserTaskInstanceDataEvent.class
                || type == KogitoJobCloudEvent.class;
    }

    @Override
    public Message<?> convert(Message<?> message, Type type) {
        try {
            if (type.getTypeName().equals(ProcessInstanceDataEvent.class.getTypeName())) {
                return message.withPayload(objectMapper.readValue(message.getPayload().toString(), ProcessInstanceDataEvent.class));
            } else if (type.getTypeName().equals(KogitoJobCloudEvent.class.getTypeName())) {
                return message.withPayload(objectMapper.readValue(message.getPayload().toString(), KogitoJobCloudEvent.class));
            } else if (type.getTypeName().equals(UserTaskInstanceDataEvent.class.getTypeName())) {
                return message.withPayload(objectMapper.readValue(message.getPayload().toString(), UserTaskInstanceDataEvent.class));
            } else {
                return message.withPayload(objectMapper.readValue(message.getPayload().toString(), (Class<?>) type));
            }
        } catch (IOException e) {
            LOGGER.error("Error converting message payload to " + type.getTypeName(), e);
            throw new DataIndexServiceException("Error converting message payload:\n" + message.getPayload() + " \n to" + type.getTypeName(), e);
        }
    }

    @Inject
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
