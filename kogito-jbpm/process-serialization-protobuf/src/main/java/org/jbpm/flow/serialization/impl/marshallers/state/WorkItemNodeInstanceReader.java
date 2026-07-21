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
package org.jbpm.flow.serialization.impl.marshallers.state;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.jbpm.flow.serialization.MarshallerContextName;
import org.jbpm.flow.serialization.MarshallerReaderContext;
import org.jbpm.flow.serialization.NodeInstanceReader;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerException;
import org.jbpm.flow.serialization.impl.ProtobufVariableReader;
import org.jbpm.flow.serialization.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;
import org.kie.kogito.process.workitems.impl.KogitoWorkItemImpl;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;

public class WorkItemNodeInstanceReader implements NodeInstanceReader {

    @Override
    public boolean accept(Any value) {
        return value.is(WorkItemNodeInstanceContent.class);
    }

    @Override
    public Class<? extends GeneratedMessageV3> type() {
        return WorkItemNodeInstanceContent.class;
    }

    @Override
    public NodeInstance read(MarshallerReaderContext context, Any value) {
        try {
            ProtobufVariableReader varReader = new ProtobufVariableReader(context);
            WorkItemNodeInstanceContent content = value.unpack(WorkItemNodeInstanceContent.class);
            WorkItemNodeInstance nodeInstance = instanceWorkItem(content.getName());
            RuleFlowProcessInstance ruleFlowProcessInstance = context.get(MarshallerContextName.MARSHALLER_PROCESS_INSTANCE);
            nodeInstance.internalSetWorkItemId(content.getWorkItemId());
            InternalKogitoWorkItem workItem = nodeInstance.getWorkItem();
            workItem.setId(content.getWorkItemId());
            workItem.setProcessInstanceId(ruleFlowProcessInstance.getStringId());
            workItem.setName(content.getName());
            workItem.setState(content.getState());
            workItem.setDeploymentId(ruleFlowProcessInstance.getDeploymentId());
            workItem.setProcessInstance(ruleFlowProcessInstance);
            workItem.setPhaseId(content.getPhaseId());
            workItem.setPhaseStatus(content.getPhaseStatus());
            workItem.setStartDate(new Date(content.getStartDate()));
            if (content.hasExternalReferenceId()) {
                workItem.setExternalReferenceId(content.getExternalReferenceId());
            }
            if (content.hasActualOwner()) {
                workItem.setActualOwner(content.getActualOwner());
            }
            if (content.getCompleteDate() > 0) {
                workItem.setCompleteDate(new Date(content.getCompleteDate()));
            }

            if (content.getTimerInstanceIdCount() > 0) {
                nodeInstance.internalSetTimerInstances(new ArrayList<>(content.getTimerInstanceIdList()));
            }
            if (!content.getTimerInstanceReferenceMap().isEmpty()) {
                nodeInstance.internalSetTimerInstancesReference(new HashMap<>(content.getTimerInstanceReferenceMap()));
            }
            nodeInstance.internalSetProcessInstanceId(content.getErrorHandlingProcessInstanceId());
            varReader.buildVariables(content.getVariableList()).forEach(var -> nodeInstance.getWorkItem().getParameters().put(var.getName(), var.getValue()));
            varReader.buildVariables(content.getResultList()).forEach(var -> nodeInstance.getWorkItem().getResults().put(var.getName(), var.getValue()));
            return nodeInstance;
        } catch (InvalidProtocolBufferException ex) {
            throw new ProcessInstanceMarshallerException("cannot unpack node instance", ex);
        }
    }

    private WorkItemNodeInstance instanceWorkItem(String name) {
        WorkItemNodeInstance nodeInstance = "Human Task".equals(name) ? new HumanTaskNodeInstance() : new WorkItemNodeInstance();
        KogitoWorkItemImpl workItem = new KogitoWorkItemImpl();
        workItem.setId(UUID.randomUUID().toString());
        nodeInstance.internalSetWorkItem(workItem);
        return nodeInstance;
    }
}
