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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.Group;
import org.kie.kogito.taskassigning.core.model.ModelConstants;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

import static org.kie.kogito.taskassigning.core.model.DefaultLabels.AFFINITIES;
import static org.kie.kogito.taskassigning.core.model.DefaultLabels.SKILLS;

class DefaultTaskAssigningConstraintProviderTest {

    private static final String GROUP_ID_1 = "GROUP_ID_1";
    private static final String GROUP_ID_2 = "GROUP_ID_2";
    private static final String USER_ID = "USER_ID";
    private static final String SKILL_1 = "SKILL_1";
    private static final String SKILL_2 = "SKILL_2";
    private static final String HIGH_PRIORITY = "0";
    private static final String MEDIUM_PRIORITY = "3";
    private static final String LOW_PRIORITY = "7";
    private static final String AFFINITY_1 = "AFFINITY_1";
    private static final String AFFINITY_2 = "AFFINITY_2";

    private ConstraintVerifier<DefaultTaskAssigningConstraintProvider, TaskAssigningSolution> constraintVerifier;

    @BeforeEach
    void setUp() {
        constraintVerifier = ConstraintVerifier.build(new DefaultTaskAssigningConstraintProvider(),
                TaskAssigningSolution.class,
                TaskAssignment.class,
                ChainElement.class);
    }

    @ParameterizedTest
    @MethodSource("requiredPotentialOwnerParams")
    void requiredPotentialOwner(User user, Task task, int expectedPenalization) {
        constraintVerifier.verifyThat(DefaultTaskAssigningConstraintProvider::requiredPotentialOwner)
                .given(prepareTaskAssignment(user, task))
                .penalizesBy(expectedPenalization);
    }

    private static Stream<Arguments> requiredPotentialOwnerParams() {
        return Stream.of(
                Arguments.of(new User(USER_ID, false, Collections.singleton(new Group(GROUP_ID_1)), Collections.emptyMap()),
                        Task.newBuilder().potentialGroups(Collections.singleton(GROUP_ID_1)).build(),
                        1),
                Arguments.of(new User(USER_ID, true, Collections.singleton(new Group(GROUP_ID_1)), Collections.emptyMap()),
                        Task.newBuilder().build(),
                        1),
                Arguments.of(new User(USER_ID, true, Collections.singleton(new Group(GROUP_ID_1)), Collections.emptyMap()),
                        Task.newBuilder().potentialGroups(Collections.singleton(GROUP_ID_2)).build(),
                        1),
                Arguments.of(ModelConstants.PLANNING_USER,
                        Task.newBuilder().build(),
                        0),
                Arguments.of(ModelConstants.PLANNING_USER,
                        Task.newBuilder().potentialGroups(Collections.singleton(GROUP_ID_1)).build(),
                        0),
                Arguments.of(new User(USER_ID, true, Collections.singleton(new Group(GROUP_ID_1)), Collections.emptyMap()),
                        Task.newBuilder().potentialGroups(Collections.singleton(GROUP_ID_1)).build(),
                        0)

        );
    }

    @ParameterizedTest
    @MethodSource("requiredSkillsParams")
    void requiredSkills(User user, Task task, int expectedPenalization) {
        constraintVerifier.verifyThat(DefaultTaskAssigningConstraintProvider::requiredSkills)
                .given(prepareTaskAssignment(user, task))
                .penalizesBy(expectedPenalization);
    }

    private static Stream<Arguments> requiredSkillsParams() {
        return Stream.of(
                Arguments.of(new User(USER_ID, false, Collections.emptySet(), mockAttributes(SKILLS.name(), Collections.singleton(SKILL_1))),
                        Task.newBuilder().attributes(mockAttributes(SKILLS.name(), Collections.singleton(SKILL_1))).build(),
                        1),
                Arguments.of(new User(USER_ID, true, Collections.emptySet(), Collections.emptyMap()),
                        Task.newBuilder().attributes(mockAttributes(SKILLS.name(), Collections.singleton(SKILL_1))).build(),
                        1),
                Arguments.of(new User(USER_ID, true, Collections.emptySet(), mockAttributes(SKILLS.name(), Collections.singleton(SKILL_2))),
                        Task.newBuilder().attributes(mockAttributes(SKILLS.name(), Collections.singleton(SKILL_1))).build(),
                        1),
                Arguments.of(new User(USER_ID, true, Collections.emptySet(), mockAttributes(SKILLS.name(), Collections.singleton(SKILL_1))),
                        Task.newBuilder().attributes(mockAttributes(SKILLS.name(), Collections.singleton(SKILL_1))).build(),
                        0),
                Arguments.of(new User(USER_ID, true, Collections.emptySet(), mockAttributes(SKILLS.name(), Collections.singleton(SKILL_1))),
                        Task.newBuilder().build(),
                        0));
    }

    @ParameterizedTest
    @MethodSource("planningUserAssignmentParams")
    void planningUserAssignment(User user, Task task, int expectedPenalization) {
        constraintVerifier.verifyThat(DefaultTaskAssigningConstraintProvider::planningUserAssignment)
                .given(prepareTaskAssignment(user, task))
                .penalizesBy(expectedPenalization);
    }

    private static Stream<Arguments> planningUserAssignmentParams() {
        return Stream.of(
                Arguments.of(ModelConstants.PLANNING_USER, Task.newBuilder().build(), 1),
                Arguments.of(new User(), Task.newBuilder().build(), 0));
    }

    @ParameterizedTest
    @MethodSource("highLevelPriorityParams")
    void highLevelPriority(User user, Task task, int endTimeInMinutes, int expectedPenalization) {
        constraintVerifier.verifyThat(DefaultTaskAssigningConstraintProvider::highLevelPriority)
                .given(prepareTaskAssignment(user, task, endTimeInMinutes))
                .penalizesBy(expectedPenalization);
    }

    private static Stream<Arguments> highLevelPriorityParams() {
        return Stream.of(
                Arguments.of(new User(), Task.newBuilder().priority(HIGH_PRIORITY).build(), 10, 10),
                Arguments.of(new User(), Task.newBuilder().priority(MEDIUM_PRIORITY).build(), 10, 0),
                Arguments.of(new User(), Task.newBuilder().priority(LOW_PRIORITY).build(), 10, 0));
    }

    @ParameterizedTest
    @MethodSource("desiredAffinitiesParams")
    void desiredAffinities(User user, Task task, int expectedReward) {
        constraintVerifier.verifyThat(DefaultTaskAssigningConstraintProvider::desiredAffinities)
                .given(prepareTaskAssignment(user, task))
                .rewardsWith(expectedReward);
    }

    private static Stream<Arguments> desiredAffinitiesParams() {
        return Stream.of(
                Arguments.of(new User(USER_ID, false, Collections.emptySet(), mockAttributes(AFFINITIES.name(), Collections.singleton(AFFINITY_1))),
                        Task.newBuilder().attributes(mockAttributes(AFFINITIES.name(), Collections.singleton(AFFINITY_1))).build(),
                        0),
                Arguments.of(new User(USER_ID, true, Collections.emptySet(), Collections.emptyMap()),
                        Task.newBuilder().attributes(mockAttributes(AFFINITIES.name(), Collections.singleton(AFFINITY_1))).build(),
                        0),
                Arguments.of(new User(USER_ID, true, Collections.emptySet(), mockAttributes(AFFINITIES.name(), Collections.singleton(AFFINITY_2))),
                        Task.newBuilder().attributes(mockAttributes(AFFINITIES.name(), Collections.singleton(AFFINITY_1))).build(),
                        0),
                Arguments.of(new User(USER_ID, true, Collections.emptySet(), mockAttributes(AFFINITIES.name(), Collections.singleton(AFFINITY_1))),
                        Task.newBuilder().attributes(mockAttributes(AFFINITIES.name(), Collections.singleton(AFFINITY_1))).build(),
                        1),
                Arguments.of(new User(USER_ID, true, Collections.emptySet(), mockAttributes(AFFINITIES.name(), new HashSet<>(Arrays.asList(AFFINITY_1, AFFINITY_2)))),
                        Task.newBuilder().attributes(mockAttributes(AFFINITIES.name(), new HashSet<>(Arrays.asList(AFFINITY_1, AFFINITY_2)))).build(),
                        2));
    }

    @ParameterizedTest
    @MethodSource("minimizeMakespanParams")
    void minimizeMakespan(TaskAssignment taskAssignment, int expectedPenalization) {
        constraintVerifier.verifyThat(DefaultTaskAssigningConstraintProvider::minimizeMakespan)
                .given(taskAssignment)
                .penalizesBy(expectedPenalization);
    }

    private static Stream<Arguments> minimizeMakespanParams() {
        TaskAssignment taskAssignmentAtTheEnd = new TaskAssignment();
        taskAssignmentAtTheEnd.setEndTimeInMinutes(7);
        taskAssignmentAtTheEnd.setNextElement(null);

        TaskAssignment taskAssignment = new TaskAssignment();
        taskAssignment.setEndTimeInMinutes(6);
        taskAssignment.setNextElement(taskAssignmentAtTheEnd);

        taskAssignmentAtTheEnd.setPreviousElement(taskAssignment);
        taskAssignment.setPreviousElement(new User());

        return Stream.of(
                Arguments.of(taskAssignment, 0),
                Arguments.of(taskAssignmentAtTheEnd, 49));
    }

    @ParameterizedTest
    @MethodSource("mediumLevelPriorityParams")
    void mediumLevelPriority(User user, Task task, int endTimeInMinutes, int expectedPenalization) {
        constraintVerifier.verifyThat(DefaultTaskAssigningConstraintProvider::mediumLevelPriority)
                .given(prepareTaskAssignment(user, task, endTimeInMinutes))
                .penalizesBy(expectedPenalization);
    }

    private static Stream<Arguments> mediumLevelPriorityParams() {
        return Stream.of(
                Arguments.of(new User(), Task.newBuilder().priority(HIGH_PRIORITY).build(), 10, 0),
                Arguments.of(new User(), Task.newBuilder().priority(MEDIUM_PRIORITY).build(), 10, 10),
                Arguments.of(new User(), Task.newBuilder().priority(LOW_PRIORITY).build(), 10, 0));
    }

    @ParameterizedTest
    @MethodSource("lowLevelPriorityParams")
    void lowLevelPriority(User user, Task task, int endTimeInMinutes, int expectedPenalization) {
        constraintVerifier.verifyThat(DefaultTaskAssigningConstraintProvider::lowLevelPriority)
                .given(prepareTaskAssignment(user, task, endTimeInMinutes))
                .penalizesBy(expectedPenalization);
    }

    private static Stream<Arguments> lowLevelPriorityParams() {
        return Stream.of(
                Arguments.of(new User(), Task.newBuilder().priority(HIGH_PRIORITY).build(), 10, 0),
                Arguments.of(new User(), Task.newBuilder().priority(MEDIUM_PRIORITY).build(), 10, 0),
                Arguments.of(new User(), Task.newBuilder().priority(LOW_PRIORITY).build(), 10, 10));
    }

    private static HashMap<String, Object> mockAttributes(String labelName, Set<Object> values) {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(labelName, values);
        return attributes;
    }

    private static TaskAssignment prepareTaskAssignment(User user, Task task) {
        TaskAssignment taskAssignment = new TaskAssignment(task);
        taskAssignment.setUser(user);
        taskAssignment.setPreviousElement(new User());
        return taskAssignment;
    }

    private static TaskAssignment prepareTaskAssignment(User user, Task task, int endTimeInMinutes) {
        TaskAssignment taskAssignment = prepareTaskAssignment(user, task);
        taskAssignment.setEndTimeInMinutes(endTimeInMinutes);
        return taskAssignment;
    }
}
