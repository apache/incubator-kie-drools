/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.commands;

import javax.enterprise.util.AnnotationLiteral;
import org.kie.command.Context;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.events.AfterTaskActivatedEvent;
import org.jbpm.task.events.BeforeTaskActivatedEvent;
import org.jbpm.task.exception.PermissionDeniedException;

/**
 * Operation.Activate : [ new OperationCommand().{ status = [ Status.Created ],
 * allowed = [ Allowed.Owner, Allowed.BusinessAdministrator ], newStatus =
 * Status.Ready } ],
 */
@Transactional
public class ActivateTaskCommand<Void> extends TaskCommand {


    public ActivateTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskActivatedEvent>() {
        }).fire(task);
        boolean adminAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getBusinessAdministrators());
        boolean ownerAllowed = (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
        if (!adminAllowed && !ownerAllowed) {
            String errorMessage = "The user" + user + "is not allowed to Start the task " + task.getId();
            throw new PermissionDeniedException(errorMessage);
        }

        if (task.getTaskData().getStatus().equals(Status.Created)) {
            task.getTaskData().setStatus(Status.Ready);
        }

        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskActivatedEvent>() {
        }).fire(task);

        return null;
    }
}
