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

import org.kie.api.runtime.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
@XmlRootElement(name="forward-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ForwardTaskCommand extends UserGroupCallbackTaskCommand<Void> {
	
	private static final long serialVersionUID = -3291367442760747824L;

	public ForwardTaskCommand() {
	}

    public ForwardTaskCommand(long taskId, String userId, String targetEntityId) {
        this.taskId = taskId;
        this.userId = userId;
        this.targetEntityId = targetEntityId;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context, true);
        doCallbackUserOperation(targetEntityId, context, true);
        groupIds = doUserGroupCallbackOperation(userId, null, context);
        context.set("local:groups", groupIds);
    	context.getTaskInstanceService().forward(taskId, userId, targetEntityId);
    	return null;
       
    }
}
