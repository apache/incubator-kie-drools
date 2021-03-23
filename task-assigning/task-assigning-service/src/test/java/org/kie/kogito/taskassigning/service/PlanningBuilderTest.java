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

package org.kie.kogito.taskassigning.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.DUMMY_TASK_ASSIGNMENT;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.PLANNING_USER_ID;
import static org.kie.kogito.taskassigning.service.TestUtil.mockTaskAssignment;
import static org.kie.kogito.taskassigning.service.TestUtil.mockUser;

class PlanningBuilderTest {

    private static final String USER1 = "USER1";
    private static final String USER2 = "USER2";
    private static final String USER3 = "USER3";

    private static final String USER1_TASK1 = "USER1_TASK1";
    private static final String USER1_TASK2 = "USER1_TASK2";
    private static final String USER1_TASK3 = "USER1_TASK3";

    private static final String USER2_TASK1 = "USER2_TASK1";
    private static final String USER2_TASK2 = "USER2_TASK2";

    private static final String USER3_TASK1 = "USER3_TASK1";
    private static final String USER3_TASK2 = "USER3_TASK2";
    private static final String USER3_TASK3 = "USER3_TASK3";
    private static final String USER3_TASK4 = "USER3_TASK4";

    private static final String PLANNING_USER_TASK1 = "PLANNING_USER_TASK1";
    private static final String PLANNING_USER_TASK2 = "PLANNING_USER_TASK2";
    private static final String PLANNING_USER_TASK3 = "PLANNING_USER_TASK3";
    private static final String PLANNING_USER_TASK4 = "PLANNING_USER_TASK4";
    private static final String PLANNING_USER_TASK5 = "PLANNING_USER_TASK5";

    private TaskAssigningServiceContext context;

    @Test
    void build() {
        context = new TaskAssigningServiceContext();
        List<TaskAssignment> user1Assignments = Arrays.asList(mockTaskAssignment(USER1_TASK1),
                mockTaskAssignment(DUMMY_TASK_ASSIGNMENT.getId()),
                mockTaskAssignment(USER1_TASK2),
                mockTaskAssignment(USER1_TASK3));
        User user1 = mockUser(USER1, user1Assignments);

        List<TaskAssignment> user2Assignments = Arrays.asList(mockTaskAssignment(USER2_TASK1),
                mockTaskAssignment(USER2_TASK2));

        User user2 = mockUser(USER2, user2Assignments);

        List<TaskAssignment> user3Assignments = Arrays.asList(mockTaskAssignment(USER3_TASK1),
                mockTaskAssignment(USER3_TASK2),
                mockTaskAssignment(USER3_TASK3),
                mockTaskAssignment(USER3_TASK4));

        User user3 = mockUser(USER3, user3Assignments);

        List<TaskAssignment> planningUserAssignments = Arrays.asList(mockTaskAssignment(PLANNING_USER_TASK1),
                mockTaskAssignment(PLANNING_USER_TASK2),
                mockTaskAssignment(PLANNING_USER_TASK3),
                mockTaskAssignment(PLANNING_USER_TASK4),
                mockTaskAssignment(PLANNING_USER_TASK5));

        User planningUser = mockUser(PLANNING_USER_ID, planningUserAssignments);

        List<TaskAssignment> allAssignments = new ArrayList<>(user1Assignments);
        allAssignments.addAll(user2Assignments);
        allAssignments.addAll(user3Assignments);
        allAssignments.addAll(planningUserAssignments);

        List<User> allUsers = Arrays.asList(user1, user2, user3, planningUser);

        TaskAssigningSolution solution = new TaskAssigningSolution("1", allUsers, allAssignments);

        context.setTaskPublished(USER1_TASK1, false);
        context.setTaskPublished(DUMMY_TASK_ASSIGNMENT.getId(), false);
        context.setTaskPublished(USER1_TASK2, false);
        context.setTaskPublished(USER1_TASK3, false);

        context.setTaskPublished(USER2_TASK1, true);
        context.setTaskPublished(USER2_TASK2, true);

        context.setTaskPublished(USER3_TASK1, false);
        context.setTaskPublished(USER3_TASK2, true);
        context.setTaskPublished(USER3_TASK3, true);
        context.setTaskPublished(USER3_TASK4, false);

        context.setTaskPublished(PLANNING_USER_TASK1, true);
        context.setTaskPublished(PLANNING_USER_TASK2, false);
        context.setTaskPublished(PLANNING_USER_TASK3, false);
        context.setTaskPublished(PLANNING_USER_TASK4, false);
        context.setTaskPublished(PLANNING_USER_TASK5, false);

        List<PlanningItem> planningItems = PlanningBuilder.create()
                .withContext(context)
                .withPublishWindowSize(2)
                .forSolution(solution)
                .build();

        List<TaskAssignment> expectedAssignmentsInPlanning = new ArrayList<>();
        expectedAssignmentsInPlanning.add(user1Assignments.get(0));
        expectedAssignmentsInPlanning.add(user1Assignments.get(2));
        expectedAssignmentsInPlanning.add(user3Assignments.get(0));
        expectedAssignmentsInPlanning.add(planningUserAssignments.get(1));
        expectedAssignmentsInPlanning.add(planningUserAssignments.get(2));
        expectedAssignmentsInPlanning.add(planningUserAssignments.get(3));
        expectedAssignmentsInPlanning.add(planningUserAssignments.get(4));

        assertItemsForAssignments(planningItems, expectedAssignmentsInPlanning);
    }

    private void assertItemsForAssignments(List<PlanningItem> planningItems, List<TaskAssignment> taskAssignments) {
        assertThat(planningItems).hasSize(taskAssignments.size());
        PlanningItem planningItem;
        TaskAssignment taskAssignment;
        for (int i = 0; i < planningItems.size(); i++) {
            planningItem = planningItems.get(i);
            taskAssignment = taskAssignments.get(i);
            assertThat(planningItem.getTask()).isSameAs(taskAssignment.getTask());
            assertThat(planningItem.getTargetUser()).isEqualTo(taskAssignment.getUser().getId());
        }
    }
}
