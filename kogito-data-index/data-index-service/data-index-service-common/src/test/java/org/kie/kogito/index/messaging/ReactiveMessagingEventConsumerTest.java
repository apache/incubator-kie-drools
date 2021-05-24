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

import javax.enterprise.event.Event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.event.KogitoCloudEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.service.IndexingService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

import static org.kie.kogito.index.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.TestUtils.getUserTaskCloudEvent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReactiveMessagingEventConsumerTest {

    @Mock
    IndexingService service;

    @Mock
    Event<KogitoCloudEvent> eventPublisher;

    @InjectMocks
    @Spy
    ReactiveMessagingEventConsumer consumer;

    @Test
    public void testOnProcessInstanceEvent() throws Exception {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        KogitoProcessCloudEvent event = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, null,
                null, null);

        UniAssertSubscriber<Void> future = consumer.onProcessInstanceEvent(event).subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        future.await().assertCompleted();
        verify(service).indexProcessInstance(event.getData());
        verify(eventPublisher).fire(event);
    }

    @Test
    public void testOnUserTaskInstanceEvent() throws Exception {

        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        KogitoUserTaskCloudEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, "InProgress");

        UniAssertSubscriber<Void> future = consumer.onUserTaskInstanceEvent(event).subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        future.await().assertCompleted();
        verify(service).indexUserTaskInstance(event.getData());
        verify(eventPublisher).fire(event);
    }

    @Test
    public void testOnProcessInstanceEventException() throws Exception {
        KogitoProcessCloudEvent event = mock(KogitoProcessCloudEvent.class);
        doThrow(new RuntimeException("")).when(service).indexProcessInstance(any());

        UniAssertSubscriber<Void> future = consumer.onProcessInstanceEvent(event).subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        future.await().assertFailedWith(RuntimeException.class, "");
        verify(service).indexProcessInstance(event.getData());
        verify(eventPublisher, never()).fire(event);
    }

    @Test
    public void testOnUserTaskInstanceEventException() throws Exception {
        KogitoUserTaskCloudEvent event = mock(KogitoUserTaskCloudEvent.class);
        doThrow(new RuntimeException("")).when(service).indexUserTaskInstance(any());

        UniAssertSubscriber<Void> future = consumer.onUserTaskInstanceEvent(event).subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        future.await().assertFailedWith(RuntimeException.class, "");
        verify(service).indexUserTaskInstance(event.getData());
        verify(eventPublisher, never()).fire(event);
    }

    @Test
    public void testOnJobEvent() throws Exception {
        KogitoJobCloudEvent event = mock(KogitoJobCloudEvent.class);

        UniAssertSubscriber<Void> future = consumer.onJobEvent(event).subscribe().withSubscriber(UniAssertSubscriber.create());

        future.await().assertCompleted();
        verify(service).indexJob(event.getData());
    }

    @Test
    public void testOnJobEventException() throws Exception {
        KogitoJobCloudEvent event = mock(KogitoJobCloudEvent.class);
        doThrow(new RuntimeException("")).when(service).indexJob(any());

        UniAssertSubscriber<Void> future = consumer.onJobEvent(event).subscribe().withSubscriber(UniAssertSubscriber.create());

        future.await().assertFailedWith(RuntimeException.class, "");
        verify(service).indexJob(event.getData());
    }
}
