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

package org.jbpm.casemgmt.impl.audit;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;

@Entity
@SequenceGenerator(name = "caseRoleAssignLogIdSeq", sequenceName = "CASE_ROLE_ASSIGN_LOG_ID_SEQ", allocationSize = 1)
public class CaseRoleAssignmentLog implements Serializable {

    private static final long serialVersionUID = 5757703502285633210L;

    private static final int USER_TYPE = 1;
    private static final int GROUP_TYPE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "caseRoleAssignLogIdSeq")
    private long id;

    private long processInstanceId;

    private String caseId;

    private String roleName;

    private String entityId;

    private int type;

    public CaseRoleAssignmentLog() {
    }

    public CaseRoleAssignmentLog(long processInstanceId, String caseId, String roleName, OrganizationalEntity entity) {
        this.processInstanceId = processInstanceId;
        this.caseId = caseId;
        this.roleName = roleName;
        this.entityId = entity.getId();
        if (entity instanceof User) {
            this.type = USER_TYPE;
        } else if (entity instanceof Group) {
            this.type = GROUP_TYPE;
        }
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((caseId == null) ? 0 : caseId.hashCode());
        result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (processInstanceId ^ (processInstanceId >>> 32));
        result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
        result = prime * result + type;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CaseRoleAssignmentLog other = (CaseRoleAssignmentLog) obj;
        if (caseId == null) {
            if (other.caseId != null)
                return false;
        } else if (!caseId.equals(other.caseId))
            return false;
        if (entityId == null) {
            if (other.entityId != null)
                return false;
        } else if (!entityId.equals(other.entityId))
            return false;
        if (id != other.id)
            return false;
        if (processInstanceId != other.processInstanceId)
            return false;
        if (roleName == null) {
            if (other.roleName != null)
                return false;
        } else if (!roleName.equals(other.roleName))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CaseRoleAssignmentLog [processInstanceId=" + processInstanceId + ", caseId=" + caseId + ", roleName=" + roleName + ", entityId=" + entityId + ", type=" + type + "]";
    }

}
