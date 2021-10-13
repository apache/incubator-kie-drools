/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.process;

import java.util.Collection;

import org.kie.api.task.model.OrganizationalEntity;

/**
 * Represents case assignment which usually means named role to individuals or groups.
 *
 */
public interface CaseAssignment {

    /**
     * Assigns given entity (either user or group) to given role
     * @param roleName name of the role entity should be assigned to
     * @param entity user or group to be assigned
     */
    void assign(String roleName, OrganizationalEntity entity);
    
    /**
     * Assigns given user to given role
     * @param roleName name of the role user should be assigned to
     * @param userId user to be assigned
     */
    void assignUser(String roleName, String userId);
    
    /**
     * Assigns given group to given role
     * @param roleName name of the role group should be assigned to
     * @param groupId group to be assigned
     */
    void assignGroup(String roleName, String groupId);
    
    /**
     * Removes given entity from the role
     * @param roleName name of the role that given entity should be removed from
     * @param entity use or group to be removed
     */
    void remove(String roleName, OrganizationalEntity entity);
    
    /**
     * Returns assigned entities for given role
     * @param roleName name of the role assignment should be returned for
     * @return returns all assignments for the given role
     */
    Collection<OrganizationalEntity> getAssignments(String roleName);
    
    /**
     * Returns defined roles
     * @return returns all roles
     */
    Collection<String> getRoles();
}
