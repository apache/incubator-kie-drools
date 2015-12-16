/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.Date;
import java.util.List;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;

public interface InternalTaskSummary extends TaskSummary {

    void setId(long id);

    void setProcessInstanceId(long processInstanceId);

    void setName(String name);

    void setSubject(String subject);

    void setDescription(String description);

    void setStatus(Status status);

    void setPriority(int priority);

    void setSkipable(boolean skipable);

    void setActualOwner(User actualOwner);

    void setCreatedBy(User createdBy);

    void setCreatedOn(Date createdOn);

    void setActivationTime(Date activationTime);

    void setExpirationTime(Date expirationTime);

    void setProcessId(String processId);

    void setProcessSessionId(long processSessionId);

    SubTasksStrategy getSubTaskStrategy();

    void setSubTaskStrategy(SubTasksStrategy subTaskStrategy);

    Long getParentId();

    void setParentId(long parentId);

    @Deprecated // remove in 7.0 since this field is never filled
    void setPotentialOwners(List<String> potentialOwners);
    
}
