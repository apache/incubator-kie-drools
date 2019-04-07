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
package org.jbpm.services.task.audit.impl.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
@Table(name="BAMTaskSummary",
       indexes = {@Index(name = "IDX_BAMTaskSumm_createdDate",  columnList="createdDate"),
                  @Index(name = "IDX_BAMTaskSumm_duration",  columnList="duration"),
                  @Index(name = "IDX_BAMTaskSumm_endDate",  columnList="endDate"),
                  @Index(name = "IDX_BAMTaskSumm_pInstId",  columnList="processInstanceId"),
                  @Index(name = "IDX_BAMTaskSumm_startDate",  columnList="startDate"),
                  @Index(name = "IDX_BAMTaskSumm_status",  columnList="status"),
                  @Index(name = "IDX_BAMTaskSumm_taskId",  columnList="taskId"),
                  @Index(name = "IDX_BAMTaskSumm_taskName",  columnList="taskName"),
                  @Index(name = "IDX_BAMTaskSumm_userId", columnList="userId")})

@SequenceGenerator(name="bamTaskIdSeq", sequenceName="BAM_TASK_ID_SEQ")
public class BAMTaskSummaryImpl implements Serializable {

    private static final long serialVersionUID = 2793651602463099870L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="bamTaskIdSeq")
    @Column(name = "pk")
    private Long pk  = 0L;
    
    @Version
    @Column(name = "OPTLOCK")
    private Integer version;
    
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


    public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((pk == null) ? 0 : pk.hashCode());
		result = prime * result
				+ (int) (processInstanceId ^ (processInstanceId >>> 32));
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + (int) (taskId ^ (taskId >>> 32));
		result = prime * result
				+ ((taskName == null) ? 0 : taskName.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		BAMTaskSummaryImpl other = (BAMTaskSummaryImpl) obj;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (duration == null) {
			if (other.duration != null)
				return false;
		} else if (!duration.equals(other.duration))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (pk == null) {
			if (other.pk != null)
				return false;
		} else if (!pk.equals(other.pk))
			return false;
		if (processInstanceId != other.processInstanceId)
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (taskId != other.taskId)
			return false;
		if (taskName == null) {
			if (other.taskName != null)
				return false;
		} else if (!taskName.equals(other.taskName))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
    
	
}
