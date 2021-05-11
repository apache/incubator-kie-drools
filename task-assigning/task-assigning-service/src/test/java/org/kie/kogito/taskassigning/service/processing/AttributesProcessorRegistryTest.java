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

package org.kie.kogito.taskassigning.service.processing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.model.processing.AttributesProcessor;
import org.kie.kogito.taskassigning.model.processing.TaskAttributesProcessor;
import org.kie.kogito.taskassigning.model.processing.TaskInfo;
import org.kie.kogito.taskassigning.model.processing.UserAttributesProcessor;
import org.kie.kogito.taskassigning.user.service.User;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AttributesProcessorRegistryTest {

    private static final String USER_ID = "USER_ID";
    private static final String TASK_ID = "TASK_ID";

    @Mock
    private Instance<AttributesProcessor<?>> processorInstance;

    private List<UserAttributesProcessor> userProcessors;

    private List<TaskAttributesProcessor> taskProcessors;

    private AttributesProcessorRegistry registry;

    @Captor
    private ArgumentCaptor<TaskInfo> taskInfoCaptor;

    @BeforeEach
    void setUp() {
        userProcessors = Arrays.asList(
                mockUserProcessor(true, 4),
                mockUserProcessor(true, 1),
                mockUserProcessor(false, 2),
                mockUserProcessor(true, 0),
                mockUserProcessor(false, 3));

        taskProcessors = Arrays.asList(
                mockTaskProcessor(false, 4),
                mockTaskProcessor(false, 1),
                mockTaskProcessor(false, 2),
                mockTaskProcessor(false, 0),
                mockTaskProcessor(true, 6),
                mockTaskProcessor(true, 5));

        List<AttributesProcessor<?>> processorList = new ArrayList<>(userProcessors);
        processorList.addAll(taskProcessors);
        doReturn(processorList.iterator()).when(processorInstance).iterator();
        registry = new AttributesProcessorRegistry(processorInstance);
    }

    @Test
    void applyUserAttributesProcessor() {
        User user = mock(User.class);
        Map<String, Object> attributes = new HashMap<>();
        registry.applyAttributesProcessor(user, new HashMap<>());
        InOrder inOrder = inOrder(
                userProcessors.get(0),
                userProcessors.get(1),
                userProcessors.get(2),
                userProcessors.get(3),
                userProcessors.get(4));
        inOrder.verify(userProcessors.get(3)).process(user, attributes);
        inOrder.verify(userProcessors.get(1)).process(user, attributes);
        inOrder.verify(userProcessors.get(0)).process(user, attributes);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void applyUserAttributesProcessorWithFailure() {
        User user = mock(User.class);
        doReturn(USER_ID).when(user).getId();
        Map<String, Object> attributes = new HashMap<>();
        doThrow(new RuntimeException("Internal user processor error")).when(userProcessors.get(3)).process(user, attributes);
        Assertions.assertThatThrownBy(() -> registry.applyAttributesProcessor(user, attributes))
                .hasMessageStartingWith("An error was produced during a user processor execution")
                .hasMessageContaining("userId: %s", USER_ID);
    }

    @Test
    void applyTaskAttributesProcessor() {
        Task task = mock(Task.class);
        doReturn(TASK_ID).when(task).getId();
        Map<String, Object> attributes = new HashMap<>();
        registry.applyAttributesProcessor(task, new HashMap<>());
        InOrder inOrder = inOrder(
                taskProcessors.get(0),
                taskProcessors.get(1),
                taskProcessors.get(2),
                taskProcessors.get(3),
                taskProcessors.get(4),
                taskProcessors.get(5));
        inOrder.verify(taskProcessors.get(5)).process(taskInfoCaptor.capture(), eq(attributes));
        assertThat(taskInfoCaptor.getValue().getTaskId()).isEqualTo(TASK_ID);
        inOrder.verify(taskProcessors.get(4)).process(taskInfoCaptor.capture(), eq(attributes));
        assertThat(taskInfoCaptor.getValue().getTaskId()).isEqualTo(TASK_ID);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void applyTaskAttributesProcessorWithFailure() {
        Task task = mock(Task.class);
        doReturn(TASK_ID).when(task).getId();
        Map<String, Object> attributes = new HashMap<>();
        doThrow(new RuntimeException("Internal task processor error"))
                .when(taskProcessors.get(5)).process(taskInfoCaptor.capture(), eq(attributes));
        Assertions.assertThatThrownBy(() -> registry.applyAttributesProcessor(task, attributes))
                .hasMessageStartingWith("An error was produced during a task processor execution")
                .hasMessageContaining("taskId: %s", TASK_ID);
    }

    private static UserAttributesProcessor mockUserProcessor(boolean enabled, int priority) {
        UserAttributesProcessor processor = mock(UserAttributesProcessor.class);
        mockCommonAttributes(processor, enabled, priority);
        return processor;
    }

    private static TaskAttributesProcessor mockTaskProcessor(boolean enabled, int priority) {
        TaskAttributesProcessor processor = mock(TaskAttributesProcessor.class);
        mockCommonAttributes(processor, enabled, priority);
        return processor;
    }

    private static void mockCommonAttributes(AttributesProcessor<?> processorMock, boolean enabled, int priority) {
        lenient().doReturn(enabled).when(processorMock).isEnabled();
        lenient().doReturn(priority).when(processorMock).getPriority();
    }
}