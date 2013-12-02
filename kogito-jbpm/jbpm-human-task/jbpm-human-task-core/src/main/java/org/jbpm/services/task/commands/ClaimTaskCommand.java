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
 * Operation.Claim 
        : [ new OperationCommand().{ 
                status = [ Status.Ready ],
                allowed = [ Allowed.PotentialOwner, Allowed.BusinessAdministrator ],    
                setNewOwnerToUser = true,           
                newStatus = Status.Reserved
            } ],
 */
@XmlRootElement(name="claim-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ClaimTaskCommand extends UserGroupCallbackTaskCommand<Void> {

	private static final long serialVersionUID = 3457622878127569389L;

	public ClaimTaskCommand() {
	}
	
	public ClaimTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        doUserGroupCallbackOperation(userId, null, context);
    	context.getTaskInstanceService().claim(taskId, userId);
    	return null;
        
    }

   
}
