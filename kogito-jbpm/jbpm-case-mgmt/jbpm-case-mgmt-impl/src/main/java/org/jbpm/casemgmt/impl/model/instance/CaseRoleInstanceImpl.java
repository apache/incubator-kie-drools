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

package org.jbpm.casemgmt.impl.model.instance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.jbpm.casemgmt.api.model.instance.CaseRoleInstance;
import org.kie.api.task.model.OrganizationalEntity;


public class CaseRoleInstanceImpl implements CaseRoleInstance, Serializable {

    private static final long serialVersionUID = 6062519787106306778L;

    private String roleName;
    private Integer cardinality;
    private Collection<OrganizationalEntity> roleAssignments;
    
    public CaseRoleInstanceImpl(String roleName, Integer cardinality) {
        this.roleName = roleName;
        this.cardinality = cardinality;
        this.roleAssignments = new ArrayList<>();
    }
    
    public CaseRoleInstanceImpl(String roleName, Collection<OrganizationalEntity> roleAssignments) {
        this.roleName = roleName;
        this.roleAssignments = roleAssignments;
    }

    @Override
    public String getRoleName() {
        return roleName;
    }

    @Override
    public Collection<OrganizationalEntity> getRoleAssignments() {
        return roleAssignments;
    }

    public void addRoleAssignment(OrganizationalEntity entity) {
        if (cardinality != null && cardinality > 0) {
            if (cardinality < roleAssignments.size() + 1) {
                throw new IllegalArgumentException("Cannot add more users for role " + roleName + ", maximum cardinality " + cardinality + " already reached");
            }
        }
        this.roleAssignments.add(entity);

    }

    public void removeRoleAssignment(OrganizationalEntity entity) {
        this.roleAssignments.remove(entity);

    }

    @Override
    public String toString() {
        return "CaseRoleInstanceImpl [roleName=" + roleName + ", cardinality=" + cardinality + ",roleAssignments=" + roleAssignments + "]";
    }

}
