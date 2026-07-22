/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.task.management.service;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.usertask.*;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManagementService implements TaskManagementOperations {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManagementService.class);
    public static final String WORKFLOW_ENGINE_USER = "WORKFLOW_ENGINE_USER";

    private UserTasks userTasks;
    // unit of work needs to add the publisher and this is not shared.
    private UserTaskConfig userTaskConfig;
    private ProcessConfig processesConfig;

    public TaskManagementService(UserTasks userTasks, UserTaskConfig userTaskConfig, ProcessConfig processConfig) {
        this.userTasks = userTasks;
        this.userTaskConfig = userTaskConfig;
        this.processesConfig = processConfig;
    }

    @Override
    public TaskInfo updateTask(String taskId, TaskInfo taskInfo, boolean shouldReplace, IdentityProvider identity) {
        UserTaskInstance updatedUserTaskInstance = UnitOfWorkExecutor.executeInUnitOfWork(processesConfig.unitOfWorkManager(), () -> {
            DefaultUserTaskInstance ut = (DefaultUserTaskInstance) getUserTaskInstance(taskId);
            enforceAdminOrOwner(ut, identity);
            setField(ut::setTaskDescription, taskInfo::getDescription, shouldReplace);
            setField(ut::setTaskPriority, taskInfo::getPriority, shouldReplace);
            setField(ut::setAdminGroups, taskInfo::getAdminGroups, shouldReplace);
            setField(ut::setAdminUsers, taskInfo::getAdminUsers, shouldReplace);
            setField(ut::setExcludedUsers, taskInfo::getExcludedUsers, shouldReplace);
            setField(ut::setPotentialUsers, taskInfo::getPotentialUsers, shouldReplace);
            setField(ut::setPotentialGroups, taskInfo::getPotentialGroups, shouldReplace);
            setMap(ut::setInputs, ut::setInput, taskInfo.getInputParams(), shouldReplace);
            return ut;
        });
        LOG.trace("updated task through management endpoint to {}", updatedUserTaskInstance);
        return convert(updatedUserTaskInstance);
    }

    private <T> boolean setField(Consumer<T> consumer, Supplier<T> supplier, boolean shouldReplace) {
        T value = supplier.get();
        boolean result = shouldReplace || value != null;
        if (result) {
            consumer.accept(value);
        }
        return result;
    }

    private void setMap(Consumer<Map<String, Object>> allConsumer,
            BiConsumer<String, Object> entryConsumer,
            Map<String, Object> params,
            boolean shouldReplace) {
        if (params != null) {
            if (shouldReplace) {
                allConsumer.accept(params);
            } else {
                for (Entry<String, Object> entry : params.entrySet()) {
                    entryConsumer.accept(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    @Override
    public TaskInfo getTask(String taskId) {
        return convert(getUserTaskInstance(taskId));
    }

    private TaskInfo convert(UserTaskInstance userTaskInstance) {
        return new TaskInfo(
                userTaskInstance.getTaskDescription(),
                userTaskInstance.getTaskPriority(),
                userTaskInstance.getPotentialUsers(),
                userTaskInstance.getPotentialGroups(),
                userTaskInstance.getExcludedUsers(),
                userTaskInstance.getAdminUsers(),
                userTaskInstance.getAdminGroups(),
                userTaskInstance.getInputs());
    }

    private UserTaskInstance getUserTaskInstance(String taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("Task id must be given");
        }
        Optional<UserTaskInstance> userTaskInstance = userTasks.instances().findById(taskId);
        if (userTaskInstance.isEmpty()) {
            throw new UserTaskInstanceNotFoundException(String.format("user task instance with id %s not found", taskId));
        }
        return userTaskInstance.get();
    }

    private void enforceAdminOrOwner(UserTaskInstance userTaskInstance, IdentityProvider identity) {
        String user = identity.getName();
        Collection<String> roles = identity.getRoles();
        String taskId = userTaskInstance.getId();

        if (WORKFLOW_ENGINE_USER.equals(user)) {
            LOG.debug("User {} authorized for user task {} as system user.", user, taskId);
            return;
        }

        if (user == null) {
            LOG.debug("No user defined to perform update on user task {}", userTaskInstance.getId());
            throw new UserTaskInstanceNotAuthorizedException("No user defined to perform update on user task " + userTaskInstance.getId());
        }

        Set<String> adminUsers = userTaskInstance.getAdminUsers();
        if (adminUsers.contains(user)) {
            LOG.debug("User {} authorized for user task {} as admin user.", user, taskId);
            return;
        }

        Set<String> userAdminGroups = new HashSet<>(userTaskInstance.getAdminGroups());
        userAdminGroups.retainAll(roles);
        if (!userAdminGroups.isEmpty()) {
            LOG.debug("User {} with roles {} authorized for user task {} as a member of admin group.", user, roles, taskId);
            return;
        }

        if (user.equals(userTaskInstance.getActualOwner())) {
            LOG.debug("User {} authorized for user task {} as owner.", user, taskId);
            return;
        }

        LOG.debug("identity {} with roles {} not authorized for user task {} with adminUsers {} and adminGroups {}",
                identity.getName(),
                identity.getRoles(),
                userTaskInstance.getId(),
                userTaskInstance.getAdminUsers(),
                userTaskInstance.getAdminGroups());
        throw new UserTaskInstanceNotAuthorizedException("User " + user + " with roles " + identity.getRoles() + " not authorized to perform an operation on user task " + userTaskInstance.getId());
    }
}
