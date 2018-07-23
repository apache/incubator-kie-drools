/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.commands;

import org.jbpm.services.task.impl.util.DeadlineSchedulerHelper;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.model.InternalTask;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Operation.Release 
        : [ new OperationCommand().{ 
                status = [ Status.Reserved, Status.InProgress ],
                allowed = [Allowed.Owner, Allowed.BusinessAdministrator ],  
                setNewOwnerToNull = true,            
                newStatus = Status.Ready
            } ],    
 */
@XmlRootElement(name="release-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ReleaseTaskCommand extends UserGroupCallbackTaskCommand<Void> {
	
	private static final long serialVersionUID = -9094809920345727802L;

	public ReleaseTaskCommand() {
	}

    public ReleaseTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context, true);
        groupIds = doUserGroupCallbackOperation(userId, null, context);
        context.set("local:groups", groupIds);
        context.getTaskInstanceService().release(taskId, userId);
        Task task = context.getPersistenceContext().findTask(taskId);
        if(task.getTaskData().getPreviousStatus().equals(Status.InProgress)) {
            DeadlineSchedulerHelper.rescheduleDeadlinesForTask((InternalTask) task, context, DeadlineType.START);
        }
        return null;

    }

}
