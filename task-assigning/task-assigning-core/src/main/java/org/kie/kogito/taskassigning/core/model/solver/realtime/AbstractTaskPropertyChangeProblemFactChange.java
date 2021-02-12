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

import org.kie.kogito.taskassigning.core.TaskAssigningRuntimeException;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.ProblemFactChange;

public abstract class AbstractTaskPropertyChangeProblemFactChange implements ProblemFactChange<TaskAssigningSolution> {

    private TaskAssignment taskAssignment;

    protected AbstractTaskPropertyChangeProblemFactChange(TaskAssignment taskAssignment) {
        this.taskAssignment = taskAssignment;
    }

    public TaskAssignment getTaskAssignment() {
        return taskAssignment;
    }

    @Override
    public void doChange(ScoreDirector<TaskAssigningSolution> scoreDirector) {
        TaskAssignment workingTaskAssignment = scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment);
        if (workingTaskAssignment == null) {
            throw new TaskAssigningRuntimeException(String.format("Expected task assignment: %s was not found in current working solution", taskAssignment));
        }
        scoreDirector.beforeProblemPropertyChanged(workingTaskAssignment);
        Task currentTask = workingTaskAssignment.getTask();
        Task clonedTask = cloneCurrentTask(currentTask);
        applyChange(clonedTask);
        workingTaskAssignment.setTask(clonedTask);
        scoreDirector.afterProblemPropertyChanged(workingTaskAssignment);
        scoreDirector.triggerVariableListeners();
    }

    /**
     * Apply the necessary changes on the shallow cloned task instance.
     * @see ##cloneCurrentTask(Task)
     * @see Task.CloneBuilder
     */
    protected abstract void applyChange(Task task);

    /**
     * Generates a convenient shallow clone of currentTask.
     * @see Task.CloneBuilder
     */
    protected Task cloneCurrentTask(Task currentTask) {
        return Task.CloneBuilder.newInstance(currentTask).build();
    }
}
