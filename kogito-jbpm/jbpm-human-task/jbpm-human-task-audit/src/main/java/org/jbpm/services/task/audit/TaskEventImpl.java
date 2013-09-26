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
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.internal.task.api.model.TaskEvent.TaskEventType;

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

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // TODO Auto-generated method stub
        
    }
    
    
}
