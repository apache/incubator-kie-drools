/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.core.model.solver.realtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockTaskAssignment;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignTaskProblemFactChangeTest {

    private static final String USER_ID = "USER_ID";
    private static final String TASK_0 = "TASK_0";
    private static final String TASK_1 = "TASK_1";
    private static final String TASK_2 = "TASK_2";
    private static final String TASK_3 = "TASK_3";
    private static final String TASK_4 = "TASK_4";

    private static final String NEW_TASK_ID = "NEW_TASK_ID";

    private static final String USER2_TASK_0 = "USER2_TASK_0";
    private static final String USER2_TASK_1 = "USER2_TASK_1";
    private static final String USER2_TASK_2 = "USER2_TASK_2";

    @Mock
    private ScoreDirector<TaskAssigningSolution> scoreDirector;

    private TaskAssigningSolution solution;

    private AssignTaskProblemFactChange change;

    @BeforeEach
    void setUp() {
        solution = new TaskAssigningSolution("1", new ArrayList<>(), new ArrayList<>());
        lenient().when(scoreDirector.getWorkingSolution()).thenReturn(solution);
    }

    @Test
    void getTaskAssignment() {
        TaskAssignment taskAssignment = new TaskAssignment();
        change = new AssignTaskProblemFactChange(taskAssignment, new User());
        assertThat(change.getTaskAssignment()).isSameAs(taskAssignment);
    }

    @Test
    void getUser() {
        User user = new User();
        change = new AssignTaskProblemFactChange(new TaskAssignment(), user);
        assertThat(change.getUser()).isSameAs(user);
    }

    @Test
    void doChangeForNewTaskAssignment() {
        // Initial assignments:
        // User <- TASK_0 <- TASK_1 <- TASK_2 <- TASK_3 <- TASK_4
        List<TaskAssignment> userTasks = buildUserTasks();
        User user = buildUser(userTasks);

        TaskAssignment taskAssignment = mockTaskAssignment(NEW_TASK_ID, false);
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(user)).thenReturn(user);
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment)).thenReturn(null);
        change = new AssignTaskProblemFactChange(taskAssignment, user);
        change.doChange(scoreDirector);

        assertThat(solution.getTaskAssignmentList()).contains(taskAssignment);
        assertPinning(taskAssignment);
        verify(scoreDirector).beforeEntityAdded(taskAssignment);
        verify(scoreDirector).afterEntityAdded(taskAssignment);
        verify(scoreDirector).triggerVariableListeners();
        // Expected assignments:
        // User <- TASK_0 <- TASK_1 <- TASK_2 <- NEW_TASK_ID <- TASK_3 <- TASK_4
        assertTaskPositions(user, userTasks.get(0), userTasks.get(1), taskAssignment, userTasks.get(2), userTasks.get(3), userTasks.get(4));
    }

    @Test
    void doChangeForExistingTaskAssignmentThatBelongsToSameUserWithPositionChange() {
        // Initial assignments:
        // User <- TASK_0 <- TASK_1 <- TASK_2 <- TASK_3 <- TASK_4
        List<TaskAssignment> userTasks = buildUserTasks();
        User user = buildUser(userTasks);

        TaskAssignment taskAssignment = mockTaskAssignment(TASK_3, false);
        TaskAssignment workingTaskAssignment = userTasks.get(3);
        TaskAssignment expectedPreviousElement = userTasks.get(1);

        // Expected assignments:
        // User <- TASK_0 <- TASK_1 <- TASK_3 <- TASK_2 <- TASK_4
        doChangeForExistingTaskAssignmentThatBelongsToSameUser(user,
                                                               taskAssignment,
                                                               workingTaskAssignment,
                                                               expectedPreviousElement,
                                                               Arrays.asList(userTasks.get(0),
                                                                             userTasks.get(1),
                                                                             workingTaskAssignment,
                                                                             userTasks.get(2),
                                                                             userTasks.get(4)));
    }

    @Test
    void doChangeForExistingTaskAssignmentThatBelongsToSameUserWithoutPositionChange() {
        // Initial assignments:
        // User <- TASK_0 <- TASK_1 <- TASK_2 <- TASK_3 <- TASK_4
        List<TaskAssignment> userTasks = buildUserTasks();
        User user = buildUser(userTasks);

        TaskAssignment taskAssignment = mockTaskAssignment(TASK_2, false);
        TaskAssignment originalPreviousElement = userTasks.get(1);
        TaskAssignment workingTaskAssignment = userTasks.get(2);

        // Expected assignments:
        // User <- TASK_0 <- TASK_1 <- TASK_2 <- TASK_3 <- TASK_4
        doChangeForExistingTaskAssignmentThatBelongsToSameUser(user,
                                                               taskAssignment,
                                                               workingTaskAssignment,
                                                               originalPreviousElement,
                                                               Arrays.asList(userTasks.get(0),
                                                                             userTasks.get(1),
                                                                             workingTaskAssignment,
                                                                             userTasks.get(3),
                                                                             userTasks.get(4)));
    }

    private void doChangeForExistingTaskAssignmentThatBelongsToSameUser(User user,
                                                                        TaskAssignment taskAssignment,
                                                                        TaskAssignment workingTaskAssignment,
                                                                        TaskAssignment expectedPreviousElement,
                                                                        List<TaskAssignment> expectedTasksPositions) {
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(user)).thenReturn(user);
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment)).thenReturn(workingTaskAssignment);
        change = new AssignTaskProblemFactChange(taskAssignment, user);
        change.doChange(scoreDirector);

        assertPinning(workingTaskAssignment);
        assertThat(workingTaskAssignment.getPreviousElement()).isSameAs(expectedPreviousElement);
        verify(scoreDirector, never()).beforeEntityAdded(any());
        verify(scoreDirector, never()).afterEntityAdded(any());
        verify(scoreDirector).triggerVariableListeners();

        assertTaskPositions(user, expectedTasksPositions.toArray(new TaskAssignment[0]));
    }

    @Test
    void doChangeForExistingUserAndTaskAssignmentThatBelongsToAnotherUser() {
        // Initial assignments:
        // User1 <- TASK_0 <- TASK_1 <- TASK_2 <- TASK_3 <- TASK_4
        // User2 <- USER2_TASK_0 <- USER2_TASK_1 <- USER2_TASK_2
        List<TaskAssignment> user1Tasks = buildUserTasks();
        User user1 = buildUser(user1Tasks);

        List<TaskAssignment> user2Tasks = Arrays.asList(mockTaskAssignment(USER2_TASK_0, false),
                                                        mockTaskAssignment(USER2_TASK_1, false),
                                                        mockTaskAssignment(USER2_TASK_2, false));
        User user2 = mockUser("USER_2", user2Tasks);

        TaskAssignment taskAssignment = mockTaskAssignment(USER2_TASK_1, false);
        TaskAssignment workingTaskAssignment = user2Tasks.get(1);
        ChainElement originalPreviousElement = workingTaskAssignment.getPreviousElement();
        TaskAssignment expectedPreviousElement = user1Tasks.get(1);

        when(scoreDirector.lookUpWorkingObjectOrReturnNull(user1)).thenReturn(user1);
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment)).thenReturn(workingTaskAssignment);
        change = new AssignTaskProblemFactChange(taskAssignment, user1);
        change.doChange(scoreDirector);

        assertPinning(workingTaskAssignment);
        assertThat(workingTaskAssignment.getPreviousElement()).isSameAs(expectedPreviousElement);
        assertThat(user2Tasks.get(2).getPreviousElement()).isSameAs(originalPreviousElement);
        verify(scoreDirector, never()).beforeEntityAdded(any());
        verify(scoreDirector, never()).afterEntityAdded(any());
        verify(scoreDirector).triggerVariableListeners();

        // Expected assignments:
        // User1 <- TASK_0 <- TASK_1 <- USER2_TASK_1 <- TASK_2 <- TASK_3 <- TASK_4
        // User2 <- USER2_TASK_0 <- USER2_TASK_2
        assertTaskPositions(user1, user1Tasks.get(0), user1Tasks.get(1), workingTaskAssignment, user1Tasks.get(2), user1Tasks.get(3), user1Tasks.get(4));
        assertTaskPositions(user2, user2Tasks.get(0), user2Tasks.get(2));
    }

    @Test
    void doChangeForNonExistingUserAndNotAddIt() {
        User user = new User();
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(user)).thenReturn(null);
        change = new AssignTaskProblemFactChange(new TaskAssignment(), user, false);
        assertThatThrownBy(() -> change.doChange(scoreDirector))
                .hasMessage("Expected user: %s was not found in current working solution", user);
        assertThat(solution.getUserList()).isEmpty();
    }

    @Test
    void doChangeForNonExistingUserAndAddIt() {
        User user = new User();
        TaskAssignment taskAssignment = new TaskAssignment();
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(user)).thenReturn(null);
        change = new AssignTaskProblemFactChange(taskAssignment, user, true);
        change.doChange(scoreDirector);
        verify(scoreDirector).beforeProblemFactAdded(user);
        verify(scoreDirector).afterProblemFactAdded(user);
        assertTaskPositions(user, taskAssignment);
        assertThat(solution.getUserList()).contains(user);
    }

    private void assertTaskPositions(User user, TaskAssignment... taskAssignments) {
        for (int i = taskAssignments.length - 1; i > 0; i--) {
            assertThat(taskAssignments[i].getPreviousElement()).isSameAs(taskAssignments[i - 1]);
        }
        assertThat(taskAssignments[0].getPreviousElement()).isEqualTo(user);
    }

    private void assertPinning(TaskAssignment taskAssignment) {
        verify(scoreDirector).beforeProblemPropertyChanged(taskAssignment);
        assertThat(taskAssignment.isPinned()).isTrue();
        verify(scoreDirector).afterProblemPropertyChanged(taskAssignment);
    }

    /**
     * Generates the following task assignments
     * <p>
     * TASK_0(pinned = true) -> TASK_1(pinned = true) -> TASK_2(pinned = false) -> TASK_3(pinned = false) -> TASK_4(pinned = false)
     */
    private List<TaskAssignment> buildUserTasks() {
        return Arrays.asList(mockTaskAssignment(TASK_0, true),
                             mockTaskAssignment(TASK_1, true),
                             mockTaskAssignment(TASK_2, false),
                             mockTaskAssignment(TASK_3, false),
                             mockTaskAssignment(TASK_4, false));
    }

    private User buildUser(List<TaskAssignment> userTasks) {
        return mockUser(USER_ID, userTasks);
    }
}
