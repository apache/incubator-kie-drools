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
import org.jbpm.task.events.AfterTaskStoppedEvent;
import org.jbpm.task.events.BeforeTaskStoppedEvent;
import org.jbpm.task.exception.PermissionDeniedException;

/**
 *  Operation.Stop 
        : [ new OperationCommand().{ 
                status = [ Status.InProgress ],
                allowed = [ Allowed.Owner, Allowed.BusinessAdministrator ],               
                newStatus = Status.Reserved
            } ],                 
 */

@Transactional
public class StopTaskCommand extends TaskCommand {

    public StopTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskStoppedEvent>() {}).fire(task);
        boolean ownerAllowed = (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
        boolean adminAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getBusinessAdministrators());
        if (!ownerAllowed && !adminAllowed) {
            String errorMessage = "The user" + user + "is not allowed to Start the task "+task.getId();
            throw new PermissionDeniedException(errorMessage);
        }
        if (task.getTaskData().getStatus().equals(Status.InProgress)) {
            task.getTaskData().setStatus(Status.Reserved); 
        }
        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskStoppedEvent>() {}).fire(task);

        return null;
    }

    
}
