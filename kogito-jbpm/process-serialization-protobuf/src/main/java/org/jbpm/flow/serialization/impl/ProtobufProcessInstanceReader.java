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
package org.jbpm.flow.serialization.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jbpm.flow.serialization.MarshallerContextName;
import org.jbpm.flow.serialization.MarshallerReaderContext;
import org.jbpm.flow.serialization.NodeInstanceReader;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerListener;
import org.jbpm.flow.serialization.protobuf.KogitoProcessInstanceProtobuf;
import org.jbpm.flow.serialization.protobuf.KogitoTypesProtobuf;
import org.jbpm.flow.serialization.protobuf.KogitoTypesProtobuf.SLAContext;
import org.jbpm.flow.serialization.protobuf.KogitoTypesProtobuf.WorkflowContext;
import org.jbpm.process.core.context.exclusive.ExclusiveGroup;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.ContextableInstance;
import org.jbpm.process.instance.context.exclusive.ExclusiveGroupInstance;
import org.jbpm.process.instance.context.swimlane.SwimlaneContextInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstanceContainer;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.process.impl.AbstractProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.util.JsonFormat;

import static org.jbpm.flow.serialization.protobuf.ProtobufTypeRegistryFactory.protobufTypeRegistryFactoryInstance;

public class ProtobufProcessInstanceReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufProcessInstanceReader.class);
    private RuleFlowProcessInstance ruleFlowProcessInstance;
    private MarshallerReaderContext context;
    private ProtobufVariableReader varReader;
    private ProcessInstanceMarshallerListener[] listeners;

    public ProtobufProcessInstanceReader(MarshallerReaderContext context) {
        this.context = context;
        this.ruleFlowProcessInstance = new RuleFlowProcessInstance();
        this.varReader = new ProtobufVariableReader(context);
        this.listeners = context.get(MarshallerContextName.MARSHALLER_INSTANCE_LISTENER);
        context.set(MarshallerContextName.MARSHALLER_PROCESS_INSTANCE, ruleFlowProcessInstance);
    }

    public RuleFlowProcessInstance read(InputStream input) throws IOException {
        LOGGER.debug("read process");
        KogitoProcessInstanceProtobuf.ProcessInstance processInstanceProtobuf;

        String format = this.context.get(MarshallerContextName.MARSHALLER_FORMAT);
        if (format != null && MarshallerContextName.MARSHALLER_FORMAT_JSON.equals(format)) {
            KogitoProcessInstanceProtobuf.ProcessInstance.Builder builder = KogitoProcessInstanceProtobuf.ProcessInstance.newBuilder();
            JsonFormat.parser().usingTypeRegistry(protobufTypeRegistryFactoryInstance().create()).ignoringUnknownFields().merge(new InputStreamReader(input), builder);
            processInstanceProtobuf = builder.build();
        } else {
            processInstanceProtobuf = KogitoProcessInstanceProtobuf.ProcessInstance.parseFrom(input);
        }
        return buildWorkflow(processInstanceProtobuf);
    }

    private RuleFlowProcessInstance buildWorkflow(KogitoProcessInstanceProtobuf.ProcessInstance processInstanceProtobuf) {

        RuleFlowProcessInstance processInstance = ruleFlowProcessInstance;
        processInstance.setProcessId(processInstanceProtobuf.getProcessId());
        processInstance.setProcessVersion(processInstanceProtobuf.getProcessVersion());

        processInstance.setInternalProcess(((AbstractProcess<?>) context.get(MarshallerContextName.MARSHALLER_PROCESS)).get());

        processInstance.setId(processInstanceProtobuf.getId());
        processInstance.setState(processInstanceProtobuf.getState());
        processInstance.setSignalCompletion(processInstanceProtobuf.getSignalCompletion());

        if (processInstanceProtobuf.hasStartDate()) {
            processInstance.setStartDate(new Date(processInstanceProtobuf.getStartDate()));
        }

        if (processInstanceProtobuf.hasDescription()) {
            processInstance.setDescription(processInstanceProtobuf.getDescription());
        }

        if (processInstanceProtobuf.hasDeploymentId()) {
            processInstance.setDeploymentId(processInstanceProtobuf.getDeploymentId());
        }

        for (String completedNodeId : processInstanceProtobuf.getCompletedNodeIdsList()) {
            processInstance.addCompletedNodeId(completedNodeId);
        }

        if (processInstanceProtobuf.hasBusinessKey()) {
            processInstance.setCorrelationKey(processInstanceProtobuf.getBusinessKey());
        }

        if (processInstanceProtobuf.hasSla()) {
            SLAContext slaContext = processInstanceProtobuf.getSla();
            if (slaContext.getSlaDueDate() > 0) {
                processInstance.internalSetSlaDueDate(new Date(slaContext.getSlaDueDate()));
            }

            if (slaContext.hasSlaTimerId()) {
                processInstance.internalSetSlaTimerId(slaContext.getSlaTimerId());
            }
            if (slaContext.hasSlaCompliance()) {
                processInstance.internalSetSlaCompliance(slaContext.getSlaCompliance());
            }
        }

        if (processInstanceProtobuf.hasCancelTimerId()) {
            processInstance.internalSetCancelTimerId(processInstanceProtobuf.getCancelTimerId());
        }

        if (processInstanceProtobuf.hasParentProcessInstanceId()) {
            processInstance.setParentProcessInstanceId(processInstanceProtobuf.getParentProcessInstanceId());
        }
        if (processInstanceProtobuf.hasRootProcessInstanceId()) {
            processInstance.setRootProcessInstanceId(processInstanceProtobuf.getRootProcessInstanceId());
        }
        if (processInstanceProtobuf.hasRootProcessId()) {
            processInstance.setRootProcessId(processInstanceProtobuf.getRootProcessId());
        }

        if (processInstanceProtobuf.hasErrorNodeId()) {
            processInstance.internalSetErrorNodeId(processInstanceProtobuf.getErrorNodeId());
        }

        if (processInstanceProtobuf.hasErrorMessage()) {
            processInstance.internalSetErrorMessage(processInstanceProtobuf.getErrorMessage());
        }

        if (processInstanceProtobuf.hasReferenceId()) {
            processInstance.setReferenceId(processInstanceProtobuf.getReferenceId());
        }

        if (processInstanceProtobuf.getSwimlaneContextCount() > 0) {
            SwimlaneContextInstance swimlaneContextInstance = (SwimlaneContextInstance) processInstance.getContextInstance(SwimlaneContext.SWIMLANE_SCOPE);
            for (KogitoTypesProtobuf.SwimlaneContext _swimlane : processInstanceProtobuf.getSwimlaneContextList()) {
                swimlaneContextInstance.setActorId(_swimlane.getSwimlane(), _swimlane.getActorId());
            }
        }

        WorkflowContext workflowContext = processInstanceProtobuf.getContext();
        buildWorkflowContext(processInstance, workflowContext);

        KogitoProcessRuntime runtime = ((AbstractProcess<?>) context.get(MarshallerContextName.MARSHALLER_PROCESS)).getProcessRuntime();
        Arrays.stream(listeners).forEach(e -> e.afterUnmarshallProcess(runtime, processInstance));
        return processInstance;
    }

    private void setCommonNodeInstanceData(RuleFlowProcessInstance processInstance, KogitoNodeInstanceContainer parentContainer, KogitoTypesProtobuf.NodeInstance nodeInstanceProtobuf,
            NodeInstanceImpl nodeInstanceImpl) {
        if (nodeInstanceImpl.getStringId() == null) {
            nodeInstanceImpl.setId(nodeInstanceProtobuf.getId());
        }

        if (nodeInstanceImpl.getNodeId() == null) {
            nodeInstanceImpl.setNodeId(WorkflowElementIdentifierFactory.fromExternalFormat(nodeInstanceProtobuf.getNodeId()));
        }

        if (nodeInstanceImpl.getNodeInstanceContainer() == null) {
            nodeInstanceImpl.setNodeInstanceContainer(parentContainer);
        }
        if (nodeInstanceImpl.getProcessInstance() == null) {
            nodeInstanceImpl.setProcessInstance(processInstance);
        }

        nodeInstanceImpl.setLevel(nodeInstanceProtobuf.getLevel() == 0 ? 1 : nodeInstanceProtobuf.getLevel());
    }

    protected NodeInstanceImpl buildNodeInstance(KogitoTypesProtobuf.NodeInstance nodeInstance, KogitoNodeInstanceContainer parent) {
        try {
            com.google.protobuf.Any nodeContentProtobuf = nodeInstance.getContent();

            NodeInstanceReader reader = context.findNodeInstanceReader(nodeContentProtobuf);
            if (reader == null) {
                throw new IllegalArgumentException("Unknown node instance " + nodeInstance);
            }
            LOGGER.debug("Node reader {}", reader);
            NodeInstanceImpl result = (NodeInstanceImpl) reader.read(context, nodeContentProtobuf);
            setCommonNodeInstanceData(ruleFlowProcessInstance, parent, nodeInstance, result);

            LOGGER.debug("Node {} content {}", reader.type(), nodeContentProtobuf);
            GeneratedMessageV3 content = nodeContentProtobuf.unpack(reader.type());
            LOGGER.debug("Node instance being reading {}", result);
            FieldDescriptor fieldDescriptor = getContextField(content);
            if (fieldDescriptor != null) {
                LOGGER.debug("Node instance context being reading {}", result);
                KogitoTypesProtobuf.WorkflowContext workflowContext = (KogitoTypesProtobuf.WorkflowContext) content.getField(fieldDescriptor);
                buildWorkflowContext((NodeInstanceContainer & ContextInstanceContainer & ContextableInstance) result, workflowContext);
            }

            SLAContext slaNodeInstanceContext = nodeInstance.getSla();
            result.internalSetSlaCompliance(slaNodeInstanceContext.getSlaCompliance());
            if (slaNodeInstanceContext.getSlaDueDate() > 0) {
                result.internalSetSlaDueDate(new Date(slaNodeInstanceContext.getSlaDueDate()));
            }
            result.internalSetSlaTimerId(slaNodeInstanceContext.getSlaTimerId());
            if (nodeInstance.hasTriggerDate()) {
                result.internalSetTriggerTime(new Date(nodeInstance.getTriggerDate()));
            }

            KogitoNodeInstance kogitoNodeInstance = (KogitoNodeInstance) result;
            KogitoProcessRuntime runtime = ((AbstractProcess<?>) context.get(MarshallerContextName.MARSHALLER_PROCESS)).getProcessRuntime();
            Arrays.stream(listeners).forEach(e -> e.afterUnmarshallNode(runtime, kogitoNodeInstance));
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read node instance content", e);
        }
    }

    public FieldDescriptor getContextField(GeneratedMessageV3 message) {
        for (FieldDescriptor field : message.getDescriptorForType().getFields()) {
            if ("context".equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    private <T extends NodeInstanceContainer & ContextInstanceContainer & ContextableInstance> void buildWorkflowContext(T container, WorkflowContext workflowContext) {
        if (workflowContext.getNodeInstanceCount() > 0) {
            for (KogitoTypesProtobuf.NodeInstance nodeInstanceProtobuf : workflowContext.getNodeInstanceList()) {
                buildNodeInstance(nodeInstanceProtobuf, container);
            }
        }

        container.addContextInstance(VariableScope.VARIABLE_SCOPE, new VariableScopeInstance());
        if (workflowContext.getVariableCount() > 0) {
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance) container.getContextInstance(VariableScope.VARIABLE_SCOPE);
            varReader.buildVariables(workflowContext.getVariableList()).forEach(v -> variableScopeInstance.internalSetVariable(v.getName(), v.getValue()));
        }
        if (workflowContext.getIterationLevelsCount() > 0) {
            container.getIterationLevels().putAll(buildIterationLevels(workflowContext.getIterationLevelsList()));
        }
        for (KogitoTypesProtobuf.NodeInstanceGroup group : workflowContext.getExclusiveGroupList()) {
            Function<String, KogitoNodeInstance> finder = nodeInstanceId -> container.getNodeInstance(nodeInstanceId, true);
            container.addContextInstance(ExclusiveGroup.EXCLUSIVE_GROUP, buildExclusiveGroupInstance(group, finder));
        }
    }

    private ExclusiveGroupInstance buildExclusiveGroupInstance(KogitoTypesProtobuf.NodeInstanceGroup group, Function<String, KogitoNodeInstance> finder) {
        ExclusiveGroupInstance exclusiveGroupInstance = new ExclusiveGroupInstance();
        for (String nodeInstanceId : group.getGroupNodeInstanceIdList()) {
            KogitoNodeInstance kogitoNodeInstance = finder.apply(nodeInstanceId);
            if (kogitoNodeInstance == null) {
                throw new IllegalArgumentException("Could not find node instance when deserializing exclusive group instance: " + nodeInstanceId);
            }
            exclusiveGroupInstance.addNodeInstance(kogitoNodeInstance);
        }
        return exclusiveGroupInstance;
    }

    private Map<String, Integer> buildIterationLevels(List<KogitoTypesProtobuf.IterationLevel> iterationLevel) {
        Function<KogitoTypesProtobuf.IterationLevel, String> mapKey = KogitoTypesProtobuf.IterationLevel::getId;
        Function<KogitoTypesProtobuf.IterationLevel, Integer> mapValue = KogitoTypesProtobuf.IterationLevel::getLevel;
        return iterationLevel.stream().collect(Collectors.toMap(mapKey, mapValue));
    }

}
