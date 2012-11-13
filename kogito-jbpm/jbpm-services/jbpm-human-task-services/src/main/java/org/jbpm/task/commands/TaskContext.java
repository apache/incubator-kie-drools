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
package org.jbpm.task.commands;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.kie.command.Context;
import org.kie.command.World;
import org.jbpm.task.Task;
import org.jbpm.task.annotations.Internal;
import org.jbpm.task.api.TaskDefService;
import org.jbpm.task.api.TaskIdentityService;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.identity.UserGroupCallback;
import org.jbpm.task.lifecycle.listeners.TaskLifeCycleEventListener;

/**
 *
 */
public class TaskContext implements Context{
    @Inject 
    private EntityManager em;
    @Inject
    private TaskDefService taskDefService;
    @Inject
    private TaskQueryService taskQueryService;
    @Inject
    private TaskIdentityService taskIdentityService;
    @Inject 
    private Event<Task> taskEvents;
    @Inject @Internal
    private TaskLifeCycleEventListener eventListener;
    @Inject 
    private UserGroupCallback userGroupCallback;
    
    public TaskContext() {
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public TaskDefService getTaskDefService() {
        return taskDefService;
    }

    public void setTaskDefService(TaskDefService taskDefService) {
        this.taskDefService = taskDefService;
    }

    public TaskQueryService getTaskQueryService() {
        return taskQueryService;
    }

    public void setTaskQueryService(TaskQueryService taskQueryService) {
        this.taskQueryService = taskQueryService;
    }

    public TaskIdentityService getTaskIdentityService() {
        return taskIdentityService;
    }

    public void setTaskIdentityService(TaskIdentityService taskIdentityService) {
        this.taskIdentityService = taskIdentityService;
    }

    public Event<Task> getTaskEvents() {
        return taskEvents;
    }

    public void setTaskEvents(Event<Task> taskEvents) {
        this.taskEvents = taskEvents;
    }

    public TaskLifeCycleEventListener getEventListener() {
        return eventListener;
    }

    public UserGroupCallback getUserGroupCallback() {
        return userGroupCallback;
    }

    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        this.userGroupCallback = userGroupCallback;
    }
    
    public void setEventListener(TaskLifeCycleEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public World getContextManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object get(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void set(String string, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void remove(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

   
}
