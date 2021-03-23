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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.DUMMY_TASK_ASSIGNMENT;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.DUMMY_TASK_ASSIGNMENT_PLANNER_1738;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.PLANNING_USER_ID;
import static org.kie.kogito.taskassigning.service.TaskState.READY;
import static org.kie.kogito.taskassigning.service.TaskState.RESERVED;
import static org.kie.kogito.taskassigning.service.TestUtil.mockExternalUser;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SolutionBuilderTest {

    private static final String TASK1 = "TASK1";
    private static final String TASK2 = "TASK2";
    private static final String TASK3 = "TASK3";
    private static final String TASK4 = "TASK4";
    private static final String TASK5 = "TASK5";
    private static final String TASK6 = "TASK6";
    private static final String TASK7 = "TASK7";
    private static final String TASK8 = "TASK8";
    private static final String TASK9 = "TASK9";
    private static final String TASK10 = "TASK10";
    private static final String TASK11 = "TASK11";

    private static final String USER1 = "USER1";
    private static final String USER2 = "USER2";
    private static final String USER3 = "USER3";
    private static final String USER4 = "USER4";
    private static final String USER_NOT_IN_THE_EXTERNAL_SYSTEM = "USER_NOT_IN_THE_EXTERNAL_SYSTEM";

    @Test
    void build() {
        List<TaskData> taskDataList = Arrays.asList(mockTaskData(TASK1, READY.value()),
                mockTaskData(TASK2, RESERVED.value(), USER2),
                mockTaskData(TASK3, READY.value()),
                mockTaskData(TASK4, READY.value()),
                mockTaskData(TASK5, RESERVED.value(), USER2),
                mockTaskData(TASK6, READY.value()),
                mockTaskData(TASK7, RESERVED.value(), USER4),
                mockTaskData(TASK8, READY.value()),
                mockTaskData(TASK9, RESERVED.value(), USER4),
                mockTaskData(TASK10, RESERVED.value(), USER1),
                mockTaskData(TASK11, RESERVED.value(), USER_NOT_IN_THE_EXTERNAL_SYSTEM));

        List<org.kie.kogito.taskassigning.user.service.api.User> externalUsers = Arrays.asList(mockExternalUser(USER1),
                mockExternalUser(USER2),
                mockExternalUser(USER3),
                mockExternalUser(USER4));

        TaskAssigningSolution solution = SolutionBuilder.newBuilder()
                .withTasks(taskDataList)
                .withUsers(externalUsers)
                .build();

        assertThat(solution.getTaskAssignmentList()).hasSize(13);
        assertThat(solution.getUserList()).hasSize(6);

        assertThatUserHasTask(solution, USER1, 1, 0, TASK10, 0, 1);
        assertThatUserHasTask(solution, USER2, 2, 0, TASK2, 0, 1);
        assertThatUserHasTask(solution, USER2, 2, 1, TASK5, 1, 2);
        assertThatUserNoTasks(solution, USER3);
        assertThatUserHasTask(solution, USER4, 2, 0, TASK7, 0, 1);
        assertThatUserHasTask(solution, USER4, 2, 1, TASK9, 1, 2);
        assertThatUserHasTask(solution, USER_NOT_IN_THE_EXTERNAL_SYSTEM, 1, 0, TASK11, 0, 1);
        assertThatUserNoTasks(solution, PLANNING_USER_ID);

        assertThatTaskIsNotAssigned(solution, TASK1);
        assertThatTaskIsAssignedToUser(solution, TASK2, USER2);
        assertThatTaskIsNotAssigned(solution, TASK3);
        assertThatTaskIsNotAssigned(solution, TASK4);
        assertThatTaskIsAssignedToUser(solution, TASK5, USER2);
        assertThatTaskIsNotAssigned(solution, TASK6);
        assertThatTaskIsAssignedToUser(solution, TASK7, USER4);
        assertThatTaskIsNotAssigned(solution, TASK8);
        assertThatTaskIsAssignedToUser(solution, TASK9, USER4);
        assertThatTaskIsAssignedToUser(solution, TASK10, USER1);
        assertThatTaskIsAssignedToUser(solution, TASK11, USER_NOT_IN_THE_EXTERNAL_SYSTEM);
        assertThatTaskIsNotAssigned(solution, DUMMY_TASK_ASSIGNMENT.getId());
        assertThatTaskIsNotAssigned(solution, DUMMY_TASK_ASSIGNMENT_PLANNER_1738.getId());
    }

    private void assertThatUserHasTask(TaskAssigningSolution solution, String userId,
            int expectedTasks, int expectedTaskPosition,
            String expectedTask, int expectedStartTimeInMinutes, int expectedEndTimeInMinutes) {
        User user = solution.getUserList().stream().filter(u -> userId.equals(u.getId())).findFirst().orElse(null);
        assertThat(user)
                .withFailMessage("User %s is not present in solution.", userId)
                .isNotNull();
        assertThat(user.getNextElement())
                .withFailMessage("User %s must have %s task assignments", userId, expectedTasks)
                .isNotNull();
        TaskAssignment nextElement = user.getNextElement();
        List<TaskAssignment> assignments = new ArrayList<>();
        while (nextElement != null) {
            assignments.add(nextElement);
            nextElement = nextElement.getNextElement();
        }
        assertThat(assignments.size())
                .withFailMessage("User %s must have %s task assignments, but have %s", userId, expectedTasks, assignments.size())
                .isEqualTo(expectedTasks);
        TaskAssignment taskAssignment = assignments.get(expectedTaskPosition);
        assertThat(taskAssignment.getId())
                .withFailMessage("User %s must have the task %s at the position %s", userId, expectedTask, expectedTaskPosition)
                .isEqualTo(expectedTask);
        assertThat(taskAssignment.getStartTimeInMinutes())
                .withFailMessage("Task %s must start at time %s", expectedTask, expectedStartTimeInMinutes)
                .isEqualTo(expectedStartTimeInMinutes);
        assertThat(taskAssignment.getEndTimeInMinutes())
                .withFailMessage("Task %s must finish at time %s", expectedTask, expectedEndTimeInMinutes)
                .isEqualTo(expectedEndTimeInMinutes);
    }

    private void assertThatUserNoTasks(TaskAssigningSolution solution, String userId) {
        User user = solution.getUserList().stream().filter(u -> userId.equals(u.getId())).findFirst().orElse(null);
        assertThat(user)
                .withFailMessage("User %s is not present in solution.", userId)
                .isNotNull();
        assertThat(user.getNextElement())
                .withFailMessage("User %s must not have task assignments", userId)
                .isNull();
    }

    private void assertThatTaskIsAssignedToUser(TaskAssigningSolution solution, String taskId, String userId) {
        TaskAssignment taskAssignment = solution.getTaskAssignmentList().stream()
                .filter(ta -> taskId.equals(ta.getId())).findFirst().orElse(null);
        assertThat(taskAssignment)
                .withFailMessage("Task %s is not present in solution.", taskId)
                .isNotNull();
        User user = solution.getUserList().stream().filter(u -> userId.equals(u.getId())).findFirst().orElse(null);
        assertThat(user)
                .withFailMessage("User %s is not present in solution.", userId)
                .isNotNull();
        assertThat(taskAssignment.getUser())
                .withFailMessage("Task %s must be assigned to user %s but is assigned to nobody.", taskId, userId)
                .isNotNull();
        assertThat(taskAssignment.getUser().getId())
                .withFailMessage("Task %s must be assigned to user %s.", taskId, userId)
                .isEqualTo(userId);
    }

    private void assertThatTaskIsNotAssigned(TaskAssigningSolution solution, String taskId) {
        TaskAssignment taskAssignment = solution.getTaskAssignmentList().stream()
                .filter(ta -> taskId.equals(ta.getId())).findFirst().orElse(null);
        assertThat(taskAssignment)
                .withFailMessage("Task %s is not present in solution.", taskId)
                .isNotNull();
        assertThat(taskAssignment.getUser())
                .withFailMessage("Task %s must not be assigned", taskId)
                .isNull();
    }

    private static TaskData mockTaskData(String taskId, String state, String actualOwner) {
        TaskData taskData = mock(TaskData.class);
        doReturn(taskId).when(taskData).getId();
        doReturn(state).when(taskData).getState();
        doReturn(actualOwner).when(taskData).getActualOwner();
        return taskData;
    }

    private static TaskData mockTaskData(String taskId, String state) {
        return mockTaskData(taskId, state, null);
    }
}
