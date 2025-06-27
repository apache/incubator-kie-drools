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
import java.util.Collection;

import org.kie.kogito.event.Converter;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.DataEventFactory;
import org.kie.kogito.event.impl.JacksonCloudEventDataConverter;
import org.kie.kogito.event.impl.JacksonTypeCloudEventDataConverter;
import org.kie.kogito.event.process.MultipleProcessInstanceDataEvent;
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
import org.kie.kogito.event.serializer.MultipleProcessDataInstanceConverterFactory;
import org.kie.kogito.event.usertask.MultipleUserTaskInstanceDataEvent;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;

public class IndexEventConverterHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexEventConverterHelper.class);

    private Converter<CloudEventData, ProcessInstanceErrorEventBody> errorConverter;
    private Converter<CloudEventData, ProcessInstanceNodeEventBody> nodeConverter;
    private Converter<CloudEventData, ProcessInstanceSLAEventBody> slaConverter;
    private Converter<CloudEventData, ProcessInstanceVariableEventBody> varConverter;
    private Converter<CloudEventData, ProcessInstanceStateEventBody> stateConverter;
    private Converter<CloudEventData, UserTaskInstanceAssignmentEventBody> assignConverter;
    private Converter<CloudEventData, UserTaskInstanceAttachmentEventBody> attachConverter;
    private Converter<CloudEventData, UserTaskInstanceCommentEventBody> commentConverter;
    private Converter<CloudEventData, UserTaskInstanceDeadlineEventBody> deadlineConverter;
    private Converter<CloudEventData, UserTaskInstanceStateEventBody> taskStateConverter;
    private Converter<CloudEventData, UserTaskInstanceVariableEventBody> taskVariableConverter;
    private Converter<CloudEventData, Collection<UserTaskInstanceDataEvent<?>>> taskCollectionConverter;
    private Converter<CloudEventData, ProcessDefinitionEventBody> definitionConverter;
    private ObjectMapper objectMapper;

    public IndexEventConverterHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.errorConverter = new JacksonCloudEventDataConverter<>(objectMapper, ProcessInstanceErrorEventBody.class);
        this.nodeConverter = new JacksonCloudEventDataConverter<>(objectMapper, ProcessInstanceNodeEventBody.class);
        this.slaConverter = new JacksonCloudEventDataConverter<>(objectMapper, ProcessInstanceSLAEventBody.class);
        this.varConverter = new JacksonCloudEventDataConverter<>(objectMapper, ProcessInstanceVariableEventBody.class);
        this.stateConverter = new JacksonCloudEventDataConverter<>(objectMapper, ProcessInstanceStateEventBody.class);
        this.assignConverter = new JacksonCloudEventDataConverter<>(objectMapper, UserTaskInstanceAssignmentEventBody.class);
        this.attachConverter = new JacksonCloudEventDataConverter<>(objectMapper, UserTaskInstanceAttachmentEventBody.class);
        this.commentConverter = new JacksonCloudEventDataConverter<>(objectMapper, UserTaskInstanceCommentEventBody.class);
        this.deadlineConverter = new JacksonCloudEventDataConverter<>(objectMapper, UserTaskInstanceDeadlineEventBody.class);
        this.taskStateConverter = new JacksonCloudEventDataConverter<>(objectMapper, UserTaskInstanceStateEventBody.class);
        this.taskVariableConverter = new JacksonCloudEventDataConverter<>(objectMapper, UserTaskInstanceVariableEventBody.class);
        this.taskCollectionConverter = new JacksonTypeCloudEventDataConverter<>(objectMapper, new TypeReference<Collection<UserTaskInstanceDataEvent<?>>>() {
        });
        this.definitionConverter = new JacksonCloudEventDataConverter<>(objectMapper, ProcessDefinitionEventBody.class);

    }

    public boolean isIndexable(Type type) {
        return type == ProcessInstanceDataEvent.class
                || type == ProcessDefinitionDataEvent.class
                || type == UserTaskInstanceDataEvent.class
                || type == KogitoJobCloudEvent.class;
    }

    public Object convert(CloudEvent cloudEvent, Type type) {
        try {
            if (type.getTypeName().equals(ProcessInstanceDataEvent.class.getTypeName())) {
                return buildProcessInstanceDataEventVariant(cloudEvent);
            } else if (type.getTypeName().equals(KogitoJobCloudEvent.class.getTypeName())) {
                return buildKogitoJobCloudEvent(cloudEvent);
            } else if (type.getTypeName().equals(UserTaskInstanceDataEvent.class.getTypeName())) {
                return buildUserTaskInstanceDataEvent(cloudEvent);
            } else if (type.getTypeName().equals(ProcessDefinitionDataEvent.class.getTypeName())) {
                return buildProcessDefinitionEvent(cloudEvent);
            }
        } catch (IOException e) {
            LOGGER.error("Error converting cloudevent to " + type.getTypeName(), e);
            throw new DataIndexServiceException("Error converting cloud event:\n" + cloudEvent + " \n to" + type.getTypeName(), e);
        }

        throw new IllegalArgumentException("Unknown event type: " + type);
    }

    private ProcessDefinitionDataEvent buildProcessDefinitionEvent(CloudEvent cloudEvent) throws IOException {
        return DataEventFactory.from(new ProcessDefinitionDataEvent(), cloudEvent, definitionConverter);
    }

    private DataEvent<?> buildProcessInstanceDataEventVariant(CloudEvent cloudEvent) throws IOException {
        switch (cloudEvent.getType()) {
            case MultipleProcessInstanceDataEvent.MULTIPLE_TYPE:
                return DataEventFactory.from(new MultipleProcessInstanceDataEvent(), cloudEvent, MultipleProcessDataInstanceConverterFactory.fromCloudEvent(cloudEvent, objectMapper));
            case ProcessInstanceErrorDataEvent.ERROR_TYPE:
                return DataEventFactory.from(new ProcessInstanceErrorDataEvent(), cloudEvent, errorConverter);
            case ProcessInstanceNodeDataEvent.NODE_TYPE:
                return DataEventFactory.from(new ProcessInstanceNodeDataEvent(), cloudEvent, nodeConverter);
            case ProcessInstanceSLADataEvent.SLA_TYPE:
                return DataEventFactory.from(new ProcessInstanceSLADataEvent(), cloudEvent, slaConverter);
            case ProcessInstanceStateDataEvent.STATE_TYPE:
                return DataEventFactory.from(new ProcessInstanceStateDataEvent(), cloudEvent, stateConverter);
            case ProcessInstanceVariableDataEvent.VAR_TYPE:
                return DataEventFactory.from(new ProcessInstanceVariableDataEvent(), cloudEvent, varConverter);
            default:
                throw new IllegalArgumentException("Unknown ProcessInstanceDataEvent variant: " + cloudEvent.getType());
        }
    }

    private DataEvent<?> buildUserTaskInstanceDataEvent(CloudEvent cloudEvent) throws IOException {
        switch (cloudEvent.getType()) {
            case MultipleUserTaskInstanceDataEvent.TYPE:
                return DataEventFactory.from(new MultipleUserTaskInstanceDataEvent(), cloudEvent, taskCollectionConverter);
            case "UserTaskInstanceAssignmentDataEvent":
                return DataEventFactory.from(new UserTaskInstanceAssignmentDataEvent(), cloudEvent, assignConverter);
            case "UserTaskInstanceAttachmentDataEvent":
                return DataEventFactory.from(new UserTaskInstanceAttachmentDataEvent(), cloudEvent, attachConverter);
            case "UserTaskInstanceCommentDataEvent":
                return DataEventFactory.from(new UserTaskInstanceCommentDataEvent(), cloudEvent, commentConverter);
            case "UserTaskInstanceDeadlineDataEvent":
                return DataEventFactory.from(new UserTaskInstanceDeadlineDataEvent(), cloudEvent, deadlineConverter);
            case "UserTaskInstanceStateDataEvent":
                return DataEventFactory.from(new UserTaskInstanceStateDataEvent(), cloudEvent, taskStateConverter);
            case "UserTaskInstanceVariableDataEvent":
                return DataEventFactory.from(new UserTaskInstanceVariableDataEvent(), cloudEvent, taskVariableConverter);
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

}
