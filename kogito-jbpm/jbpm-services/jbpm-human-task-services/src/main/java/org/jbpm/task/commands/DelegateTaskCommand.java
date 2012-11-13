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
import org.jbpm.task.events.AfterTaskDelegatedEvent;
import org.jbpm.task.events.BeforeTaskDelegatedEvent;
import org.jbpm.task.exception.PermissionDeniedException;

/**
*Operation.Delegate 
        : [ new OperationCommand().{ 
                status = [ Status.Ready ],
                allowed = [ Allowed.PotentialOwner, Allowed.BusinessAdministrator  ],
                addTargetUserToPotentialOwners = true,            
                newStatus = Status.Ready,
                exec = Operation.Claim
            },
            new OperationCommand().{ 
                status = [ Status.Reserved, Status.InProgress ],
                allowed = [ Allowed.Owner, Allowed.BusinessAdministrator ],
                addTargetUserToPotentialOwners = true,                         
                newStatus = Status.Ready,
                exec = Operation.Claim
            } ],
 */
@Transactional
public class DelegateTaskCommand extends TaskCommand {

   

    public DelegateTaskCommand(long taskId, String userId, String targetEntityId) {
        this.taskId = taskId;
        this.userId = userId;
        this.targetEntityId = targetEntityId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        OrganizationalEntity targetEntity = context.getTaskIdentityService()
                                                    .getOrganizationalEntityById(targetEntityId);
        
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskDelegatedEvent>() {
        }).fire(task);
        // CHeck for potential Owner allowed (decorator?)
        boolean adminAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getBusinessAdministrators());
        boolean potOwnerAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getPotentialOwners());
        boolean ownerAllowed = (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
        if (!adminAllowed && !potOwnerAllowed && !ownerAllowed) {
            String errorMessage = "The user" + user + "is not allowed to Start the task " + task.getId();
            throw new PermissionDeniedException(errorMessage);
        }
        if (potOwnerAllowed || adminAllowed ) {
            if (task.getTaskData().getStatus().equals(Status.Ready)) {

                task.getTaskData().setStatus(Status.Ready);
                if ( !task.getPeopleAssignments().getPotentialOwners().contains(targetEntity)) {
                    task.getPeopleAssignments().getPotentialOwners().add(targetEntity);
                }

            }
        }
        if (ownerAllowed || adminAllowed) {
            if (task.getTaskData().getStatus().equals(Status.Reserved)
                    || task.getTaskData().getStatus().equals(Status.InProgress)) {
                task.getTaskData().setStatus(Status.Ready);
                if ( !task.getPeopleAssignments().getPotentialOwners().contains(targetEntity)) {
                    task.getPeopleAssignments().getPotentialOwners().add(targetEntity);
                }
            }
        }

        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskDelegatedEvent>() {
        }).fire(task);

        return null;
    }
}
