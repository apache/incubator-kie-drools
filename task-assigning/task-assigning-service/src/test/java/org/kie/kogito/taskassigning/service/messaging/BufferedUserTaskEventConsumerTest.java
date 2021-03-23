/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BufferedUserTaskEventConsumerTest {

    private BufferedUserTaskEventConsumer userTaskEventConsumer;

    @Mock
    private Consumer<List<UserTaskEvent>> consumer;

    @Captor
    private ArgumentCaptor<List<UserTaskEvent>> eventsCaptor;

    @Mock
    private UserTaskEvent event1;

    @Mock
    private UserTaskEvent event2;

    @BeforeEach
    void setUp() {
        userTaskEventConsumer = new BufferedUserTaskEventConsumer();
        userTaskEventConsumer.setConsumer(consumer);
    }

    @Test
    void pause() {
        userTaskEventConsumer.pause();
        userTaskEventConsumer.accept(event1);
        userTaskEventConsumer.accept(event2);
        verify(consumer, never()).accept(anyList());
    }

    @Test
    void resume() {
        userTaskEventConsumer.pause();
        userTaskEventConsumer.accept(event1);
        userTaskEventConsumer.accept(event2);
        verify(consumer, never()).accept(anyList());
        userTaskEventConsumer.resume();
        verify(consumer).accept(eventsCaptor.capture());
        assertThat(eventsCaptor.getValue()).isNotNull();
        assertThat(eventsCaptor.getValue()).containsExactlyElementsOf(Arrays.asList(event1, event2));
    }

    @Test
    void pollEvents() {
        userTaskEventConsumer.pause();
        userTaskEventConsumer.accept(event1);
        userTaskEventConsumer.accept(event2);
        verify(consumer, never()).accept(anyList());
        List<UserTaskEvent> events = userTaskEventConsumer.pollEvents();
        assertThat(events)
                .hasSize(2)
                .containsExactlyElementsOf(Arrays.asList(event1, event2));
        userTaskEventConsumer.resume();
        verify(consumer, never()).accept(anyList());
    }

    @Test
    void queuedEvents() {
        userTaskEventConsumer.pause();
        userTaskEventConsumer.accept(event1);
        userTaskEventConsumer.accept(event2);
        assertThat(userTaskEventConsumer.queuedEvents()).isEqualTo(2);
    }
}
