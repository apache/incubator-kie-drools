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
package org.kie.kogito.index.jpa.storage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kie.kogito.event.process.MultipleProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorEventBody;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLAEventBody;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;
import org.kie.kogito.index.CommonUtils;
import org.kie.kogito.index.jpa.mapper.ProcessInstanceEntityMapper;
import org.kie.kogito.index.jpa.model.MilestoneEntity;
import org.kie.kogito.index.jpa.model.NodeInstanceEntity;
import org.kie.kogito.index.jpa.model.ProcessInstanceEntity;
import org.kie.kogito.index.jpa.model.ProcessInstanceEntityRepository;
import org.kie.kogito.index.jpa.model.ProcessInstanceErrorEntity;
import org.kie.kogito.index.json.JsonUtils;
import org.kie.kogito.index.model.MilestoneStatus;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.storage.ProcessInstanceStorage;
import org.kie.kogito.persistence.api.StorageServiceCapability;

import io.quarkus.arc.DefaultBean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_ENTER;
import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_EXIT;
import static org.kie.kogito.index.DateTimeUtils.toZonedDateTime;

@ApplicationScoped
@DefaultBean
public class ProcessInstanceEntityStorage extends AbstractJPAStorageFetcher<String, ProcessInstanceEntity, ProcessInstance> implements ProcessInstanceStorage {

    protected ProcessInstanceEntityStorage() {
    }

    @Inject
    public ProcessInstanceEntityStorage(ProcessInstanceEntityRepository repository, ProcessInstanceEntityMapper mapper) {
        super(repository, ProcessInstanceEntity.class, mapper::mapToModel);
    }

    @Override
    @Transactional
    public void indexGroup(MultipleProcessInstanceDataEvent events) {
        Map<String, ProcessInstanceEntity> piMap = new HashMap<>();
        for (ProcessInstanceDataEvent<?> event : events.getData()) {
            indexEvent(piMap.computeIfAbsent(event.getKogitoProcessInstanceId(), id -> findOrInit(event)), event);
        }
    }

    @Override
    @Transactional
    public void indexError(ProcessInstanceErrorDataEvent event) {
        indexError(findOrInit(event), event.getData());
    }

    @Override
    @Transactional
    public void indexNode(ProcessInstanceNodeDataEvent event) {
        indexNode(findOrInit(event), event.getData());
    }

    @Override
    @Transactional
    public void indexSLA(ProcessInstanceSLADataEvent event) {
        indexSla(findOrInit(event), event.getData());
    }

    @Override
    @Transactional
    public void indexState(ProcessInstanceStateDataEvent event) {
        indexState(findOrInit(event), event);
    }

    @Override
    @Transactional
    public void indexVariable(ProcessInstanceVariableDataEvent event) {
        indexVariable(findOrInit(event), event.getData());
    }

    private ProcessInstanceEntity findOrInit(ProcessInstanceDataEvent<?> event) {
        return repository.findByIdOptional(event.getKogitoProcessInstanceId()).orElseGet(() -> {
            ProcessInstanceEntity pi = new ProcessInstanceEntity();
            pi.setProcessId(event.getKogitoProcessId());
            pi.setVersion(event.getKogitoProcessInstanceVersion());
            pi.setId(event.getKogitoProcessInstanceId());
            pi.setLastUpdate(toZonedDateTime(event.getTime()));
            pi.setNodes(new ArrayList<>());
            pi.setMilestones(new ArrayList<>());
            repository.persist(pi);
            return pi;
        });
    }

    private void indexEvent(ProcessInstanceEntity pi, ProcessInstanceDataEvent<?> event) {
        if (event instanceof ProcessInstanceErrorDataEvent) {
            indexError(pi, ((ProcessInstanceErrorDataEvent) event).getData());
        } else if (event instanceof ProcessInstanceNodeDataEvent) {
            indexNode(pi, ((ProcessInstanceNodeDataEvent) event).getData());
        } else if (event instanceof ProcessInstanceSLADataEvent) {
            indexSla(pi, ((ProcessInstanceSLADataEvent) event).getData());
        } else if (event instanceof ProcessInstanceStateDataEvent) {
            indexState(pi, (ProcessInstanceStateDataEvent) event);
        } else if (event instanceof ProcessInstanceVariableDataEvent) {
            indexVariable(pi, ((ProcessInstanceVariableDataEvent) event).getData());
        }
    }

    private void indexError(ProcessInstanceEntity pi, ProcessInstanceErrorEventBody error) {
        ProcessInstanceErrorEntity errorEntity = pi.getError();
        if (errorEntity == null) {
            errorEntity = new ProcessInstanceErrorEntity();
            pi.setError(errorEntity);
        }
        errorEntity.setMessage(error.getErrorMessage());
        errorEntity.setNodeDefinitionId(error.getNodeDefinitionId());
        pi.setState(CommonUtils.ERROR_STATE);
    }

    private void indexNode(ProcessInstanceEntity pi, ProcessInstanceNodeEventBody data) {
        pi.getNodes().stream().filter(n -> n.getId().equals(data.getNodeInstanceId())).findAny().ifPresentOrElse(n -> updateNode(n, data), () -> createNode(pi, data));
        if ("MilestoneNode".equals(data.getNodeType())) {
            pi.getMilestones().stream().filter(n -> n.getId().equals(data.getNodeInstanceId())).findAny().ifPresentOrElse(n -> updateMilestone(n, data), () -> createMilestone(pi, data));
        }
    }

    private MilestoneEntity createMilestone(ProcessInstanceEntity pi, ProcessInstanceNodeEventBody data) {
        MilestoneEntity milestone = new MilestoneEntity();
        milestone.setProcessInstance(pi);
        pi.getMilestones().add(milestone);
        return updateMilestone(milestone, data);
    }

    private MilestoneEntity updateMilestone(MilestoneEntity milestone, ProcessInstanceNodeEventBody body) {
        milestone.setId(body.getNodeInstanceId());
        milestone.setName(body.getNodeName());
        milestone.setStatus(body.getEventType() == EVENT_TYPE_EXIT ? MilestoneStatus.COMPLETED.name() : MilestoneStatus.ACTIVE.name());
        return milestone;
    }

    private NodeInstanceEntity createNode(ProcessInstanceEntity pi, ProcessInstanceNodeEventBody data) {
        NodeInstanceEntity node = new NodeInstanceEntity();
        node.setProcessInstance(pi);
        updateNode(node, data);
        pi.getNodes().add(node);
        return node;
    }

    private NodeInstanceEntity updateNode(NodeInstanceEntity nodeInstance, ProcessInstanceNodeEventBody body) {
        nodeInstance.setDefinitionId(body.getNodeDefinitionId());
        nodeInstance.setId(body.getNodeInstanceId());
        nodeInstance.setNodeId(body.getNodeDefinitionId());
        nodeInstance.setName(body.getNodeName());
        nodeInstance.setType(body.getNodeType());
        nodeInstance.setSlaDueDate(toZonedDateTime(body.getSlaDueDate()));
        ZonedDateTime eventDate = toZonedDateTime(body.getEventDate());
        switch (body.getEventType()) {
            case EVENT_TYPE_ENTER:
                nodeInstance.setEnter(eventDate);
                break;
            case EVENT_TYPE_EXIT:
                nodeInstance.setExit(eventDate);
            default:
                if (nodeInstance.getEnter() == null) {
                    // Adding a default enter time for exit events triggered by EventNodeInstances
                    nodeInstance.setEnter(eventDate);
                }
        }
        return nodeInstance;
    }

    private void indexState(ProcessInstanceEntity pi, ProcessInstanceStateDataEvent event) {
        indexState(pi, event.getData(), (event.getKogitoAddons() == null || event.getKogitoAddons().isEmpty()) ? Set.of() : Set.of(event.getKogitoAddons().split(",")),
                event.getSource() == null ? null : event.getSource().toString());
    }

    private void indexState(ProcessInstanceEntity pi, ProcessInstanceStateEventBody data, Set<String> addons, String endpoint) {
        pi.setVersion(data.getProcessVersion());
        pi.setProcessName(data.getProcessName());
        pi.setRootProcessInstanceId(data.getRootProcessInstanceId());
        pi.setRootProcessId(data.getRootProcessId());
        pi.setParentProcessInstanceId(data.getParentInstanceId());
        pi.setRoles(data.getRoles());
        pi.setState(data.getState());
        if (data.getEventType() == null || data.getEventType() == ProcessInstanceStateEventBody.EVENT_TYPE_STARTED) {
            pi.setStart(toZonedDateTime(data.getEventDate()));
            pi.setCreatedBy(data.getEventUser());
        } else if (data.getEventType() == ProcessInstanceStateEventBody.EVENT_TYPE_ENDED) {
            pi.setEnd(toZonedDateTime(data.getEventDate()));
        }
        pi.setBusinessKey(data.getBusinessKey());
        pi.setUpdatedBy(data.getEventUser());
        pi.setLastUpdate(toZonedDateTime(data.getEventDate()));
        pi.setAddons(addons);
        pi.setEndpoint(endpoint);
        pi.setSlaDueDate(toZonedDateTime(data.getSlaDueDate()));
    }

    private void indexVariable(ProcessInstanceEntity pi, ProcessInstanceVariableEventBody data) {
        pi.setVariables(JsonUtils.mergeVariable(data.getVariableName(), data.getVariableValue(), pi.getVariables()));
    }

    private void indexSla(ProcessInstanceEntity orInit, ProcessInstanceSLAEventBody data) {
        // SLA does nothing for now
    }

    public Set<StorageServiceCapability> capabilities() {
        return EnumSet.of(StorageServiceCapability.COUNT);
    }
}
