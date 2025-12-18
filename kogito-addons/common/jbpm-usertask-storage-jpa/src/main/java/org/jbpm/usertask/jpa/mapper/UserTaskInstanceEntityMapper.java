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

package org.jbpm.usertask.jpa.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbpm.usertask.jpa.model.TaskProcessInfoEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.model.ProcessInfo;

public class UserTaskInstanceEntityMapper {

    private List<EntityMapper> mappers;

    public UserTaskInstanceEntityMapper(Iterable<EntityMapper> mappers) {
        this.mappers = new ArrayList<>();
        mappers.forEach(this.mappers::add);
    }

    public UserTaskInstanceEntity mapTaskInstanceToEntity(UserTaskInstance userTaskInstance, UserTaskInstanceEntity entity) {
        entity.setId(userTaskInstance.getId());
        entity.setTaskName(userTaskInstance.getTaskName());
        entity.setTaskDescription(userTaskInstance.getTaskDescription());
        entity.setTaskPriority(userTaskInstance.getTaskPriority());
        entity.setStatus(userTaskInstance.getStatus().getName());
        entity.setTerminationType(userTaskInstance.getStatus().getTerminate() == null ? null : userTaskInstance.getStatus().getTerminate().name());
        entity.setExternalReferenceId(userTaskInstance.getExternalReferenceId());
        entity.setUserTaskId(userTaskInstance.getUserTaskId());

        TaskProcessInfoEntity processInfoEntity = entity.getProcessInfo();

        if (processInfoEntity == null) {
            processInfoEntity = new TaskProcessInfoEntity();
            entity.setProcessInfo(processInfoEntity);
        }

        ProcessInfo processInfo = userTaskInstance.getProcessInfo();
        if (processInfo != null) {
            processInfoEntity.setProcessInstanceId(processInfo.getProcessInstanceId());
            processInfoEntity.setProcessId(processInfo.getProcessId());
            processInfoEntity.setProcessVersion(processInfo.getProcessVersion());

            processInfoEntity.setParentProcessInstanceId(processInfo.getParentProcessInstanceId());
            processInfoEntity.setRootProcessInstanceId(processInfo.getRootProcessInstanceId());
            processInfoEntity.setRootProcessId(processInfo.getRootProcessId());
        }

        entity.setActualOwner(userTaskInstance.getActualOwner());
        entity.setPotentialUsers(Set.copyOf(userTaskInstance.getPotentialUsers()));
        entity.setPotentialGroups(Set.copyOf(userTaskInstance.getPotentialGroups()));
        entity.setAdminUsers(Set.copyOf(userTaskInstance.getAdminUsers()));
        entity.setAdminGroups(Set.copyOf(userTaskInstance.getAdminGroups()));
        entity.setExcludedUsers(Set.copyOf(userTaskInstance.getExcludedUsers()));

        mappers.forEach(e -> e.mapInstanceToEntity(userTaskInstance, entity));

        return entity;
    }

    public UserTaskInstance mapTaskEntityToInstance(UserTaskInstanceEntity entity) {

        DefaultUserTaskInstance instance = new DefaultUserTaskInstance();

        instance.setId(entity.getId());
        instance.setUserTaskId(entity.getUserTaskId());
        instance.setExternalReferenceId(entity.getExternalReferenceId());
        instance.setTaskName(entity.getTaskName());
        instance.setTaskDescription(entity.getTaskDescription());
        instance.setTaskPriority(entity.getTaskPriority());

        TaskProcessInfoEntity processInfoEntity = entity.getProcessInfo();

        if (processInfoEntity != null) {
            ProcessInfo processInfo = ProcessInfo.builder()
                    .withProcessInstanceId(processInfoEntity.getProcessInstanceId())
                    .withProcessId(processInfoEntity.getProcessId())
                    .withProcessVersion(processInfoEntity.getProcessVersion())
                    .withParentProcessInstanceId(processInfoEntity.getParentProcessInstanceId())
                    .withRootProcessInstanceId(processInfoEntity.getRootProcessInstanceId())
                    .withRootProcessId(processInfoEntity.getRootProcessId())
                    .build();

            instance.setProcessInfo(processInfo);
        }

        UserTaskState.TerminationType terminationType = entity.getTerminationType() == null ? null : UserTaskState.TerminationType.valueOf(entity.getTerminationType());
        instance.setStatus(UserTaskState.of(entity.getStatus(), terminationType));

        instance.setActualOwner(entity.getActualOwner());
        instance.setPotentialUsers(Set.copyOf(entity.getPotentialUsers()));
        instance.setPotentialGroups(Set.copyOf(entity.getPotentialGroups()));
        instance.setAdminUsers(Set.copyOf(entity.getAdminUsers()));
        instance.setAdminGroups(Set.copyOf(entity.getAdminGroups()));
        instance.setExcludedUsers(Set.copyOf(entity.getExcludedUsers()));

        mappers.forEach(e -> e.mapEntityToInstance(entity, instance));

        return instance;
    }
}
