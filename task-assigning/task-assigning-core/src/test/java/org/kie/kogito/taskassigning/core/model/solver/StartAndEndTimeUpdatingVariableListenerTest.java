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

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StartAndEndTimeUpdatingVariableListenerTest {

    @Mock
    private ScoreDirector<TaskAssigningSolution> scoreDirector;

    private StartAndEndTimeUpdatingVariableListener listener;

    private TaskAssignment taskAssignment1;
    private TaskAssignment taskAssignment2;
    private TaskAssignment taskAssignment3;
    private TaskAssignment taskAssignment4;
    private TaskAssignment taskAssignment5;

    @BeforeEach
    void setUp() {
        listener = new StartAndEndTimeUpdatingVariableListener();

        User anchor = new User("User1");
        taskAssignment1 = new TaskAssignment(Task.newBuilder().id("1").build());
        taskAssignment1.setStartTimeInMinutes(0);
        taskAssignment1.setDurationInMinutes(1);
        taskAssignment1.setEndTimeInMinutes(taskAssignment1.getStartTimeInMinutes() + taskAssignment1.getDurationInMinutes());
        taskAssignment1.setPreviousElement(anchor);

        taskAssignment2 = new TaskAssignment(Task.newBuilder().id("2").build());
        taskAssignment2.setDurationInMinutes(2);
        taskAssignment2.setPreviousElement(taskAssignment1);
        taskAssignment2.setStartTimeInMinutes(taskAssignment1.getEndTimeInMinutes());
        taskAssignment2.setEndTimeInMinutes(taskAssignment2.getStartTimeInMinutes() + taskAssignment2.getDurationInMinutes());
        taskAssignment1.setNextElement(taskAssignment2);

        taskAssignment3 = new TaskAssignment(Task.newBuilder().id("3").build());
        taskAssignment3.setDurationInMinutes(3);
        taskAssignment3.setPreviousElement(taskAssignment2);

        taskAssignment4 = new TaskAssignment(Task.newBuilder().id("4").build());
        taskAssignment4.setDurationInMinutes(4);
        taskAssignment4.setPreviousElement(taskAssignment3);
        taskAssignment3.setNextElement(taskAssignment4);

        taskAssignment5 = new TaskAssignment(Task.newBuilder().id("5").build());
        taskAssignment5.setDurationInMinutes(5);
        taskAssignment5.setPreviousElement(taskAssignment4);
        taskAssignment4.setNextElement(taskAssignment5);
    }

    @Test
    void afterEntityAdded() {
        listener.afterEntityAdded(scoreDirector, taskAssignment3);
        verifyTimes();
    }

    @Test
    void afterVariableChanged() {
        listener.afterVariableChanged(scoreDirector, taskAssignment3);
        verifyTimes();
    }

    private void verifyTimes() {
        assertThat(taskAssignment3.getStartTimeInMinutes()).isEqualTo(taskAssignment1.getDurationInMinutes() + taskAssignment2.getDurationInMinutes());
        assertThat(taskAssignment3.getEndTimeInMinutes()).isEqualTo(taskAssignment1.getDurationInMinutes() + taskAssignment2.getDurationInMinutes() + taskAssignment3.getDurationInMinutes());
        assertThat(taskAssignment4.getStartTimeInMinutes()).isEqualTo(taskAssignment1.getDurationInMinutes() + taskAssignment2.getDurationInMinutes() + taskAssignment3.getDurationInMinutes());
        assertThat(taskAssignment4.getEndTimeInMinutes()).isEqualTo(taskAssignment1.getDurationInMinutes() + taskAssignment2.getDurationInMinutes() + taskAssignment3.getDurationInMinutes() + taskAssignment4.getDurationInMinutes());
        assertThat(taskAssignment5.getStartTimeInMinutes()).isEqualTo(taskAssignment1.getDurationInMinutes() + taskAssignment2.getDurationInMinutes() + taskAssignment3.getDurationInMinutes() + taskAssignment4.getDurationInMinutes());
        assertThat(taskAssignment5.getEndTimeInMinutes()).isEqualTo(taskAssignment1.getDurationInMinutes() + taskAssignment2.getDurationInMinutes() + taskAssignment3.getDurationInMinutes() + taskAssignment4.getDurationInMinutes() + taskAssignment5.getDurationInMinutes());

        Stream.of(taskAssignment3, taskAssignment4, taskAssignment5).forEach(taskAssignment -> {
            verify(scoreDirector).beforeVariableChanged(taskAssignment, TaskAssignment.START_TIME_IN_MINUTES);
            verify(scoreDirector).afterVariableChanged(taskAssignment, TaskAssignment.START_TIME_IN_MINUTES);
            verify(scoreDirector).beforeVariableChanged(taskAssignment, TaskAssignment.END_TIME_IN_MINUTES);
            verify(scoreDirector).afterVariableChanged(taskAssignment, TaskAssignment.END_TIME_IN_MINUTES);
        });
    }
}
