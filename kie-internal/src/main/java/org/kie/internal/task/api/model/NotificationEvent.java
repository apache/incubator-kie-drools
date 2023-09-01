package org.kie.internal.task.api.model;

import java.io.Serializable;
import java.util.Map;

import org.kie.api.task.model.Task;


/**
 *
 */
public class NotificationEvent implements Serializable{
    private Notification notification;
    private Task task;
    private Map<String, Object> variables;

    public NotificationEvent(Notification notification, Task task, Map<String, Object> variables) {
        this.notification = notification;
        this.task = task;
        this.variables = variables;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Map<String, Object> getContent() {
        return variables;
    }

    public void setContent(Map<String, Object> variables) {
        this.variables = variables;
    }



}
