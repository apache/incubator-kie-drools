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

import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.ProblemFactChange;

import static org.kie.kogito.taskassigning.core.model.TaskAssignment.PREVIOUS_ELEMENT;
import static org.kie.kogito.taskassigning.core.model.solver.realtime.ProblemFactChangeUtil.unlinkTaskAssignment;

public class ReleaseTaskProblemFactChange implements ProblemFactChange<TaskAssigningSolution> {

    private TaskAssignment taskAssignment;

    public ReleaseTaskProblemFactChange(TaskAssignment taskAssignment) {
        this.taskAssignment = taskAssignment;
    }

    public TaskAssignment getTaskAssignment() {
        return taskAssignment;
    }

    @Override
    public void doChange(ScoreDirector<TaskAssigningSolution> scoreDirector) {
        TaskAssignment workingTaskAssignment = scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment);
        if (workingTaskAssignment == null || workingTaskAssignment.getPreviousElement() == null) {
            // The taskAssignment could have been removed in the middle by a previous change
            // or it's simply not yet assigned.
            return;
        }

        // unlink the workingTaskAssignment from the chain.
        ChainElement previousElement = workingTaskAssignment.getPreviousElement();
        unlinkTaskAssignment(workingTaskAssignment, previousElement, scoreDirector);
        scoreDirector.beforeVariableChanged(workingTaskAssignment, PREVIOUS_ELEMENT);
        workingTaskAssignment.setPreviousElement(null);
        scoreDirector.afterVariableChanged(workingTaskAssignment, PREVIOUS_ELEMENT);
        if (workingTaskAssignment.isPinned()) {
            scoreDirector.beforeProblemPropertyChanged(workingTaskAssignment);
            workingTaskAssignment.setPinned(false);
            scoreDirector.afterProblemPropertyChanged(workingTaskAssignment);
        }
        scoreDirector.triggerVariableListeners();
    }
}