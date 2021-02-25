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
package org.kie.kogito.taskassigning.core.model.solver.condition;

import org.kie.kogito.taskassigning.core.model.DefaultLabels;
import org.kie.kogito.taskassigning.core.model.ModelConstants;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.User;

import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.hasAllLabels;
import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.isPotentialOwner;

public class TaskAssigningConditions {

    private TaskAssigningConditions() {
    }

    /**
     * @param task a task instance for evaluation.
     * @param user a user instance for evaluation.
     * @return true if the given user is enabled and is a potential owner for the task or is the planning user,
     *         false in any other case.
     */
    public static boolean userMeetsPotentialOwnerOrPlanningUserCondition(Task task, User user) {
        return user != null && user.isEnabled() && (ModelConstants.IS_PLANNING_USER.test(user.getId()) || isPotentialOwner(task, user));
    }

    /**
     * @param task a task instance for evaluation.
     * @param user a user instance for evaluation.
     * @return true if the given user is enabled and has all the task defined skills if any or is the planning user,
     *         false in any other case.
     */
    public static boolean userMeetsRequiredSkillsOrPlanningUserCondition(Task task, User user) {
        return user != null && user.isEnabled() && (ModelConstants.IS_PLANNING_USER.test(user.getId()) || hasAllLabels(task, user, DefaultLabels.SKILLS.name()));
    }
}
