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
package org.kie.kogito.index.messaging;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.service.IndexingService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.TestUtils.getUserTaskCloudEvent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReactiveMessagingEventConsumerTest {

    @Mock
    IndexingService service;

    @InjectMocks
    @Spy
    ReactiveMessagingEventConsumer consumer;

    @Test
    public void testOnProcessInstanceDomainEvent() throws Exception {
        ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
        doReturn(CompletableFuture.completedFuture(null)).when(consumer).sendMessage(captor.capture());

        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        Message<KogitoProcessCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, null, null, null));

        CompletableFuture<Void> future = consumer.onProcessInstanceDomainEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event).ack();
        assertThat(future).isDone().isNotCompletedExceptionally();

        assertThatJson(captor.getValue().toString())
                .isObject()
                .containsEntry("id", processInstanceId)
                .containsEntry("processId", processId);
    }

    @Test
    public void testOnProcessInstanceDomainEventIndexingException() throws Exception {
        doReturn(CompletableFuture.failedFuture(new RuntimeException())).when(consumer).sendMessage(any());

        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        Message<KogitoProcessCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, null, null, null));

        CompletableFuture<Void> future = consumer.onProcessInstanceDomainEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event, never()).ack();
        assertThat(future).isDone().isNotCompletedExceptionally();
    }

    @Test
    public void testOnProcessInstanceDomainEventMappingException() throws Exception {
        Message<KogitoProcessCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(mock(KogitoProcessCloudEvent.class));

        CompletableFuture<Void> future = consumer.onProcessInstanceDomainEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event, never()).ack();
        assertThat(future).isDone().isNotCompletedExceptionally();
        verify(consumer, never()).sendMessage(any());
    }

    @Test
    public void testOnUserTaskInstanceDomainEvent() throws Exception {
        ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
        doReturn(CompletableFuture.completedFuture(null)).when(consumer).sendMessage(captor.capture());

        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        Message<KogitoUserTaskCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, "InProgress"));

        CompletableFuture<Void> future = consumer.onUserTaskInstanceDomainEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event).ack();
        assertThat(future).isDone().isNotCompletedExceptionally();

        assertThatJson(captor.getValue().toString())
                .isObject()
                .containsEntry("id", processInstanceId)
                .containsEntry("processId", processId);
    }

    @Test
    public void testOnUserTaskInstanceDomainEventIndexingException() throws Exception {
        doReturn(CompletableFuture.failedFuture(new RuntimeException())).when(consumer).sendMessage(any());

        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        Message<KogitoUserTaskCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, "InProgress"));

        CompletableFuture<Void> future = consumer.onUserTaskInstanceDomainEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event, never()).ack();
        assertThat(future).isDone().isNotCompletedExceptionally();
    }

    @Test
    public void testOnUserTaskInstanceDomainEventMappingException() throws Exception {
        Message<KogitoUserTaskCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(mock(KogitoUserTaskCloudEvent.class));

        CompletableFuture<Void> future = consumer.onUserTaskInstanceDomainEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event, never()).ack();
        assertThat(future).isDone().isNotCompletedExceptionally();
        verify(consumer, never()).sendMessage(any());
    }

    @Test
    public void testOnProcessInstanceEvent() throws Exception {
        Message<KogitoProcessCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(mock(KogitoProcessCloudEvent.class));

        CompletableFuture<Void> future = consumer.onProcessInstanceEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event).ack();
        verify(service).indexProcessInstance(event.getPayload().getData());
        assertThat(future).isDone().isNotCompletedExceptionally();
    }

    @Test
    public void testOnProcessInstanceEventException() throws Exception {
        Message<KogitoProcessCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(mock(KogitoProcessCloudEvent.class));
        doThrow(new RuntimeException()).when(service).indexProcessInstance(any());

        CompletableFuture<Void> future = consumer.onProcessInstanceEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event, never()).ack();
        verify(service).indexProcessInstance(event.getPayload().getData());
        assertThat(future).isDone().isNotCompletedExceptionally();
    }

    @Test
    public void testOnUserTaskInstanceEvent() throws Exception {
        Message<KogitoUserTaskCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(mock(KogitoUserTaskCloudEvent.class));

        CompletableFuture<Void> future = consumer.onUserTaskInstanceEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event).ack();
        verify(service).indexUserTaskInstance(event.getPayload().getData());
        assertThat(future).isDone().isNotCompletedExceptionally();
    }

    @Test
    public void testOnUserTaskInstanceEventException() throws Exception {
        Message<KogitoUserTaskCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(mock(KogitoUserTaskCloudEvent.class));
        doThrow(new RuntimeException()).when(service).indexUserTaskInstance(any());

        CompletableFuture<Void> future = consumer.onUserTaskInstanceEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event, never()).ack();
        verify(service).indexUserTaskInstance(event.getPayload().getData());
        assertThat(future).isDone().isNotCompletedExceptionally();
    }

    @Test
    public void testOnJobEvent() throws Exception {
        Message<KogitoJobCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(mock(KogitoJobCloudEvent.class));

        CompletableFuture<Void> future = consumer.onJobEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event).ack();
        verify(service).indexJob(event.getPayload().getData());
        assertThat(future).isDone().isNotCompletedExceptionally();
    }

    @Test
    public void testOnJobEventException() throws Exception {
        Message<KogitoJobCloudEvent> event = mock(Message.class);
        when(event.getPayload()).thenReturn(mock(KogitoJobCloudEvent.class));
        doThrow(new RuntimeException()).when(service).indexJob(any());

        CompletableFuture<Void> future = consumer.onJobEvent(event).toCompletableFuture();
        future.get(1, TimeUnit.MINUTES);

        verify(event, never()).ack();
        verify(service).indexJob(event.getPayload().getData());
        assertThat(future).isDone().isNotCompletedExceptionally();
    }
}