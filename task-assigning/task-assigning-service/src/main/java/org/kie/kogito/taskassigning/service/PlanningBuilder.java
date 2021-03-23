/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;

import static org.kie.kogito.taskassigning.core.model.ModelConstants.IS_NOT_DUMMY_TASK_ASSIGNMENT;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.IS_PLANNING_USER;
import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.extractTaskAssignments;

public class PlanningBuilder {

    private TaskAssigningSolution solution;
    private int publishWindowSize;
    private TaskAssigningServiceContext context;

    private PlanningBuilder() {
    }

    public static PlanningBuilder create() {
        return new PlanningBuilder();
    }

    public PlanningBuilder forSolution(TaskAssigningSolution solution) {
        this.solution = solution;
        return this;
    }

    public PlanningBuilder withContext(TaskAssigningServiceContext context) {
        this.context = context;
        return this;
    }

    public PlanningBuilder withPublishWindowSize(int publishWindowSize) {
        this.publishWindowSize = publishWindowSize;
        return this;
    }

    public List<PlanningItem> build() {
        return solution.getUserList().stream()
                .map(this::buildPlanningItems)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<PlanningItem> buildPlanningItems(User user) {
        List<TaskAssignment> taskAssignments = extractTaskAssignments(user, IS_NOT_DUMMY_TASK_ASSIGNMENT);
        Iterator<TaskAssignment> taskAssignmentsIt = taskAssignments.iterator();
        TaskAssignment taskAssignment;
        List<PlanningItem> result = new ArrayList<>();

        int count = 0;
        while (taskAssignmentsIt.hasNext() && (count < publishWindowSize || IS_PLANNING_USER.test(user.getId()))) {
            taskAssignment = taskAssignmentsIt.next();
            if (!context.isTaskPublished(taskAssignment.getId())) {
                result.add(new PlanningItem(taskAssignment.getTask(), user.getId()));
            }
            count++;
        }
        return result;
    }
}