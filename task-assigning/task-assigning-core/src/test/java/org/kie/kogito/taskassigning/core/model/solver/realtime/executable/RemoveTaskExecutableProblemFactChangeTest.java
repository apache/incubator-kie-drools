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
package org.kie.kogito.taskassigning.core.model.solver.realtime.executable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.core.TestConstants;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.solver.realtime.RemoveTaskProblemFactChange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_100TASKS_5USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_24TASKS_8USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_500TASKS_20USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_50TASKS_5USERS_SOLUTION;

class RemoveTaskExecutableProblemFactChangeTest extends AbstractExecutableProblemFactChangeTest {

    @Test
    void removeTaskProblemFactChange24Tasks8Users() {
        removeTaskProblemFactChange(SET_OF_24TASKS_8USERS_SOLUTION.resource(),
                                    Arrays.asList("0", "10", "11", "4", "20", "100", "78"));
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void removeTaskProblemFactChange24Tasks8UsersRandom() {
        removeTaskProblemFactChangeRandomSet(SET_OF_24TASKS_8USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.TURTLE_TEST)
    void removeTaskProblemFactChange50Tasks5Users() {
        removeTaskProblemFactChange(SET_OF_50TASKS_5USERS_SOLUTION.resource(),
                                    Arrays.asList("0", "10", "1", "4", "20", "30", "35", "40", "45", "57", "60"));
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void removeTaskProblemFactChange50Tasks5UsersRandom() {
        removeTaskProblemFactChangeRandomSet(SET_OF_50TASKS_5USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.TURTLE_TEST)
    void removeTaskProblemFactChange100Tasks5Users() {
        removeTaskProblemFactChange(SET_OF_100TASKS_5USERS_SOLUTION.resource(),
                                    Arrays.asList("5", "15", "11", "4", "20", "30", "36", "40", "45", "58", "99", "130",
                                                  "200"));
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void removeTaskProblemFactChange100Tasks5UsersRandom() {
        removeTaskProblemFactChangeRandomSet(SET_OF_100TASKS_5USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.TURTLE_TEST)
    void removeTaskProblemFactChange500Tasks20Users() {
        removeTaskProblemFactChange(SET_OF_500TASKS_20USERS_SOLUTION.resource(),
                                    Arrays.asList("5", "15", "11", "4", "20", "30", "36", "40", "45", "58", "99", "300",
                                                  "400", "25", "1000", "1001"));
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void removeTaskProblemFactChange500Tasks20UsersRandom() {
        removeTaskProblemFactChangeRandomSet(SET_OF_500TASKS_20USERS_SOLUTION.resource());
    }

    private void removeTaskProblemFactChange(String solutionResource, List<String> taskIds) {
        removeTaskProblemFactChange(readTaskAssigningSolution(solutionResource), taskIds);
    }

    private void removeTaskProblemFactChange(TaskAssigningSolution solution, List<String> taskIds) {
        List<ProgrammedProblemFactChange<RemoveTaskProblemFactChange>> programmedChanges = taskIds.stream()
                .map(id -> findTaskAssignmentOrCreate(solution, id))
                .map(taskAssignment -> new ProgrammedProblemFactChange<>(new RemoveTaskProblemFactChange(taskAssignment)))
                .collect(Collectors.toList());

        //each partial solution must have the change that was applied on it.
        executeSequentialChanges(solution, programmedChanges);
        programmedChanges.forEach(change -> assertRemoveTaskProblemFactChangeWasProduced(change.getChange(), change.getSolutionAfterChange()));

        //finally the last solution must have the result of all the changes.
        TaskAssigningSolution lastSolution = programmedChanges.get(programmedChanges.size() - 1).getSolutionAfterChange();
        programmedChanges.forEach(change -> assertRemoveTaskProblemFactChangeWasProduced(change.getChange(), lastSolution));
    }

    private void removeTaskProblemFactChangeRandomSet(String solutionResource) {
        TaskAssigningSolution solution = readTaskAssigningSolution(solutionResource);
        int taskCount = solution.getTaskAssignmentList().size();
        int randomChanges = taskCount / 2 + random.nextInt(taskCount / 2);
        List<String> taskIds = new ArrayList<>();
        for (int i = 0; i < randomChanges; i++) {
            taskIds.add(Long.toString(taskCount++));
        }
        removeTaskProblemFactChange(solution, taskIds);
    }

    /**
     * Given a RemoveTaskProblemFact change and a solution that was produced as the result of applying the change,
     * asserts that the pointed task assignment is not present in the solution.
     * @param change The change that was executed for producing the solution.
     * @param solution The produced solution.
     */
    private void assertRemoveTaskProblemFactChangeWasProduced(RemoveTaskProblemFactChange change, TaskAssigningSolution solution) {
        assertThat(solution.getTaskAssignmentList().stream().anyMatch(taskAssignment -> Objects.equals(change.getTaskAssignment().getId(), taskAssignment.getId()))).isFalse();
    }

    private static TaskAssignment findTaskAssignmentOrCreate(TaskAssigningSolution solution, String id) {
        return solution.getTaskAssignmentList().stream()
                .filter(taskAssignment -> Objects.equals(taskAssignment.getId(), id))
                .findFirst().orElse(new TaskAssignment(Task.newBuilder().id(id).name("NonExisting_" + id).build()));
    }
}
