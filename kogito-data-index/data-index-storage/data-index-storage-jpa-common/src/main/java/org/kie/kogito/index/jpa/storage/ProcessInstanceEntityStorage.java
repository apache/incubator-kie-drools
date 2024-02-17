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
import java.util.Set;

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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_ENTER;
import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_EXIT;
import static org.kie.kogito.index.DateTimeUtils.toZonedDateTime;

@ApplicationScoped
public class ProcessInstanceEntityStorage extends AbstractJPAStorageFetcher<String, ProcessInstanceEntity, ProcessInstance> implements ProcessInstanceStorage {

    protected ProcessInstanceEntityStorage() {
    }

    @Inject
    public ProcessInstanceEntityStorage(ProcessInstanceEntityRepository repository, ProcessInstanceEntityMapper mapper) {
        super(repository, ProcessInstanceEntity.class, mapper::mapToModel);
    }

    @Override
    @Transactional
    public void indexError(ProcessInstanceErrorDataEvent event) {
        indexError(event.getData());
    }

    @Override
    @Transactional
    public void indexNode(ProcessInstanceNodeDataEvent event) {
        indexNode(event.getData());
    }

    @Override
    @Transactional
    public void indexSLA(ProcessInstanceSLADataEvent event) {
        indexSLA(event.getData());

    }

    @Override
    @Transactional
    public void indexState(ProcessInstanceStateDataEvent event) {
        indexState(event.getData(), event.getKogitoAddons() == null ? Set.of() : Set.of(event.getKogitoAddons().split(",")), event.getSource() == null ? null : event.getSource().toString());
    }

    @Override
    @Transactional
    public void indexVariable(ProcessInstanceVariableDataEvent event) {
        indexVariable(event.getData());
    }

    private ProcessInstanceEntity findOrInit(String processId, String processInstanceId) {
        return repository.findByIdOptional(processInstanceId).orElseGet(() -> {
            ProcessInstanceEntity pi = new ProcessInstanceEntity();
            pi.setProcessId(processId);
            pi.setId(processInstanceId);
            pi.setNodes(new ArrayList<>());
            pi.setMilestones(new ArrayList<>());
            repository.persist(pi);
            return pi;
        });
    }

    private void indexError(ProcessInstanceErrorEventBody error) {
        ProcessInstanceEntity pi = findOrInit(error.getProcessId(), error.getProcessInstanceId());
        ProcessInstanceErrorEntity errorEntity = pi.getError();
        if (errorEntity == null) {
            errorEntity = new ProcessInstanceErrorEntity();
            pi.setError(errorEntity);
        }
        errorEntity.setMessage(error.getErrorMessage());
        errorEntity.setNodeDefinitionId(error.getNodeDefinitionId());
        repository.flush();
    }

    private void indexNode(ProcessInstanceNodeEventBody data) {
        ProcessInstanceEntity pi = findOrInit(data.getProcessId(), data.getProcessInstanceId());
        pi.getNodes().stream().filter(n -> n.getId().equals(data.getNodeInstanceId())).findAny().ifPresentOrElse(n -> updateNode(n, data), () -> createNode(pi, data));
        if ("MilestoneNode".equals(data.getNodeType())) {
            pi.getMilestones().stream().filter(n -> n.getId().equals(data.getNodeInstanceId())).findAny().ifPresentOrElse(n -> updateMilestone(n, data), () -> createMilestone(pi, data));
        }
        repository.flush();
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
        pi.getNodes().add(node);
        node.setProcessInstance(pi);
        return updateNode(node, data);
    }

    private NodeInstanceEntity updateNode(NodeInstanceEntity nodeInstance, ProcessInstanceNodeEventBody body) {
        nodeInstance.setDefinitionId(body.getNodeDefinitionId());
        nodeInstance.setId(body.getNodeInstanceId());
        nodeInstance.setNodeId(body.getNodeDefinitionId());
        nodeInstance.setName(body.getNodeName());
        nodeInstance.setType(body.getNodeType());
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

    private void indexSLA(ProcessInstanceSLAEventBody data) {
        findOrInit(data.getProcessId(), data.getProcessInstanceId());
        repository.flush();
    }

    private void indexState(ProcessInstanceStateEventBody data, Set<String> addons, String endpoint) {
        ProcessInstanceEntity pi = findOrInit(data.getProcessId(), data.getProcessInstanceId());
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
        repository.flush();
    }

    private void indexVariable(ProcessInstanceVariableEventBody data) {
        ProcessInstanceEntity pi = findOrInit(data.getProcessId(), data.getProcessInstanceId());
        pi.setVariables(JsonUtils.mergeVariable(data.getVariableName(), data.getVariableValue(), pi.getVariables()));
        repository.flush();
    }

}
