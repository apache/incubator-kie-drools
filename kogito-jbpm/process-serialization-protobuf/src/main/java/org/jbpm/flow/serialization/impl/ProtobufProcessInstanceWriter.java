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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.flow.serialization.MarshallerContextName;
import org.jbpm.flow.serialization.MarshallerWriterContext;
import org.jbpm.flow.serialization.NodeInstanceWriter;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerListener;
import org.jbpm.flow.serialization.protobuf.KogitoProcessInstanceProtobuf;
import org.jbpm.flow.serialization.protobuf.KogitoProcessInstanceProtobuf.HeaderEntry;
import org.jbpm.flow.serialization.protobuf.KogitoTypesProtobuf;
import org.jbpm.flow.serialization.protobuf.KogitoTypesProtobuf.WorkflowContext;
import org.jbpm.process.core.context.exclusive.ExclusiveGroup;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.ContextableInstance;
import org.jbpm.process.instance.context.exclusive.ExclusiveGroupInstance;
import org.jbpm.process.instance.context.swimlane.SwimlaneContextInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.kogito.internal.process.runtime.HeadersPersistentConfig;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.process.impl.AbstractProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Any;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.util.JsonFormat;

import static org.jbpm.flow.serialization.MarshallerContextName.MARSHALLER_FORMAT;
import static org.jbpm.flow.serialization.MarshallerContextName.MARSHALLER_FORMAT_JSON;
import static org.jbpm.flow.serialization.MarshallerContextName.MARSHALLER_HEADERS_CONFIG;
import static org.jbpm.flow.serialization.protobuf.ProtobufTypeRegistryFactory.protobufTypeRegistryFactoryInstance;

public class ProtobufProcessInstanceWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufProcessInstanceWriter.class);
    private MarshallerWriterContext context;
    private ProtobufVariableWriter varWriter;
    private ProcessInstanceMarshallerListener[] listeners;

    public ProtobufProcessInstanceWriter(MarshallerWriterContext context) {
        this.context = context;
        this.varWriter = new ProtobufVariableWriter(context);
        this.listeners = context.get(MarshallerContextName.MARSHALLER_INSTANCE_LISTENER);
    }

    public void writeProcessInstance(WorkflowProcessInstanceImpl workFlow, OutputStream os) throws IOException {
        context.set(MarshallerContextName.MARSHALLER_PROCESS_INSTANCE, (RuleFlowProcessInstance) workFlow);
        LOGGER.debug("writing process instance {}", workFlow.getId());
        AbstractProcess<?> process = ((AbstractProcess<?>) context.get(MarshallerContextName.MARSHALLER_PROCESS));
        KogitoProcessRuntime runtime = process.getProcessRuntime();
        Arrays.stream(listeners).forEach(e -> e.beforeMarshallProcess(runtime, workFlow));

        KogitoProcessInstanceProtobuf.ProcessInstance.Builder instance = KogitoProcessInstanceProtobuf.ProcessInstance.newBuilder()
                .setId(workFlow.getStringId())
                .setProcessId(workFlow.getProcessId())
                .setState(workFlow.getState())
                .setProcessType(workFlow.getProcess().getType())
                .setSignalCompletion(workFlow.isSignalCompletion());

        if (workFlow.getProcessVersion() != null) {
            instance.setProcessVersion(workFlow.getProcessVersion());
        }
        if (workFlow.getStartDate() != null) {
            instance.setStartDate(workFlow.getStartDate().getTime());
        }
        if (workFlow.getDescription() != null) {
            instance.setDescription(workFlow.getDescription());
        }
        if (workFlow.getDeploymentId() != null) {
            instance.setDeploymentId(workFlow.getDeploymentId());
        }
        instance.addAllCompletedNodeIds(workFlow.getCompletedNodeIds());
        if (workFlow.getCorrelationKey() != null) {
            instance.setBusinessKey(workFlow.getCorrelationKey());
        }

        instance.setSla(buildSLAContext(workFlow.getSlaCompliance(), workFlow.getSlaDueDate(), workFlow.getSlaTimerId()));

        if (workFlow.getCancelTimerId() != null) {
            instance.setCancelTimerId(workFlow.getCancelTimerId());
        }

        if (workFlow.getParentProcessInstanceId() != null) {
            instance.setParentProcessInstanceId(workFlow.getParentProcessInstanceId());
        }
        if (workFlow.getRootProcessInstanceId() != null) {
            instance.setRootProcessInstanceId(workFlow.getRootProcessInstanceId());
        }
        if (workFlow.getRootProcessId() != null) {
            instance.setRootProcessId(workFlow.getRootProcessId());
        }
        if (workFlow.getNodeIdInError() != null) {
            instance.setErrorNodeId(workFlow.getNodeIdInError());
        }
        if (workFlow.getNodeInstanceIdInError() != null) {
            instance.setErrorNodeInstanceId(workFlow.getNodeInstanceIdInError());
        }
        if (workFlow.getErrorMessage() != null) {
            instance.setErrorMessage(workFlow.getErrorMessage());
        }
        if (workFlow.getReferenceId() != null) {
            instance.setReferenceId(workFlow.getReferenceId());
        }

        HeadersPersistentConfig headersConfig = context.get(MARSHALLER_HEADERS_CONFIG);
        if (workFlow.getHeaders() != null && headersConfig != null && headersConfig.enabled()) {
            Stream<Entry<String, List<String>>> stream = workFlow.getHeaders().entrySet().stream();
            if (headersConfig.excluded() != null && !headersConfig.excluded().isEmpty()) {
                stream = stream.filter(e -> !headersConfig.excluded().contains(e.getKey()));
            }
            instance.addAllHeaders(stream.map(e -> HeaderEntry.newBuilder().setKey(e.getKey()).addAllValue(e.getValue()).build()).collect(Collectors.toList()));
        }

        instance.addAllSwimlaneContext(buildSwimlaneContexts((SwimlaneContextInstance) workFlow.getContextInstance(SwimlaneContext.SWIMLANE_SCOPE)));

        instance.setContext(buildWorkflowContext(workFlow));

        KogitoProcessInstanceProtobuf.ProcessInstance piProtobuf = instance.build();

        String format = this.context.get(MARSHALLER_FORMAT);
        if (format != null && MARSHALLER_FORMAT_JSON.equals(format)) {
            os.write(JsonFormat.printer().usingTypeRegistry(protobufTypeRegistryFactoryInstance().create()).print(piProtobuf).getBytes());
        } else {
            piProtobuf.writeTo(os);
        }
    }

    private KogitoTypesProtobuf.SLAContext buildSLAContext(int slaCompliance, Date slaDueDate, String slaTimerId) {
        KogitoTypesProtobuf.SLAContext.Builder slaContextBuilder = KogitoTypesProtobuf.SLAContext.newBuilder()
                .setSlaCompliance(slaCompliance);
        if (slaDueDate != null) {
            slaContextBuilder.setSlaDueDate(slaDueDate.getTime());
        }
        if (slaTimerId != null) {
            slaContextBuilder.setSlaTimerId(slaTimerId);
        }
        return slaContextBuilder.build();
    }

    private List<KogitoTypesProtobuf.SwimlaneContext> buildSwimlaneContexts(SwimlaneContextInstance swimlaneContextInstance) {
        if (swimlaneContextInstance == null) {
            return Collections.emptyList();
        }

        List<KogitoTypesProtobuf.SwimlaneContext> contexts = new ArrayList<>();

        Map<String, String> swimlaneActors = swimlaneContextInstance.getSwimlaneActors();
        for (Map.Entry<String, String> entry : swimlaneActors.entrySet()) {
            contexts.add(KogitoTypesProtobuf.SwimlaneContext.newBuilder()
                    .setSwimlane(entry.getKey())
                    .setActorId(entry.getValue())
                    .build());
        }
        return contexts;
    }

    protected KogitoTypesProtobuf.WorkflowContext buildWorkflowContext(List<NodeInstance> nodeInstances,
            List<ContextInstance> exclusiveGroupInstances,
            List<Entry<String, Object>> variables,
            List<Entry<String, Integer>> iterationlevels) {

        KogitoTypesProtobuf.WorkflowContext.Builder workflowContextBuilder = KogitoTypesProtobuf.WorkflowContext.newBuilder();
        workflowContextBuilder.addAllNodeInstance(buildNodeInstances(nodeInstances));
        workflowContextBuilder.addAllExclusiveGroup(buildGroups(exclusiveGroupInstances));
        workflowContextBuilder.addAllVariable(varWriter.buildVariables(variables));
        workflowContextBuilder.addAllIterationLevels(buildIterationLevels(iterationlevels));
        return workflowContextBuilder.build();

    }

    private List<org.jbpm.flow.serialization.protobuf.KogitoTypesProtobuf.NodeInstance> buildNodeInstances(List<NodeInstance> nodeInstances) {
        Comparator<NodeInstance> comparator = (o1, o2) -> ((KogitoNodeInstance) o1).getStringId().compareTo(((KogitoNodeInstance) o2).getStringId());
        Collections.sort(nodeInstances, comparator);

        List<KogitoTypesProtobuf.NodeInstance> nodeInstancesProtobuf = new ArrayList<>();
        for (NodeInstance nodeInstance : nodeInstances) {
            KogitoTypesProtobuf.NodeInstance.Builder node = KogitoTypesProtobuf.NodeInstance.newBuilder()
                    .setId(((KogitoNodeInstance) nodeInstance).getStringId())
                    .setNodeId(nodeInstance.getNodeId().toExternalFormat())
                    .setLevel(((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getLevel())
                    .setRetrigger(((org.jbpm.workflow.instance.NodeInstance) nodeInstance).isRetrigger());

            Date triggerDate = ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getTriggerTime();
            if (triggerDate != null) {
                node.setTriggerDate(triggerDate.getTime());
            }

            node.setSla(buildSLAContext(((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getSlaCompliance(),
                    ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getSlaDueDate(),
                    ((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getSlaTimerId()));

            node.setContent(buildNodeInstanceContent(nodeInstance));

            nodeInstancesProtobuf.add(node.build());
        }
        return nodeInstancesProtobuf;

    }

    private Any buildNodeInstanceContent(NodeInstance nodeInstance) {
        KogitoProcessRuntime runtime = ((AbstractProcess<?>) context.get(MarshallerContextName.MARSHALLER_PROCESS)).getProcessRuntime();
        Arrays.stream(listeners).forEach(e -> e.beforeMarshallNode(runtime, (KogitoNodeInstance) nodeInstance));

        NodeInstanceWriter writer = context.findNodeInstanceWriter(nodeInstance);
        if (writer == null) {
            throw new IllegalArgumentException("Unknown node instance type: " + nodeInstance);
        }

        LOGGER.debug("Node writer {}", writer);
        GeneratedMessageV3.Builder<?> builder = writer.write(context, nodeInstance);

        LOGGER.debug("Node instance being writing {}", nodeInstance);
        FieldDescriptor fieldContext = getContextField(builder);
        if (fieldContext != null) {
            LOGGER.debug("Node instance context being writing {}", nodeInstance);
            builder.setField(fieldContext, buildWorkflowContext((NodeInstanceContainer & ContextInstanceContainer & ContextableInstance) nodeInstance));
        }

        return Any.pack(builder.build());
    }

    public FieldDescriptor getContextField(GeneratedMessageV3.Builder<?> builder) {
        for (FieldDescriptor field : builder.getDescriptorForType().getFields()) {
            if ("context".equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    protected <T extends NodeInstanceContainer & ContextInstanceContainer & ContextableInstance> WorkflowContext buildWorkflowContext(T nodeInstance) {
        List<NodeInstance> nodeInstances = new ArrayList<>(nodeInstance.getSerializableNodeInstances());
        List<ContextInstance> exclusiveGroupInstances = nodeInstance.getContextInstances(ExclusiveGroup.EXCLUSIVE_GROUP);
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) nodeInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
        List<Map.Entry<String, Object>> variables = (variableScopeInstance != null) ? new ArrayList<>(variableScopeInstance.getVariables().entrySet()) : Collections.emptyList();
        List<Map.Entry<String, Integer>> iterationlevels = new ArrayList<>(nodeInstance.getIterationLevels().entrySet());
        return buildWorkflowContext(nodeInstances, exclusiveGroupInstances, variables, iterationlevels);
    }

    private List<KogitoTypesProtobuf.NodeInstanceGroup> buildGroups(List<ContextInstance> exclusiveGroupInstances) {
        if (exclusiveGroupInstances == null) {
            return Collections.emptyList();
        }

        List<KogitoTypesProtobuf.NodeInstanceGroup> groupProtobuf = new ArrayList<>();
        for (ContextInstance contextInstance : exclusiveGroupInstances) {
            KogitoTypesProtobuf.NodeInstanceGroup.Builder exclusiveNodeInstanceGroup = KogitoTypesProtobuf.NodeInstanceGroup.newBuilder();
            ExclusiveGroupInstance exclusiveGroupInstance = (ExclusiveGroupInstance) contextInstance;
            Collection<KogitoNodeInstance> groupNodeInstances = exclusiveGroupInstance.getNodeInstances();
            for (KogitoNodeInstance nodeInstance : groupNodeInstances) {
                exclusiveNodeInstanceGroup.addGroupNodeInstanceId(nodeInstance.getStringId());
            }
            groupProtobuf.add(exclusiveNodeInstanceGroup.build());
        }

        return groupProtobuf;
    }

    private List<KogitoTypesProtobuf.IterationLevel> buildIterationLevels(List<Entry<String, Integer>> iterationlevels) {
        Comparator<Map.Entry<String, Integer>> comparator = (o1, o2) -> o1.getKey().compareTo(o2.getKey());
        Collections.sort(iterationlevels, comparator);

        List<KogitoTypesProtobuf.IterationLevel> levelsProtobuf = new ArrayList<>();
        for (Map.Entry<String, Integer> level : iterationlevels) {
            if (level.getValue() != null) {
                KogitoTypesProtobuf.IterationLevel levelProtobuf = KogitoTypesProtobuf.IterationLevel.newBuilder()
                        .setId(level.getKey())
                        .setLevel(level.getValue())
                        .build();
                levelsProtobuf.add(levelProtobuf);
            }
        }
        return levelsProtobuf;
    }

}
