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
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.SubTasksStrategy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

@XmlRootElement(name="process-sub-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ProcessSubTaskCommand extends UserGroupCallbackTaskCommand<Void> {

	private static final long serialVersionUID = -1315897796195789680L;
	
	@XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement
    protected Map<String, Object> data;
	
    public ProcessSubTaskCommand() {
    }
    
    public ProcessSubTaskCommand(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public ProcessSubTaskCommand(long taskId, String userId, Map<String, Object> data) {
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
	
	@Override
	public Void execute(Context cntxt) {
		
		TaskContext context = (TaskContext) cntxt;
		
		TaskInstanceService instanceService = context.getTaskInstanceService();
        TaskQueryService queryService = context.getTaskQueryService();
        
	      Task task = queryService.getTaskInstanceById(taskId);
	        if(task == null){
	            return null;
	        }
	        Task parentTask = null;
	        if (task.getTaskData().getParentId() != -1){
	            parentTask = queryService.getTaskInstanceById(task.getTaskData().getParentId());
	        }
	        if (parentTask != null){
	            if(((InternalTask) parentTask).getSubTaskStrategy() != null && 
	            		((InternalTask) parentTask).getSubTaskStrategy().equals(SubTasksStrategy.EndParentOnAllSubTasksEnd)){
	                List<TaskSummary> subTasks = queryService.getSubTasksByParent(parentTask.getId());
	                    // If there are no more sub tasks or if the last sub task is the one that we are completing now
	                    if (subTasks.isEmpty() || (subTasks.size() == 1 && subTasks.get(0).getId().equals(taskId)) ) {
	                        // Completing parent task if all the sub task has being completed, including the one that we are completing now
	                    	instanceService.complete(parentTask.getId(), "Administrator", data);
	                    }
	            }
	        }
	        if (((InternalTask) task).getSubTaskStrategy() != null && 
	        		((InternalTask) task).getSubTaskStrategy().equals(SubTasksStrategy.SkipAllSubTasksOnParentSkip)){
	            List<TaskSummary> subTasks = queryService.getSubTasksByParent(task.getId());
	            for(TaskSummary taskSummary : subTasks){
	                Task subTask = queryService.getTaskInstanceById(taskSummary.getId());
	                // Exit each sub task because the parent task was aborted
	                instanceService.skip(subTask.getId(), "Administrator");
	            }
	        }
		return null;
	}

}
