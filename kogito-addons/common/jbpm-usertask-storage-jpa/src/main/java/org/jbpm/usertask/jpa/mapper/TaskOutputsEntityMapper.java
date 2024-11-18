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

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jbpm.usertask.jpa.mapper.json.utils.JSONUtils;
import org.jbpm.usertask.jpa.model.TaskOutputEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.TaskOutputRepository;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;

public class TaskOutputsEntityMapper implements EntityMapper {

    private final TaskOutputRepository repository;

    public TaskOutputsEntityMapper(TaskOutputRepository repository) {
        this.repository = repository;
    }

    @Override
    public void mapInstanceToEntity(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskInstanceEntity) {
        Collection<TaskOutputEntity> toRemove = userTaskInstanceEntity.getOutputs()
                .stream()
                .filter(entity -> !userTaskInstance.getOutputs().containsKey(entity.getName()))
                .toList();

        toRemove.forEach(output -> {
            repository.remove(output);
            userTaskInstanceEntity.removeOutput(output);
        });

        userTaskInstance.getOutputs().forEach((key, value) -> {
            TaskOutputEntity outputEntity = userTaskInstanceEntity.getOutputs().stream().filter(entity -> entity.getName().equals(key)).findFirst().orElseGet(() -> {
                TaskOutputEntity entity = new TaskOutputEntity();
                entity.setName(key);
                userTaskInstanceEntity.addOutput(entity);
                return entity;
            });
            outputEntity.setName(key);
            if (Objects.nonNull(value)) {
                outputEntity.setValue(JSONUtils.valueToString(value).getBytes(StandardCharsets.UTF_8));
                outputEntity.setJavaType(value.getClass().getName());
            }
        });
    }

    @Override
    public void mapEntityToInstance(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        Map<String, Object> outputs = new HashMap<>();
        userTaskInstanceEntity.getOutputs().forEach(taskOutputEntity -> {
            String value = taskOutputEntity.getValue() == null ? null : new String(taskOutputEntity.getValue(), StandardCharsets.UTF_8);
            outputs.put(taskOutputEntity.getName(), JSONUtils.stringTreeToValue(value, taskOutputEntity.getJavaType()));
        });
        ((DefaultUserTaskInstance) userTaskInstance).setOutputs(outputs);
    }
}
