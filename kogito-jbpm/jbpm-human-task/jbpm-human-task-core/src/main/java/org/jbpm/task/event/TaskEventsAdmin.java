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
package org.jbpm.task.event;

import java.util.List;


public interface TaskEventsAdmin {
    
    public void storeEvent(TaskEvent event);
    
    public List<TaskEvent> getEventsByTaskId(Long taskId);
    
    public List<TaskEvent> getEventsByTypeByTaskId(Long taskId, String type);
    
    //Event status queries
    
    public List<TaskEvent> getTaskForwardedEventsByTaskId(Long taskId);
    
    public List<TaskEvent> getTaskClaimedEventsByTaskId(Long taskId);
    
    public List<TaskEvent> getTaskCompletedEventsByTaskId(Long taskId);
    
    public List<TaskEvent> getTaskFailedEventsByTaskId(Long taskId);
    
    public List<TaskEvent> getTaskSkippedEventsByTaskId(Long taskId);
    
    public List<TaskEvent> getTaskStartedEventsByTaskId(Long taskId);
    
    public List<TaskEvent> getTaskStoppedEventsByTaskId(Long taskId);
    
    public List<TaskEvent> getTaskCreatedEventsByTaskId(Long taskId);
    
    public List<TaskEvent> getTaskReleasedEventsByTaskId(Long taskId);
    
    
}
