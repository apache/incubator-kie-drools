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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl;

import java.util.List;

import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskIdentityService;
import org.kie.internal.task.api.TaskPersistenceContext;

/**
 *
 */

public class TaskIdentityServiceImpl implements TaskIdentityService {

    private TaskPersistenceContext persistenceContext;

    public TaskIdentityServiceImpl() {
    }
    
    public TaskIdentityServiceImpl(TaskPersistenceContext persistenceContext) {
    	this.persistenceContext = persistenceContext;
    }

    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }
    
    public void addUser(User user) {
        persistenceContext.persistUser(user);
 
    }

    public void addGroup(Group group) {
        persistenceContext.persistGroup(group);
    }

    public void removeGroup(String groupId) {
        Group group = persistenceContext.findGroup(groupId);
        persistenceContext.remove(group);
    }
    
    public void removeUser(String userId) {
        User user = persistenceContext.findUser(userId);
        persistenceContext.remove(user);
    }

    public List<User> getUsers() {
        return persistenceContext.queryStringInTransaction("from User", 
        		ClassUtil.<List<User>>castClass(List.class));
    }

    public List<Group> getGroups() {
        return persistenceContext.queryStringInTransaction("from Group",
        		ClassUtil.<List<Group>>castClass(List.class));
    }

    public User getUserById(String userId) {
        return persistenceContext.findUser(userId);
    }

    public Group getGroupById(String groupId) {
        return persistenceContext.findGroup(groupId);
    }

    public OrganizationalEntity getOrganizationalEntityById(String entityId) {
        return persistenceContext.findOrgEntity(entityId);
    }
}
