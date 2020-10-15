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

package org.optaplanner.examples.taskassigning.domain.solver;

import java.util.Objects;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.domain.TaskOrEmployee;

public class StartTimeUpdatingVariableListener implements VariableListener<TaskAssigningSolution, Task> {

    @Override
    public void beforeEntityAdded(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        updateStartTime(scoreDirector, task);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        updateStartTime(scoreDirector, task);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        // Do nothing
    }

    protected void updateStartTime(ScoreDirector<TaskAssigningSolution> scoreDirector, Task sourceTask) {
        TaskOrEmployee previous = sourceTask.getPreviousTaskOrEmployee();
        Task shadowTask = sourceTask;
        Integer previousEndTime = (previous == null ? null : previous.getEndTime());
        Integer startTime = calculateStartTime(shadowTask, previousEndTime);
        while (shadowTask != null && !Objects.equals(shadowTask.getStartTime(), startTime)) {
            scoreDirector.beforeVariableChanged(shadowTask, "startTime");
            shadowTask.setStartTime(startTime);
            scoreDirector.afterVariableChanged(shadowTask, "startTime");
            previousEndTime = shadowTask.getEndTime();
            shadowTask = shadowTask.getNextTask();
            startTime = calculateStartTime(shadowTask, previousEndTime);
        }
    }

    private Integer calculateStartTime(Task task, Integer previousEndTime) {
        if (task == null || previousEndTime == null) {
            return null;
        }
        return Math.max(task.getReadyTime(), previousEndTime);
    }

}
