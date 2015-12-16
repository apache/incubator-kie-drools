/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.task.api;

import java.util.List;

import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;

/**
 * The Task Identity Service provides all the 
 *  functionality related with the Organizational Entities
 *  that will be handled internally by jBPM. This methods
 *  will allow us to create the Mappings against external 
 *  identity directories to the internal inforamtion required
 *  by jBPM.
 */
public interface TaskIdentityService {

    public void addUser(User user);

    public void addGroup(Group group);

    public void removeGroup(String groupId);

    public void removeUser(String userId);

    public List<User> getUsers();

    public List<Group> getGroups();

    public User getUserById(String userId);

    public Group getGroupById(String groupId);
    
    public OrganizationalEntity getOrganizationalEntityById(String entityId);

    
}
