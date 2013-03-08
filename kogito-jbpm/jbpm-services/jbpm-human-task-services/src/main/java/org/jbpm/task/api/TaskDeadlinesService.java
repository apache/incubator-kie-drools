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
package org.jbpm.task.api;

import java.util.Arrays;
import java.util.List;

import org.jbpm.task.Status;

/**
 * The Task Deadlines Service is intended to handle
 *  all the Deadlines associated with a Task
 */
public interface TaskDeadlinesService {
    
    public enum DeadlineType {
        START(Status.Created, Status.Ready, Status.Reserved),
        END(Status.Created, Status.Ready, Status.Reserved, Status.InProgress, Status.Suspended);
        private List<Status> validStatuses;
        
        private DeadlineType(Status... statuses) {
            this.validStatuses = Arrays.asList(statuses);
        }
        
        public boolean isValidStatus(Status status) {
            return this.validStatuses.contains(status);
        }

    }

    public void schedule(long taskId, long deadlineId, long delay, DeadlineType type);
    
    public void unschedule(long taskId, DeadlineType type); 
    
}
