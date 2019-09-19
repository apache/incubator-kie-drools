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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.service.IndexingService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReactiveMessagingEventConsumerTest {

    @Mock
    IndexingService service;

    @InjectMocks
    ReactiveMessagingEventConsumer consumer;

    @Test
    public void testOnProcessInstanceDomainEvent() {
        KogitoProcessCloudEvent event = mock(KogitoProcessCloudEvent.class);
        consumer.onProcessInstanceDomainEvent(event);
        verify(service).indexProcessInstanceModel(event);
    }

    @Test
    public void testOnProcessInstanceEvent() {
        KogitoProcessCloudEvent event = mock(KogitoProcessCloudEvent.class);
        consumer.onProcessInstanceEvent(event);
        verify(service).indexProcessInstance(event);
    }

    @Test
    public void testOnProcessInstanceDomainEventException() {
        KogitoProcessCloudEvent event = mock(KogitoProcessCloudEvent.class);
        doThrow(new RuntimeException()).when(service).indexProcessInstanceModel(event);
        consumer.onProcessInstanceDomainEvent(event);
        verify(service).indexProcessInstanceModel(event);
    }

    @Test
    public void testOnProcessInstanceEventException() {
        KogitoProcessCloudEvent event = mock(KogitoProcessCloudEvent.class);
        doThrow(new RuntimeException()).when(service).indexProcessInstance(event);
        consumer.onProcessInstanceEvent(event);
        verify(service).indexProcessInstance(event);
    }
}
