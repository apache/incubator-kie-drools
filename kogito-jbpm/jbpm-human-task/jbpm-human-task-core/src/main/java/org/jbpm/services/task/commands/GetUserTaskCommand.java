/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;

@XmlRootElement(name = "get-user-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetUserTaskCommand extends TaskCommand<Task> {

    private static final long serialVersionUID = -3066272693452263188L;

    public GetUserTaskCommand() {
    }

    public GetUserTaskCommand(String userId,
                              long taskId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    @Override
    public Task execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);

        if (task == null) {
            throw new IllegalStateException("Unable to find task with id " + taskId);
        }

        if (!checkUserPermissions(userId,
                                  task,
                                  context)) {
            throw new PermissionDeniedException("User " + userId + " isn't allowed to see the task " + taskId);
        }

        return task;
    }

    protected boolean checkUserPermissions(String userId,
                                           Task task,
                                           TaskContext context) {
        List<String> usersGroup = context.getUserGroupCallback().getGroupsForUser(userId);
        usersGroup.add(userId);

        if (checkUserPermissions(usersGroup,
                                 task.getPeopleAssignments().getPotentialOwners())) {
            return true;
        }

        return checkUserPermissions(usersGroup,
                                    task.getPeopleAssignments().getBusinessAdministrators());
    }

    protected boolean checkUserPermissions(List<String> userGroups,
                                           List<OrganizationalEntity> organizationalEntities) {
        for (OrganizationalEntity oe : organizationalEntities) {
            if (userGroups.contains(oe.getId())) {
                return true;
            }
        }
        return false;
    }
}
