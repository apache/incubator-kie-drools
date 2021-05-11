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

package org.kie.kogito.taskassigning.service;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance;
import org.kie.kogito.taskassigning.user.service.User;
import org.kie.kogito.taskassigning.user.service.UserServiceConnector;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolutionDataLoaderTest {

    private static final String UNEXPECTED_SERVICE_ERROR = "UNEXPECTED_SERVICE_ERROR";
    private static final String TASK_ID = "TASK_ID";
    private static final int PAGE_SIZE = 10;

    @Mock
    private TaskServiceConnector taskServiceConnector;

    @Mock
    private UserServiceConnector userServiceConnector;

    private SolutionDataLoader solutionDataLoader;

    @BeforeEach
    void setUp() {
        solutionDataLoader = new SolutionDataLoader(taskServiceConnector, userServiceConnector);
    }

    @Test
    void loadSolutionDataSuccessful() throws Exception {
        List<UserTaskInstance> taskServiceResults = Collections.singletonList(createUserTaskInstance());
        List<User> userServiceResults = Collections.singletonList(createExternalUser());
        doReturn(taskServiceResults).when(taskServiceConnector).findAllTasks(anyList(), eq(PAGE_SIZE));
        doReturn(userServiceResults).when(userServiceConnector).findAllUsers();
        solutionDataLoader.loadSolutionData(true, true, PAGE_SIZE)
                .thenApply(result -> {
                    assertThat(result.getTasks())
                            .isNotNull()
                            .hasSize(1)
                            .element(0)
                            .isNotNull();
                    assertThat(result.getTasks().get(0).getId()).isEqualTo(TASK_ID);
                    assertThat(result.getUsers()).isSameAs(userServiceResults);
                    return null;
                }).toCompletableFuture().get();
    }

    @Test
    void loadSolutionDataWithUnsuccessfulTasksQuery() {
        doThrow(new RuntimeException(UNEXPECTED_SERVICE_ERROR)).when(taskServiceConnector).findAllTasks(anyList(), eq(PAGE_SIZE));
        assertThatThrownBy(() -> solutionDataLoader.loadSolutionData(true, true, PAGE_SIZE).toCompletableFuture().get())
                .hasMessageContaining(UNEXPECTED_SERVICE_ERROR)
                .hasMessageContaining("Task Service");
        verify(taskServiceConnector).findAllTasks(anyList(), eq(PAGE_SIZE));
        verify(userServiceConnector, never()).findAllUsers();
    }

    @Test
    void loadSolutionDataWithUnsuccessfulUsersQuery() {
        doReturn(Collections.emptyList()).when(taskServiceConnector).findAllTasks(any(), eq(PAGE_SIZE));
        doThrow(new RuntimeException(UNEXPECTED_SERVICE_ERROR)).when(userServiceConnector).findAllUsers();
        assertThatThrownBy(() -> solutionDataLoader.loadSolutionData(true, true, PAGE_SIZE).toCompletableFuture().get())
                .hasMessageContaining(UNEXPECTED_SERVICE_ERROR)
                .hasMessageContaining("User Service");
        verify(taskServiceConnector).findAllTasks(anyList(), eq(PAGE_SIZE));
        verify(userServiceConnector).findAllUsers();
    }

    private static UserTaskInstance createUserTaskInstance() {
        UserTaskInstance userTaskInstance = new UserTaskInstance();
        userTaskInstance.setId(TASK_ID);
        return userTaskInstance;
    }

    private static org.kie.kogito.taskassigning.user.service.User createExternalUser() {
        return mock(org.kie.kogito.taskassigning.user.service.User.class);
    }
}
