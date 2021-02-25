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

import java.util.List;

import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.kie.kogito.taskassigning.core.model.TaskAssignment.PREVIOUS_ELEMENT;
import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.extractTaskAssignments;

public class ProblemFactChangeUtil {

    private ProblemFactChangeUtil() {
    }

    /**
     * Releases all the task assignments linked to a given user.
     * 
     * @param workingUser a user instance to get the task assignments from from. Important! the user must belong to
     *        the solution currently managed by the scoreDirector, i.e. the scoreDirector.getWorkingSolution().
     * @param scoreDirector a scoreDirector instance for executing the required beforeVariableChanged and
     *        afterVariableChanged methods.
     */
    public static void releaseAllTaskAssignments(User workingUser, ScoreDirector<TaskAssigningSolution> scoreDirector) {
        releaseTaskAssignments(workingUser, true, scoreDirector);
    }

    /**
     * Releases all the non-pinned tasks linked to a given user.
     * 
     * @param workingUser a user instance to get the tasks from. Important! the user must belong to the solution
     *        currently managed by the scoreDirector, i.e. the scoreDirector.getWorkingSolution().
     * @param scoreDirector a scoreDirector instance for executing the required beforeVariableChanged and
     *        afterVariableChanged methods.
     */
    public static void releaseNonPinnedTaskAssignments(User workingUser, ScoreDirector<TaskAssigningSolution> scoreDirector) {
        releaseTaskAssignments(workingUser, false, scoreDirector);
    }

    /**
     * Release the task assignments previously associated by OptaPlanner to a user.
     * note: Optimizes the generated graph e.g. User <-> T1 <-> T2 <-> T3 <-> T4 navigation and structure changing
     * by iterating in reverse order.
     * 
     * @param workingUser a user instance previously populated by OptaPlanner.
     * @param includePinnedAssignments true if the pinned tasks must also be released, false if only non pinned tasks
     *        must be released.
     * @param scoreDirector a scored director instance for notifying the changes.
     */
    private static void releaseTaskAssignments(User workingUser, boolean includePinnedAssignments, ScoreDirector<TaskAssigningSolution> scoreDirector) {
        final List<TaskAssignment> taskAssignments = extractTaskAssignments(workingUser, testedAssignment -> includePinnedAssignments || !testedAssignment.isPinned());
        TaskAssignment taskAssignment;
        for (int index = taskAssignments.size() - 1; index >= 0; index--) {
            taskAssignment = taskAssignments.get(index);
            scoreDirector.beforeProblemPropertyChanged(taskAssignment);
            taskAssignment.setPinned(false);
            scoreDirector.afterProblemPropertyChanged(taskAssignment);
            scoreDirector.beforeVariableChanged(taskAssignment, PREVIOUS_ELEMENT);
            taskAssignment.setPreviousElement(null);
            scoreDirector.afterVariableChanged(taskAssignment, PREVIOUS_ELEMENT);
        }
    }

    /**
     * Unlinks a task assignment from the elements chain and relinks the remaining part of the chain to the specified
     * previous chain element.
     * 
     * @param taskAssignment a task assigning element to unlink from the chain.
     * @param previousElement a ChainElement to relink the chain remainder to.
     */
    public static void unlinkTaskAssignment(TaskAssignment taskAssignment, ChainElement previousElement, ScoreDirector<TaskAssigningSolution> scoreDirector) {
        TaskAssignment nextTaskAssignment = taskAssignment.getNextElement();
        if (nextTaskAssignment != null) {
            scoreDirector.beforeVariableChanged(nextTaskAssignment, PREVIOUS_ELEMENT);
            nextTaskAssignment.setPreviousElement(previousElement);
            scoreDirector.afterVariableChanged(nextTaskAssignment, PREVIOUS_ELEMENT);
        }
    }
}
