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
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.events.AfterTaskForwardedEvent;
import org.jbpm.task.events.BeforeTaskForwardedEvent;
import org.jbpm.task.exception.PermissionDeniedException;

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
public class ForwardTaskCommand<Void> extends TaskCommand {

    public ForwardTaskCommand(long taskId, String userId, String targetEntityId) {
        this.taskId = taskId;
        this.userId = userId;
        this.targetEntityId = targetEntityId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
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
                task.getTaskData().setStatus(Status.Ready);
                if ( !task.getPeopleAssignments().getPotentialOwners().contains(targetEntity)) {
                    task.getPeopleAssignments().getPotentialOwners().add(targetEntity);
                }
                task.getTaskData().setActualOwner(null);
                task.getPeopleAssignments().getPotentialOwners().remove(user);
                noOp = false;
            }
        }
        
       
        
        if (ownerAllowed || adminAllowed  ) {
            if (task.getTaskData().getStatus().equals(Status.Reserved)
                    || task.getTaskData().getStatus().equals(Status.InProgress)) {
                task.getTaskData().setStatus(Status.Ready);
                if ( !task.getPeopleAssignments().getPotentialOwners().contains(targetEntity)) {
                    task.getPeopleAssignments().getPotentialOwners().add(targetEntity);
                }
                task.getTaskData().setActualOwner(null);
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
