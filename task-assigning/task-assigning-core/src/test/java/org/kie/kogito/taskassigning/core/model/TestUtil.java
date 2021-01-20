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

package org.kie.kogito.taskassigning.core.model;

import java.util.List;
import java.util.Set;

public class TestUtil {

    private TestUtil() {
    }

    public static TaskAssignment mockTaskAssignment(String taskId, boolean pinned) {
        TaskAssignment taskAssignment = new TaskAssignment(Task.newBuilder()
                                                                   .id(taskId)
                                                                   .name("Task_" + taskId)
                                                                   .priority("0").build());
        taskAssignment.setPinned(pinned);
        return taskAssignment;
    }

    public static User mockUser(String id, List<TaskAssignment> taskAssignments) {
        User user = new User(id);
        ChainElement previous = user;
        for (TaskAssignment taskAssignment : taskAssignments) {
            taskAssignment.setPreviousElement(previous);
            taskAssignment.setUser(user);
            previous.setNextElement(taskAssignment);
            previous = taskAssignment;
        }
        return user;
    }

    public static Task mockTask(List<OrganizationalEntity> potentialOwners, Set<Object> skills) {
        Task task = Task.newBuilder().build();
        potentialOwners.forEach(potentialUser -> {
            if (potentialUser.isUser()) {
                task.getPotentialUsers().add(potentialUser.getId());
            } else {
                task.getPotentialGroups().add(potentialUser.getId());
            }
        });
        task.getAttributes().put(DefaultLabels.SKILLS.name(), skills);
        return task;
    }

    public static User mockUser(String userId, boolean enabled, List<Group> groups, Set<Object> skills) {
        User user = new User(userId, enabled);
        user.getGroups().addAll(groups);
        user.getAttributes().put(DefaultLabels.SKILLS.name(), skills);
        return user;
    }

    public static Group mockGroup(String groupId) {
        return new Group(groupId);
    }
}
