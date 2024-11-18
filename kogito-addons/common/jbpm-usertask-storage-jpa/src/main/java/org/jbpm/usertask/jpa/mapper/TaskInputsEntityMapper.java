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
import org.jbpm.usertask.jpa.model.TaskInputEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.TaskInputRepository;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;

public class TaskInputsEntityMapper implements EntityMapper {

    private TaskInputRepository repository;

    public TaskInputsEntityMapper(TaskInputRepository repository) {
        this.repository = repository;
    }

    @Override
    public void mapInstanceToEntity(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskInstanceEntity) {
        Collection<TaskInputEntity> toRemove = userTaskInstanceEntity.getInputs()
                .stream()
                .filter(entity -> !userTaskInstance.getInputs().containsKey(entity.getName()))
                .toList();

        toRemove.forEach(input -> {
            repository.remove(input);
            userTaskInstanceEntity.removeInput(input);
        });

        userTaskInstance.getInputs().forEach((key, value) -> {
            TaskInputEntity inputEntity = userTaskInstanceEntity.getInputs().stream().filter(entity -> entity.getName().equals(key)).findFirst().orElseGet(() -> {
                TaskInputEntity entity = new TaskInputEntity();
                entity.setName(key);
                userTaskInstanceEntity.addInput(entity);
                return entity;
            });
            inputEntity.setName(key);
            if (Objects.nonNull(value)) {
                inputEntity.setValue(JSONUtils.valueToString(value).getBytes(StandardCharsets.UTF_8));
                inputEntity.setJavaType(value.getClass().getName());
            }
        });
    }

    @Override
    public void mapEntityToInstance(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        Map<String, Object> inputs = new HashMap<>();
        userTaskInstanceEntity.getInputs().forEach(taskInputEntity -> {
            String value = taskInputEntity.getValue() == null ? null : new String(taskInputEntity.getValue(), StandardCharsets.UTF_8);
            inputs.put(taskInputEntity.getName(), JSONUtils.stringTreeToValue(value, taskInputEntity.getJavaType()));
        });
        ((DefaultUserTaskInstance) userTaskInstance).setInputs(inputs);
    }
}
