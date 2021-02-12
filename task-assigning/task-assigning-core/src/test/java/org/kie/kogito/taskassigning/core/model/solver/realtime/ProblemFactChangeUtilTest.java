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
package org.kie.kogito.taskassigning.core.model.solver.realtime;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.model.TaskAssignment.PREVIOUS_ELEMENT;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockTaskAssignment;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockUser;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProblemFactChangeUtilTest {

    private static final String USER_ID = "USER_ID";
    private static final String TASK_ID1 = "1";
    private static final String TASK_ID2 = "2";
    private static final String TASK_ID3 = "3";
    private static final String TASK_ID4 = "4";
    private static final String TASK_ID5 = "5";

    @Mock
    private ScoreDirector<TaskAssigningSolution> scoreDirector;

    private User user;

    private List<TaskAssignment> userTasks;

    @BeforeEach
    void setUp() {
        userTasks = Arrays.asList(mockTaskAssignment(TASK_ID1, true),
                                  mockTaskAssignment(TASK_ID2, true),
                                  mockTaskAssignment(TASK_ID3, false),
                                  mockTaskAssignment(TASK_ID4, false),
                                  mockTaskAssignment(TASK_ID5, false));
        user = mockUser(USER_ID, userTasks);
    }

    @Test
    void releaseAllTaskAssignments() {
        ProblemFactChangeUtil.releaseAllTaskAssignments(user, scoreDirector);
        userTasks.forEach(taskAssignment -> assertTaskWasReleased(taskAssignment, scoreDirector));
    }

    @Test
    void releaseNonPinnedTaskAssignments() {
        ProblemFactChangeUtil.releaseNonPinnedTaskAssignments(user, scoreDirector);
        userTasks.stream().filter(TaskAssignment::isPinned).forEach(taskAssignment -> assertTaskWasNotReleased(taskAssignment, scoreDirector));
        userTasks.stream().filter(taskAssignment -> !taskAssignment.isPinned()).forEach(taskAssignment -> assertTaskWasReleased(taskAssignment, scoreDirector));
    }

    @Test
    void unlinkTaskAssignment() {
        TaskAssignment taskAssignment3 = userTasks.get(2);
        TaskAssignment taskAssignment4 = userTasks.get(3);
        TaskAssignment newPreviousElement = mockTaskAssignment("AnotherElement", false);
        ProblemFactChangeUtil.unlinkTaskAssignment(taskAssignment3, newPreviousElement, scoreDirector);
        assertThat(taskAssignment4.getPreviousElement()).isSameAs(newPreviousElement);
        verify(scoreDirector).beforeVariableChanged(taskAssignment4, PREVIOUS_ELEMENT);
        verify(scoreDirector).afterVariableChanged(taskAssignment4, PREVIOUS_ELEMENT);
    }

    static void assertTaskWasReleased(TaskAssignment taskAssignment, ScoreDirector<TaskAssigningSolution> scoreDirector) {
        verify(scoreDirector).beforeProblemPropertyChanged(taskAssignment);
        assertThat(taskAssignment.isPinned()).as("Invalid pinned status for taskAssignment: %s", taskAssignment).isFalse();
        verify(scoreDirector).afterProblemPropertyChanged(taskAssignment);
        verify(scoreDirector).beforeVariableChanged(taskAssignment, PREVIOUS_ELEMENT);
        assertThat(taskAssignment.getPreviousElement()).as("Invalid previousElement for taskAssignment: %s", taskAssignment).isNull();
        verify(scoreDirector).afterVariableChanged(taskAssignment, PREVIOUS_ELEMENT);
    }

    static void assertTaskWasNotReleased(TaskAssignment taskAssignment, ScoreDirector<TaskAssigningSolution> scoreDirector) {
        verify(scoreDirector, never()).beforeProblemPropertyChanged(taskAssignment);
        verify(scoreDirector, never()).afterProblemPropertyChanged(taskAssignment);
        verify(scoreDirector, never()).beforeVariableChanged(taskAssignment, PREVIOUS_ELEMENT);
        verify(scoreDirector, never()).afterVariableChanged(taskAssignment, PREVIOUS_ELEMENT);
    }
}
