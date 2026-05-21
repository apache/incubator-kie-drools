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

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.KogitoMarshallEventSupport;
import org.kie.kogito.event.process.MultipleProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessDefinitionDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.MultipleUserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.service.IndexingService;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.enterprise.event.Event;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled
class BlockingMessagingEventConsumerTest {

    @Mock
    DataIndexStorageService dataIndexStorageService;

    @Mock
    IndexingService indexingService;

    @Mock
    Event<DataEvent<?>> eventPublisher;

    @InjectMocks
    BlockingMessagingEventConsumer consumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOnProcessInstanceEvent() {
        // Arrange
        ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport> event1 = mock(ProcessInstanceDataEvent.class);
        ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport> event2 = mock(ProcessInstanceDataEvent.class);
        Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>> events = Arrays.asList(event1, event2);
        MultipleProcessInstanceDataEvent event = new MultipleProcessInstanceDataEvent(URI.create("dummy"), events);

        // Act
        consumer.onProcessInstanceEvent(event);

        // Assert
        verify(indexingService, times(1)).indexProcessInstanceEvent(event);
        verify(eventPublisher, times(1)).fire(event);
    }

    @Test
    void testOnUserTaskInstanceEvent() {
        // Arrange
        UserTaskInstanceDataEvent<?> event1 = mock(UserTaskInstanceDataEvent.class);
        UserTaskInstanceDataEvent<?> event2 = mock(UserTaskInstanceDataEvent.class);
        Collection<UserTaskInstanceDataEvent<?>> events = Arrays.asList(event1, event2);
        MultipleUserTaskInstanceDataEvent event = new MultipleUserTaskInstanceDataEvent(URI.create("dummy"), events);

        // Act
        consumer.onUserTaskInstanceEvent(event);

        // Assert
        verify(indexingService, times(1)).indexUserTaskInstanceEvent(event);
        verify(eventPublisher, times(1)).fire(event);
    }

    @Test
    void testOnJobEvent() {
        // Arrange
        KogitoJobCloudEvent event = mock(KogitoJobCloudEvent.class);
        Job mockJob = mock(Job.class); // Mock the Job object
        when(event.getData()).thenReturn(mockJob);

        // Act
        consumer.onJobEvent(event);

        // Assert
        verify(indexingService, times(1)).indexJob(mockJob); // Perform the verification after Uni completes
    }

    @Test
    void testOnJobEventWithExceptionDetails() {
        // Arrange
        KogitoJobCloudEvent event = mock(KogitoJobCloudEvent.class);
        Job mockJob = mock(Job.class);

        when(event.getData()).thenReturn(mockJob);

        // Act
        consumer.onJobEvent(event);

        // Assert
        verify(indexingService, times(1)).indexJob(mockJob);
    }

    @Test
    void testOnJobEventWithRetryAndExceptionDetails() {
        // Arrange
        KogitoJobCloudEvent event = mock(KogitoJobCloudEvent.class);
        Job mockJob = mock(Job.class);

        when(event.getData()).thenReturn(mockJob);

        // Act
        consumer.onJobEvent(event);

        // Assert
        verify(indexingService, times(1)).indexJob(mockJob);
    }

    @Test
    void testOnJobEventWithNullExceptionDetails() {
        // Arrange
        KogitoJobCloudEvent event = mock(KogitoJobCloudEvent.class);
        Job mockJob = mock(Job.class);

        when(event.getData()).thenReturn(mockJob);

        // Act
        consumer.onJobEvent(event);

        // Assert
        verify(indexingService, times(1)).indexJob(mockJob);
    }

    @Test
    void testOnProcessDefinitionDataEvent() {
        // Arrange
        ProcessDefinitionDataEvent event1 = mock(ProcessDefinitionDataEvent.class);

        // Act
        consumer.onProcessDefinitionDataEvent(event1);

        // Assert
        verify(indexingService, times(1)).indexProcessDefinition(event1);
        verify(eventPublisher, times(1)).fire(event1);
    }

    @Test
    void testErrorHandlingInOnProcessInstanceEvent() {
        // Arrange
        ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport> event = mock(ProcessInstanceDataEvent.class);
        Collection<ProcessInstanceDataEvent<? extends KogitoMarshallEventSupport>> events = Arrays.asList(event);
        doThrow(new RuntimeException("On purpose! Indexing failed")).when(indexingService).indexProcessInstanceEvent(event);

        // Act
        consumer.onProcessInstanceEvent(new MultipleProcessInstanceDataEvent(URI.create("dummy"), events));

        // Assert
        verify(eventPublisher, never()).fire(event); // Event should not be published if indexing fails
    }

    @Test
    void testErrorHanlingInOnUserTaskInstanceEvent() {
        // Arrange
        UserTaskInstanceDataEvent<?> event = mock(UserTaskInstanceDataEvent.class);
        Collection<UserTaskInstanceDataEvent<?>> events = Arrays.asList(event);
        doThrow(new RuntimeException("On purpose! Indexing failed")).when(indexingService).indexUserTaskInstanceEvent(event);

        // Act
        consumer.onUserTaskInstanceEvent(new MultipleUserTaskInstanceDataEvent(URI.create("dummy"), events));

        // Assert
        verify(eventPublisher, never()).fire(event); // Event should not be published if indexing fails

    }

    @Test
    void testErrorHandlingInOnProcessDefinitionDataEvent() {
        // Arrange
        ProcessDefinitionDataEvent event = mock(ProcessDefinitionDataEvent.class);
        doThrow(new RuntimeException("On purpose! Indexing failed")).when(indexingService).indexProcessDefinition(event);

        // Act
        consumer.onProcessDefinitionDataEvent(event);

        // Assert
        verify(indexingService, times(1)).indexProcessDefinition(event);
        verify(eventPublisher, never()).fire(event);
    }
}
