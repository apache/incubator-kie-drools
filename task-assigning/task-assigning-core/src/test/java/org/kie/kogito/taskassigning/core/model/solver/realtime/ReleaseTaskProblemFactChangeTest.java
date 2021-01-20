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

package org.kie.kogito.taskassigning.core.model.solver.realtime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.model.TaskAssignment.PREVIOUS_ELEMENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseTaskProblemFactChangeTest {

    private TaskAssignment taskAssignment;
    private ChainElement previousElement;
    private TaskAssignment workingTaskAssignment;
    private TaskAssignment nextTaskAssignment;

    @Mock
    private ScoreDirector<TaskAssigningSolution> scoreDirector;

    private ReleaseTaskProblemFactChange change;

    @BeforeEach
    void setUp() {
        taskAssignment = new TaskAssignment(Task.newBuilder().build());
        previousElement = spy(new TaskAssignment(Task.newBuilder().build()));
        workingTaskAssignment = spy(new TaskAssignment(Task.newBuilder().build()));
        nextTaskAssignment = spy(new TaskAssignment(Task.newBuilder().build()));
        change = new ReleaseTaskProblemFactChange(taskAssignment);
    }

    @Test
    void doChangeNonPinnedTask() {
        workingTaskAssignment.setPinned(false);
        doChange();
        verifyCommonResults();
    }

    @Test
    void doChangePinnedTask() {
        workingTaskAssignment.setPinned(true);
        doChange();
        verifyCommonResults();
        verify(scoreDirector).beforeProblemPropertyChanged(workingTaskAssignment);
        verify(workingTaskAssignment).setPinned(false);
        verify(scoreDirector).afterProblemPropertyChanged(workingTaskAssignment);
    }

    @Test
    void doChangeUnAssignedTask() {
        workingTaskAssignment.setPreviousElement(null);
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment)).thenReturn(workingTaskAssignment);
        change.doChange(scoreDirector);
        verify(scoreDirector, never()).beforeVariableChanged(any(TaskAssignment.class), anyString());
        verify(scoreDirector, never()).afterVariableChanged(any(TaskAssignment.class), anyString());
        verify(scoreDirector, never()).beforeProblemPropertyChanged(any());
        verify(scoreDirector, never()).afterProblemPropertyChanged(any());
        verify(scoreDirector, never()).triggerVariableListeners();
    }

    @Test
    void getTask() {
        assertThat(change.getTaskAssignment()).isEqualTo(taskAssignment);
    }

    private void doChange() {
        workingTaskAssignment.setPreviousElement(previousElement);
        workingTaskAssignment.setNextElement(nextTaskAssignment);
        nextTaskAssignment.setPreviousElement(workingTaskAssignment);

        when(scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment)).thenReturn(workingTaskAssignment);
        change.doChange(scoreDirector);
    }

    private void verifyCommonResults() {
        assertThat(nextTaskAssignment.getPreviousElement()).isEqualTo(previousElement);
        verify(scoreDirector).beforeVariableChanged(nextTaskAssignment, PREVIOUS_ELEMENT);
        verify(scoreDirector).afterVariableChanged(nextTaskAssignment, PREVIOUS_ELEMENT);
        verify(scoreDirector).triggerVariableListeners();
    }
}
