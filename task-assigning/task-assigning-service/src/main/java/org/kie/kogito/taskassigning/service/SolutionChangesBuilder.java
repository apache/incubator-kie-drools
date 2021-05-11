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
import org.kie.kogito.taskassigning.core.model.solver.realtime.AddUserProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AssignTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.DisableUserProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.ReleaseTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.RemoveTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.RemoveUserProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.TaskInfoChangeProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.UserPropertyChangeProblemFactChange;
import org.kie.kogito.taskassigning.service.event.UserDataEvent;
import org.kie.kogito.taskassigning.service.processing.AttributesProcessorRegistry;
import org.kie.kogito.taskassigning.service.util.IndexedElement;
import org.kie.kogito.taskassigning.service.util.TraceUtil;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.taskassigning.core.model.ModelConstants.IS_PLANNING_USER;
import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.filterNonDummyAssignments;
import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.hasPinnedTasks;
import static org.kie.kogito.taskassigning.service.TaskState.READY;
import static org.kie.kogito.taskassigning.service.TaskState.RESERVED;
import static org.kie.kogito.taskassigning.service.util.IndexedElement.addInOrder;
import static org.kie.kogito.taskassigning.service.util.TaskUtil.fromTaskData;
import static org.kie.kogito.taskassigning.service.util.UserUtil.fromExternalUser;

public class SolutionChangesBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolutionChangesBuilder.class);

    private Map<String, User> usersById;
    private final List<AddTaskProblemFactChange> newTasksChanges = new ArrayList<>();
    private final List<RemoveTaskProblemFactChange> removedTaskChanges = new ArrayList<>();
    private final Set<TaskAssignment> removedTasksSet = new HashSet<>();
    private final List<ReleaseTaskProblemFactChange> releasedTasksChanges = new ArrayList<>();
    private final Map<String, List<IndexedElement<AssignTaskProblemFactChange>>> assignToUserChangesByUserId = new HashMap<>();
    private final List<TaskInfoChangeProblemFactChange> taskPropertyChanges = new ArrayList<>();
    private final List<AddUserProblemFactChange> newUserChanges = new ArrayList<>();
    private final List<ProblemFactChange<TaskAssigningSolution>> updateUserChanges = new ArrayList<>();
    private final List<RemoveUserProblemFactChange> removableUserChanges = new ArrayList<>();
    private final List<ProblemFactChange<TaskAssigningSolution>> totalChanges = new ArrayList<>();

    private TaskAssigningServiceContext context;
    private UserServiceConnectorDelegate userServiceConnector;
    private TaskAssigningSolution solution;
    private List<TaskData> taskDataList;
    private UserDataEvent userDataEvent;
    private AttributesProcessorRegistry processorRegistry;

    private SolutionChangesBuilder() {
    }

    public static SolutionChangesBuilder create() {
        return new SolutionChangesBuilder();
    }

    public SolutionChangesBuilder withContext(TaskAssigningServiceContext context) {
        this.context = context;
        return this;
    }

    public SolutionChangesBuilder withUserServiceConnector(UserServiceConnectorDelegate userServiceConnector) {
        this.userServiceConnector = userServiceConnector;
        return this;
    }

    public SolutionChangesBuilder withProcessors(AttributesProcessorRegistry processorRegistry) {
        this.processorRegistry = processorRegistry;
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

    public SolutionChangesBuilder fromUserDataEvent(UserDataEvent userDataEvent) {
        this.userDataEvent = userDataEvent;
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

        if (userDataEvent != null) {
            addFullSyncUserChanges(userDataEvent.getData());
        } else {
            addRemovableUserChanges();
        }

        totalChanges.addAll(newUserChanges);
        totalChanges.addAll(removedTaskChanges);
        totalChanges.addAll(releasedTasksChanges);

        for (List<IndexedElement<AssignTaskProblemFactChange>> assignTaskToUserChanges : assignToUserChangesByUserId.values()) {
            List<AssignTaskProblemFactChange> assignTaskChanges = assignTaskToUserChanges.stream()
                    .map(IndexedElement::getElement)
                    .collect(Collectors.toList());
            totalChanges.addAll(assignTaskChanges);
        }

        totalChanges.addAll(taskPropertyChanges);
        totalChanges.addAll(updateUserChanges);
        totalChanges.addAll(newTasksChanges);
        totalChanges.addAll(removableUserChanges);

        traceChanges();

        if (!totalChanges.isEmpty()) {
            totalChanges.add(0, scoreDirector -> context.setCurrentChangeSetId(context.nextChangeSetId()));
        }
        return totalChanges;
    }

    private void addNewTaskChanges(TaskData taskData) {
        Task newTask;
        if (READY.value().equals(taskData.getState())) {
            newTask = fromTaskData(taskData);
            processorRegistry.applyAttributesProcessor(newTask, newTask.getAttributes());
            newTasksChanges.add(new AddTaskProblemFactChange(new TaskAssignment(newTask)));
            context.setTaskPublished(taskData.getId(), false);
        } else if (RESERVED.value().equals(taskData.getState())) {
            newTask = fromTaskData(taskData);
            processorRegistry.applyAttributesProcessor(newTask, newTask.getAttributes());
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

        if (!removedTasksSet.contains(taskAssignment)) {
            Task updatedTask = fromTaskData(taskData);
            if (!equalsByTaskInfoProperties(taskAssignment.getTask(), updatedTask)) {
                processorRegistry.applyAttributesProcessor(updatedTask, updatedTask.getAttributes());
            } else {
                updatedTask.setAttributes(taskAssignment.getTask().getAttributes());
            }
            taskPropertyChanges.add(new TaskInfoChangeProblemFactChange(taskAssignment, updatedTask));
        }
    }

    private User getUser(User existingUser, String userId) {
        if (existingUser != null) {
            return existingUser;
        } else {
            LOGGER.debug("User {} was not found in current solution, it'll we looked up in the external user system .", userId);
            User user;
            org.kie.kogito.taskassigning.user.service.User externalUser;
            try {
                externalUser = userServiceConnector.findUser(userId);
            } catch (Exception e) {
                throw new TaskAssigningException("An error was produced while querying user: " + userId + " in the external user system.", e);
            }
            if (externalUser != null) {
                user = fromExternalUser(externalUser, processorRegistry);
            } else {
                // We add it by convention, since the kogito runtime supports the assignment of tasks to whatever user id.
                LOGGER.warn("User {} was not found in the external user system, it looks like it's a manual" +
                        " assignment from the kogito tasks administration to a non existing user. It'll be added to the" +
                        " solution to respect the assignment.", userId);
                user = new User(userId);
            }
            return user;
        }
    }

    private void addFullSyncUserChanges(List<org.kie.kogito.taskassigning.user.service.User> externalUserList) {
        final Set<String> updatedUserIds = new HashSet<>();
        externalUserList.stream()
                .filter(externalUser -> !IS_PLANNING_USER.test(externalUser.getId()))
                .map(externalUser -> fromExternalUser(externalUser, processorRegistry))
                .forEach(synchedUser -> {
                    final User previousUser = usersById.get(synchedUser.getId());
                    updatedUserIds.add(synchedUser.getId());
                    if (previousUser == null) {
                        //add brand new users
                        newUserChanges.add(new AddUserProblemFactChange(synchedUser));
                    } else if (!equalsByProperties(previousUser, synchedUser)) {
                        //update the users that has changes.
                        updateUserChanges.add(new UserPropertyChangeProblemFactChange(previousUser,
                                true,
                                synchedUser.getAttributes(),
                                synchedUser.getGroups()));
                    }
                });

        //current users not present in the synchronization data set are marked for disabling.
        usersById.values().stream()
                .filter(previousUser -> !IS_PLANNING_USER.test(previousUser.getId()))
                .filter(previousUser -> !updatedUserIds.contains(previousUser.getId()))
                .filter(User::isEnabled)
                .forEach(previousUser -> updateUserChanges.add(new DisableUserProblemFactChange(previousUser)));
    }

    private void addRemovableUserChanges() {
        //disabled users with non pinned tasks and no programmed assignments are marked for deletion.
        solution.getUserList().stream()
                .filter(user -> !IS_PLANNING_USER.test(user.getId()))
                .filter(user -> !user.isEnabled())
                .filter(user -> !assignToUserChangesByUserId.containsKey(user.getId()))
                .filter(user -> !hasPinnedTasks(user))
                .forEach(user -> removableUserChanges.add(new RemoveUserProblemFactChange(user)));
    }

    private static boolean equalsByProperties(User a, User b) {
        return Objects.equals(a.isEnabled(), b.isEnabled()) &&
                Objects.equals(a.getGroups(), b.getGroups()) &&
                Objects.equals(a.getAttributes(), b.getAttributes());
    }

    private static boolean equalsByTaskInfoProperties(Task a, Task b) {
        return Objects.equals(a.getDescription(), b.getDescription()) &&
                Objects.equals(a.getPriority(), b.getPriority()) &&
                Objects.equals(a.getPotentialUsers(), b.getPotentialUsers()) &&
                Objects.equals(a.getPotentialGroups(), b.getPotentialGroups()) &&
                Objects.equals(a.getExcludedUsers(), b.getExcludedUsers()) &&
                Objects.equals(a.getAdminUsers(), b.getAdminUsers()) &&
                Objects.equals(a.getAdminGroups(), b.getAdminGroups()) &&
                Objects.equals(a.getInputData(), b.getInputData());
    }

    private void traceChanges() {
        if (LOGGER.isTraceEnabled()) {
            if (!totalChanges.isEmpty()) {
                TraceUtil.traceProgrammedChanges(LOGGER, removedTaskChanges, releasedTasksChanges, assignToUserChangesByUserId,
                        taskPropertyChanges, newTasksChanges, newUserChanges, updateUserChanges, removableUserChanges);
            } else {
                LOGGER.trace("No changes has been calculated.");
            }
        }
    }
}
