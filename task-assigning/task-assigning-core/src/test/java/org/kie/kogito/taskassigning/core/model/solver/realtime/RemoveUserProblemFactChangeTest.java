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
package org.kie.kogito.taskassigning.core.model.solver.realtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockTaskAssignment;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockUser;
import static org.kie.kogito.taskassigning.core.model.solver.realtime.ProblemFactChangeUtilTest.assertTaskWasReleased;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveUserProblemFactChangeTest {

    private static final String USER_ID_1 = "USER_ID_1";
    private static final String USER_ID_2 = "USER_ID_2";
    private static final String USER_ID_3 = "USER_ID_3";

    private static final String TASK_ID1 = "1";
    private static final String TASK_ID2 = "2";
    private static final String TASK_ID3 = "3";
    private static final String TASK_ID4 = "4";

    @Mock
    private ScoreDirector<TaskAssigningSolution> scoreDirector;

    private TaskAssigningSolution workingSolution;

    private RemoveUserProblemFactChange change;

    private User user;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user = new User(USER_ID_1);
        workingSolution = new TaskAssigningSolution("1", new ArrayList<>(), new ArrayList<>());
        user2 = new User(USER_ID_2);
        user3 = new User(USER_ID_3);
        workingSolution.getUserList().add(user2);
        workingSolution.getUserList().add(user3);
        change = new RemoveUserProblemFactChange(user);
    }

    @Test
    void getUser() {
        assertThat(change.getUser()).isEqualTo(user);
    }

    @Test
    void doChange() {
        List<TaskAssignment> workingUserTasks = Arrays.asList(mockTaskAssignment(TASK_ID1, true),
                mockTaskAssignment(TASK_ID2, true),
                mockTaskAssignment(TASK_ID3, false),
                mockTaskAssignment(TASK_ID4, false));

        User workingUser = mockUser(USER_ID_1, workingUserTasks);
        workingUser.setEnabled(true);
        workingSolution.getUserList().add(workingUser);
        int originalUsersSize = workingSolution.getUserList().size();

        when(scoreDirector.getWorkingSolution()).thenReturn(workingSolution);
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(user)).thenReturn(workingUser);

        change.doChange(scoreDirector);
        verify(scoreDirector).beforeProblemPropertyChanged(workingUser);
        verify(scoreDirector).afterProblemPropertyChanged(workingUser);
        verify(scoreDirector).triggerVariableListeners();
        assertThat(workingUser.isEnabled()).isFalse();

        workingUserTasks.forEach(task -> assertTaskWasReleased(task, scoreDirector));

        verify(scoreDirector).beforeProblemFactRemoved(workingUser);
        verify(scoreDirector).afterProblemFactRemoved(workingUser);
        assertThat(workingSolution.getUserList().size()).isEqualTo(originalUsersSize - 1);
        assertThat(workingSolution.getUserList()).contains(user2, user3);
        assertThat(workingSolution.getUserList()).doesNotContain(workingUser);
    }

    @Test
    void doChangeUserDontExists() {
        when(scoreDirector.getWorkingSolution()).thenReturn(workingSolution);
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(user)).thenReturn(null);
        change.doChange(scoreDirector);
        verify(scoreDirector, never()).beforeProblemPropertyChanged(any());
        verify(scoreDirector, never()).afterProblemPropertyChanged(any());
        verify(scoreDirector, never()).beforeProblemFactRemoved(any());
        verify(scoreDirector, never()).afterProblemFactRemoved(any());
        assertThat(workingSolution.getUserList().size()).isEqualTo(2);
        assertThat(workingSolution.getUserList()).contains(user2, user3);
        verify(scoreDirector, never()).triggerVariableListeners();
    }
}
