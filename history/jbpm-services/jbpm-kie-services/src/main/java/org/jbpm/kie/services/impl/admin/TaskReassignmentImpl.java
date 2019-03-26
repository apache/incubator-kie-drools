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

package org.jbpm.kie.services.impl.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.services.api.admin.TaskReassignment;
import org.kie.api.task.model.OrganizationalEntity;


public class TaskReassignmentImpl implements TaskReassignment {

    private static final long serialVersionUID = -3261668409546992835L;
    private Long id;
    private String name;
    private Date date;
    
    private List<OrganizationalEntity> potentialOwners;
    
    private boolean active;
    
    public TaskReassignmentImpl(Long id, String name, Date date, List<OrganizationalEntity> potentialOwners, boolean active) {
        super();
        this.id = id;
        this.name = name;
        this.date = date;
        this.potentialOwners = new ArrayList<OrganizationalEntity>(potentialOwners);
        this.active = active;
    }

    @Override
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
     
    @Override
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    @Override
    public List<OrganizationalEntity> getPotentialOwners() {
        return potentialOwners;
    }
    
    public void setPotentialOwners(List<OrganizationalEntity> recipients) {
        this.potentialOwners = recipients;
    }

    @Override
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "TaskReassignmentImpl [id=" + id + ", name=" + name + ", date=" + date + ", potentialOwners=" + potentialOwners + "]";
    }      

}
