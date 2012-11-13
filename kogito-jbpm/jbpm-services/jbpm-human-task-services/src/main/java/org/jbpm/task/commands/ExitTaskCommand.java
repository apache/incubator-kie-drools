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
import org.jbpm.task.events.AfterTaskExitedEvent;
import org.jbpm.task.events.BeforeTaskExitedEvent;
import org.jbpm.task.exception.PermissionDeniedException;

/**
 * Operation.Exit
        : [ new OperationCommand().{
                status = [ Status.Created, Status.Ready, Status.Reserved, Status.InProgress, Status.Suspended ],
                allowed = [ Allowed.BusinessAdministrator ],
                newStatus = Status.Exited
            } ]
 */
@Transactional
public class ExitTaskCommand<Void> extends TaskCommand {


    public ExitTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskExitedEvent>() {
        }).fire(task);
        boolean adminAllowed = CommandsUtil.isAllowed(user, getGroupsIds(), task.getPeopleAssignments().getBusinessAdministrators());
      
        if (!adminAllowed ) {
            String errorMessage = "The user" + user + "is not allowed to Start the task " + task.getId();
            throw new PermissionDeniedException(errorMessage);
        }

        
        
            if (task.getTaskData().getStatus().equals(Status.Created) ||
                    task.getTaskData().getStatus().equals(Status.Ready) ||
                    task.getTaskData().getStatus().equals(Status.Reserved) ||
                    task.getTaskData().getStatus().equals(Status.InProgress) ||
                    task.getTaskData().getStatus().equals(Status.Suspended)) {
                
                task.getTaskData().setStatus(Status.Exited);

            }
       

        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskExitedEvent>() {
        }).fire(task);

        return null;
    }
}
