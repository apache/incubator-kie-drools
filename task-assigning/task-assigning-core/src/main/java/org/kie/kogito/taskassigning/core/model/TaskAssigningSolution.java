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

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;

import static org.kie.kogito.taskassigning.core.model.TaskAssignment.TASK_ASSIGNMENT_RANGE;
import static org.kie.kogito.taskassigning.core.model.TaskAssignment.USER_RANGE;

@PlanningSolution
public class TaskAssigningSolution extends IdentifiableElement {

    public static final int HARD_LEVELS_SIZE = 2;
    public static final int SOFT_LEVELS_SIZE = 6;

    @ValueRangeProvider(id = USER_RANGE)
    @ProblemFactCollectionProperty
    private List<User> userList;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = TASK_ASSIGNMENT_RANGE)
    private List<TaskAssignment> taskAssignmentList;

    @PlanningScore(bendableHardLevelsSize = HARD_LEVELS_SIZE, bendableSoftLevelsSize = SOFT_LEVELS_SIZE)
    private BendableLongScore score;

    public TaskAssigningSolution() {
        // required for marshaling and FieldAccessingSolutionCloner purposes.
    }

    public TaskAssigningSolution(String id, List<User> userList, List<TaskAssignment> taskAssignmentList) {
        super(id);
        this.userList = userList;
        this.taskAssignmentList = taskAssignmentList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<TaskAssignment> getTaskAssignmentList() {
        return taskAssignmentList;
    }

    public void setTaskAssignmentList(List<TaskAssignment> taskAssignmentList) {
        this.taskAssignmentList = taskAssignmentList;
    }

    public BendableLongScore getScore() {
        return score;
    }

    public void setScore(BendableLongScore score) {
        this.score = score;
    }
}
