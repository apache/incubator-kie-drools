/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.audit;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;

import org.kie.internal.task.api.model.TaskEvent;

/**
 *
 */

@Entity
@Table(name="TaskEvent")
@SequenceGenerator(name="taskEventIdSeq", sequenceName="TASK_EVENT_ID_SEQ")
public class TaskEventImpl implements TaskEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="taskEventIdSeq")
    @Column(name = "id")
    private Long id;
    
    @Version
    @Column(name = "OPTLOCK")
    private Integer version;
    
    private Long taskId;
    
    @Enumerated(EnumType.STRING)
    private TaskEventType type;
    
    
    private String userId;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date logTime;

    public TaskEventImpl() {
    }

    public TaskEventImpl(long taskId, TaskEventType type, String userId) {
        this.taskId = taskId;
        this.type = type;
        this.userId = userId;
    }

    public TaskEventImpl(Long taskId, TaskEventType type, String userId, Date logTime) {
        this.taskId = taskId;
        this.type = type;
        this.userId = userId;
        this.logTime = logTime;
    }
    
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public TaskEventType getType() {
        return type;
    }

    public void setType(TaskEventType type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // TODO Auto-generated method stub
        
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((logTime == null) ? 0 : logTime.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TaskEventImpl other = (TaskEventImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (logTime == null) {
			if (other.logTime != null)
				return false;
		} else if (!logTime.equals(other.logTime))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		if (type != other.type)
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
