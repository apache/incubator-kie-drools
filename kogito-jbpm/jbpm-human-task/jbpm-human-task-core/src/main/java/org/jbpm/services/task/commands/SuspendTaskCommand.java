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
package org.jbpm.services.task.commands;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskSuspendedEvent;
import org.jbpm.services.task.events.BeforeTaskSuspendedEvent;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 * Operation.Suspend : [ new OperationCommand().{ status = [ Status.Ready ],
 * allowed = [ Allowed.PotentialOwner, Allowed.BusinessAdministrator ],
 * newStatus = Status.Suspended }, new OperationCommand().{ status = [
 * Status.Reserved, Status.InProgress ], allowed = [Allowed.Owner,
 * Allowed.BusinessAdministrator ], newStatus = Status.Suspended } ],
 */
@Transactional
public class SuspendTaskCommand extends TaskCommand<Void> {
	
	public SuspendTaskCommand() {
	}

    public SuspendTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	context.getTaskService().suspend(taskId, userId);
        	return null;
        }
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskSuspendedEvent>() {
        }).fire(task);
        // CHeck for potential Owner allowed (decorator?)
        boolean adminAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getBusinessAdministrators());
        boolean potOwnerAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getPotentialOwners());
        boolean ownerAllowed = (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
        boolean noOp = true;
        if (!adminAllowed && !potOwnerAllowed && !ownerAllowed) {
            String errorMessage = "The user" + user + "is not allowed to Start the task " + task.getId();
            throw new PermissionDeniedException(errorMessage);
        }
        
        if (potOwnerAllowed || adminAllowed ) {
            if (task.getTaskData().getStatus().equals(Status.Ready)) {
            	((InternalTaskData) task.getTaskData()).setStatus(Status.Suspended);
                noOp = false;
            }
        }
        if (ownerAllowed || adminAllowed  ) {
            if (task.getTaskData().getStatus().equals(Status.Reserved)
                    || task.getTaskData().getStatus().equals(Status.InProgress)) {
            	((InternalTaskData) task.getTaskData()).setStatus(Status.Suspended);
                noOp = false;
            }
        }
        
        if(noOp){
            String errorMessage = "User '" + user + "' was unable to execution operation Task Suspend on task id " + task.getId() + " due to a no 'current status' match";
            throw new PermissionDeniedException(errorMessage);
        }

        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskSuspendedEvent>() {
        }).fire(task);

        return null;
    }
}
