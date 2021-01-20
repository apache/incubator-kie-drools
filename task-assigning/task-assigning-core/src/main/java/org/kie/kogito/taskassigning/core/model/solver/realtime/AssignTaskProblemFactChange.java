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

import java.util.ArrayList;

import org.kie.kogito.taskassigning.core.TaskAssigningRuntimeException;
import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.User;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.ProblemFactChange;

import static org.kie.kogito.taskassigning.core.model.TaskAssignment.PREVIOUS_ELEMENT;
import static org.kie.kogito.taskassigning.core.model.solver.realtime.ProblemFactChangeUtil.unlinkTaskAssignment;

/**
 * Implements the "direct" assignment of a Task to a User.
 * This PFC can be useful in scenarios were e.g. a system administrator manually assigns a Task to a given user from the
 * jBPM tasks list administration. While it's expected that environments that relied the tasks assigning to OptaPlanner
 * shouldn't do this "direct" assignments, we still provide this PFC for dealing with this edge case scenarios.
 * Note that this use cases might break hard constraints or introduce considerable score penalization for soft
 * constraints.
 * Additionally since the "direct" assignment comes from an "external" system it'll remain pinned.
 * <p>
 * Both the task assignment and user to work with are looked up by using their corresponding id's.
 * If the task assignment is not found it'll be created and added to the working solution, while if the user is not
 * found it'll be added to the solution or an exception will be thrown depending on the addIfNotExists value.
 */
public class AssignTaskProblemFactChange implements ProblemFactChange<TaskAssigningSolution> {

    private TaskAssignment taskAssignment;
    private User user;
    private boolean addIfNotExists = false;

    public AssignTaskProblemFactChange(TaskAssignment taskAssignment, User user) {
        this.taskAssignment = taskAssignment;
        this.user = user;
    }

    public AssignTaskProblemFactChange(TaskAssignment taskAssignment, User user, boolean addIfNotExists) {
        this.taskAssignment = taskAssignment;
        this.user = user;
        this.addIfNotExists = addIfNotExists;
    }

    public TaskAssignment getTaskAssignment() {
        return taskAssignment;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void doChange(ScoreDirector<TaskAssigningSolution> scoreDirector) {
        User workingUser = lookupOrAddWorkingUser(user, scoreDirector, addIfNotExists);
        TaskAssignment workingTaskAssignment = lookupOrPrepareTaskAssignment(taskAssignment, scoreDirector);
        ChainElement insertPosition = findInsertPosition(workingUser);
        TaskAssignment insertPositionNextTask = insertPosition.getNextElement();

        if (taskAssignment == workingTaskAssignment) {
            processNewTaskAssignment(workingTaskAssignment, insertPosition, insertPositionNextTask, scoreDirector);
        } else if (insertPosition != workingTaskAssignment) {
            // in cases where insertPosition == workingTaskAssignment there's nothing to do, since the workingTaskAssignment
            // is already pinned and belongs to user. (see findInsertPosition)
            processExistingTaskAssignment(workingTaskAssignment, insertPosition, insertPositionNextTask, scoreDirector);
        }
    }

    private void processNewTaskAssignment(TaskAssignment newTaskAssignment,
                                          ChainElement insertPosition,
                                          TaskAssignment insertPositionNextTask,
                                          ScoreDirector<TaskAssigningSolution> scoreDirector) {

        TaskAssigningSolution solution = scoreDirector.getWorkingSolution();
        newTaskAssignment.setPreviousElement(insertPosition);
        scoreDirector.beforeEntityAdded(newTaskAssignment);
        // Planning entity lists are already cloned by the SolutionCloner, no need to clone.
        solution.getTaskAssignmentList().add(newTaskAssignment);
        scoreDirector.afterEntityAdded(newTaskAssignment);

        setPreviousElementIfApply(insertPositionNextTask, newTaskAssignment, scoreDirector);
        setPinned(newTaskAssignment, scoreDirector);
        scoreDirector.triggerVariableListeners();
    }

    private void processExistingTaskAssignment(TaskAssignment existingTaskAssignment,
                                               ChainElement insertPosition,
                                               TaskAssignment insertPositionNextTask,
                                               ScoreDirector<TaskAssigningSolution> scoreDirector) {
        if (insertPosition.getNextElement() != existingTaskAssignment) {
            // relocate the existingTaskAssignment at the desired position
            ChainElement previousElement = existingTaskAssignment.getPreviousElement();
            if (previousElement != null) {
                // unlink from the current chain position.
                unlinkTaskAssignment(existingTaskAssignment, previousElement, scoreDirector);
            }
            scoreDirector.beforeVariableChanged(existingTaskAssignment, PREVIOUS_ELEMENT);
            existingTaskAssignment.setPreviousElement(insertPosition);
            scoreDirector.afterVariableChanged(existingTaskAssignment, PREVIOUS_ELEMENT);
            setPreviousElementIfApply(insertPositionNextTask, existingTaskAssignment, scoreDirector);
        }
        setPinned(existingTaskAssignment, scoreDirector);
        scoreDirector.triggerVariableListeners();
    }

    private static User lookupOrAddWorkingUser(User user, ScoreDirector<TaskAssigningSolution> scoreDirector, boolean addIfNotExists) {
        TaskAssigningSolution solution = scoreDirector.getWorkingSolution();
        User workingUser = scoreDirector.lookUpWorkingObjectOrReturnNull(user);
        if (workingUser == null) {
            if (!addIfNotExists) {
                throw new TaskAssigningRuntimeException(String.format("Expected user: %s was not found in current working solution", user));
            } else {
                // Shallow clone the user list so only workingSolution is affected, not bestSolution
                solution.setUserList(new ArrayList<>(solution.getUserList()));
                // Ensure that the nextElement value calculated by OptaPlanner doesn't have any out-side manually
                // assigned value.
                user.setNextElement(null);
                scoreDirector.beforeProblemFactAdded(user);
                solution.getUserList().add(user);
                scoreDirector.afterProblemFactAdded(user);
                workingUser = user;
            }
        }
        return workingUser;
    }

    private static TaskAssignment lookupOrPrepareTaskAssignment(TaskAssignment taskAssignment,
                                                                ScoreDirector<TaskAssigningSolution> scoreDirector) {
        TaskAssignment workingTaskAssignment = scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment);
        if (workingTaskAssignment != null) {
            return workingTaskAssignment;
        } else {
            // The task assignment will be created by this PFC.
            // Ensure that the task assignment to be added doesn't have any out-side manually assigned values for the
            // values that are calculated by OptaPlanner
            taskAssignment.setPreviousElement(null);
            taskAssignment.setUser(null);
            taskAssignment.setPinned(false);
            taskAssignment.setNextElement(null);
            taskAssignment.setStartTimeInMinutes(null);
            taskAssignment.setEndTimeInMinutes(null);
            return taskAssignment;
        }
    }

    /**
     * Find the first available "position" where a taskAssignment can be added in the tasks chain for a given user.
     * <p>
     * For a chain like:
     * <p>
     * U -> T1 -> T2 -> T3 -> T4 -> null
     * <p>
     * if e.g. T3 is returned, a new taskAssignment Tn will be later added in the following position.
     * <p>
     * U -> T1 -> T2 -> T3 -> Tn -> T4 -> null
     * Given that we are using a chained structure, to pin a task assignment Tn to a given user, we must be sure that
     * all the previous tasks in the chain are pinned to the same user. For keeping the structure consistency a task
     * assignment Tn is inserted after the last pinned element in the chain. In the example above we have that existing
     * tasks assignments T1, T2 and T3 are pinned.
     * @param user the for adding a taskAssignment to.
     * @return the proper ChainElement object were a taskAssignment can be added. This method will never return null.
     */
    private static ChainElement findInsertPosition(User user) {
        ChainElement result = user;
        TaskAssignment nextTaskAssignment = user.getNextElement();
        while (nextTaskAssignment != null && nextTaskAssignment.isPinned()) {
            result = nextTaskAssignment;
            nextTaskAssignment = nextTaskAssignment.getNextElement();
        }
        return result;
    }

    private static void setPreviousElementIfApply(TaskAssignment insertPositionNextTask,
                                                  TaskAssignment previousElement,
                                                  ScoreDirector<TaskAssigningSolution> scoreDirector) {
        if (insertPositionNextTask != null) {
            scoreDirector.beforeVariableChanged(insertPositionNextTask, PREVIOUS_ELEMENT);
            insertPositionNextTask.setPreviousElement(previousElement);
            scoreDirector.afterVariableChanged(insertPositionNextTask, PREVIOUS_ELEMENT);
        }
    }

    private static void setPinned(TaskAssignment taskAssignment, ScoreDirector<TaskAssigningSolution> scoreDirector) {
        scoreDirector.beforeProblemPropertyChanged(taskAssignment);
        taskAssignment.setPinned(true);
        scoreDirector.afterProblemPropertyChanged(taskAssignment);
    }
}
