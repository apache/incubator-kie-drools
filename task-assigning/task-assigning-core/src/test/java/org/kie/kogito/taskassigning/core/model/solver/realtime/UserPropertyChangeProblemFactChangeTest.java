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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.Group;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockTaskAssignment;
import static org.kie.kogito.taskassigning.core.model.TestUtil.mockUser;
import static org.kie.kogito.taskassigning.core.model.solver.realtime.ProblemFactChangeUtilTest.assertTaskWasNotReleased;
import static org.kie.kogito.taskassigning.core.model.solver.realtime.ProblemFactChangeUtilTest.assertTaskWasReleased;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPropertyChangeProblemFactChangeTest {

    private static final String USER_ID = "USER_ID";
    private static final String TASK_ID1 = "1";
    private static final String TASK_ID2 = "2";
    private static final String TASK_ID3 = "3";
    private static final String TASK_ID4 = "4";

    @Mock
    private ScoreDirector<TaskAssigningSolution> scoreDirector;

    private UserPropertyChangeProblemFactChange change;

    private User user;

    private Map<String, Object> newAttributes;

    private Set<Group> newGroups;

    private boolean newEnabled;

    @BeforeEach
    void setUp() {
        newAttributes = new HashMap<>();
        newAttributes.put("attribute1", "value1");
        newGroups = new HashSet<>();
        newGroups.add(new Group("test-group"));
        newEnabled = false;
        user = new User(USER_ID);
        change = new UserPropertyChangeProblemFactChange(user, newEnabled, newAttributes, newGroups);
    }

    @Test
    void getUser() {
        assertThat(change.getUser()).isEqualTo(user);
    }

    @Test
    void getNewAttributes() {
        assertThat(change.getNewAttributes()).isEqualTo(newAttributes);
    }

    @Test
    void getNewGroups() {
        assertThat(change.getNewGroups()).isEqualTo(newGroups);
    }

    @Test
    void isEnabled() {
        assertThat(change.isEnabled()).isFalse();
    }

    @Test
    void doChange() {
        List<TaskAssignment> workingUserTasks = Arrays.asList(mockTaskAssignment(TASK_ID1, true),
                mockTaskAssignment(TASK_ID2, true),
                mockTaskAssignment(TASK_ID3, false),
                mockTaskAssignment(TASK_ID4, false));

        User workingUser = mockUser(USER_ID, workingUserTasks);
        workingUser.setGroups(new HashSet<>());
        workingUser.setAttributes(new HashMap<>());
        workingUser.setEnabled(true);

        when(scoreDirector.lookUpWorkingObjectOrReturnNull(user)).thenReturn(workingUser);
        change.doChange(scoreDirector);
        verify(scoreDirector).beforeProblemPropertyChanged(workingUser);
        verify(scoreDirector).afterProblemPropertyChanged(workingUser);
        verify(scoreDirector).triggerVariableListeners();

        assertThat(workingUser.isEnabled()).isEqualTo(newEnabled);
        assertThat(workingUser.getGroups()).isEqualTo(newGroups);
        assertThat(workingUser.getAttributes()).isEqualTo(newAttributes);

        List<TaskAssignment> pinnedTasks = Arrays.asList(workingUserTasks.get(0), workingUserTasks.get(1));
        for (TaskAssignment pinnedTask : pinnedTasks) {
            assertThat(pinnedTask.isPinned()).isTrue();
            assertTaskWasNotReleased(pinnedTask, scoreDirector);
        }

        List<TaskAssignment> releasedTasks = Arrays.asList(workingUserTasks.get(2), workingUserTasks.get(3));
        releasedTasks.forEach(releasedTask -> assertTaskWasReleased(releasedTask, scoreDirector));
    }

    @Test
    void doChangeUserDontExists() {
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(user)).thenReturn(null);
        change.doChange(scoreDirector);
        verify(scoreDirector, never()).beforeProblemPropertyChanged(any());
        verify(scoreDirector, never()).afterProblemPropertyChanged(any());
        verify(scoreDirector, never()).triggerVariableListeners();
    }
}
