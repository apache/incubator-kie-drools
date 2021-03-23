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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AddTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AssignTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.ReleaseTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.RemoveTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.TaskPriorityChangeProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.TaskStateChangeProblemFactChange;
import org.kie.kogito.taskassigning.service.util.IndexedElement;
import org.kie.kogito.taskassigning.user.service.api.UserServiceConnector;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.filterNonDummyAssignments;
import static org.kie.kogito.taskassigning.service.TaskState.READY;
import static org.kie.kogito.taskassigning.service.TaskState.RESERVED;
import static org.kie.kogito.taskassigning.service.util.IndexedElement.addInOrder;
import static org.kie.kogito.taskassigning.service.util.TaskUtil.fromTaskData;
import static org.kie.kogito.taskassigning.service.util.UserUtil.fromExternalUser;

public class SolutionChangesBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolutionChangesBuilder.class);

    private Map<String, User> usersById;
    private List<AddTaskProblemFactChange> newTasksChanges = new ArrayList<>();
    private List<RemoveTaskProblemFactChange> removedTaskChanges = new ArrayList<>();
    private Set<TaskAssignment> removedTasksSet = new HashSet<>();
    private List<ReleaseTaskProblemFactChange> releasedTasksChanges = new ArrayList<>();
    private Map<String, List<IndexedElement<AssignTaskProblemFactChange>>> assignToUserChangesByUserId = new HashMap<>();
    private List<ProblemFactChange<TaskAssigningSolution>> propertyChanges = new ArrayList<>();

    private TaskAssigningServiceContext context;
    private UserServiceConnector userServiceConnector;
    private TaskAssigningSolution solution;
    private List<TaskData> taskDataList;

    private SolutionChangesBuilder() {
    }

    public static SolutionChangesBuilder create() {
        return new SolutionChangesBuilder();
    }

    public SolutionChangesBuilder withContext(TaskAssigningServiceContext context) {
        this.context = context;
        return this;
    }

    public SolutionChangesBuilder withUserServiceConnector(UserServiceConnector userServiceConnector) {
        this.userServiceConnector = userServiceConnector;
        return this;
    }

    public SolutionChangesBuilder forSolution(TaskAssigningSolution solution) {
        this.solution = solution;
        return this;
    }

    public SolutionChangesBuilder fromTasksData(List<TaskData> taskDataList) {
        this.taskDataList = taskDataList;
        return this;
    }

    public List<ProblemFactChange<TaskAssigningSolution>> build() {
        usersById = solution.getUserList()
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Map<String, TaskAssignment> taskAssignmentById = filterNonDummyAssignments(solution.getTaskAssignmentList())
                .stream()
                .collect(Collectors.toMap(TaskAssignment::getId, Function.identity()));

        TaskAssignment taskAssignment;
        for (TaskData taskData : taskDataList) {
            taskAssignment = taskAssignmentById.remove(taskData.getId());
            if (taskAssignment == null) {
                addNewTaskChanges(taskData);
            } else {
                addTaskChanges(taskAssignment, taskData);
            }
        }

        for (TaskAssignment removedTask : removedTasksSet) {
            removedTaskChanges.add(new RemoveTaskProblemFactChange(removedTask));
        }

        List<ProblemFactChange<TaskAssigningSolution>> totalChanges = new ArrayList<>();
        totalChanges.addAll(removedTaskChanges);
        totalChanges.addAll(releasedTasksChanges);

        for (List<IndexedElement<AssignTaskProblemFactChange>> assignTaskToUserChanges : assignToUserChangesByUserId.values()) {
            totalChanges.addAll(assignTaskToUserChanges.stream().map(IndexedElement::getElement).collect(Collectors.toList()));
        }

        totalChanges.addAll(propertyChanges);
        totalChanges.addAll(newTasksChanges);

        if (!totalChanges.isEmpty()) {
            totalChanges.add(0, scoreDirector -> context.setCurrentChangeSetId(context.nextChangeSetId()));
        }
        return totalChanges;
    }

    private void addNewTaskChanges(TaskData taskData) {
        Task newTask;
        if (READY.value().equals(taskData.getState())) {
            newTask = fromTaskData(taskData);
            newTasksChanges.add(new AddTaskProblemFactChange(new TaskAssignment(newTask)));
            context.setTaskPublished(taskData.getId(), false);
        } else if (RESERVED.value().equals(taskData.getState())) {
            newTask = fromTaskData(taskData);
            User user = getUser(usersById.get(taskData.getActualOwner()), taskData.getActualOwner());
            AssignTaskProblemFactChange change = new AssignTaskProblemFactChange(new TaskAssignment(newTask), user, true);
            context.setTaskPublished(taskData.getId(), true);
            addChangeToUser(assignToUserChangesByUserId, change, user, -1, true);
        }
    }

    private static void addChangeToUser(Map<String, List<IndexedElement<AssignTaskProblemFactChange>>> changesByUserId,
            AssignTaskProblemFactChange change,
            User user,
            int index,
            boolean pinned) {
        final List<IndexedElement<AssignTaskProblemFactChange>> userChanges = changesByUserId.computeIfAbsent(user.getId(), key -> new ArrayList<>());
        addInOrder(userChanges, new IndexedElement<>(change, index, pinned));
    }

    private void addTaskChanges(TaskAssignment taskAssignment,
            TaskData taskData) {
        String taskState = taskData.getState();
        if (READY.value().equals(taskState)) {
            context.setTaskPublished(taskData.getId(), false);
            releasedTasksChanges.add(new ReleaseTaskProblemFactChange(taskAssignment));
        } else if (RESERVED.value().equals(taskState)) {
            context.setTaskPublished(taskData.getId(), true);
            if (!taskData.getActualOwner().equals(taskAssignment.getUser().getId()) || !taskAssignment.isPinned()) {
                final User user = getUser(usersById.get(taskData.getActualOwner()), taskData.getActualOwner());
                AssignTaskProblemFactChange change = new AssignTaskProblemFactChange(taskAssignment, user, true);
                addChangeToUser(assignToUserChangesByUserId, change, user, -1, true);
            }
        } else if (TaskState.isTerminal(taskState)) {
            removedTasksSet.add(taskAssignment);
        }

        //TODO, discuss other traceable change types.
        if (!removedTasksSet.contains(taskAssignment)) {
            if (!Objects.equals(taskAssignment.getTask().getPriority(), taskData.getPriority())) {
                propertyChanges.add(new TaskPriorityChangeProblemFactChange(taskAssignment, taskData.getPriority()));
            }
            if (!Objects.equals(taskAssignment.getTask().getState(), taskData.getState())) {
                propertyChanges.add(new TaskStateChangeProblemFactChange(taskAssignment, taskData.getState()));
            }
        }
    }

    private User getUser(User existingUser, String userId) {
        if (existingUser != null) {
            return existingUser;
        } else {
            LOGGER.debug("User {} was not found in current solution, it'll we looked up in the external user system .", userId);
            User user;
            org.kie.kogito.taskassigning.user.service.api.User externalUser = null;
            try {
                externalUser = userServiceConnector.findUser(userId);
            } catch (Exception e) {
                LOGGER.warn("An error was produced while querying user {} from the external user system.", userId);
            }
            if (externalUser != null) {
                user = fromExternalUser(externalUser);
            } else {
                // We add it by convention, since the kogito runtime supports the assignment of tasks to whatever user id.
                LOGGER.warn("User {} was not found in the external user system, it looks like it's a manual" +
                        " assignment from the kogito tasks administration to a non existing user or an error was produced when" +
                        " querying the external user system (in this last case the user will be updated on next synchronization)." +
                        " It'll be added to the solution to respect the assignment.", userId);
                user = new User(userId);
            }
            return user;
        }
    }
}
