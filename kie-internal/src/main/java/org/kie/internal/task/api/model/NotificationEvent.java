/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
