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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskSkippedEvent;
import org.jbpm.services.task.events.BeforeTaskSkippedEvent;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 * Operation.Skip : [ new OperationCommand().{ status = [ Status.Created ],
 * allowed = [ Allowed.Initiator, Allowed.BusinessAdministrator ], newStatus =
 * Status.Obsolete, skipable = true }, new OperationCommand().{ status = [
 * Status.Ready ], allowed = [ Allowed.PotentialOwner,
 * Allowed.BusinessAdministrator ], newStatus = Status.Obsolete, skipable = true
 * }, new OperationCommand().{ status = [ Status.Reserved, Status.InProgress ],
 * allowed = [ Allowed.Owner, Allowed.BusinessAdministrator ], newStatus =
 * Status.Obsolete, skipable = true } ],
 */
@Transactional
@XmlRootElement(name="skip-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class SkipTaskCommand extends TaskCommand<Void> {
	
	public SkipTaskCommand() {
	}

    public SkipTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	context.getTaskService().skip(taskId, userId);
        	return null;
        }
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskSkippedEvent>() {
        }).fire(task);
        boolean adminAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getBusinessAdministrators());
        boolean initiatorAllowed = (task.getTaskData().getCreatedBy() != null
                && (task.getTaskData().getCreatedBy().equals(user)));
        boolean potOwnerAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getPotentialOwners());
        boolean ownerAllowed = (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
        if (!adminAllowed && !potOwnerAllowed && !initiatorAllowed && !ownerAllowed) {
            String errorMessage = "The user" + user + "is not allowed to Start the task " + task.getId();
            throw new PermissionDeniedException(errorMessage);
        }

        
        if (adminAllowed || initiatorAllowed) {
            if (task.getTaskData().getStatus().equals(Status.Created)) {
                // CHeck for potential Owner allowed (decorator?)
            	((InternalTaskData) task.getTaskData()).setStatus(Status.Obsolete);

            }
        }
        
        if (adminAllowed || potOwnerAllowed) {
            if (task.getTaskData().getStatus().equals(Status.Ready)) {
            	((InternalTaskData) task.getTaskData()).setStatus(Status.Obsolete);
            }
        }

        
        if (adminAllowed || ownerAllowed) {
            if (task.getTaskData().getStatus().equals(Status.Reserved)
                    || task.getTaskData().getStatus().equals(Status.InProgress)) {
            	((InternalTaskData) task.getTaskData()).setStatus(Status.Obsolete);
            }
        }
        
        ((InternalTaskData) task.getTaskData()).setSkipable(true);

        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskSkippedEvent>() {
        }).fire(task);

        return null;
    }
}
