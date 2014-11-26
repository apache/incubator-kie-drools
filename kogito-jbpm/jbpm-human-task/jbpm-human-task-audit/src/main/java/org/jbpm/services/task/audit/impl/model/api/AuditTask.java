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

package org.jbpm.services.task.audit.impl.model.api;

import java.util.Date;

/**
 *
 * @author salaboy
 */
public interface AuditTask {
    long getTaskId();

    void setTaskId(long taskId);

    String getStatus();

    void setStatus(String status);

    Date getActivationTime();

    void setActivationTime(Date activationTime);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    int getPriority();

    void setPriority(int priority);

    String getCreatedBy();

    void setCreatedBy(String createdBy);

    Date getCreatedOn();

    void setCreatedOn(Date createdOn);

    Date getDueDate();

    void setDueDate(Date dueDate);

    long getProcessInstanceId();

    void setProcessInstanceId(long processInstanceId);

    String getProcessId();

    void setProcessId(String processId);

    long getProcessSessionId();

    void setProcessSessionId(long processSessionId);

    long getParentId();

    void setParentId(long parentId);
    
    String getActualOwner();

    void setActualOwner(String actualOwner);

    String getDeploymentId();

    void setDeploymentId(String deploymentId);
    
    
}
