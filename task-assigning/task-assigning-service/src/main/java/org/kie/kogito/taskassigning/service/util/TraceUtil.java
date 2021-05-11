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

package org.kie.kogito.taskassigning.service.util;

import java.util.List;
import java.util.Map;

import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AddTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AddUserProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AssignTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.DisableUserProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.ReleaseTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.RemoveTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.RemoveUserProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.TaskInfoChangeProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.UserPropertyChangeProblemFactChange;
import org.kie.kogito.taskassigning.service.PlanningItem;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.slf4j.Logger;

public class TraceUtil {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String TASK_WITH_NAME_FORMAT = " -> ({}, {})";

    private TraceUtil() {
    }

    public static void traceProgrammedChanges(Logger logger,
            List<RemoveTaskProblemFactChange> removedTasksChanges,
            List<ReleaseTaskProblemFactChange> releasedTasksChanges,
            Map<String, List<IndexedElement<AssignTaskProblemFactChange>>> changesByUserId,
            List<TaskInfoChangeProblemFactChange> taskPropertyChanges,
            List<AddTaskProblemFactChange> newTasksChanges,
            List<AddUserProblemFactChange> newUsersChanges,
            List<ProblemFactChange<TaskAssigningSolution>> usersChanges,
            List<RemoveUserProblemFactChange> removedUsersChanges) {

        logger.trace("***** Programmed changes *****");

        logger.trace("*** Removed tasks ***");
        logger.trace("Total tasks removed from solution is {}", removedTasksChanges.size());
        removedTasksChanges.forEach(change -> logger.trace(TASK_WITH_NAME_FORMAT,
                change.getTaskAssignment().getTask().getId(),
                change.getTaskAssignment().getTask().getName()));
        logger.trace("*** End of removed tasks ***{}", NEW_LINE);

        logger.trace("*** Released tasks ***");
        logger.trace("Total tasks released from solution is {}", releasedTasksChanges.size());
        releasedTasksChanges.forEach(change -> logger.trace(TASK_WITH_NAME_FORMAT,
                change.getTaskAssignment().getTask().getId(),
                change.getTaskAssignment().getTask().getName()));
        logger.trace("*** End of released tasks ***{}", NEW_LINE);

        logger.trace("*** Changes per user ***");
        logger.trace("Total users with programmed changes is {}", changesByUserId.size());
        changesByUserId.forEach((key, perUserChanges) -> {
            if (perUserChanges != null) {
                perUserChanges.forEach(change -> {
                    logger.trace("{}  AssignTaskToUserChanges for user: {}", NEW_LINE, key);
                    logger.trace("{}   -> taskId: {}, pinned: {}, index: {}",
                            NEW_LINE,
                            change.getElement().getTaskAssignment().getTask().getId(),
                            change.isPinned(),
                            change.getIndex());
                    logger.trace("  End of AssignTaskToUserChanges for user: {}", key);
                });
            }
        });
        logger.trace("*** End of changes per user ***{}", NEW_LINE);

        logger.trace("*** Task property changes ***");
        logger.trace("Total tasks with property changes is {}", taskPropertyChanges.size());
        taskPropertyChanges.forEach(change -> logger.trace(TASK_WITH_NAME_FORMAT,
                change.getTaskAssignment().getTask().getId(),
                change.getTaskAssignment().getTask().getName()));
        logger.trace("*** End of task property changes ***{}", NEW_LINE);

        logger.trace("*** New tasks ***");
        logger.trace("Total new tasks added to solution is {}", newTasksChanges.size());
        newTasksChanges.forEach(change -> logger.trace(TASK_WITH_NAME_FORMAT,
                change.getTaskAssignment().getTask().getId(),
                change.getTaskAssignment().getTask().getName()));
        logger.trace("*** End of new tasks ***{}", NEW_LINE);

        logger.trace("*** New users ***");
        logger.trace("Total new users added to solution is {}", newUsersChanges.size());
        newUsersChanges.forEach(change -> logger.trace(" -> {}", change.getUser()));
        logger.trace("*** End of new users ***{}", NEW_LINE);

        logger.trace("*** User changes ***");
        logger.trace("Total users with changes is {}", usersChanges.size());
        usersChanges.stream()
                .filter(change -> change instanceof DisableUserProblemFactChange)
                .forEach(change -> logger.trace(" -> disabled {}", ((DisableUserProblemFactChange) change).getUser()));
        usersChanges.stream()
                .filter(change -> change instanceof UserPropertyChangeProblemFactChange)
                .forEach(change -> logger.trace(" -> modified {}", ((UserPropertyChangeProblemFactChange) change).getUser()));
        logger.trace("*** End of user changes ***{}", NEW_LINE);

        logger.trace("*** Removed users ***");
        logger.trace("Total users removed from solution is {}", removedUsersChanges.size());
        removedUsersChanges.forEach(change -> logger.trace(" -> removed {}", change.getUser()));
        logger.trace("*** End of removed users ***");

        logger.trace("***** End of Programmed changes *****");
    }

    public static void traceSolution(Logger logger, TaskAssigningSolution solution) {
        logger.trace("*** Start of solution trace, with users = {} and tasks = {} ***",
                solution.getUserList().size(), solution.getTaskAssignmentList().size());
        for (User user : solution.getUserList()) {
            TaskAssignment nextElement = user.getNextElement();
            if (nextElement == null) {
                logger.trace("{} -> has no tasks", user.getId());
            } else {
                while (nextElement != null) {
                    logger.trace("{} -> {}, pinned: {}, priority: {}, state: {}",
                            user.getId(),
                            nextElement.getId(),
                            nextElement.isPinned(),
                            nextElement.getTask().getPriority(),
                            nextElement.getTask().getState());

                    nextElement = nextElement.getNextElement();
                }
            }
        }
        logger.trace("*** End of solution trace ***");
    }

    public static void tracePlanning(Logger logger, List<PlanningItem> planningItems) {
        logger.trace("*** Start of calculated planning trace with {} items ***", planningItems.size());
        planningItems.forEach(item -> logger.trace("{} -> {}",
                item.getTargetUser(),
                item.getTask().getId()));
        logger.trace("*** End of calculated planning trace ***");
    }
}
