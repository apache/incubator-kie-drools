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

package org.jbpm.services.task.wih;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.KieSession;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;

public class NonManagedTaskEventListener implements TaskLifeCycleEventListener {

	private KieSession ksession;
	private TaskService taskService;
	
	public NonManagedTaskEventListener(KieSession ksession, TaskService taskService) {
		this.ksession = ksession;
		this.taskService = taskService;
	}
	
    public void processTaskState(Task task) {

        long workItemId = task.getTaskData().getWorkItemId();
  
        if (task.getTaskData().getStatus() == Status.Completed) {
            String userId = task.getTaskData().getActualOwner().getId();
            Map<String, Object> results = new HashMap<String, Object>();
            
            long contentId = task.getTaskData().getOutputContentId();
            if (contentId != -1) {
                Content content = taskService.getContentById(contentId);
                
                Object result = ContentMarshallerHelper.unmarshall(content.getContent(), ksession.getEnvironment(), ksession.getClass().getClassLoader());
                results.put("Result", result);
                if (result instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) result;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            results.put((String) entry.getKey(), entry.getValue());
                        }
                    }
                }
                results.put("ActorId", userId);
                ksession.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), results);
            } else {
            	results.put("ActorId", userId);
            	ksession.getWorkItemManager().completeWorkItem(workItemId, results);
            }
        } else {
        	ksession.getWorkItemManager().abortWorkItem(workItemId);
        }
    }
	
	@Override
	public void beforeTaskActivatedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskClaimedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskSkippedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskStartedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskStoppedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskCompletedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskFailedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskAddedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskExitedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskReleasedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskResumedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskSuspendedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskForwardedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskDelegatedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskActivatedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskClaimedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskSkippedEvent(TaskEvent event) {
		Task task = event.getTask();
        long processInstanceId = task.getTaskData().getProcessInstanceId();
        if (processInstanceId <= 0) {
            return;
        }
        processTaskState(task);
	}

	@Override
	public void afterTaskStartedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskStoppedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskCompletedEvent(TaskEvent event) {
    	Task task = event.getTask();
        long processInstanceId = task.getTaskData().getProcessInstanceId();
        if (processInstanceId <= 0) {
            return;
        }
        processTaskState(task);
   
	}

	@Override
	public void afterTaskFailedEvent(TaskEvent event) {
		Task task = event.getTask();
        long processInstanceId = task.getTaskData().getProcessInstanceId();
        if (processInstanceId <= 0) {
            return;
        }
        processTaskState(task);
	}

	@Override
	public void afterTaskAddedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskExitedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskReleasedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskResumedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskSuspendedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskForwardedEvent(TaskEvent event) {
		

	}

	@Override
	public void afterTaskDelegatedEvent(TaskEvent event) {
		

	}

	@Override
	public void beforeTaskNominatedEvent(TaskEvent event) {
		
	}

	@Override
	public void afterTaskNominatedEvent(TaskEvent event) {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ksession == null) ? 0 : ksession.hashCode());
		result = prime * result
				+ ((taskService == null) ? 0 : taskService.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NonManagedTaskEventListener other = (NonManagedTaskEventListener) obj;
		if (ksession == null) {
			if (other.ksession != null)
				return false;
		} else if (!ksession.equals(other.ksession))
			return false;
		if (taskService == null) {
			if (other.taskService != null)
				return false;
		} else if (!taskService.equals(other.taskService))
			return false;
		return true;
	}

}
