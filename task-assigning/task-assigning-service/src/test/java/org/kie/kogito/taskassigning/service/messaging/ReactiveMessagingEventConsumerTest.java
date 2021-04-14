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
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.service.event.TaskAssigningServiceEventConsumer;
import org.kie.kogito.taskassigning.service.event.TaskDataEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.service.TestUtil.parseZonedDateTime;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReactiveMessagingEventConsumerTest {

    private static final String TASK_ID = "TASK_ID";
    private static final ZonedDateTime LAST_MODIFICATION_DATE = parseZonedDateTime("2021-03-11T15:00:00.001Z");

    @Captor
    private ArgumentCaptor<TaskDataEvent> taskDataEventCaptor;

    @Test
    @Timeout(10)
    void onUserTaskEvent() throws Exception {
        TaskAssigningServiceEventConsumer taskAssigningServiceEventConsumer = mock(TaskAssigningServiceEventConsumer.class);
        ReactiveMessagingEventConsumer consumer = spy(new ReactiveMessagingEventConsumer(taskAssigningServiceEventConsumer));
        UserTaskEvent event = new UserTaskEvent();
        event.setTaskId(TASK_ID);
        event.setLastUpdate(LAST_MODIFICATION_DATE);
        Message<UserTaskEvent> message = Message.of(event);
        CompletionStage<Void> stage = consumer.onUserTaskEvent(message);
        stage.toCompletableFuture().get();
        verify(taskAssigningServiceEventConsumer).accept(taskDataEventCaptor.capture());
        assertThat(taskDataEventCaptor.getValue().getTaskId()).isEqualTo(TASK_ID);
        assertThat(taskDataEventCaptor.getValue().getEventTime()).isEqualTo(LAST_MODIFICATION_DATE);
    }
}
