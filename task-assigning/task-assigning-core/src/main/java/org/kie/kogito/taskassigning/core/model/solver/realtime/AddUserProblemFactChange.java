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

import org.kie.kogito.taskassigning.core.TaskAssigningRuntimeException;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.User;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.ProblemFactChange;

public class AddUserProblemFactChange implements ProblemFactChange<TaskAssigningSolution> {

    private User user;

    public AddUserProblemFactChange(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void doChange(ScoreDirector<TaskAssigningSolution> scoreDirector) {
        final TaskAssigningSolution workingSolution = scoreDirector.getWorkingSolution();
        final User workingUser = scoreDirector.lookUpWorkingObjectOrReturnNull(user);
        if (workingUser != null) {
            throw new TaskAssigningRuntimeException(String.format("A user with the given identifier id: %s already exists", user.getId()));
        }
        // Shallow clone the user list so only workingSolution is affected, not bestSolution
        workingSolution.setUserList(new ArrayList<>(workingSolution.getUserList()));
        // Add the problem fact itself
        scoreDirector.beforeProblemFactAdded(user);
        workingSolution.getUserList().add(user);
        scoreDirector.afterProblemFactAdded(user);
        scoreDirector.triggerVariableListeners();
    }
}
