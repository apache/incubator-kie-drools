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

package org.kie.kogito.taskassigning.service.messaging;

import java.time.ZonedDateTime;

import javax.enterprise.event.Event;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.service.TaskAssigningException;
import org.kie.kogito.taskassigning.service.TaskAssigningService;
import org.kie.kogito.taskassigning.service.event.TaskAssigningServiceEventConsumer;
import org.kie.kogito.taskassigning.service.event.TaskDataEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.service.TestUtil.parseZonedDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReactiveMessagingEventConsumerTest {

    private static final String TASK_ID = "TASK_ID";
    private static final ZonedDateTime LAST_MODIFICATION_DATE = parseZonedDateTime("2021-03-11T15:00:00.001Z");

    @Mock
    private Event<TaskAssigningService.FailFastRequestEvent> failFastRequestEvent;

    @Mock
    private Message<UserTaskEvent> message;

    @Captor
    private ArgumentCaptor<TaskDataEvent> taskDataEventCaptor;

    @Captor
    private ArgumentCaptor<TaskAssigningService.FailFastRequestEvent> failFastRequestEventCaptor;

    @Captor
    private ArgumentCaptor<TaskAssigningException> exceptionCaptor;

    @Mock
    private TaskAssigningServiceEventConsumer taskAssigningServiceEventConsumer;

    private ReactiveMessagingEventConsumer consumer;

    @BeforeEach
    void setUp() {
        taskAssigningServiceEventConsumer = mock(TaskAssigningServiceEventConsumer.class);
        consumer = new ReactiveMessagingEventConsumer(taskAssigningServiceEventConsumer, failFastRequestEvent);
    }

    @Test
    void onUserTaskEvent() {
        UserTaskEvent event = new UserTaskEvent();
        event.setTaskId(TASK_ID);
        event.setLastUpdate(LAST_MODIFICATION_DATE);
        doReturn(event).when(message).getPayload();

        consumer.onUserTaskEvent(message);
        verify(taskAssigningServiceEventConsumer).accept(taskDataEventCaptor.capture());
        assertThat(taskDataEventCaptor.getValue().getTaskId()).isEqualTo(TASK_ID);
        assertThat(taskDataEventCaptor.getValue().getEventTime()).isEqualTo(LAST_MODIFICATION_DATE);
        verify(message).ack();
    }

    @Test
    void failFast() {
        consumer.failFast();
        consumer.onUserTaskEvent(message);
        verify(message).nack(exceptionCaptor.capture());
        assertThat(exceptionCaptor.getValue())
                .isInstanceOf(TaskAssigningException.class)
                .hasMessageStartingWith("Task assigning service is in fail fast mode");

    }

    @Test
    void onUserTaskEventFailure() {
        UserTaskEvent event = new UserTaskEvent();
        event.setTaskId(TASK_ID);
        event.setLastUpdate(LAST_MODIFICATION_DATE);
        doReturn(event).when(message).getPayload();
        String error = "Generic invented error";
        String expectedErrorMessage = "An error was produced during a UserTaskEvent event processing: " + error;
        doThrow(new RuntimeException(error)).when(taskAssigningServiceEventConsumer).accept(any());
        consumer.onUserTaskEvent(message);
        verify(message).nack(exceptionCaptor.capture());
        assertThat(exceptionCaptor.getValue())
                .hasMessage(expectedErrorMessage);
        verify(failFastRequestEvent).fire(failFastRequestEventCaptor.capture());
        assertThat(failFastRequestEventCaptor.getValue().getCause())
                .isNotNull()
                .isInstanceOf(TaskAssigningException.class)
                .hasMessage(expectedErrorMessage);
    }
}
