/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.User;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddUserProblemFactChangeTest {

    private static final String USER_ID = "USER_ID";

    @Mock
    private ScoreDirector<TaskAssigningSolution> scoreDirector;

    private TaskAssigningSolution workingSolution;

    private AddUserProblemFactChange change;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(USER_ID);
        workingSolution = new TaskAssigningSolution("1", new ArrayList<>(), new ArrayList<>());
        change = new AddUserProblemFactChange(user);
    }

    @Test
    void getUser() {
        assertThat(change.getUser()).isEqualTo(user);
    }

    @Test
    void doChange() {
        when(scoreDirector.getWorkingSolution()).thenReturn(workingSolution);
        change.doChange(scoreDirector);
        verify(scoreDirector).beforeProblemFactAdded(user);
        verify(scoreDirector).afterProblemFactAdded(user);
        verify(scoreDirector).triggerVariableListeners();
        assertThat(workingSolution.getUserList()).contains(user);
    }

    @Test
    void doChangeUserAlreadyExists() {
        when(scoreDirector.getWorkingSolution()).thenReturn(workingSolution);
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(user)).thenReturn(user);
        assertThatThrownBy(() -> change.doChange(scoreDirector))
                .hasMessage("A user with the given identifier id: %s already exists", USER_ID);
    }
}
