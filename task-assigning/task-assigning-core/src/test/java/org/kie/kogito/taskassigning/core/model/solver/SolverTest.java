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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.kogito.taskassigning.core.AbstractTaskAssigningCoreTest;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.optaplanner.core.api.solver.Solver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_24TASKS_8USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.PLANNING_USER;
import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.extractTaskAssignments;
import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.isPotentialOwner;

class SolverTest extends AbstractTaskAssigningCoreTest {

    private static final long TEST_TIMEOUT = 20;

    @Test
    @Timeout(TEST_TIMEOUT)
    void startSolverAndSolution24Tasks8Users() {
        testSolverStartAndSolution(1, SET_OF_24TASKS_8USERS_SOLUTION.resource());
    }

    /**
     * Tests that solver for the tasks assigning problem definition can be properly started, a solution can be produced,
     * and that some minimal constrains are met by de solution.
     */
    private void testSolverStartAndSolution(int stepCountLimit, String solutionResource) {
        Solver<TaskAssigningSolution> solver = createNonDaemonSolver(stepCountLimit);
        TaskAssigningSolution solution = readTaskAssigningSolution(solutionResource);
        solution.getUserList().add(PLANNING_USER);
        TaskAssigningSolution result = solver.solve(solution);
        if (!result.getScore().isFeasible()) {
            fail(String.format("With current problem definition and stepCountLimit of %s it's expected " +
                    "that a feasible solution has been produced.", stepCountLimit));
        }
        assertConstraints(result);
    }

    /**
     * Given a TaskAssigningSolution asserts the following constraints.
     * <p>
     * 1) All tasks are assigned to a user
     * 2) The assigned user for a task is a potentialOwner for the task or the PLANNING_USER
     * 3) All tasks are assigned.
     * 
     * @param solution a solution.
     */
    private void assertConstraints(TaskAssigningSolution solution) {
        int totalTasks = 0;
        for (User user : solution.getUserList()) {
            List<TaskAssignment> taskAssignmentList = extractTaskAssignments(user);
            totalTasks += taskAssignmentList.size();
            taskAssignmentList.forEach(taskAssignment -> assertAssignment(user, taskAssignment, solution.getUserList()));
        }
        assertThat(totalTasks).isEqualTo(solution.getTaskAssignmentList().size());
    }

    private void assertAssignment(User user, TaskAssignment taskAssignment, List<User> availableUsers) {
        assertThat(taskAssignment.getUser()).isNotNull();
        assertThat(taskAssignment.getUser().getId())
                .as("TaskAssignment: %s is not assigned to expected user: %s ", taskAssignment, user)
                .isEqualTo(user.getId());
        Task task = taskAssignment.getTask();
        if (task.getPotentialUsers().isEmpty() && task.getPotentialGroups().isEmpty()) {
            assertThat(user.getId())
                    .as("TaskAssignment: %s without potentialOwners can only be assigned to the PLANNING_USER", taskAssignment)
                    .isEqualTo(PLANNING_USER.getId());
        } else if (PLANNING_USER.getId().equals(user.getId())) {
            availableUsers.forEach(availableUser -> assertThat(isPotentialOwner(taskAssignment.getTask(), user))
                    .as(String.format("PLANNING_USER user was assigned but another potential owner was found, user: %s taskAssignment: %s", user, taskAssignment))
                    .isFalse());
        } else {
            assertThat(isPotentialOwner(taskAssignment.getTask(), user))
                    .as(String.format("User: %s is not a potential owner for the taskAssignment: %s", user, taskAssignment))
                    .isTrue();
        }
    }
}