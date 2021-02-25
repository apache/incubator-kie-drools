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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.core.TestConstants;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AddTaskProblemFactChange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_100TASKS_5USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_24TASKS_8USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_500TASKS_20USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_50TASKS_5USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.PLANNING_USER;

class AddTaskExecutableProblemFactChangeTest extends AbstractExecutableProblemFactChangeTest {

    @Test
    void addTaskProblemFactChange24Tasks8Users() {
        addTaskProblemFactChange(SET_OF_24TASKS_8USERS_SOLUTION.resource(), Arrays.asList("24", "25", "30", "40"));
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void addTaskProblemFactChange24Tasks8UsersRandom() {
        addTaskProblemFactChangeRandomSet(SET_OF_24TASKS_8USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.TURTLE_TEST)
    void addTaskProblemFactChange50Tasks5Users() {
        addTaskProblemFactChange(SET_OF_50TASKS_5USERS_SOLUTION.resource(), Arrays.asList("50", "52", "70", "85", "100"));
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void addTaskProblemFactChange50Tasks5UsersRandom() {
        addTaskProblemFactChangeRandomSet(SET_OF_50TASKS_5USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.TURTLE_TEST)
    void addTaskProblemFactChange100Tasks5Users() {
        addTaskProblemFactChange(SET_OF_100TASKS_5USERS_SOLUTION.resource(), Arrays.asList("100", "105", "200", "350"));
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void addTaskProblemFactChange100Tasks5UsersRandom() {
        addTaskProblemFactChangeRandomSet(SET_OF_100TASKS_5USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.TURTLE_TEST)
    void addTaskProblemFactChange500Tasks20Users() {
        addTaskProblemFactChange(SET_OF_500TASKS_20USERS_SOLUTION.resource(), Arrays.asList("500", "600", "700"));
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void addTaskProblemFactChange500Tasks20UsersRandom() {
        addTaskProblemFactChangeRandomSet(SET_OF_500TASKS_20USERS_SOLUTION.resource());
    }

    @Test
    void addTaskProblemFactChangeTaskAlreadyExists() {
        TaskAssigningSolution solution = readTaskAssigningSolution(SET_OF_24TASKS_8USERS_SOLUTION.resource());
        String taskId = "20"; //randomly selected task.
        TaskAssignment taskAssignment = new TaskAssignment(Task.newBuilder().id(taskId).build());
        Assertions.assertThatThrownBy(() -> executeSequentialChanges(solution,
                Collections.singletonList(new ProgrammedProblemFactChange<>(new AddTaskProblemFactChange(taskAssignment)))))
                .hasMessage("A task assignment with the given identifier id: %s already exists", taskId);
    }

    private void addTaskProblemFactChangeRandomSet(String solutionResource) {
        TaskAssigningSolution solution = readTaskAssigningSolution(solutionResource);
        int taskCount = solution.getTaskAssignmentList().size();
        int randomChanges = taskCount / 2 + random.nextInt(taskCount / 2);
        List<String> taskIds = new ArrayList<>();
        for (int i = 0; i < randomChanges; i++) {
            taskIds.add(Integer.toString(taskCount++));
        }
        addTaskProblemFactChange(solution, taskIds);
    }

    private void addTaskProblemFactChange(TaskAssigningSolution solution, List<String> taskIds) {
        solution.getUserList().add(PLANNING_USER);
        List<ProgrammedProblemFactChange<AddTaskProblemFactChange>> programmedChanges = taskIds.stream()
                .map(id -> new ProgrammedProblemFactChange<>(new AddTaskProblemFactChange(new TaskAssignment(Task.newBuilder().id(id).name("NewTask_" + id).build()))))
                .collect(Collectors.toList());

        //each partial solution must have the change that was applied on it.
        executeSequentialChanges(solution, programmedChanges);
        programmedChanges.forEach(change -> assertAddTaskProblemFactChangeWasProduced(change.getChange(), change.getSolutionAfterChange()));

        //finally the last solution must have the result of all the changes.
        TaskAssigningSolution lastSolution = programmedChanges.get(programmedChanges.size() - 1).getSolutionAfterChange();
        programmedChanges.forEach(change -> assertAddTaskProblemFactChangeWasProduced(change.getChange(), lastSolution));
    }

    private void addTaskProblemFactChange(String solutionResource, List<String> taskIds) {
        addTaskProblemFactChange(readTaskAssigningSolution(solutionResource), taskIds);
    }

    private void assertAddTaskProblemFactChangeWasProduced(AddTaskProblemFactChange change, TaskAssigningSolution solution) {
        assertThat(solution.getTaskAssignmentList().stream().anyMatch(taskAssignment -> Objects.equals(change.getTaskAssignment().getId(), taskAssignment.getId()))).isTrue();
    }
}
