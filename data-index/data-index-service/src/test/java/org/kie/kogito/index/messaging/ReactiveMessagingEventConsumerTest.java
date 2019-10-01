/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.service.IndexingService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.format;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.kie.kogito.index.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.TestUtils.getUserTaskCloudEvent;
import static org.kie.kogito.index.messaging.ReactiveMessagingEventConsumer.KOGITO_DOMAIN_EVENTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReactiveMessagingEventConsumerTest {

    @Mock
    IndexingService service;

    @Mock
    EventBus eventBus;

    @InjectMocks
    ReactiveMessagingEventConsumer consumer;

    @Test
    public void testOnProcessInstanceDomainEvent() throws Exception {
        when(eventBus.send(any(), any(), any(Handler.class))).thenAnswer(invocation -> {
            ((Handler) invocation.getArgument(2)).handle(null);
            return null;
        });

        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        KogitoProcessCloudEvent event = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, null, null, null);

        consumer.onProcessInstanceDomainEvent(event).toCompletableFuture().get();
        ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
        verify(eventBus).send(eq(format(KOGITO_DOMAIN_EVENTS, processId)), captor.capture(), any(Handler.class));

        assertThatJson(captor.getValue().toString())
                .isObject()
                .containsEntry("id", processInstanceId)
                .containsEntry("processId", processId);
    }

    @Test
    public void testOnUserTaskInstanceDomainEvent() throws Exception {
        when(eventBus.send(any(), any(), any(Handler.class))).thenAnswer(invocation -> {
            ((Handler) invocation.getArgument(2)).handle(null);
            return null;
        });

        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        KogitoUserTaskCloudEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null);

        consumer.onUserTaskInstanceDomainEvent(event).toCompletableFuture().get();
        ArgumentCaptor<ObjectNode> captor = ArgumentCaptor.forClass(ObjectNode.class);
        verify(eventBus).send(eq(format(KOGITO_DOMAIN_EVENTS, processId)), captor.capture(), any(Handler.class));

        assertThatJson(captor.getValue().toString())
                .isObject()
                .containsEntry("id", processInstanceId)
                .containsEntry("processId", processId);
    }

    @Test
    public void testOnProcessInstanceEvent() throws Exception {
        KogitoProcessCloudEvent event = mock(KogitoProcessCloudEvent.class);
        consumer.onProcessInstanceEvent(event).toCompletableFuture().get();
        verify(service).indexProcessInstance(event.getData());
    }

    @Test
    public void testOnUserTaskInstanceEvent() throws Exception {
        KogitoUserTaskCloudEvent event = mock(KogitoUserTaskCloudEvent.class);
        consumer.onUserTaskInstanceEvent(event).toCompletableFuture().get();
        verify(service).indexUserTaskInstance(event.getData());
    }
}
