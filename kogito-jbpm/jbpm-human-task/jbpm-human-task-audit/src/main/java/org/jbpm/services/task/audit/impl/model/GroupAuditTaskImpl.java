/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.services.task.audit.impl.model;

import org.jbpm.services.task.audit.impl.model.api.GroupAuditTask;
import java.util.Date;
import javax.persistence.Entity;

/**
 *
 * @author salaboy
 */
@Entity
public class GroupAuditTaskImpl extends AbstractAuditTaskImpl implements GroupAuditTask{
    private String potentialOwners;

    public GroupAuditTaskImpl() {
    }

    
    public GroupAuditTaskImpl(String potentialOwners, long taskId, String status, Date activationTime, String name, String description, int priority, String createdBy, Date createdOn, Date expirationTime, long processInstanceId, String processId, int processSessionId, long parentId) {
        super(taskId, status, activationTime, name, description, priority, createdBy, createdOn, expirationTime, processInstanceId, processId, processSessionId, parentId);
        this.potentialOwners = potentialOwners;
    }

    public String getPotentialOwners() {
        return potentialOwners;
    }

    public void setPotentialOwners(String potentialOwners) {
        this.potentialOwners = potentialOwners;
    }
        
}
