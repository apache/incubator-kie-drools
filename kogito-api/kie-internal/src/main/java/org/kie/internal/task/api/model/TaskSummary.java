/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.internal.task.api.model;

import java.io.Externalizable;
import java.util.Date;
import java.util.List;

public interface TaskSummary extends Externalizable {

    long getId();

    void setId(long id);

    long getProcessInstanceId();

    void setProcessInstanceId(long processInstanceId);

    String getName();

    void setName(String name);

    String getSubject();

    void setSubject(String subject);

    String getDescription();

    void setDescription(String description);

    Status getStatus();

    void setStatus(Status status);

    int getPriority();

    void setPriority(int priority);

    boolean isSkipable();

    void setSkipable(boolean skipable);

    User getActualOwner();

    void setActualOwner(User actualOwner);

    User getCreatedBy();

    void setCreatedBy(User createdBy);

    Date getCreatedOn();

    void setCreatedOn(Date createdOn);

    Date getActivationTime();

    void setActivationTime(Date activationTime);

    Date getExpirationTime();

    void setExpirationTime(Date expirationTime);

    String getProcessId();

    void setProcessId(String processId);

    int getProcessSessionId();

    void setProcessSessionId(int processSessionId);

    SubTasksStrategy getSubTaskStrategy();

    void setSubTaskStrategy(SubTasksStrategy subTaskStrategy);

    long getParentId();

    void setParentId(long parentId);

    List<String> getPotentialOwners();

    void setPotentialOwners(List<String> potentialOwners);
}
