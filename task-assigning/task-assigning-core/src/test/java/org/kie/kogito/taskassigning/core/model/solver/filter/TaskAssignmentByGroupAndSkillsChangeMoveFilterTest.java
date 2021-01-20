/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.core.model.solver.filter;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.taskassigning.core.model.ModelConstants;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockGroup;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockTask;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockUser;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskAssignmentByGroupAndSkillsChangeMoveFilterTest {

    private static final String GROUP = "GROUP";
    private static final String SKILL = "SKILL";
    private static final String USER = "USER";

    private ScoreDirector<TaskAssigningSolution> scoreDirector;

    private TaskByGroupAndSkillsChangeMoveFilter filter;

    static Stream<Arguments> testParams() {
        return Stream.of(
                Arguments.of(
                        mockChangeMove(
                                mockTask(Collections.emptyList(), Collections.emptySet()),
                                ModelConstants.PLANNING_USER),
                        true),

                Arguments.of(
                        mockChangeMove(
                                mockTask(Collections.emptyList(), Collections.emptySet()),
                                mockUser(USER, false, Collections.emptyList(), Collections.emptySet())),
                        false),

                Arguments.of(
                        mockChangeMove(
                                mockTask(Collections.emptyList(), Collections.emptySet()),
                                mockUser(USER, true, Collections.emptyList(), Collections.emptySet())),
                        false),

                Arguments.of(
                        mockChangeMove(
                                mockTask(Collections.singletonList(mockUser(USER, true, Collections.emptyList(), Collections.emptySet())), Collections.emptySet()),
                                mockUser(USER, false, Collections.emptyList(), Collections.emptySet())),
                        false),

                Arguments.of(
                        mockChangeMove(
                                mockTask(Collections.singletonList(mockUser(USER, true, Collections.emptyList(), Collections.emptySet())), Collections.emptySet()),
                                mockUser(USER, true, Collections.emptyList(), Collections.emptySet())),
                        true),

                Arguments.of(
                        mockChangeMove(
                                mockTask(Collections.singletonList(mockUser(USER, true, Collections.emptyList(), Collections.emptySet())), Collections.singleton(SKILL)),
                                mockUser(USER, true, Collections.emptyList(), Collections.emptySet())),
                        false),

                Arguments.of(
                        mockChangeMove(
                                mockTask(Collections.singletonList(mockUser(USER, true, Collections.emptyList(), Collections.emptySet())), Collections.singleton(SKILL)),
                                mockUser(USER, true, Collections.emptyList(), Collections.singleton(SKILL))),
                        true),

                Arguments.of(
                        mockChangeMove(
                                mockTask(Collections.singletonList(mockGroup(GROUP)), Collections.emptySet()),
                                mockUser(USER, true, Collections.emptyList(), Collections.emptySet())),
                        false),

                Arguments.of(
                        mockChangeMove(
                                mockTask(Collections.singletonList(mockGroup(GROUP)), Collections.emptySet()),
                                mockUser(USER, true, Collections.singletonList(mockGroup(GROUP)), Collections.emptySet())),
                        true)
        );
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        scoreDirector = mock(ScoreDirector.class);
        filter = new TaskByGroupAndSkillsChangeMoveFilter();
    }

    @ParameterizedTest
    @MethodSource("testParams")
    void accept(ChangeMove<TaskAssigningSolution> changeMove, boolean moveAccepted) {
        assertThat(filter.accept(scoreDirector, changeMove)).isEqualTo(moveAccepted);
    }

    @SuppressWarnings("unchecked")
    private static ChangeMove<TaskAssigningSolution> mockChangeMove(Task task, User user) {
        ChangeMove<TaskAssigningSolution> changeMove = mock(ChangeMove.class);
        TaskAssignment element = mock(TaskAssignment.class);
        when(element.isTaskAssignment()).thenReturn(true);
        when(element.getTask()).thenReturn(task);
        when(element.getUser()).thenReturn(user);
        when(changeMove.getToPlanningValue()).thenReturn(element);
        when(changeMove.getEntity()).thenReturn(element);
        return changeMove;
    }
}
