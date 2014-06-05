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
package org.kie.internal.task.api.model;

import java.io.Externalizable;
import java.util.Date;

public interface TaskEvent extends Externalizable {
    
    public enum TaskEventType{STARTED, ACTIVATED, COMPLETED, 
                                STOPPED, EXITED, FAILED, ADDED,
                                CLAIMED, SKIPPED, SUSPENDED, CREATED, 
                                FORWARDED, RELEASED, RESUMED, DELEGATED, NOMINATED};
   
    long getId();

    void setId(long id);

    long getTaskId();

    void setTaskId(long taskId);

    TaskEventType getType();

    void setType(TaskEventType type);

    String getUserId();

    void setUserId(String userId);
    
    Date getLogTime();
    
    void setLogTime(Date timestamp);
    
}
