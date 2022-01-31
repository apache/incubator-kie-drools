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
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;

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

    protected void updateStartTime(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        Integer startTime = calculateStartTime(task);
        if (!Objects.equals(task.getStartTime(), startTime)) {
            scoreDirector.beforeVariableChanged(task, "startTime");
            task.setStartTime(startTime);
            scoreDirector.afterVariableChanged(task, "startTime");
        }
    }

    private Integer calculateStartTime(Task task) {
        Employee employee = task.getEmployee();
        if (employee == null) {
            return null;
        }
        Integer index = task.getIndex();
        Integer previousEndTime = index == 0 ? Integer.valueOf(0) : employee.getTasks().get(index - 1).getEndTime();
        if (previousEndTime == null) {
            return null;
        }
        return Math.max(task.getReadyTime(), previousEndTime);
    }

}
