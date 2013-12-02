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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.internal.command.Context;

/**
 *  Operation.Stop 
        : [ new OperationCommand().{ 
                status = [ Status.InProgress ],
                allowed = [ Allowed.Owner, Allowed.BusinessAdministrator ],               
                newStatus = Status.Reserved
            } ],                 
 */
@XmlRootElement(name="stop-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class StopTaskCommand extends UserGroupCallbackTaskCommand<Void> {
	
	private static final long serialVersionUID = -4282226852650036375L;

	public StopTaskCommand() {
	}

    public StopTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        doUserGroupCallbackOperation(userId, null, context);
    	context.getTaskInstanceService().stop(taskId, userId);
    	return null;
        
    }

    
}
