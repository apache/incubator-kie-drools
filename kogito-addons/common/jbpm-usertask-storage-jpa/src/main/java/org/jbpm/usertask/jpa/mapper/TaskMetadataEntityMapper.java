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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jbpm.usertask.jpa.mapper.json.utils.JSONUtils;
import org.jbpm.usertask.jpa.model.TaskMetadataEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.TaskMetadataRepository;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;

public class TaskMetadataEntityMapper implements EntityMapper {

    private final TaskMetadataRepository repository;

    public TaskMetadataEntityMapper(TaskMetadataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void mapInstanceToEntity(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskInstanceEntity) {
        Collection<TaskMetadataEntity> toRemove = userTaskInstanceEntity.getMetadata()
                .stream()
                .filter(entity -> !userTaskInstance.getMetadata().containsKey(entity.getName()))
                .toList();

        toRemove.forEach(metadata -> {
            repository.remove(metadata);
            userTaskInstanceEntity.removeMetadata(metadata);
        });

        userTaskInstance.getMetadata().forEach((key, value) -> {
            TaskMetadataEntity metadataEntity = userTaskInstanceEntity.getMetadata().stream().filter(entity -> entity.getName().equals(key)).findFirst().orElseGet(() -> {
                TaskMetadataEntity entity = new TaskMetadataEntity();
                entity.setName(key);
                userTaskInstanceEntity.addMetadata(entity);
                return entity;
            });

            if (Objects.nonNull(value)) {
                metadataEntity.setValue(JSONUtils.valueToString(value));
                metadataEntity.setJavaType(value.getClass().getName());
            }
            repository.persist(metadataEntity);
        });
    }

    @Override
    public void mapEntityToInstance(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        Map<String, Object> metadata = new HashMap<>();
        userTaskInstanceEntity.getMetadata().forEach(metadataEntry -> {
            metadata.put(metadataEntry.getName(), JSONUtils.stringTreeToValue(metadataEntry.getValue(), metadataEntry.getJavaType()));
        });
        ((DefaultUserTaskInstance) userTaskInstance).setMetadata(metadata);
    }
}
