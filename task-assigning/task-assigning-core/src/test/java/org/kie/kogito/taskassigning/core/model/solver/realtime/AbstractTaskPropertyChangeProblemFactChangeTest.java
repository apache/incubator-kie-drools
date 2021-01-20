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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
abstract class AbstractTaskPropertyChangeProblemFactChangeTest<T extends AbstractTaskPropertyChangeProblemFactChange> {

    @Mock
    protected ScoreDirector<TaskAssigningSolution> scoreDirector;

    protected TaskAssignment workingTaskAssignment;

    protected TaskAssignment taskAssignment;

    protected Task task;

    protected T change;

    @BeforeEach
    public void setUp() {
        task = createTask();
        taskAssignment = new TaskAssignment(Task.newBuilder().build());
        workingTaskAssignment = new TaskAssignment(task);
        lenient().when(scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment)).thenReturn(workingTaskAssignment);
        change = createChange(taskAssignment);
    }

    protected Task createTask() {
        return Task.newBuilder().build();
    }

    protected abstract T createChange(TaskAssignment task);

    @Test
    void getTaskAssignment() {
        assertThat(change.getTaskAssignment()).isEqualTo(taskAssignment);
    }

    @Test
    void doChange() {
        change.doChange(scoreDirector);
        assertThat(task).isNotSameAs(workingTaskAssignment.getTask());
        verify(scoreDirector, times(1)).beforeProblemPropertyChanged(workingTaskAssignment);
        verify(scoreDirector, times(1)).afterProblemPropertyChanged(workingTaskAssignment);
        verify(scoreDirector).triggerVariableListeners();
        verifyValuesWhereApplied();
    }

    protected abstract void verifyValuesWhereApplied();

    @Test
    void doChangeFailure() {
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment)).thenReturn(null);
        Assertions.assertThatThrownBy(() -> change.doChange(scoreDirector))
                .hasMessageContaining("was not found in current working solution");
    }
}
