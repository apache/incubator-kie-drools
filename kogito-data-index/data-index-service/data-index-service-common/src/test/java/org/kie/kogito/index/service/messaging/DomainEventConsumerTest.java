/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.service.messaging;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.service.IndexingService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.kogito.index.test.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.test.TestUtils.getUserTaskCloudEvent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DomainEventConsumerTest {

    @Mock
    IndexingService service;

    @InjectMocks
    DomainEventConsumer consumer;

    @BeforeEach
    public void setup() {
        consumer.indexDomain = true;
    }

    @Test
    public void testOnUserTaskInstanceDomainEventMappingException() {
        UserTaskInstanceDataEvent event = mock(UserTaskInstanceDataEvent.class);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> consumer.onDomainEvent(event));

        verify(service, never()).indexModel(any());
    }

    @Test
    public void testOnUserTaskInstanceDomainEventIndexingException() {
        doThrow(new RuntimeException("")).when(service).indexModel(any());

        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, "InProgress");

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> consumer.onDomainEvent(event));
        verify(service).indexModel(any());
    }

    @Test
    public void testOnUserTaskInstanceEvent() {
        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, "InProgress");

        consumer.onDomainEvent(event);

        ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
        verify(service).indexModel(captor.capture());

        assertThatJson(captor.getValue().toString())
                .isObject()
                .containsEntry("id", processInstanceId)
                .containsEntry("processId", processId);
    }

    @Test
    public void testOnProcessInstanceDomainEventMappingException() {
        ProcessInstanceDataEvent event = mock(ProcessInstanceDataEvent.class);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> consumer.onDomainEvent(event));

        verify(service, never()).indexModel(any());
    }

    @Test
    public void testOnProcessInstanceDomainEventIndexingException() {
        doThrow(new RuntimeException("")).when(service).indexModel(any());

        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        ProcessInstanceDataEvent event = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, null,
                null, null);

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> consumer.onDomainEvent(event));
        verify(service).indexModel(any());
    }
}
