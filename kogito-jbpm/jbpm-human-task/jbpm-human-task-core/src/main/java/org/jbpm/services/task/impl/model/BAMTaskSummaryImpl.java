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
package org.jbpm.services.task.impl.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author salaboy
 */
@Entity
@Table(name="BAMTaskSummary")
public class BAMTaskSummaryImpl implements Serializable {

    private static final long serialVersionUID = 2793651602463099870L;

    @Id
    @GeneratedValue()
    private Long pk;
    
    private long taskId;
    
    private String taskName;
    
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    private long processInstanceId;
    
     // Initiator
    private String userId;
    private Long duration;

    public BAMTaskSummaryImpl() {
    }

    public BAMTaskSummaryImpl(long taskId, String name,  String status, Date createdDate, String userId, long processInstanceId) {
        this.taskId = taskId;
        this.taskName = name;
        this.userId = userId;
        this.status = status;
        this.createdDate = createdDate;
        this.processInstanceId = processInstanceId;
    }
    
    

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return taskName;
    }

    public void setName(String name) {
        this.taskName = name;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public String toString() {
        return "BAMTaskSummary{" + "pk=" + pk + ", taskId=" + taskId + ", name=" + taskName + ", status=" + status + ", createdDate=" + createdDate + ", startDate=" + startDate + ", endDate=" + endDate + ", processInstanceId=" + processInstanceId + ", userId=" + userId + ", duration=" + duration + '}';
    }
    
    



    
}
