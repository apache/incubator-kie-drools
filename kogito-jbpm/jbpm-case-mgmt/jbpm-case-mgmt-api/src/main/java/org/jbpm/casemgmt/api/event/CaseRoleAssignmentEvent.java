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

package org.jbpm.casemgmt.api.event;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.kie.api.task.model.OrganizationalEntity;

/**
 * Represents occurrence of role assignment on a case operation
 */
public class CaseRoleAssignmentEvent extends CaseEvent {

    private String role;
    private OrganizationalEntity entity;
    
    public CaseRoleAssignmentEvent(String user, String caseId, CaseFileInstance caseFile, String role, OrganizationalEntity entity) {
        super(user, caseId, caseFile);
        this.role = role;
        this.entity = entity;
    }
    
    /**
     * Returns name of the role that assignment is changed for
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Returns organizational entity (user or group) that the assignment is changed for
     */
    public OrganizationalEntity getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return "CaseRoleAssignmentEvent [role=" + role + ", entity=" + entity + ", caseId=" + getCaseId() + "]";
    }
    

}
