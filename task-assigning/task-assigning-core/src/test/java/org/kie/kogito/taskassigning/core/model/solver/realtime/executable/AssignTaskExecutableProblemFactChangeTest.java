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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.core.TestConstants;
import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AssignTaskProblemFactChange;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_100TASKS_5USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_24TASKS_8USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_500TASKS_20USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.TestDataSet.SET_OF_50TASKS_5USERS_SOLUTION;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.PLANNING_USER;

class AssignTaskExecutableProblemFactChangeTest extends AbstractExecutableProblemFactChangeTest {

    private static final String FIXED_TEST = "Fixed";
    private static final String RANDOM_TEST = "Random";
    private static final String NEW_TASK = "NEW_TASK";

    private class WorkingSolutionAwareProblemFactChange
            extends AssignTaskProblemFactChange {

        private Consumer<TaskAssigningSolution> solutionBeforeChangesConsumer;

        WorkingSolutionAwareProblemFactChange(TaskAssignment taskAssignment,
                User user,
                Consumer<TaskAssigningSolution> solutionBeforeChangesConsumer) {
            super(taskAssignment, user);
            this.solutionBeforeChangesConsumer = solutionBeforeChangesConsumer;
        }

        @Override
        public void doChange(ScoreDirector<TaskAssigningSolution> scoreDirector) {
            TaskAssigningSolution solution = scoreDirector.getWorkingSolution();
            if (solutionBeforeChangesConsumer != null) {
                solutionBeforeChangesConsumer.accept(solution);
            }
            super.doChange(scoreDirector);
        }
    }

    private class ProgrammedAssignTaskProblemFactChange extends ProgrammedProblemFactChange<AssignTaskProblemFactChange> {

        StringBuilder workingSolutionBeforeChange = new StringBuilder();

        ProgrammedAssignTaskProblemFactChange(TaskAssignment taskAssignment, User user) {
            setChange(new WorkingSolutionAwareProblemFactChange(taskAssignment,
                    user,
                    workingSolution -> printSolution(workingSolution, workingSolutionBeforeChange)));
        }

        String workingSolutionBeforeChangeAsString() {
            return workingSolutionBeforeChange.toString();
        }

        String solutionAfterChangeAsString() {
            return printSolution(super.getSolutionAfterChange());
        }
    }

    @Test
    void assignTaskProblemFactChange24Tasks8Users() throws Exception {
        assignTaskProblemFactChangeFixedChangeSet(SET_OF_24TASKS_8USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void assignTaskProblemFactChange24Tasks8UsersRandom() throws Exception {
        assignTaskProblemFactChangeRandomChangeSet(SET_OF_24TASKS_8USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.TURTLE_TEST)
    void assignTaskProblemFactChange50Tasks5Users() throws Exception {
        assignTaskProblemFactChangeFixedChangeSet(SET_OF_50TASKS_5USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void assignTaskProblemFactChange50Tasks5UsersRandom() throws Exception {
        assignTaskProblemFactChangeRandomChangeSet(SET_OF_50TASKS_5USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.TURTLE_TEST)
    void assignTaskProblemFactChange100Tasks5Users() throws Exception {
        assignTaskProblemFactChangeFixedChangeSet(SET_OF_100TASKS_5USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void assignTaskProblemFactChange100Tasks5UsersRandom() throws Exception {
        assignTaskProblemFactChangeRandomChangeSet(SET_OF_100TASKS_5USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.TURTLE_TEST)
    void assignTaskProblemFactChange500Tasks20Users() throws Exception {
        assignTaskProblemFactChangeFixedChangeSet(SET_OF_500TASKS_20USERS_SOLUTION.resource());
    }

    @Test
    @Tag(TestConstants.DEVELOPMENT_ONLY_TEST)
    void assignTaskProblemFactChange500Tasks20UsersRandom() throws Exception {
        assignTaskProblemFactChangeRandomChangeSet(SET_OF_500TASKS_20USERS_SOLUTION.resource());
    }

    @Test
    void assignTaskProblemFactChangeUserNotFound() {
        TaskAssigningSolution solution = readTaskAssigningSolution(SET_OF_24TASKS_8USERS_SOLUTION.resource());
        TaskAssignment taskAssignment = solution.getTaskAssignmentList().get(0);
        User user = new User("Non Existing");
        Assertions.assertThatThrownBy(() -> executeSequentialChanges(solution, Collections.singletonList(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user))))
                .hasMessage("Expected user: %s was not found in current working solution", user);
    }

    private void assignTaskProblemFactChangeFixedChangeSet(String solutionResource) throws Exception {
        TaskAssigningSolution solution = readTaskAssigningSolution(solutionResource);
        solution.getUserList().add(PLANNING_USER);

        //prepare the list of changes to program
        List<ProgrammedAssignTaskProblemFactChange> programmedChanges = new ArrayList<>();

        //assign Task_0 to User_0
        TaskAssignment taskAssignment = solution.getTaskAssignmentList().get(0);
        User user = solution.getUserList().get(0);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //assign Task_10 to User_0
        taskAssignment = solution.getTaskAssignmentList().get(10);
        user = solution.getUserList().get(0);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //assign Task_15 to User_2
        taskAssignment = solution.getTaskAssignmentList().get(15);
        user = solution.getUserList().get(2);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //assign Task_13 to User_3
        taskAssignment = solution.getTaskAssignmentList().get(13);
        user = solution.getUserList().get(3);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //assign Task_13 to User_4
        taskAssignment = solution.getTaskAssignmentList().get(13);
        user = solution.getUserList().get(4);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //assign Task_13 to User_5
        taskAssignment = solution.getTaskAssignmentList().get(13);
        user = solution.getUserList().get(5);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //assign Task_15 to User_5
        taskAssignment = solution.getTaskAssignmentList().get(15);
        user = solution.getUserList().get(5);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //assign Task_16 to User_5
        taskAssignment = solution.getTaskAssignmentList().get(16);
        user = solution.getUserList().get(5);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //assign Task_17 to User_5
        taskAssignment = solution.getTaskAssignmentList().get(17);
        user = solution.getUserList().get(5);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //repeat assign Task_17 to User_5
        taskAssignment = solution.getTaskAssignmentList().get(17);
        user = solution.getUserList().get(5);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //repeat assign Task_15 to User_5
        taskAssignment = solution.getTaskAssignmentList().get(15);
        user = solution.getUserList().get(5);
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        String nextTaskId = NEW_TASK + 1;

        //assign a brand new task "NewTask_x and assign to User_0
        user = solution.getUserList().get(0);
        taskAssignment = new TaskAssignment(Task.newBuilder().id(nextTaskId).name(nextTaskId).build());
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //assign a brand new task "NewTask_x and assign to User_2
        nextTaskId = NEW_TASK + 2;
        user = solution.getUserList().get(2);
        taskAssignment = new TaskAssignment(Task.newBuilder().id(nextTaskId).name(nextTaskId).build());
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        //assign a brand new task "NewTask_x and assign to User_5
        nextTaskId = NEW_TASK + 3;
        user = solution.getUserList().get(5);
        taskAssignment = new TaskAssignment(Task.newBuilder().id(nextTaskId).name(nextTaskId).build());
        programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(taskAssignment, user));

        assignTaskProblemFactChange(solution, solutionResource, FIXED_TEST, programmedChanges);
    }

    private void assignTaskProblemFactChangeRandomChangeSet(String solutionResource) throws Exception {
        TaskAssigningSolution solution = readTaskAssigningSolution(solutionResource);
        solution.getUserList().add(PLANNING_USER);

        int taskCount = solution.getTaskAssignmentList().size();
        int userCount = solution.getUserList().size();
        int randomChanges = taskCount / 2 + random.nextInt(taskCount / 2);

        //prepare the list of changes to program
        List<ProgrammedAssignTaskProblemFactChange> programmedChanges = new ArrayList<>();

        TaskAssignment randomTask;
        User randomUser;
        for (int i = 0; i < randomChanges; i++) {
            randomTask = solution.getTaskAssignmentList().get(random.nextInt(taskCount));
            randomUser = solution.getUserList().get(random.nextInt(userCount));
            programmedChanges.add(new ProgrammedAssignTaskProblemFactChange(randomTask, randomUser));
        }
        assignTaskProblemFactChange(solution, solutionResource, RANDOM_TEST, programmedChanges);
    }

    private void assignTaskProblemFactChange(TaskAssigningSolution solution,
            String solutionResource,
            String testType,
            List<ProgrammedAssignTaskProblemFactChange> programmedChanges) throws Exception {
        TaskAssigningSolution initialSolution = executeSequentialChanges(solution, programmedChanges);
        if (writeTestFiles()) {
            writeProblemFactChangesTestFiles(initialSolution,
                    solutionResource,
                    "AssignTaskExecutableProblemFactChangeTest.assignTaskProblemFactChangeTest",
                    testType,
                    programmedChanges,
                    ProgrammedAssignTaskProblemFactChange::workingSolutionBeforeChangeAsString,
                    ProgrammedAssignTaskProblemFactChange::solutionAfterChangeAsString);
        }

        //each partial solution must have the change that was applied on it.
        for (ProgrammedAssignTaskProblemFactChange change : programmedChanges) {
            assertAssignTaskProblemFactChangeWasProduced(change.getChange(), change.getSolutionAfterChange());
        }

        //finally the last solution must have the result of all the changes.
        TaskAssigningSolution lastSolution = programmedChanges.get(programmedChanges.size() - 1).getSolutionAfterChange();
        Map<String, AssignTaskProblemFactChange> summarizedChanges = new HashMap<>();
        programmedChanges.forEach(change -> {
            //if task was changed multiple times record only the last change.
            summarizedChanges.put(change.getChange().getTaskAssignment().getId(), change.getChange());
        });
        for (AssignTaskProblemFactChange change : summarizedChanges.values()) {
            assertAssignTaskProblemFactChangeWasProduced(change, lastSolution);
        }
    }

    /**
     * Given an AssignTaskProblemFactChange and a solution that was produced as the result of applying the change,
     * asserts that the assignment defined by the change is not violated (exists in) by the solution.
     * The assignment defined in the change must also be pinned in the produced solution as well as any other
     * previous assignment for the given user.
     * 
     * @param change The change that was executed for producing the solution.
     * @param solution The produced solution.
     */
    private void assertAssignTaskProblemFactChangeWasProduced(AssignTaskProblemFactChange change, TaskAssigningSolution solution) throws Exception {
        User internalUser = solution.getUserList().stream()
                .filter(user -> Objects.equals(user.getId(), change.getUser().getId()))
                .findFirst().orElseThrow(() -> new Exception("User: " + change.getUser() + " was not found in solution."));

        TaskAssignment internalTaskAssignment = solution.getTaskAssignmentList().stream()
                .filter(taskAssignment -> Objects.equals(taskAssignment.getId(), change.getTaskAssignment().getId()))
                .findFirst().orElseThrow(() -> new Exception("TaskAssignment: " + change + " was not found in solution."));
        assertThat(internalTaskAssignment.getUser()).isEqualTo(internalUser);
        assertThat(internalTaskAssignment.isPinned()).isTrue();
        //all the previous tasks must be pinned by construction and be assigned to the user
        ChainElement previousElement = internalTaskAssignment.getPreviousElement();
        while (previousElement != null) {
            if (previousElement.isTaskAssignment()) {
                TaskAssignment previousTaskAssignment = (TaskAssignment) previousElement;
                assertThat(previousTaskAssignment.isPinned()).isTrue();
                assertThat(previousTaskAssignment.getUser()).isEqualTo(internalUser);
                previousElement = previousTaskAssignment.getPreviousElement();
            } else {
                assertThat(previousElement).isEqualTo(internalUser);
                previousElement = null;
            }
        }
        //all the next tasks must to the user.
        TaskAssignment nextTaskAssignment = internalTaskAssignment.getNextElement();
        while (nextTaskAssignment != null) {
            assertThat(nextTaskAssignment.getUser()).isEqualTo(internalUser);
            nextTaskAssignment = nextTaskAssignment.getNextElement();
        }
    }
}
