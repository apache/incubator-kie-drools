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
import org.jbpm.services.task.events.AfterTaskForwardedEvent;
import org.jbpm.services.task.events.BeforeTaskForwardedEvent;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 Operation.Forward 
        : [ new OperationCommand().{ 
                status = [ Status.Ready ],
                allowed = [ Allowed.PotentialOwner, Allowed.BusinessAdministrator  ],
                userIsExplicitPotentialOwner = true,                
                addTargetUserToPotentialOwners = true,     
                removeUserFromPotentialOwners = true,   
                setNewOwnerToNull = true,         
                newStatus = Status.Ready
            },
            new OperationCommand().{ 
                status = [ Status.Reserved, Status.InProgress ],
                allowed = [ Allowed.Owner, Allowed.BusinessAdministrator ],
                userIsExplicitPotentialOwner = true,
                addTargetUserToPotentialOwners = true,     
                removeUserFromPotentialOwners = true, 
                setNewOwnerToNull = true,                             
                newStatus = Status.Ready
            }],          
 */
@Transactional
public class ForwardTaskCommand extends TaskCommand<Void> {
	
	public ForwardTaskCommand() {
	}

    public ForwardTaskCommand(long taskId, String userId, String targetEntityId) {
        this.taskId = taskId;
        this.userId = userId;
        this.targetEntityId = targetEntityId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	context.getTaskService().forward(taskId, userId, targetEntityId);
        	return null;
        }
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        OrganizationalEntity targetEntity = context.getTaskIdentityService().getOrganizationalEntityById(targetEntityId);
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskForwardedEvent>() {
        }).fire(task);
        boolean adminAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getBusinessAdministrators());
        boolean potOwnerAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getPotentialOwners());
        boolean ownerAllowed = (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
        
        
        if ((!adminAllowed && !potOwnerAllowed && !ownerAllowed)) {
            String errorMessage = "The user" + user + "is not allowed to Start the task " + task.getId();
            throw new PermissionDeniedException(errorMessage);
        }

        boolean noOp = true;
        if (potOwnerAllowed || adminAllowed ) {
            if (task.getTaskData().getStatus().equals(Status.Ready)) {
            	((InternalTaskData) task.getTaskData()).setStatus(Status.Ready);
                if ( !task.getPeopleAssignments().getPotentialOwners().contains(targetEntity)) {
                    task.getPeopleAssignments().getPotentialOwners().add(targetEntity);
                }
                ((InternalTaskData) task.getTaskData()).setActualOwner(null);
                task.getPeopleAssignments().getPotentialOwners().remove(user);
                noOp = false;
            }
        }
        
       
        
        if (ownerAllowed || adminAllowed  ) {
            if (task.getTaskData().getStatus().equals(Status.Reserved)
                    || task.getTaskData().getStatus().equals(Status.InProgress)) {
            	((InternalTaskData) task.getTaskData()).setStatus(Status.Ready);
                if ( !task.getPeopleAssignments().getPotentialOwners().contains(targetEntity)) {
                    task.getPeopleAssignments().getPotentialOwners().add(targetEntity);
                }
                ((InternalTaskData) task.getTaskData()).setActualOwner(null);
                task.getPeopleAssignments().getPotentialOwners().remove(user);
                noOp = false;
            }
        }
        if(noOp){
            String errorMessage = "The action Forward Task on " + task.getId() +" was not applied by "+userId;
            throw new PermissionDeniedException(errorMessage);
        }
        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskForwardedEvent>() {
        }).fire(task);

        return null;
    }
}
