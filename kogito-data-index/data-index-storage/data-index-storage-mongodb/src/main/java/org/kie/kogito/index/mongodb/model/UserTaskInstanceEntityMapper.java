/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.index.mongodb.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;

import static org.kie.kogito.persistence.mongodb.model.ModelUtils.documentToJsonNode;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.instantToZonedDateTime;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.jsonNodeToDocument;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.zonedDateTimeToInstant;

public class UserTaskInstanceEntityMapper implements MongoEntityMapper<UserTaskInstance, UserTaskInstanceEntity> {

    @Override
    public Class<UserTaskInstanceEntity> getEntityClass() {
        return UserTaskInstanceEntity.class;
    }

    @Override
    public UserTaskInstanceEntity mapToEntity(String key, UserTaskInstance instance) {
        if (instance == null) {
            return null;
        }

        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();
        entity.setId(instance.getId());
        entity.setDescription(instance.getDescription());
        entity.setName(instance.getName());
        entity.setPriority(instance.getPriority());
        entity.setProcessInstanceId(instance.getProcessInstanceId());
        entity.setState(instance.getState());
        entity.setActualOwner(instance.getActualOwner());
        entity.setAdminGroups(instance.getAdminGroups());
        entity.setAdminUsers(instance.getAdminUsers());
        entity.setCompleted(zonedDateTimeToInstant(instance.getCompleted()));
        entity.setStarted(zonedDateTimeToInstant(instance.getStarted()));
        entity.setExcludedUsers(instance.getExcludedUsers());
        entity.setPotentialGroups(instance.getPotentialGroups());
        entity.setPotentialUsers(instance.getPotentialUsers());
        entity.setReferenceName(instance.getReferenceName());
        entity.setLastUpdate(zonedDateTimeToInstant(instance.getLastUpdate()));
        entity.setProcessId(instance.getProcessId());
        entity.setRootProcessId(instance.getRootProcessId());
        entity.setRootProcessInstanceId(instance.getRootProcessInstanceId());
        entity.setInputs(jsonNodeToDocument(instance.getInputs()));
        entity.setOutputs(jsonNodeToDocument(instance.getOutputs()));
        return entity;
    }

    @Override
    public UserTaskInstance mapToModel(UserTaskInstanceEntity entity) {
        if (entity == null) {
            return null;
        }

        UserTaskInstance instance = new UserTaskInstance();
        instance.setId(entity.getId());
        instance.setDescription(entity.getDescription());
        instance.setName(entity.getName());
        instance.setPriority(entity.getPriority());
        instance.setProcessInstanceId(entity.getProcessInstanceId());
        instance.setState(entity.getState());
        instance.setActualOwner(entity.getActualOwner());
        instance.setAdminGroups(entity.getAdminGroups());
        instance.setAdminUsers(entity.getAdminUsers());
        instance.setCompleted(instantToZonedDateTime(entity.getCompleted()));
        instance.setStarted(instantToZonedDateTime(entity.getStarted()));
        instance.setExcludedUsers(entity.getExcludedUsers());
        instance.setPotentialGroups(entity.getPotentialGroups());
        instance.setPotentialUsers(entity.getPotentialUsers());
        instance.setReferenceName(entity.getReferenceName());
        instance.setLastUpdate(instantToZonedDateTime(entity.getLastUpdate()));
        instance.setProcessId(entity.getProcessId());
        instance.setRootProcessId(entity.getRootProcessId());
        instance.setRootProcessInstanceId(entity.getRootProcessInstanceId());
        instance.setInputs(documentToJsonNode(entity.getInputs(), JsonNode.class));
        instance.setOutputs(documentToJsonNode(entity.getOutputs(), JsonNode.class));
        return instance;
    }
}
