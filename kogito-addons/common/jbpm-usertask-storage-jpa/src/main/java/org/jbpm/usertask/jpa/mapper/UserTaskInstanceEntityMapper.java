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

import java.util.Set;

import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.lifecycle.UserTaskState;

public class UserTaskInstanceEntityMapper {

    private final AttachmentsEntityMapper attachmentsMapper;
    private final CommentsEntityMapper commentMapper;
    private final TaskInputsEntityMapper taskInputsMapper;
    private final TaskOutputsEntityMapper taskOutputsMapper;
    private final TaskMetadataEntityMapper taskMetadataMapper;

    public UserTaskInstanceEntityMapper(AttachmentsEntityMapper attachmentsMapper, CommentsEntityMapper commentsMapper, TaskMetadataEntityMapper taskMetadataMapper,
            TaskInputsEntityMapper taskInputsMapper, TaskOutputsEntityMapper taskOutputMapper) {
        this.attachmentsMapper = attachmentsMapper;
        this.commentMapper = commentsMapper;
        this.taskMetadataMapper = taskMetadataMapper;
        this.taskInputsMapper = taskInputsMapper;
        this.taskOutputsMapper = taskOutputMapper;
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

        entity.setActualOwner(userTaskInstance.getActualOwner());
        entity.setPotentialUsers(Set.copyOf(userTaskInstance.getPotentialUsers()));
        entity.setPotentialGroups(Set.copyOf(userTaskInstance.getPotentialGroups()));
        entity.setAdminUsers(Set.copyOf(userTaskInstance.getAdminUsers()));
        entity.setAdminGroups(Set.copyOf(userTaskInstance.getAdminGroups()));
        entity.setExcludedUsers(Set.copyOf(userTaskInstance.getExcludedUsers()));

        attachmentsMapper.mapInstanceToEntity(userTaskInstance, entity);
        commentMapper.mapInstanceToEntity(userTaskInstance, entity);
        taskInputsMapper.mapInstanceToEntity(userTaskInstance, entity);
        taskOutputsMapper.mapInstanceToEntity(userTaskInstance, entity);
        taskMetadataMapper.mapInstanceToEntity(userTaskInstance, entity);

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

        UserTaskState.TerminationType terminationType = entity.getTerminationType() == null ? null : UserTaskState.TerminationType.valueOf(entity.getTerminationType());
        instance.setStatus(UserTaskState.of(entity.getStatus(), terminationType));

        instance.setActualOwner(entity.getActualOwner());
        instance.setPotentialUsers(Set.copyOf(entity.getPotentialUsers()));
        instance.setPotentialGroups(Set.copyOf(entity.getPotentialGroups()));
        instance.setAdminUsers(Set.copyOf(entity.getAdminUsers()));
        instance.setAdminGroups(Set.copyOf(entity.getAdminGroups()));
        instance.setExcludedUsers(Set.copyOf(entity.getExcludedUsers()));

        attachmentsMapper.mapEntityToInstance(entity, instance);
        commentMapper.mapEntityToInstance(entity, instance);
        taskInputsMapper.mapEntityToInstance(entity, instance);
        taskOutputsMapper.mapEntityToInstance(entity, instance);
        taskMetadataMapper.mapEntityToInstance(entity, instance);

        return instance;
    }
}
