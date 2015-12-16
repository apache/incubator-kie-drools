/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.fluent.task;

import java.util.List;
import java.util.Map;

import org.kie.api.task.TaskService;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;

/**
 * See {@link TaskService}
 * 
 */
public interface TaskFluent<T> {
    
    T activate(long taskId, String userId);

    T claim(long taskId, String userId);

    T claimNextAvailable(String userId, String language);

    T complete(long taskId, String userId, Map<String, Object> data);

    T delegate(long taskId, String userId, String targetUserId);

    T exit(long taskId, String userId);

    T fail(long taskId, String userId, Map<String, Object> faultData);

    T forward(long taskId, String userId, String targetEntityId);

    T getTaskByWorkItemId(long workItemId);

    T getTaskById(long taskId);

    T getTasksAssignedAsBusinessAdministrator(String userId, String language);

    T getTasksAssignedAsPotentialOwner(String userId, String language);

    T getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language);

    T getTasksOwned(String userId, String language);

    T getTasksOwnedByStatus(String userId, List<Status> status, String language);

    T getTasksByStatusByProcessInstanceId(long processInstanceId, List<Status> status, String language);

    T getTasksByProcessInstanceId(long processInstanceId);
    
    T addTask(Task task, Map<String, Object> params);

    T release(long taskId, String userId);

    T resume(long taskId, String userId);

    T skip(long taskId, String userId);

    T start(long taskId, String userId);

    T stop(long taskId, String userId);

    T suspend(long taskId, String userId);

    T nominate(long taskId, String userId, List<OrganizationalEntity> potentialOwners);

    T getContentById(long contentId);

    T getAttachmentById(long attachId);
    
}
