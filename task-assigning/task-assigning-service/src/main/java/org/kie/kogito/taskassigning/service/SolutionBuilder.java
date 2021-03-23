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

package org.kie.kogito.taskassigning.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.kie.kogito.taskassigning.service.util.IndexedElement;
import org.kie.kogito.taskassigning.service.util.UserUtil;

import static org.kie.kogito.taskassigning.core.model.ModelConstants.DUMMY_TASK_ASSIGNMENT;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.DUMMY_TASK_ASSIGNMENT_PLANNER_1738;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.IS_PLANNING_USER;
import static org.kie.kogito.taskassigning.core.model.ModelConstants.PLANNING_USER;
import static org.kie.kogito.taskassigning.service.TaskState.RESERVED;
import static org.kie.kogito.taskassigning.service.util.IndexedElement.addInOrder;
import static org.kie.kogito.taskassigning.service.util.TaskUtil.fromTaskData;
import static org.kie.kogito.taskassigning.service.util.UserUtil.filterDuplicates;

public class SolutionBuilder {

    private List<TaskData> taskDataList;
    private List<org.kie.kogito.taskassigning.user.service.api.User> externalUsers;

    private SolutionBuilder() {
    }

    public static SolutionBuilder newBuilder() {
        return new SolutionBuilder();
    }

    public SolutionBuilder withTasks(List<TaskData> taskDataList) {
        this.taskDataList = taskDataList;
        return this;
    }

    public SolutionBuilder withUsers(List<org.kie.kogito.taskassigning.user.service.api.User> externalUsers) {
        this.externalUsers = externalUsers;
        return this;
    }

    public TaskAssigningSolution build() {
        final List<TaskAssignment> taskAssignments = new ArrayList<>();
        final Map<String, List<IndexedElement<TaskAssignment>>> assignmentsByUserId = new HashMap<>();
        final Map<String, User> usersById = filterDuplicates(externalUsers)
                .filter(externalUser -> !IS_PLANNING_USER.test(externalUser.getId()))
                .map(UserUtil::fromExternalUser)
                .collect(Collectors.toMap(User::getId, Function.identity()));
        usersById.put(PLANNING_USER.getId(), PLANNING_USER);

        taskDataList.forEach(taskData -> {
            Task task = fromTaskData(taskData);
            TaskAssignment taskAssignment = new TaskAssignment(task);
            String state = task.getState();
            taskAssignments.add(taskAssignment);
            if (RESERVED.value().equals(state)) {
                addTaskAssignmentToUser(assignmentsByUserId, taskAssignment, taskData.getActualOwner(), -1, true);
            }
        });

        assignmentsByUserId.forEach((key, assignedTasks) -> {
            User user = usersById.get(key);
            if (user == null) {
                // create the user by convention.
                user = new User(key);
                usersById.put(key, user);
            }
            final List<TaskAssignment> userTasks = assignedTasks.stream().map(IndexedElement::getElement).collect(Collectors.toList());
            addAssignmentsToUser(user, userTasks);
        });

        // Add the DUMMY_TASKs to avoid running into scenarios where the solution remains with no tasks or only one task
        // for selection. (https://issues.redhat.com/browse/PLANNER-1738)
        taskAssignments.add(DUMMY_TASK_ASSIGNMENT);
        taskAssignments.add(DUMMY_TASK_ASSIGNMENT_PLANNER_1738);

        final List<User> users = new ArrayList<>(usersById.values());
        return new TaskAssigningSolution("-1", users, taskAssignments);
    }

    /**
     * Link the list of tasks to the given user. The tasks comes in the expected order.
     * 
     * @param user the user that will "own" the tasks in the chained graph.
     * @param taskAssignments the tasks to link.
     */
    private static void addAssignmentsToUser(User user, List<TaskAssignment> taskAssignments) {
        ChainElement previousElement = user;
        int previousEndTimeInMinutes = 0;
        TaskAssignment nextElement;

        // startTime, endTime, nextTask and user are shadow variables that should be calculated by the solver at
        // start time. However this is not yet implemented see: https://issues.jboss.org/browse/PLANNER-1316 so by now
        // they are initialized as part of the solution restoring.
        for (TaskAssignment taskAssignment : taskAssignments) {
            nextElement = taskAssignment;
            previousElement.setNextElement(nextElement);

            nextElement.setStartTimeInMinutes(previousEndTimeInMinutes);
            nextElement.setEndTimeInMinutes(nextElement.getStartTimeInMinutes() + nextElement.getDurationInMinutes());
            nextElement.setPreviousElement(previousElement);
            nextElement.setUser(user);

            previousEndTimeInMinutes = nextElement.getEndTimeInMinutes();
            previousElement = nextElement;
        }
    }

    private static void addTaskAssignmentToUser(Map<String, List<IndexedElement<TaskAssignment>>> assignmentsByUserId,
            TaskAssignment taskAssignment,
            String actualOwner,
            int index,
            boolean pinned) {
        taskAssignment.setPinned(pinned);
        List<IndexedElement<TaskAssignment>> userAssignments = assignmentsByUserId.computeIfAbsent(actualOwner, key -> new ArrayList<>());
        addInOrder(userAssignments, new IndexedElement<>(taskAssignment, index, taskAssignment.isPinned()));
    }
}