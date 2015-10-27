/*
 * Copyright 2013 JBoss Inc
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
package org.jbpm.services.task.lifecycle.listeners;

import java.util.Map;

import org.kie.api.task.TaskEvent;

/**
 * Methods of this interface will be promoted to main (public) TaskLifeCycleEventListener on next major version
 *
 */
public interface TaskLifeCycleEventListener extends org.kie.api.task.TaskLifeCycleEventListener {
    
	public void beforeTaskUpdatedEvent(TaskEvent event);
    
    public void afterTaskUpdatedEvent(TaskEvent event); 
    
    public void beforeTaskReassignedEvent(TaskEvent event);
    
    public void afterTaskReassignedEvent(TaskEvent event); 
    
    public void beforeTaskNotificationEvent(TaskEvent event);
    
    public void afterTaskNotificationEvent(TaskEvent event); 
    
    public void afterTaskInputVariableChangedEvent(TaskEvent event, Map<String, Object> variables);
    
    public void afterTaskOutputVariableChangedEvent(TaskEvent event, Map<String, Object> variables); 
}
