/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 */

@Entity
public class TaskEvent implements Serializable {
    
    public enum TaskEventType{STARTED, COMPLETED, SUSPENDED};
    @Id
    @GeneratedValue
    private long id;
    
    private long taskId;
    
    private TaskEventType type;
    
    @ManyToOne
    private User user;

    public TaskEvent() {
    }

    public TaskEvent(long taskId, TaskEventType type, User user) {
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
    
    
}
