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
package org.kie.kogito.index.event;

import java.util.Set;
import java.util.function.Function;

import org.kie.kogito.event.process.MilestoneEventBody;
import org.kie.kogito.event.process.NodeInstanceEventBody;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceError;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static org.kie.kogito.index.DateTimeUtils.toZonedDateTime;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

public class ProcessInstanceEventMapper implements Function<ProcessInstanceDataEvent, ProcessInstance> {

    @Override
    public ProcessInstance apply(ProcessInstanceDataEvent event) {
        if (event == null || event.getData() == null) {
            return null;
        }

        ProcessInstance pi = new ProcessInstance();
        pi.setId(event.getData().getId());
        pi.setProcessId(event.getData().getProcessId());
        pi.setProcessName(event.getData().getProcessName());
        pi.setRootProcessInstanceId(event.getData().getRootInstanceId());
        pi.setRootProcessId(event.getData().getRootProcessId());
        pi.setParentProcessInstanceId(event.getData().getParentInstanceId());
        pi.setRoles(event.getData().getRoles());
        pi.setVariables(getObjectMapper().valueToTree(event.getData().getVariables()));
        pi.setNodes(event.getData().getNodeInstances().stream().map(nodeInstance()).collect(toList()));
        pi.setState(event.getData().getState());
        pi.setStart(toZonedDateTime(event.getData().getStartDate()));
        pi.setEnd(toZonedDateTime(event.getData().getEndDate()));
        if (event.getData().getError() != null) {
            pi.setError(new ProcessInstanceError(event.getData().getError().getNodeDefinitionId(), event.getData().getError().getErrorMessage()));
        }
        pi.setMilestones(event.getData().getMilestones().stream().map(milestone()).collect(toList()));
        pi.setBusinessKey(event.getData().getBusinessKey());
        pi.setAddons(isNullOrEmpty(event.getKogitoAddons()) ? null : Set.of(event.getKogitoAddons().split(",")));
        pi.setEndpoint(event.getSource() == null ? null : event.getSource().toString());
        pi.setLastUpdate(toZonedDateTime(event.getTime()));
        pi.setVersion(event.getData().getVersion());
        pi.setDefinition(definition().apply(event));
        pi.setUpdatedBy(event.getData().getIdentity());
        return pi;
    }

    private Function<ProcessInstanceDataEvent, ProcessDefinition> definition() {
        return event -> {
            ProcessDefinition pd = new ProcessDefinition();
            pd.setId(event.getData().getProcessId());
            pd.setName(event.getData().getProcessName());
            pd.setVersion(event.getData().getVersion());
            pd.setAddons(isNullOrEmpty(event.getKogitoAddons()) ? null : Set.of(event.getKogitoAddons().split(",")));
            pd.setRoles(event.getData().getRoles());
            pd.setType(event.getKogitoProcessType());
            pd.setEndpoint(event.getSource() == null ? null : event.getSource().toString());
            return pd;
        };
    }

    private Function<NodeInstanceEventBody, NodeInstance> nodeInstance() {
        return nib -> {
            NodeInstance ni = new NodeInstance();
            ni.setId(nib.getId());
            ni.setEnter(toZonedDateTime(nib.getTriggerTime()));
            ni.setName(nib.getNodeName());
            ni.setType(nib.getNodeType());
            ni.setNodeId(nib.getNodeId());
            ni.setDefinitionId(nib.getNodeDefinitionId());
            ni.setExit(toZonedDateTime(nib.getLeaveTime()));
            return ni;
        };
    }

    private Function<MilestoneEventBody, Milestone> milestone() {
        return m -> Milestone.builder()
                .id(m.getId())
                .name(m.getName())
                .status(m.getStatus())
                .build();
    }
}
