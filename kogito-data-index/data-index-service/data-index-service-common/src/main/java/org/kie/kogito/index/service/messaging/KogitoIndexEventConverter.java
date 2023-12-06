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
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.ProcessDefinitionDataEvent;
import org.kie.kogito.event.process.ProcessDefinitionEventBody;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorEventBody;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLAEventBody;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableEventBody;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.service.DataIndexServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.quarkus.reactivemessaging.http.runtime.IncomingHttpMetadata;
import io.smallrye.reactive.messaging.MessageConverter;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;

/**
 * Converts the message payload into an indexable object. The conversion takes into account that the
 * message can be coded in the structured or binary format.
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
                || type == ProcessDefinitionDataEvent.class
                || type == UserTaskInstanceDataEvent.class
                || type == KogitoJobCloudEvent.class;
    }

    @Override
    public Message<?> convert(Message<?> message, Type type) {
        try {
            // quarkus-http connector case, let Vertx manage binary and structured mode.
            IncomingHttpMetadata httpMetadata = message.getMetadata(IncomingHttpMetadata.class)
                    .orElseThrow(() -> new IllegalStateException("No IncomingHttpMetadata metadata was found current message."));
            CloudEvent cloudEvent;
            MultiMap httpHeaders = httpMetadata.getHeaders();
            Buffer buffer = (Buffer) message.getPayload();
            MessageReader messageReader = VertxMessageFactory.createReader(httpHeaders, buffer);
            cloudEvent = messageReader.toEvent();

            if (type.getTypeName().equals(ProcessInstanceDataEvent.class.getTypeName())) {
                return message.withPayload(buildProcessInstanceDataEventVariant(cloudEvent));
            } else if (type.getTypeName().equals(KogitoJobCloudEvent.class.getTypeName())) {
                return message.withPayload(buildKogitoJobCloudEvent(cloudEvent));
            } else if (type.getTypeName().equals(UserTaskInstanceDataEvent.class.getTypeName())) {
                return message.withPayload(buildUserTaskInstanceDataEvent(cloudEvent));
            } else if (type.getTypeName().equals(ProcessDefinitionDataEvent.class.getTypeName())) {
                return message.withPayload(buildProcessDefinitionEvent(cloudEvent));
            }
            // never happens, see isIndexable.
            throw new IllegalArgumentException("Unknown event type: " + type);
        } catch (IOException e) {
            LOGGER.error("Error converting message payload to " + type.getTypeName(), e);
            throw new DataIndexServiceException("Error converting message payload:\n" + message.getPayload() + " \n to" + type.getTypeName(), e);
        }
    }

    private ProcessDefinitionDataEvent buildProcessDefinitionEvent(CloudEvent cloudEvent) throws IOException {
        ProcessDefinitionDataEvent event = new ProcessDefinitionDataEvent();
        applyCloudEventAttributes(cloudEvent, event);
        if (cloudEvent.getData() != null) {
            event.setData(objectMapper.readValue(cloudEvent.getData().toBytes(), ProcessDefinitionEventBody.class));
        }
        return event;
    }

    @Inject
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private DataEvent<?> buildProcessInstanceDataEventVariant(CloudEvent cloudEvent) throws IOException {
        switch (cloudEvent.getType()) {
            case "ProcessInstanceErrorDataEvent":
                return buildDataEvent(cloudEvent, objectMapper, ProcessInstanceErrorDataEvent::new, ProcessInstanceErrorEventBody.class);
            case "ProcessInstanceNodeDataEvent":
                return buildDataEvent(cloudEvent, objectMapper, ProcessInstanceNodeDataEvent::new, ProcessInstanceNodeEventBody.class);
            case "ProcessInstanceSLADataEvent":
                return buildDataEvent(cloudEvent, objectMapper, ProcessInstanceSLADataEvent::new, ProcessInstanceSLAEventBody.class);
            case "ProcessInstanceStateDataEvent":
                return buildDataEvent(cloudEvent, objectMapper, ProcessInstanceStateDataEvent::new, ProcessInstanceStateEventBody.class);
            case "ProcessInstanceVariableDataEvent":
                return buildDataEvent(cloudEvent, objectMapper, ProcessInstanceVariableDataEvent::new, ProcessInstanceVariableEventBody.class);
            default:
                throw new IllegalArgumentException("Unknown ProcessInstanceDataEvent variant: " + cloudEvent.getType());
        }
    }

    private DataEvent<?> buildUserTaskInstanceDataEvent(CloudEvent cloudEvent) throws IOException {
        switch (cloudEvent.getType()) {
            case "UserTaskInstanceAssignmentDataEvent":
                return buildDataEvent(cloudEvent, objectMapper, UserTaskInstanceAssignmentDataEvent::new, UserTaskInstanceAssignmentEventBody.class);
            case "UserTaskInstanceAttachmentDataEvent":
                return buildDataEvent(cloudEvent, objectMapper, UserTaskInstanceAttachmentDataEvent::new, UserTaskInstanceAttachmentEventBody.class);
            case "UserTaskInstanceCommentDataEvent":
                return buildDataEvent(cloudEvent, objectMapper, UserTaskInstanceCommentDataEvent::new, UserTaskInstanceCommentEventBody.class);
            case "UserTaskInstanceDeadlineDataEvent":
                return buildDataEvent(cloudEvent, objectMapper, UserTaskInstanceDeadlineDataEvent::new, UserTaskInstanceDeadlineEventBody.class);
            case "UserTaskInstanceStateDataEvent":
                return buildDataEvent(cloudEvent, objectMapper, UserTaskInstanceStateDataEvent::new, UserTaskInstanceStateEventBody.class);
            case "UserTaskInstanceVariableDataEvent":
                return buildDataEvent(cloudEvent, objectMapper, UserTaskInstanceVariableDataEvent::new, UserTaskInstanceVariableEventBody.class);
            default:
                throw new IllegalArgumentException("Unknown UserTaskInstanceDataEvent variant: " + cloudEvent.getType());
        }
    }

    private KogitoJobCloudEvent buildKogitoJobCloudEvent(CloudEvent cloudEvent) throws IOException {
        KogitoJobCloudEvent jobCloudEvent = new KogitoJobCloudEvent();
        jobCloudEvent.setId(cloudEvent.getId());
        jobCloudEvent.setType(cloudEvent.getType());
        jobCloudEvent.setSource(cloudEvent.getSource());
        jobCloudEvent.setContentType(cloudEvent.getDataContentType());
        jobCloudEvent.setSchemaURL(cloudEvent.getDataSchema());
        jobCloudEvent.setSubject(cloudEvent.getSubject());
        jobCloudEvent.setTime(cloudEvent.getTime() != null ? cloudEvent.getTime().toZonedDateTime() : null);
        if (cloudEvent.getData() != null) {
            jobCloudEvent.setData(objectMapper.readValue(cloudEvent.getData().toBytes(), Job.class));
        }
        return jobCloudEvent;
    }

    private static <E extends AbstractDataEvent<T>, T> E buildDataEvent(CloudEvent cloudEvent, ObjectMapper objectMapper, Supplier<E> supplier, Class<T> clazz) throws IOException {
        E dataEvent = supplier.get();
        applyCloudEventAttributes(cloudEvent, dataEvent);
        applyExtensions(cloudEvent, dataEvent);
        if (cloudEvent.getData() != null) {
            dataEvent.setData(objectMapper.readValue(cloudEvent.getData().toBytes(), clazz));
        }
        return dataEvent;
    }

    private static void applyCloudEventAttributes(CloudEvent cloudEvent, AbstractDataEvent<?> dataEvent) {
        dataEvent.setSpecVersion(cloudEvent.getSpecVersion());
        dataEvent.setId(cloudEvent.getId());
        dataEvent.setType(cloudEvent.getType());
        dataEvent.setSource(cloudEvent.getSource());
        dataEvent.setDataContentType(cloudEvent.getDataContentType());
        dataEvent.setDataSchema(cloudEvent.getDataSchema());
        dataEvent.setSubject(cloudEvent.getSubject());
        dataEvent.setTime(cloudEvent.getTime());
    }

    private static void applyExtensions(CloudEvent cloudEvent, AbstractDataEvent<?> dataEvent) {
        cloudEvent.getExtensionNames().forEach(extensionName -> dataEvent.addExtensionAttribute(extensionName, cloudEvent.getExtension(extensionName)));
    }
}
