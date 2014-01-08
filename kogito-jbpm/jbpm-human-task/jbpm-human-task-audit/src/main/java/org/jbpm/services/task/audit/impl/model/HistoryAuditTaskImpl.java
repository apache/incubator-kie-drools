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

import java.util.Date;
import javax.persistence.Entity;
import org.jbpm.services.task.audit.impl.model.api.AuditTask;

/**
 *
 * @author salaboy
 */
@Entity
public class HistoryAuditTaskImpl extends UserAuditTaskImpl implements AuditTask{

    public HistoryAuditTaskImpl(String owner, long taskId, String status, Date activationTime, String name, String description, int priority, String createdBy, Date createdOn, Date dueDate, long processInstanceId, String processId, int processSessionId, long parentId) {
        super(owner, taskId, status, activationTime, name, description, priority, createdBy, createdOn, dueDate, processInstanceId, processId, processSessionId, parentId);
    }

    public HistoryAuditTaskImpl() {
    }
    
    
}
