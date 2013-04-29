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

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskReleasedEvent;
import org.jbpm.services.task.events.BeforeTaskReleasedEvent;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 * Operation.Release 
        : [ new OperationCommand().{ 
                status = [ Status.Reserved, Status.InProgress ],
                allowed = [Allowed.Owner, Allowed.BusinessAdministrator ],  
                setNewOwnerToNull = true,            
                newStatus = Status.Ready
            } ],    
 */

@Transactional
public class ReleaseTaskCommand extends TaskCommand<Void> {


    public ReleaseTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskReleasedEvent>() {}).fire(task);
        // CHeck for potential Owner allowed (decorator?)
        boolean ownerAllowed = (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
        
        boolean adminAllowed = isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getBusinessAdministrators());
        
        
        if (!ownerAllowed && !adminAllowed) {
            String errorMessage = "The user" + user + "is not allowed to Start the task "+task.getId();
            throw new PermissionDeniedException(errorMessage);
        }
        
        if (task.getTaskData().getStatus().equals(Status.Reserved) || 
                        task.getTaskData().getStatus().equals(Status.InProgress)) {
            
        	((InternalTaskData) task.getTaskData()).setStatus(Status.Ready);
        	((InternalTaskData) task.getTaskData()).setActualOwner(null);
        }
 
        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskReleasedEvent>() {}).fire(task);

        return null;
    }

    private boolean isAllowed(final User user, final List<String> groupIds, final List<OrganizationalEntity> entities) {
        // for now just do a contains, I'll figure out group membership later.
        for (OrganizationalEntity entity : entities) {
            if (entity instanceof User && entity.equals(user)) {
                return true;
            }
            if (entity instanceof Group && groupIds != null && groupIds.contains(entity.getId())) {
                return true;
            }
        }
        return false;
    }
}
