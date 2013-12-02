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

import java.util.List;

import javax.enterprise.util.AnnotationLiteral;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskResumedEvent;
import org.jbpm.services.task.events.BeforeTaskResumedEvent;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.OrganizationalEntityImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 * Operation.Resume : [ new OperationCommand().{ previousStatus = [ Status.Ready
 * ], allowed = [ Allowed.PotentialOwner, Allowed.BusinessAdministrator ],
 * setToPreviousStatus = true }, new OperationCommand().{ previousStatus = [
 * Status.Reserved, Status.InProgress ], allowed = [ Allowed.Owner,
 * Allowed.BusinessAdministrator ], setToPreviousStatus = true } ],
 */
@Transactional
@XmlRootElement(name="resume-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ResumeTaskCommand extends TaskCommand<Void> {
	
	public ResumeTaskCommand() {
	}

    public ResumeTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	context.getTaskService().resume(taskId, userId);
        	return null;
        }
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskResumedEvent>() {
        }).fire(task);
        // CHeck for potential Owner allowed (decorator?)
        boolean adminAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getBusinessAdministrators());
        boolean potOwnerAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getPotentialOwners());
        boolean ownerAllowed = (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
        if (!adminAllowed && !potOwnerAllowed && !ownerAllowed) {
            String errorMessage = "The user" + user + "is not allowed to Start the task " + task.getId();
            throw new PermissionDeniedException(errorMessage);
        }

        if (potOwnerAllowed || adminAllowed) {
            if (task.getTaskData().getPreviousStatus().equals(Status.Ready)) {

            	((InternalTaskData) task.getTaskData()).setStatus(task.getTaskData().getPreviousStatus());
                
            }
        }
        
         if (ownerAllowed || adminAllowed) {
            if (task.getTaskData().getPreviousStatus().equals(Status.Reserved) || 
                    task.getTaskData().getPreviousStatus().equals(Status.InProgress)) {

            	((InternalTaskData) task.getTaskData()).setStatus(task.getTaskData().getPreviousStatus());
                
            }
        }

        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskResumedEvent>() {
        }).fire(task);

        return null;
    }

    private boolean isAllowed(final UserImpl user, final List<String> groupIds, final List<OrganizationalEntityImpl> entities) {
        // for now just do a contains, I'll figure out group membership later.
        for (OrganizationalEntityImpl entity : entities) {
            if (entity instanceof UserImpl && entity.equals(user)) {
                return true;
            }
            if (entity instanceof GroupImpl && groupIds != null && groupIds.contains(entity.getId())) {
                return true;
            }
        }
        return false;
    }
}
