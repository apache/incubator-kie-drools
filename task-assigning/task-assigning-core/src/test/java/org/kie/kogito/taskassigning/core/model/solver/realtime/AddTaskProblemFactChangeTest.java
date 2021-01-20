/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.core.model.solver.realtime;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddTaskProblemFactChangeTest {

    private static final String TASK_ID = "TASK_ID";

    @Mock
    private ScoreDirector<TaskAssigningSolution> scoreDirector;

    private TaskAssigningSolution solution;

    @Mock
    private TaskAssignment taskAssignment;

    private AddTaskProblemFactChange change;

    @BeforeEach
    void setUp() {
        solution = new TaskAssigningSolution("1", new ArrayList<>(), new ArrayList<>());
        lenient().when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        change = new AddTaskProblemFactChange(taskAssignment);
    }

    @Test
    void getTaskAssignment() {
        assertThat(change.getTaskAssignment()).isSameAs(taskAssignment);
    }

    @Test
    void doChange() {
        change.doChange(scoreDirector);
        verify(scoreDirector).beforeEntityAdded(taskAssignment);
        verify(scoreDirector).afterEntityAdded(taskAssignment);
        verify(scoreDirector).triggerVariableListeners();
        assertThat(solution.getTaskAssignmentList()).contains(taskAssignment);
    }

    @Test
    void doChangeTaskAlreadyExists() {
        when(taskAssignment.getId()).thenReturn(TASK_ID);
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment)).thenReturn(taskAssignment);
        assertThatThrownBy(() -> change.doChange(scoreDirector))
                .hasMessage("A task assignment with the given identifier id: %s already exists", TASK_ID);
    }
}
