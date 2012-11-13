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

import java.util.Map;
import javax.enterprise.util.AnnotationLiteral;
import org.kie.command.Context;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Content;
import org.jbpm.task.FaultData;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.events.AfterTaskFailedEvent;
import org.jbpm.task.events.BeforeTaskFailedEvent;
import org.jbpm.task.exception.PermissionDeniedException;
import org.jbpm.task.utils.ContentMarshallerHelper;

/**
 * Operation.Fail : [ new OperationCommand().{ status = [ Status.InProgress ],
 * allowed = [ Allowed.Owner ], newStatus = Status.Failed } ],
 */
@Transactional
public class FailTaskCommand<Void> extends TaskCommand {

    private Map<String, Object> data;

    public FailTaskCommand(long taskId, String userId, Map<String, Object> data) {
        this.taskId = taskId;
        this.userId = userId;
        this.data = data;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        User user = context.getTaskIdentityService().getUserById(userId);
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskFailedEvent>() {
        }).fire(task);

        boolean ownerAllowed = (task.getTaskData().getActualOwner() != null && task.getTaskData().getActualOwner().equals(user));
        if (!ownerAllowed) {
            String errorMessage = "The user" + user + "is not allowed to Start the task " + task.getId();
            throw new PermissionDeniedException(errorMessage);
        }

        if (task.getTaskData().getStatus().equals(Status.InProgress)) {
            task.getTaskData().setStatus(Status.Failed);
        }

        if (data != null) {

            FaultData faultData = ContentMarshallerHelper.marshalFault(data, null);
            Content content = new Content();
            content.setContent(faultData.getContent());
            context.getEm().persist(content);
            task.getTaskData().setFault(content.getId(), faultData);

        }
        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskFailedEvent>() {
        }).fire(task);

        return null;
    }
}
