/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.audit;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.jbpm.services.task.impl.model.UserImpl;

import org.kie.api.task.model.User;
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
    
    private TaskEventType type;
    
    @ManyToOne(targetEntity=UserImpl.class)
    private User user;

    public TaskEventImpl() {
    }

    public TaskEventImpl(long taskId, TaskEventType type, User user) {
        this.taskId = taskId;
        this.type = type;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
