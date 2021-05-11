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

public class TaskInfoChangeProblemFactChange implements ProblemFactChange<TaskAssigningSolution> {

    private TaskAssignment taskAssignment;

    private Task taskInfo;

    public TaskInfoChangeProblemFactChange(TaskAssignment taskAssignment, Task taskInfo) {
        this.taskAssignment = taskAssignment;
        this.taskInfo = taskInfo;
    }

    public TaskAssignment getTaskAssignment() {
        return taskAssignment;
    }

    public Task getTaskInfo() {
        return taskInfo;
    }

    @Override
    public void doChange(ScoreDirector<TaskAssigningSolution> scoreDirector) {
        TaskAssignment workingTaskAssignment = scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment);
        if (workingTaskAssignment == null) {
            throw new TaskAssigningRuntimeException(String.format("Expected task assignment: %s was not found in current working solution", taskAssignment));
        }
        scoreDirector.beforeProblemPropertyChanged(workingTaskAssignment);
        Task currentTask = workingTaskAssignment.getTask();
        Task clonedTask = cloneByUnmodifiableFields(currentTask);
        setModifiableFields(clonedTask, taskInfo);
        workingTaskAssignment.setTask(clonedTask);
        scoreDirector.afterProblemPropertyChanged(workingTaskAssignment);
        scoreDirector.triggerVariableListeners();
    }

    private Task cloneByUnmodifiableFields(Task task) {
        return Task.newBuilder()
                .id(task.getId())
                .name(task.getName())
                .referenceName(task.getReferenceName())
                .processInstanceId(task.getProcessInstanceId())
                .processId(task.getProcessId())
                .rootProcessInstanceId(task.getRootProcessInstanceId())
                .rootProcessId(task.getRootProcessId())
                .started(task.getStarted())
                .endpoint(task.getEndpoint())
                .build();
    }

    private void setModifiableFields(Task currentTask, Task taskInfo) {
        currentTask.setState(taskInfo.getState());
        currentTask.setDescription(taskInfo.getDescription());
        currentTask.setPriority(taskInfo.getPriority());
        currentTask.setPotentialUsers(taskInfo.getPotentialUsers());
        currentTask.setPotentialGroups(taskInfo.getPotentialGroups());
        currentTask.setAdminUsers(taskInfo.getAdminUsers());
        currentTask.setAdminGroups(taskInfo.getAdminGroups());
        currentTask.setExcludedUsers(taskInfo.getExcludedUsers());
        currentTask.setCompleted(taskInfo.getCompleted());
        currentTask.setLastUpdate(taskInfo.getLastUpdate());
        currentTask.setInputData(taskInfo.getInputData());
        currentTask.setAttributes(taskInfo.getAttributes());
    }
}
