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
package org.kie.kogito.taskassigning.core.model.solver;

import org.kie.kogito.taskassigning.core.model.DefaultLabels;
import org.kie.kogito.taskassigning.core.model.ModelConstants;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.solver.condition.TaskAssigningConditions;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;

import static org.kie.kogito.taskassigning.core.model.TaskAssigningSolution.HARD_LEVELS_SIZE;
import static org.kie.kogito.taskassigning.core.model.TaskAssigningSolution.SOFT_LEVELS_SIZE;

public class DefaultTaskAssigningConstraints {

    private DefaultTaskAssigningConstraints() {
    }

    public static Constraint requiredPotentialOwner(ConstraintFactory constraintFactory, Score<?> constraintWeight) {
        return constraintFactory.forEach(TaskAssignment.class)
                .filter(taskAssignment -> !TaskAssigningConditions.userMeetsPotentialOwnerOrPlanningUserCondition(taskAssignment.getTask(), taskAssignment.getUser()))
                .penalize("Required Potential Owner", constraintWeight);
    }

    public static Constraint requiredSkills(ConstraintFactory constraintFactory, Score<?> constraintWeight) {
        return constraintFactory.forEach(TaskAssignment.class)
                .filter(taskAssignment -> !TaskAssigningConditions.userMeetsRequiredSkillsOrPlanningUserCondition(taskAssignment.getTask(), taskAssignment.getUser()))
                .penalize("Required Skills", constraintWeight);
    }

    public static Constraint planningUserAssignment(ConstraintFactory constraintFactory, Score<?> constraintWeight) {
        return constraintFactory.forEach(TaskAssignment.class)
                .filter(taskAssignment -> ModelConstants.IS_PLANNING_USER.test(taskAssignment.getUser().getId()))
                .penalize("PlanningUser assignment", constraintWeight);
    }

    public static Constraint highLevelPriority(ConstraintFactory constraintFactory, Score<?> constraintWeight) {
        return constraintFactory.forEach(TaskAssignment.class)
                .filter(taskAssignment -> PriorityHelper.isHighLevel(taskAssignment.getTask().getPriority()))
                .penalize("High level priority",
                        constraintWeight,
                        TaskAssignment::getEndTimeInMinutes);
    }

    public static Constraint desiredAffinities(ConstraintFactory constraintFactory, Score<?> constraintWeight) {
        return constraintFactory.forEach(TaskAssignment.class)
                .filter(taskAssignment -> taskAssignment.getUser().isEnabled())
                .reward("Desired Affinities",
                        constraintWeight,
                        taskAssignment -> TaskHelper.countMatchingLabels(taskAssignment.getTask(), taskAssignment.getUser(), DefaultLabels.AFFINITIES.name()));
    }

    public static Constraint minimizeMakespan(ConstraintFactory constraintFactory, Score<?> constraintWeight) {
        return constraintFactory.forEach(TaskAssignment.class)
                .filter(taskAssignment -> taskAssignment.getNextElement() == null)
                .penalize("Minimize makespan",
                        constraintWeight,
                        taskAssignment -> taskAssignment.getEndTimeInMinutes() * taskAssignment.getEndTimeInMinutes());
    }

    public static Constraint mediumLevelPriority(ConstraintFactory constraintFactory, Score<?> constraintWeight) {
        return constraintFactory.forEach(TaskAssignment.class)
                .filter(taskAssignment -> PriorityHelper.isMediumLevel(taskAssignment.getTask().getPriority()))
                .penalize("Medium level priority",
                        constraintWeight,
                        TaskAssignment::getEndTimeInMinutes);
    }

    public static Constraint lowLevelPriority(ConstraintFactory constraintFactory, Score<?> constraintWeight) {
        return constraintFactory.forEach(TaskAssignment.class)
                .filter(taskAssignment -> PriorityHelper.isLowLevel(taskAssignment.getTask().getPriority()))
                .penalize("Low level priority",
                        constraintWeight,
                        TaskAssignment::getEndTimeInMinutes);
    }

    public static BendableLongScore hardLevelWeight(int hardLevel, long hardScore) {
        return BendableLongScore.ofHard(HARD_LEVELS_SIZE, SOFT_LEVELS_SIZE, hardLevel, hardScore);
    }

    public static BendableLongScore softLevelWeight(int softLevel, long softScore) {
        return BendableLongScore.ofSoft(HARD_LEVELS_SIZE, SOFT_LEVELS_SIZE, softLevel, softScore);
    }
}
