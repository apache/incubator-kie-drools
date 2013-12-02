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

import java.util.Map;

import javax.enterprise.util.AnnotationLiteral;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskFailedEvent;
import org.jbpm.services.task.events.BeforeTaskFailedEvent;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.impl.model.ContentImpl;
import org.jbpm.services.task.impl.model.FaultDataImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 * Operation.Fail : [ new OperationCommand().{ status = [ Status.InProgress ],
 * allowed = [ Allowed.Owner ], newStatus = Status.Failed } ],
 */
@Transactional
@XmlRootElement(name="fail-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class FailTaskCommand extends TaskCommand<Void> {

    private Map<String, Object> data;

    public FailTaskCommand() {
    }
    
    public FailTaskCommand(long taskId, String userId, Map<String, Object> data) {
        this.taskId = taskId;
        this.userId = userId;
        this.data = data;
    }

    public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	context.getTaskService().fail(taskId, userId, data);
        	return null;
        }
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
        	((InternalTaskData) task.getTaskData()).setStatus(Status.Failed);
        }

        if (data != null) {

            FaultDataImpl faultData = ContentMarshallerHelper.marshalFault(data, null);
            ContentImpl content = new ContentImpl();
            content.setContent(faultData.getContent());
            context.getPm().persist(content);
            ((InternalTaskData) task.getTaskData()).setFault(content.getId(), faultData);

        }
        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskFailedEvent>() {
        }).fire(task);

        return null;
    }
}
