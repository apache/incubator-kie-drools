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
package org.kie.kogito.taskassigning.core.model.solver.condition;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.taskassigning.core.model.ModelConstants;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockGroup;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockTask;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockUser;

class TaskAssigningConditionsTest {

    private static final String GROUP1 = "GROUP1";
    private static final String SKILL1 = "SKILL1";
    private static final String USER1 = "USER1";

    static class ConditionsCheckResult {

        private boolean meetsPotentialOwner;
        private boolean meetRequiredSkills;

        ConditionsCheckResult(boolean meetsPotentialOwner, boolean meetRequiredSkills) {
            this.meetsPotentialOwner = meetsPotentialOwner;
            this.meetRequiredSkills = meetRequiredSkills;
        }
    }

    static Stream<Arguments> testParams() {
        return Stream.of(
                Arguments.of(mockTask(Collections.emptyList(), Collections.emptySet()),
                        null,
                        new ConditionsCheckResult(false, false)),

                Arguments.of(mockTask(Collections.emptyList(), Collections.emptySet()),
                        ModelConstants.PLANNING_USER,
                        new ConditionsCheckResult(true, true)),

                Arguments.of(mockTask(Collections.emptyList(), Collections.emptySet()),
                        mockUser(USER1, false, Collections.emptyList(), Collections.emptySet()),
                        new ConditionsCheckResult(false, false)),

                Arguments.of(mockTask(Collections.emptyList(), Collections.emptySet()),
                        mockUser(USER1, true, Collections.emptyList(), Collections.emptySet()),
                        new ConditionsCheckResult(false, true)),

                Arguments.of(mockTask(Collections.singletonList(mockUser(USER1, true, Collections.emptyList(), Collections.emptySet())), Collections.emptySet()),
                        mockUser(USER1, false, Collections.emptyList(), Collections.emptySet()),
                        new ConditionsCheckResult(false, false)),

                Arguments.of(mockTask(Collections.singletonList(mockUser(USER1, true, Collections.emptyList(), Collections.emptySet())), Collections.emptySet()),
                        mockUser(USER1, true, Collections.emptyList(), Collections.emptySet()),
                        new ConditionsCheckResult(true, true)),

                Arguments.of(mockTask(Collections.singletonList(mockUser(USER1, true, Collections.emptyList(), Collections.emptySet())), Collections.singleton(SKILL1)),
                        mockUser(USER1, true, Collections.emptyList(), Collections.emptySet()),
                        new ConditionsCheckResult(true, false)),

                Arguments.of(mockTask(Collections.singletonList(mockUser(USER1, true, Collections.emptyList(), Collections.emptySet())), Collections.singleton(SKILL1)),
                        mockUser(USER1, true, Collections.emptyList(), Collections.singleton(SKILL1)),
                        new ConditionsCheckResult(true, true)),

                Arguments.of(mockTask(Collections.singletonList(mockGroup(GROUP1)), Collections.emptySet()),
                        mockUser(USER1, true, Collections.emptyList(), Collections.emptySet()),
                        new ConditionsCheckResult(false, true)),

                Arguments.of(mockTask(Collections.singletonList(mockGroup(GROUP1)), Collections.emptySet()),
                        mockUser(USER1, true, Collections.singletonList(mockGroup(GROUP1)), Collections.emptySet()),
                        new ConditionsCheckResult(true, true)));
    }

    @ParameterizedTest
    @MethodSource("testParams")
    void userMeetsPotentialOwnerOrPlanningUserCondition(Task task, User user, ConditionsCheckResult checkResult) {
        assertThat(TaskAssigningConditions.userMeetsPotentialOwnerOrPlanningUserCondition(task, user))
                .isEqualTo(checkResult.meetsPotentialOwner);
    }

    @ParameterizedTest
    @MethodSource("testParams")
    void userMeetsRequiredSkillsOrPlanningUserCondition(Task task, User user, ConditionsCheckResult checkResult) {
        assertThat(TaskAssigningConditions.userMeetsRequiredSkillsOrPlanningUserCondition(task, user))
                .isEqualTo(checkResult.meetRequiredSkills);
    }
}
