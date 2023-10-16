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
package org.kie.kogito.index.mongodb.model;

import java.util.Optional;

import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceError;
import org.kie.kogito.persistence.mongodb.model.ModelUtils;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.documentToJsonNode;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.instantToZonedDateTime;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.jsonNodeToDocument;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.zonedDateTimeToInstant;

public class ProcessInstanceEntityMapper implements MongoEntityMapper<ProcessInstance, ProcessInstanceEntity> {

    static final String NODES_ID_ATTRIBUTE = "nodes.id";

    static final String MONGO_NODES_ID_ATTRIBUTE = "nodes." + MONGO_ID;

    static final String MILESTONES_ID_ATTRIBUTE = "milestones.id";

    static final String MONGO_MILESTONES_ID_ATTRIBUTE = "milestones." + MONGO_ID;

    @Override
    public Class<ProcessInstanceEntity> getEntityClass() {
        return ProcessInstanceEntity.class;
    }

    @Override
    public ProcessInstanceEntity mapToEntity(String key, ProcessInstance instance) {
        if (instance == null) {
            return null;
        }

        ProcessInstanceEntity entity = new ProcessInstanceEntity();
        entity.setId(instance.getId());
        entity.setProcessId(instance.getProcessId());
        entity.setRoles(instance.getRoles());
        entity.setVariables(jsonNodeToDocument(instance.getVariables()));
        entity.setEndpoint(instance.getEndpoint());
        entity.setNodes(Optional.ofNullable(instance.getNodes()).map(nodes -> nodes.stream().map(this::fromNodeInstance).collect(toList())).orElse(null));
        entity.setState(instance.getState());
        entity.setStart(zonedDateTimeToInstant(instance.getStart()));
        entity.setEnd(zonedDateTimeToInstant(instance.getEnd()));
        entity.setRootProcessId(instance.getRootProcessId());
        entity.setRootProcessInstanceId(instance.getRootProcessInstanceId());
        entity.setParentProcessInstanceId(instance.getParentProcessInstanceId());
        entity.setProcessName(instance.getProcessName());
        entity.setError(Optional.ofNullable(instance.getError()).map(this::fromProcessInstanceError).orElse(null));
        entity.setAddons(instance.getAddons());
        entity.setLastUpdate(zonedDateTimeToInstant(instance.getLastUpdate()));
        entity.setBusinessKey(instance.getBusinessKey());
        entity.setMilestones(Optional.ofNullable(instance.getMilestones()).map(milestones -> milestones.stream().map(this::fromMilestone).collect(toList())).orElse(null));
        entity.setVersion(instance.getVersion());
        entity.setCreatedBy(instance.getCreatedBy());
        entity.setUpdatedBy(instance.getUpdatedBy());
        return entity;
    }

    @Override
    public ProcessInstance mapToModel(ProcessInstanceEntity entity) {
        if (entity == null) {
            return null;
        }

        ProcessInstance instance = new ProcessInstance();
        instance.setId(entity.getId());
        instance.setProcessId(entity.getProcessId());
        instance.setRoles(entity.getRoles());
        instance.setVariables(documentToJsonNode(entity.getVariables()));
        instance.setEndpoint(entity.getEndpoint());
        instance.setNodes(Optional.ofNullable(entity.getNodes()).map(nodes -> nodes.stream().map(this::toNodeInstance).collect(toList())).orElse(null));
        instance.setState(entity.getState());
        instance.setStart(instantToZonedDateTime(entity.getStart()));
        instance.setEnd(instantToZonedDateTime(entity.getEnd()));
        instance.setRootProcessId(entity.getRootProcessId());
        instance.setRootProcessInstanceId(entity.getRootProcessInstanceId());
        instance.setParentProcessInstanceId(entity.getParentProcessInstanceId());
        instance.setProcessName(entity.getProcessName());
        instance.setError(Optional.ofNullable(entity.getError()).map(this::toProcessInstanceError).orElse(null));
        instance.setAddons(entity.getAddons());
        instance.setLastUpdate(instantToZonedDateTime(entity.getLastUpdate()));
        instance.setBusinessKey(entity.getBusinessKey());
        instance.setMilestones(Optional.ofNullable(entity.getMilestones()).map(milestones -> milestones.stream().map(this::toMilestone).collect(toList())).orElse(null));
        instance.setVersion(entity.getVersion());
        instance.setCreatedBy(entity.getCreatedBy());
        instance.setUpdatedBy(entity.getCreatedBy());
        return instance;
    }

    @Override
    public String convertToMongoAttribute(String attribute) {
        if (NODES_ID_ATTRIBUTE.equals(attribute)) {
            return MONGO_NODES_ID_ATTRIBUTE;
        }
        if (MILESTONES_ID_ATTRIBUTE.equals(attribute)) {
            return MONGO_MILESTONES_ID_ATTRIBUTE;
        }
        return MongoEntityMapper.super.convertToMongoAttribute(attribute);
    }

    @Override
    public String convertToModelAttribute(String attribute) {
        if (MONGO_NODES_ID_ATTRIBUTE.equals(attribute)) {
            return ModelUtils.ID;
        }
        if (MONGO_MILESTONES_ID_ATTRIBUTE.equals(attribute)) {
            return ModelUtils.ID;
        }
        return MongoEntityMapper.super.convertToModelAttribute(attribute);
    }

    NodeInstance toNodeInstance(ProcessInstanceEntity.NodeInstanceEntity entity) {
        if (entity == null) {
            return null;
        }

        NodeInstance instance = new NodeInstance();
        instance.setId(entity.getId());
        instance.setName(entity.getName());
        instance.setNodeId(entity.getNodeId());
        instance.setType(entity.getType());
        instance.setEnter(instantToZonedDateTime(entity.getEnter()));
        instance.setExit(instantToZonedDateTime(entity.getExit()));
        instance.setDefinitionId(entity.getDefinitionId());
        return instance;
    }

    ProcessInstanceEntity.NodeInstanceEntity fromNodeInstance(NodeInstance instance) {
        if (instance == null) {
            return null;
        }

        ProcessInstanceEntity.NodeInstanceEntity entity = new ProcessInstanceEntity.NodeInstanceEntity();
        entity.setId(instance.getId());
        entity.setName(instance.getName());
        entity.setNodeId(instance.getNodeId());
        entity.setType(instance.getType());
        entity.setEnter(zonedDateTimeToInstant(instance.getEnter()));
        entity.setExit(zonedDateTimeToInstant(instance.getExit()));
        entity.setDefinitionId(instance.getDefinitionId());
        return entity;
    }

    ProcessInstanceError toProcessInstanceError(ProcessInstanceEntity.ProcessInstanceErrorEntity entity) {
        if (entity == null) {
            return null;
        }

        ProcessInstanceError error = new ProcessInstanceError();
        error.setNodeDefinitionId(entity.getNodeDefinitionId());
        error.setMessage(entity.getMessage());
        return error;
    }

    ProcessInstanceEntity.ProcessInstanceErrorEntity fromProcessInstanceError(ProcessInstanceError error) {
        if (error == null) {
            return null;
        }

        ProcessInstanceEntity.ProcessInstanceErrorEntity entity = new ProcessInstanceEntity.ProcessInstanceErrorEntity();
        entity.setNodeDefinitionId(error.getNodeDefinitionId());
        entity.setMessage(error.getMessage());
        return entity;
    }

    Milestone toMilestone(ProcessInstanceEntity.MilestoneEntity entity) {
        if (entity == null) {
            return null;
        }

        Milestone milestone = new Milestone();
        milestone.setId(entity.getId());
        milestone.setName(entity.getName());
        milestone.setStatus(entity.getStatus());
        return milestone;
    }

    ProcessInstanceEntity.MilestoneEntity fromMilestone(Milestone milestone) {
        if (milestone == null) {
            return null;
        }

        ProcessInstanceEntity.MilestoneEntity entity = new ProcessInstanceEntity.MilestoneEntity();
        entity.setId(milestone.getId());
        entity.setName(milestone.getName());
        entity.setStatus(milestone.getStatus());
        return entity;
    }
}
