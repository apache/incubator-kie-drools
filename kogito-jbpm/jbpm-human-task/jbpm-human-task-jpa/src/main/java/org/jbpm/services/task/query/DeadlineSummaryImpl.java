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

package org.jbpm.services.task.query;

import java.util.Date;

public class DeadlineSummaryImpl implements org.kie.internal.task.api.model.DeadlineSummary {
    private long taskId;
    private long deadlineId;
    private Date date;
    
    public DeadlineSummaryImpl() { 
        // default constructor
    }
            
    public DeadlineSummaryImpl(long taskId,
                           long deadlineId,
                           Date date) {
        super();
        this.taskId = taskId;
        this.deadlineId = deadlineId;
        this.date = date;
    }
    
    public long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
    
    public long getDeadlineId() {
        return deadlineId;
    }
    
    public void setDeadlineId(long deadlineId) {
        this.deadlineId = deadlineId;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + (int) (deadlineId ^ (deadlineId >>> 32));
        result = prime * result + (int) (taskId ^ (taskId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof DeadlineSummaryImpl) ) return false;
        DeadlineSummaryImpl other = (DeadlineSummaryImpl) obj;
        if ( date == null ) {
            if ( other.date != null ) return false;
        } else if ( date.getTime() != other.date.getTime() ) return false;
        if ( deadlineId != other.deadlineId ) return false;
        if ( taskId != other.taskId ) return false;
        return true;
    }
    
    
}
