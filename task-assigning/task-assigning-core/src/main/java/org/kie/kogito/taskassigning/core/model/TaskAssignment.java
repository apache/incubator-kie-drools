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

package org.kie.kogito.taskassigning.core.model;

import org.kie.kogito.taskassigning.core.model.solver.StartAndEndTimeUpdatingVariableListener;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

/**
 * TaskAssignment is the only planning entity that will be changed during the problem solving, and we have only one
 * PlanningVariable.
 * <p>
 * In this particular problem we want to do the assignments in a way that the list of tasks
 * for a given user is ordered. (see e.g. that Higher priority tasks must be resolved first is a soft constraint)
 * <p>
 * User1: A <- B <- C <- D
 * <p>
 * User2: E <- F
 * <p>
 * The initial task of each sequence points to the user that will own all the tasks in the list, so when a solution is
 * created we'll have something like this.
 * <p>
 * User1 <- A <- B <- C <- D  (In this example, User1 is the anchor)
 * <p>
 * This explains why property "previousElement" can be assigned with User or a TaskAssignment.
 * <p>
 * BUT the solver builds the solutions in a way that only the first item of the "chain" points to
 * a user. This is how a CHAINED configuration works. And the way the solver knows which must be the fact class
 * that must be used for setting the previousElement property is by considering the order in the PlanningVariable
 * configuration.
 * @PlanningVariable(valueRangeProviderRefs = {"userRange", "taskRange"}, graphType = PlanningVariableGraphType.CHAINED)
 * <p>
 * Here we basically declared that we want to build a CHAINED graph but also since the "userRange" is the first
 * valueRangeProviderRef we are declaring that the User will be used as anchor.
 * So the solver will always start by using an User as the head of the list and then it'll consider adding Tasks
 * to the linked structure.
 * <p>
 * Additionally for the calculation of the scores, etc, given a TaskAssignment of a particular solution we'll want to know quickly
 * which is the User that was assigned to this task.
 * <p>
 * If we have the following chain
 * <p>
 * Employee1 (the anchor) <- A <- B <- C <- D
 * <p>
 * and we take e.g. D, then to get the assigned Employee1 the list must be iterated, but this probably not the best idea.
 * <p>
 * Solution, add a shadow variable and let the solver populate this variable when the solution is constructed.
 * With the declaration below a shadow variable is defined for keeping a reference to the anchor. This shadow variable
 * is populated and kept consistent by the solver.
 * <p>
 * Shadow variable:
 * Let all Tasks have a reference to the anchor of the chain, the assigned user.
 * @AnchorShadowVariable(sourceVariableName = "previousElement")
 * private User user;
 * <p>
 * CustomShadowVariable startTimeInMinutes:
 * Convenient shadow variable is declared for having the startTimeInMinutes of a task already calculated.
 * @CustomShadowVariable(variableListenerClass = StartAndEndTimeUpdatingVariableListener.class,
 * sources = {@PlanningVariableReference(variableName = "previousElement")})
 * private Integer startTimeInMinutes;
 * <p>
 * So the variableListenerClass is invoked when the source variable is changed/assigned.
 */
@PlanningEntity
public class TaskAssignment extends ChainElement {

    public static final String PREVIOUS_ELEMENT = "previousElement";
    public static final String USER_RANGE = "userRange";
    public static final String TASK_ASSIGNMENT_RANGE = "taskAssignmentRange";
    public static final String START_TIME_IN_MINUTES = "startTimeInMinutes";
    public static final String END_TIME_IN_MINUTES = "endTimeInMinutes";

    private Task task;

    @PlanningPin
    private boolean pinned;

    /**
     * Planning variable: changes during planning, between score calculations.
     */
    @PlanningVariable(valueRangeProviderRefs = {USER_RANGE, TASK_ASSIGNMENT_RANGE},
            graphType = PlanningVariableGraphType.CHAINED)
    private ChainElement previousElement;

    /**
     * Shadow variable, let all Tasks have a reference to the anchor of the chain, the assigned user.
     */
    @AnchorShadowVariable(sourceVariableName = PREVIOUS_ELEMENT)
    private User user;

    /**
     * When the previousTask changes we need to update the startTimeInMinutes for current task and also the
     * startTimeInMinutes for all the tasks that comes after.  previousTask -> currentTask -> C -> D -> E since each
     * task can only start after his previous one has finished. As part of the update the endTimeInMinutes for the
     * modified tasks will also be updated.
     */
    @CustomShadowVariable(variableListenerClass = StartAndEndTimeUpdatingVariableListener.class,
            sources = {@PlanningVariableReference(variableName = PREVIOUS_ELEMENT)})
    private Integer startTimeInMinutes;

    /**
     * Assume a duration of 1 minute for all tasks.
     */
    private int durationInMinutes = 1;

    /**
     * This declaration basically indicates that the endTimeInMinutes is actually calculated as part of the
     * startTimeInMinutes time shadow variable calculation.
     */
    @CustomShadowVariable(variableListenerRef = @PlanningVariableReference(variableName = START_TIME_IN_MINUTES))
    private Integer endTimeInMinutes;

    public TaskAssignment() {
        // required for marshaling and FieldAccessingSolutionCloner purposes.
    }

    public TaskAssignment(Task task) {
        super(task.getId());
        this.task = task;
    }

    @Override
    public boolean isTaskAssignment() {
        return true;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public ChainElement getPreviousElement() {
        return previousElement;
    }

    public void setPreviousElement(ChainElement previousElement) {
        this.previousElement = previousElement;
    }

    /**
     * @return sometimes null, when a just created task wasn't yet assigned.
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getStartTimeInMinutes() {
        return startTimeInMinutes;
    }

    public void setStartTimeInMinutes(Integer startTimeInMinutes) {
        this.startTimeInMinutes = startTimeInMinutes;
    }

    /**
     * @return The endTimeInMinutes of a task. Can be null when the endTimeInMinutes of a just created task wasn't yet
     * calculated.
     */
    public Integer getEndTimeInMinutes() {
        return endTimeInMinutes;
    }

    public void setEndTimeInMinutes(Integer endTimeInMinutes) {
        this.endTimeInMinutes = endTimeInMinutes;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    @Override
    public String toString() {
        return "TaskAssignment{" +
                "id=" + id +
                ", task=" + task +
                ", pinned=" + pinned +
                ", previousElement=" + previousElement +
                ", user=" + user +
                ", startTimeInMinutes=" + startTimeInMinutes +
                ", durationInMinutes=" + durationInMinutes +
                ", endTimeInMinutes=" + endTimeInMinutes +
                '}';
    }
}
