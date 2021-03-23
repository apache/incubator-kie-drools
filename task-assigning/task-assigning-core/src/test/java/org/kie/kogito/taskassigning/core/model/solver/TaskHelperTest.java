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
package org.kie.kogito.taskassigning.core.model.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.Group;
import org.kie.kogito.taskassigning.core.model.OrganizationalEntity;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.DUMMY_TASK_ASSIGNMENT;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.DUMMY_TASK_ASSIGNMENT_PLANNER_1738;
import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.isPotentialOwner;

class TaskHelperTest {

    private static final String USER_ID = "USER_ID";
    private static final String TASK_ID_1 = "TASK_ID_1";
    private static final String TASK_ID_2 = "TASK_ID_2";
    private static final String TASK_ID_3 = "TASK_ID_3";
    private static final String TASK_ID_4 = "TASK_ID_4";
    private static final String LABEL_NAME1 = "LABEL_NAME1";
    private static final String LABEL_NAME2 = "LABEL_NAME2";
    private static final String LABEL_VALUE1 = "LABEL_VALUE1";
    private static final Integer LABEL_VALUE2 = 2;

    private static final int SIZE = 2;

    private List<User> availableUsers;
    private List<Group> availableGroups;
    private Task task;

    static class LabelsCheckResult {

        private boolean hasAllLabels;
        private int matchingLabels;

        LabelsCheckResult(boolean hasAllLabels, int matchingLabels) {
            this.hasAllLabels = hasAllLabels;
            this.matchingLabels = matchingLabels;
        }
    }

    static Stream<Arguments> testParams() {
        return Stream.of(
                Arguments.of(LABEL_NAME1, new HashSet<>(Arrays.asList(LABEL_VALUE1, LABEL_VALUE2)), LABEL_NAME1, new HashSet<>(Arrays.asList(LABEL_VALUE1, LABEL_VALUE2)),
                        new LabelsCheckResult(true, 2)),
                Arguments.of(LABEL_NAME1, new HashSet<>(Collections.singletonList(LABEL_VALUE1)), LABEL_NAME1, new HashSet<>(Arrays.asList(LABEL_VALUE1, LABEL_VALUE2)),
                        new LabelsCheckResult(true, 1)),
                Arguments.of(LABEL_NAME1, Collections.emptySet(), LABEL_NAME1, new HashSet<>(Arrays.asList(LABEL_VALUE1, LABEL_VALUE2)), new LabelsCheckResult(true, 0)),
                Arguments.of(LABEL_NAME1, null, LABEL_NAME1, new HashSet<>(Arrays.asList(LABEL_VALUE1, LABEL_VALUE2)), new LabelsCheckResult(true, 0)),
                Arguments.of(LABEL_NAME1, null, LABEL_NAME1, Collections.emptySet(), new LabelsCheckResult(true, 0)),
                Arguments.of(LABEL_NAME1, null, LABEL_NAME1, null, new LabelsCheckResult(true, 0)),
                Arguments.of(LABEL_NAME1, new HashSet<>(Arrays.asList(LABEL_VALUE1, LABEL_VALUE2)), LABEL_NAME1, new HashSet<>(Collections.singletonList(LABEL_VALUE2)),
                        new LabelsCheckResult(false, 1)),
                Arguments.of(LABEL_NAME1, new HashSet<>(Arrays.asList(LABEL_VALUE1, LABEL_VALUE2)), LABEL_NAME1, new HashSet<>(), new LabelsCheckResult(false, 0)),
                Arguments.of(LABEL_NAME1, new HashSet<>(Arrays.asList(LABEL_VALUE1, LABEL_VALUE2)), LABEL_NAME1, null, new LabelsCheckResult(false, 0)),
                Arguments.of(LABEL_NAME1, new HashSet<>(Arrays.asList(LABEL_VALUE1, LABEL_VALUE2)), LABEL_NAME2, new HashSet<>(Arrays.asList(LABEL_VALUE1, LABEL_VALUE2)),
                        new LabelsCheckResult(false, 0)));
    }

    @BeforeEach
    void setUp() {
        availableUsers = buildUsers(SIZE);
        availableGroups = buildGroups(SIZE);
        List<OrganizationalEntity> potentialOwners = new ArrayList<>(availableUsers);
        potentialOwners.addAll(availableGroups);
        task = buildTask(potentialOwners);
    }

    @Test
    void isPotentialOwnerDirectAssignmentTrue() {
        for (User user : availableUsers) {
            assertThat(isPotentialOwner(task, user)).isTrue();
        }
    }

    @Test
    void isPotentialOwnerDirectAssignmentFalse() {
        for (User user : availableUsers) {
            task.getPotentialUsers().remove(user.getId());
            assertThat(isPotentialOwner(task, user)).isFalse();
        }
    }

    @Test
    void isPotentialOwnerInDirectAssignmentTrue() {
        for (User user : availableUsers) {
            task.getPotentialUsers().remove(user.getId());
            for (Group group : availableGroups) {
                user.getGroups().add(group);
                assertThat(isPotentialOwner(task, user)).isTrue();
                user.getGroups().remove(group);
            }
        }
    }

    @Test
    void isPotentialOwnerInDirectAssignmentFalse() {
        for (User user : availableUsers) {
            task.getPotentialUsers().remove(user.getId());
            for (Group group : availableGroups) {
                user.getGroups().add(group);
                assertThat(isPotentialOwner(task, user)).isTrue();
                user.getGroups().remove(group);
                assertThat(isPotentialOwner(task, user)).isFalse();
            }
        }
    }

    @Test
    void isPotentialOwnerOfTaskWithNoGroupsAndUserNoGroups() {
        Task task = Task.newBuilder().build();
        User user = availableUsers.get(0);
        assertThat(isPotentialOwner(task, user)).isFalse();
    }

    @Test
    void isPotentialOwnerOfTaskWithGroupsAndUserNoGroups() {
        Task task = Task.newBuilder().build();
        task.getPotentialGroups().add(availableGroups.get(0).getId());
        User user = availableUsers.get(0);
        assertThat(isPotentialOwner(task, user)).isFalse();
    }

    @Test
    void isPotentialOwnerOfTaskWithNoGroupsAndUserWithGroups() {
        Task task = Task.newBuilder().build();
        User user = availableUsers.get(0);
        user.getGroups().add(availableGroups.get(0));
        assertThat(isPotentialOwner(task, user)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("testParams")
    void hasAllLabels(String taskLabelName, Set<Object> taskLabelValues,
            String userLabelName, Set<Object> userLabelValues,
            LabelsCheckResult checkResult) {
        Task task = mockTask(taskLabelName, taskLabelValues);
        User user = mockUser(userLabelName, userLabelValues);
        assertThat(TaskHelper.hasAllLabels(task, user, taskLabelName)).isEqualTo(checkResult.hasAllLabels);
    }

    @ParameterizedTest
    @MethodSource("testParams")
    void matchingLabels(String taskLabelName, Set<Object> taskLabelValues,
            String userLabelName, Set<Object> userLabelValues,
            LabelsCheckResult checkResult) {
        Task task = mockTask(taskLabelName, taskLabelValues);
        User user = mockUser(userLabelName, userLabelValues);
        assertThat(TaskHelper.countMatchingLabels(task, user, taskLabelName)).isEqualTo(checkResult.matchingLabels);
    }

    @Test
    void extractTasks() {
        ChainElement chainElement = buildChainElement();
        List<TaskAssignment> result = TaskHelper.extractTaskAssignments(chainElement);
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0).getId()).isEqualTo(TASK_ID_1);
        assertThat(result.get(1).getId()).isEqualTo(TASK_ID_2);
        assertThat(result.get(2).getId()).isEqualTo(TASK_ID_3);
        assertThat(result.get(3).getId()).isEqualTo(TASK_ID_4);
    }

    @Test
    void extractTasksFiltered() {
        ChainElement chainElement = buildChainElement();
        List<TaskAssignment> result = TaskHelper.extractTaskAssignments(chainElement, testedTask -> testedTask.getId().equals(TASK_ID_1) || testedTask.getId().equals(TASK_ID_4));
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getId()).isEqualTo(TASK_ID_1);
        assertThat(result.get(1).getId()).isEqualTo(TASK_ID_4);
    }

    @Test
    void hasPinnedTasks() {
        ChainElement chainElement = buildChainElement();
        assertThat(TaskHelper.hasPinnedTasks(chainElement)).isTrue();
    }

    @Test
    void filterNonDummyAssignments() {
        List<TaskAssignment> taskAssignments = Arrays.asList(
                new TaskAssignment(Task.newBuilder().id(TASK_ID_1).build()),
                new TaskAssignment(Task.newBuilder().id(DUMMY_TASK_ASSIGNMENT.getId()).build()),
                new TaskAssignment(Task.newBuilder().id(TASK_ID_2).build()),
                new TaskAssignment(Task.newBuilder().id(DUMMY_TASK_ASSIGNMENT_PLANNER_1738.getId()).build()));
        List<TaskAssignment> result = TaskHelper.filterNonDummyAssignments(taskAssignments);
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isSameAs(taskAssignments.get(0));
        assertThat(result.get(1)).isSameAs(taskAssignments.get(2));
    }

    private ChainElement buildChainElement() {
        ChainElement chainElement = new TaskAssignment(Task.newBuilder().build());
        TaskAssignment taskAssignment1 = new TaskAssignment(Task.newBuilder().id(TASK_ID_1).build());
        TaskAssignment taskAssignment2 = new TaskAssignment(Task.newBuilder().id(TASK_ID_2).build());
        TaskAssignment taskAssignment3 = new TaskAssignment(Task.newBuilder().id(TASK_ID_3).build());
        taskAssignment3.setPinned(true);
        TaskAssignment taskAssignment4 = new TaskAssignment(Task.newBuilder().id(TASK_ID_4).build());
        taskAssignment2.setPinned(true);
        chainElement.setNextElement(taskAssignment1);
        taskAssignment1.setNextElement(taskAssignment2);
        taskAssignment2.setNextElement(taskAssignment3);
        taskAssignment3.setNextElement(taskAssignment4);
        return chainElement;
    }

    private static Task buildTask(List<OrganizationalEntity> potentialOwners) {
        Task task = Task.newBuilder().build();
        potentialOwners.forEach(potentialOwner -> {
            if (potentialOwner.isUser()) {
                task.getPotentialUsers().add(potentialOwner.getId());
            } else {
                task.getPotentialGroups().add(potentialOwner.getId());
            }
        });
        return task;
    }

    private static List<User> buildUsers(int size) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            users.add(new User("User" + i));
        }
        return users;
    }

    private static List<Group> buildGroups(int size) {
        List<Group> groupList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            groupList.add(new Group("Group" + i));
        }
        return groupList;
    }

    private static User mockUser(String labelName, Set<Object> labelValues) {
        User user = new User(USER_ID);
        user.getAttributes().put(labelName, labelValues);
        return user;
    }

    private static Task mockTask(String labelName, Set<Object> labelValues) {
        Task task = Task.newBuilder().id(TASK_ID_1).build();
        task.getAttributes().put(labelName, labelValues);
        return task;
    }
}
