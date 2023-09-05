/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;

public class ProcessDefinitionEntityMapper implements MongoEntityMapper<ProcessDefinition, ProcessDefinitionEntity> {

    @Override
    public Class<ProcessDefinitionEntity> getEntityClass() {
        return ProcessDefinitionEntity.class;
    }

    @Override
    public ProcessDefinitionEntity mapToEntity(String key, ProcessDefinition pd) {
        if (pd == null) {
            return null;
        }

        ProcessDefinitionEntity entity = new ProcessDefinitionEntity();
        entity.setKey(pd.getKey());
        entity.setId(pd.getId());
        entity.setVersion(pd.getVersion());
        entity.setName(pd.getName());
        entity.setRoles(pd.getRoles());
        entity.setAddons(pd.getAddons());
        entity.setType(pd.getType());
        entity.setEndpoint(pd.getEndpoint());
        entity.setSource(pd.getSource() == null ? null : pd.getSource().getBytes());
        return entity;
    }

    @Override
    public ProcessDefinition mapToModel(ProcessDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        ProcessDefinition pd = new ProcessDefinition();
        pd.setId(entity.getId());
        pd.setVersion(entity.getVersion());
        pd.setName(entity.getName());
        pd.setRoles(entity.getRoles());
        pd.setAddons(entity.getAddons());
        pd.setType(entity.getType());
        pd.setEndpoint(entity.getEndpoint());
        pd.setSource(entity.getSource() == null ? null : new String(entity.getSource()));
        return pd;
    }

    @Override
    public String convertToMongoAttribute(String attribute) {
        return attribute;
    }

    @Override
    public String convertToModelAttribute(String attribute) {
        return attribute;
    }
}