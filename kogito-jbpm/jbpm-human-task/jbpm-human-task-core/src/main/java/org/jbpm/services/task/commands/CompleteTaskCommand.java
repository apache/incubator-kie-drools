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

import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.rule.TaskRuleService;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.model.InternalTaskData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.HashMap;
import java.util.Map;


/**
 * Operation.Start : [ new OperationCommand().{ status = [ Status.Ready ],
 * allowed = [ Allowed.PotentialOwner ], setNewOwnerToUser = true, newStatus =
 * Status.InProgress }, new OperationCommand().{ status = [ Status.Reserved ],
 * allowed = [ Allowed.Owner ], newStatus = Status.InProgress } ], *
 */
@XmlRootElement(name="complete-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class CompleteTaskCommand extends UserGroupCallbackTaskCommand<Void> {

	private static final long serialVersionUID = 412409697422083299L;
	
	@XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement
    protected Map<String, Object> data;
    
    public CompleteTaskCommand() {
    }

    public CompleteTaskCommand(long taskId, String userId, Map<String, Object> data) {
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

	public Void execute(Context cntxt ) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context, true);
        groupIds = doUserGroupCallbackOperation(userId, null, context);
        context.set("local:groups", groupIds);
        
        Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
        if (task == null) {            
            throw new PermissionDeniedException("Task '" + taskId + "' not found");
        }
        
        context.loadTaskVariables(task);

        Map<String, Object> outputdata = task.getTaskData().getTaskOutputVariables();
        if (outputdata != null) {
            // if there are data given with completion, merged them into existing outputs
            if (data != null) {
                outputdata.putAll(data);
            }
            // since output data was non null make it the actual data
            data = outputdata;
            
        }
        
        
        context.getTaskRuleService().executeRules(task, userId, data, TaskRuleService.COMPLETE_TASK_SCOPE);
        ((InternalTaskData)task.getTaskData()).setTaskOutputVariables(data);
        
        TaskInstanceService instanceService = context.getTaskInstanceService();
        instanceService.complete(taskId, userId, data);
    	return null;
        
    }
	
}
