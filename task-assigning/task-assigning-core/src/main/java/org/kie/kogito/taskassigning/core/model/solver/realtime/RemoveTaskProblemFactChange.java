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

import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.ProblemFactChange;

import static org.kie.kogito.taskassigning.core.model.solver.realtime.ProblemFactChangeUtil.unlinkTaskAssignment;

/**
 * Implements the removal of a TaskAssignment from the working solution. If a TaskAssignment with the given identifier
 * not exists it does no action.
 */
public class RemoveTaskProblemFactChange implements ProblemFactChange<TaskAssigningSolution> {

    private TaskAssignment taskAssignment;

    public RemoveTaskProblemFactChange(TaskAssignment taskAssignment) {
        this.taskAssignment = taskAssignment;
    }

    public TaskAssignment getTaskAssignment() {
        return taskAssignment;
    }

    @Override
    public void doChange(ScoreDirector<TaskAssigningSolution> scoreDirector) {
        TaskAssigningSolution solution = scoreDirector.getWorkingSolution();
        TaskAssignment workingTaskAssignment = scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment);
        if (workingTaskAssignment != null) {
            ChainElement previousElement = workingTaskAssignment.getPreviousElement();
            unlinkTaskAssignment(workingTaskAssignment, previousElement, scoreDirector);
            scoreDirector.beforeEntityRemoved(workingTaskAssignment);
            // Planning entity lists are already cloned by the SolutionCloner, no need to clone.
            solution.getTaskAssignmentList().remove(workingTaskAssignment);
            scoreDirector.afterEntityRemoved(workingTaskAssignment);
            scoreDirector.triggerVariableListeners();
        }
    }
}
