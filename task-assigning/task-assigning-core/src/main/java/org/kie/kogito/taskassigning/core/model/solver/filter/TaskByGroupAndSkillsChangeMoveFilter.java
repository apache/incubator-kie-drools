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
package org.kie.kogito.taskassigning.core.model.solver.filter;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.DefaultLabels;
import org.kie.kogito.taskassigning.core.model.ModelConstants;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.User;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;

import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.hasAllLabels;
import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.isPotentialOwner;

/**
 * SelectionFilter implementation for determining if a move of a TaskA to UserB can be realized. The move is accepted
 * if UserB is a potential owner for the task and has all the required skills for the task if any, or if it's the
 * planning user.
 */
@RegisterForReflection
public class TaskByGroupAndSkillsChangeMoveFilter
        implements SelectionFilter<TaskAssigningSolution, ChangeMove<TaskAssigningSolution>> {

    @Override
    public boolean accept(ScoreDirector<TaskAssigningSolution> scoreDirector, ChangeMove<TaskAssigningSolution> changeMove) {
        final TaskAssignment assignmentToMove = (TaskAssignment) changeMove.getEntity();
        final ChainElement chainElement = (ChainElement) changeMove.getToPlanningValue();
        final User user = chainElement.isTaskAssignment() ? ((TaskAssignment) chainElement).getUser() : (User) chainElement;

        return user != null && user.isEnabled() &&
                (ModelConstants.IS_PLANNING_USER.test(user.getId()) ||
                        (isPotentialOwner(assignmentToMove.getTask(), user) && hasAllLabels(assignmentToMove.getTask(), user, DefaultLabels.SKILLS.name())));
    }
}
