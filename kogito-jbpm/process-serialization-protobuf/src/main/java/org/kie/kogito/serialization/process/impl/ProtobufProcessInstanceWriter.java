/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serialization.process.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jbpm.process.core.context.exclusive.ExclusiveGroup;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.context.exclusive.ExclusiveGroupInstance;
import org.jbpm.process.instance.context.swimlane.SwimlaneContextInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.humantask.Reassignment;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.CompositeContextNodeInstance;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.jbpm.workflow.instance.node.EventSubProcessNodeInstance;
import org.jbpm.workflow.instance.node.ForEachNodeInstance;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.jbpm.workflow.instance.node.JoinInstance;
import org.jbpm.workflow.instance.node.LambdaSubProcessNodeInstance;
import org.jbpm.workflow.instance.node.MilestoneNodeInstance;
import org.jbpm.workflow.instance.node.RuleSetNodeInstance;
import org.jbpm.workflow.instance.node.StateNodeInstance;
import org.jbpm.workflow.instance.node.SubProcessNodeInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.Comment;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.serialization.process.MarshallerWriterContext;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent;
import org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf;
import org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf;
import org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext;
import org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf;
import org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData;

import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;

import static org.kie.kogito.serialization.process.MarshallerContextName.MARSHALLER_FORMAT;
import static org.kie.kogito.serialization.process.protobuf.ProtobufTypeRegistryFactory.protobufTypeRegistryFactoryInstance;

public class ProtobufProcessInstanceWriter {

    private MarshallerWriterContext context;
    private ProtobufVariableWriter varWriter;

    public ProtobufProcessInstanceWriter(MarshallerWriterContext context) {
        this.context = context;
        this.varWriter = new ProtobufVariableWriter(context);
    }

    public void writeProcessInstance(WorkflowProcessInstanceImpl workFlow, OutputStream os) throws IOException {

        KogitoProcessInstanceProtobuf.ProcessInstance.Builder instance = KogitoProcessInstanceProtobuf.ProcessInstance.newBuilder()
                .setId(workFlow.getStringId())
                .setProcessId(workFlow.getProcessId())
                .setState(workFlow.getState())
                .setProcessType(workFlow.getProcess().getType())
                .setSignalCompletion(workFlow.isSignalCompletion())
                .setStartDate(workFlow.getStartDate().getTime());

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

        if (workFlow.getParentProcessInstanceStringId() != null) {
            instance.setParentProcessInstanceId(workFlow.getParentProcessInstanceStringId());
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
        if (workFlow.getErrorMessage() != null) {
            instance.setErrorMessage(workFlow.getErrorMessage());
        }
        if (workFlow.getReferenceId() != null) {
            instance.setReferenceId(workFlow.getReferenceId());
        }

        instance.addAllSwimlaneContext(buildSwimlaneContexts((SwimlaneContextInstance) workFlow.getContextInstance(SwimlaneContext.SWIMLANE_SCOPE)));

        List<NodeInstance> nodeInstances = new ArrayList<>(workFlow.getNodeInstances());
        List<ContextInstance> exclusiveGroupInstances = workFlow.getContextInstances(ExclusiveGroup.EXCLUSIVE_GROUP);
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) workFlow.getContextInstance(VariableScope.VARIABLE_SCOPE);
        List<Map.Entry<String, Object>> variables = new ArrayList<>(variableScopeInstance.getVariables().entrySet());
        List<Map.Entry<String, Integer>> iterationlevels = new ArrayList<>(workFlow.getIterationLevels().entrySet());
        instance.setContext(buildWorkflowContext(nodeInstances, exclusiveGroupInstances, variables, iterationlevels));

        KogitoProcessInstanceProtobuf.ProcessInstance piProtobuf = instance.build();

        String format = (String) this.context.get(MARSHALLER_FORMAT);
        if (format != null && "json".equals(format)) {
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

    private KogitoTypesProtobuf.WorkflowContext buildWorkflowContext(List<NodeInstance> nodeInstances,
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

    private List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance> buildNodeInstances(List<NodeInstance> nodeInstances) {
        Comparator<NodeInstance> comparator = (o1, o2) -> ((KogitoNodeInstance) o1).getStringId().compareTo(((KogitoNodeInstance) o2).getStringId());
        Collections.sort(nodeInstances, comparator);

        List<KogitoTypesProtobuf.NodeInstance> nodeInstancesProtobuf = new ArrayList<>();
        for (NodeInstance nodeInstance : nodeInstances) {
            KogitoTypesProtobuf.NodeInstance.Builder node = KogitoTypesProtobuf.NodeInstance.newBuilder()
                    .setId(((KogitoNodeInstance) nodeInstance).getStringId())
                    .setNodeId(nodeInstance.getNodeId())
                    .setLevel(((org.jbpm.workflow.instance.NodeInstance) nodeInstance).getLevel());

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
        if (nodeInstance instanceof RuleSetNodeInstance) {
            return buildRuleSetNodeInstance((RuleSetNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof ForEachNodeInstance) {
            return buildForEachNodeInstance((ForEachNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof LambdaSubProcessNodeInstance) {
            return buildLambdaSubProcessNodeInstance((LambdaSubProcessNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof SubProcessNodeInstance) {
            return buildSubProcessNodeInstance((SubProcessNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof StateNodeInstance) {
            return buildStateNodeInstance((StateNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof JoinInstance) {
            return buildJoinInstance((JoinInstance) nodeInstance);
        } else if (nodeInstance instanceof TimerNodeInstance) {
            return buildTimerNodeInstance((TimerNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof EventNodeInstance) {
            return buildEventNodeInstance();
        } else if (nodeInstance instanceof MilestoneNodeInstance) {
            return buildMilestoneNodeInstance((MilestoneNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof DynamicNodeInstance) {
            return buildDynamicNodeInstance((DynamicNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof EventSubProcessNodeInstance) {
            return buildEventSubProcessNodeInstance((EventSubProcessNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof CompositeContextNodeInstance) {
            return buildCompositeContextNodeInstance((CompositeContextNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof HumanTaskNodeInstance) {
            return buildHumanTaskNodeInstance((HumanTaskNodeInstance) nodeInstance);
        } else if (nodeInstance instanceof WorkItemNodeInstance) {
            return buildWorkItemNodeInstance((WorkItemNodeInstance) nodeInstance);
        } else {
            throw new IllegalArgumentException("Unknown node instance type: " + nodeInstance);
        }
    }

    private Any buildRuleSetNodeInstance(RuleSetNodeInstance nodeInstance) {
        RuleSetNodeInstanceContent.Builder ruleSet = RuleSetNodeInstanceContent.newBuilder();
        ruleSet.setRuleFlowGroup(nodeInstance.getRuleFlowGroup());
        ruleSet.addAllTimerInstanceId(nodeInstance.getTimerInstances());
        return Any.pack(ruleSet.build());
    }

    private Any buildForEachNodeInstance(ForEachNodeInstance nodeInstance) {
        ForEachNodeInstanceContent.Builder foreachBuilder = ForEachNodeInstanceContent.newBuilder();

        foreachBuilder.addAllTimerInstanceId(nodeInstance.getTimerInstances());
        List<NodeInstance> nodeInstances = nodeInstance.getNodeInstances().stream().filter(CompositeContextNodeInstance.class::isInstance).collect(Collectors.toList());
        List<ContextInstance> exclusiveGroupInstances = nodeInstance.getContextInstances(ExclusiveGroup.EXCLUSIVE_GROUP);
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) nodeInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
        List<Map.Entry<String, Object>> variables = new ArrayList<>(variableScopeInstance.getVariables().entrySet());
        List<Map.Entry<String, Integer>> iterationlevels = new ArrayList<>(nodeInstance.getIterationLevels().entrySet());
        foreachBuilder.setContext(buildWorkflowContext(nodeInstances, exclusiveGroupInstances, variables, iterationlevels));

        return Any.pack(foreachBuilder.build());
    }

    private Any buildLambdaSubProcessNodeInstance(LambdaSubProcessNodeInstance nodeInstance) {

        LambdaSubProcessNodeInstanceContent.Builder builder = LambdaSubProcessNodeInstanceContent.newBuilder();
        builder.setProcessInstanceId(nodeInstance.getProcessInstanceId());
        List<String> timerInstances = nodeInstance.getTimerInstances();
        if (timerInstances != null) {
            builder.addAllTimerInstanceId(timerInstances);
        }

        return Any.pack(builder.build());
    }

    private Any buildSubProcessNodeInstance(SubProcessNodeInstance nodeInstance) {
        SubProcessNodeInstanceContent.Builder builder = SubProcessNodeInstanceContent.newBuilder();
        builder.setProcessInstanceId(nodeInstance.getProcessInstanceId());
        List<String> timerInstances = nodeInstance.getTimerInstances();
        if (timerInstances != null) {
            builder.addAllTimerInstanceId(timerInstances);
        }
        return Any.pack(builder.build());
    }

    private Any buildStateNodeInstance(StateNodeInstance nodeInstance) {
        StateNodeInstanceContent.Builder builder = StateNodeInstanceContent.newBuilder();
        List<String> timerInstances = nodeInstance.getTimerInstances();
        if (timerInstances != null) {
            builder.addAllTimerInstanceId(timerInstances);
        }
        return Any.pack(builder.build());
    }

    private Any buildJoinInstance(JoinInstance nodeInstance) {
        JoinNodeInstanceContent.Builder joinBuilder = JoinNodeInstanceContent.newBuilder();
        Map<Long, Integer> triggers = nodeInstance.getTriggers();
        List<Long> keys = new ArrayList<>(triggers.keySet());
        Comparator<Long> comparator = (o1, o2) -> o1.compareTo(o2);
        Collections.sort(keys, comparator);

        for (Long key : keys) {
            joinBuilder.addTrigger(JoinTrigger.newBuilder()
                    .setNodeId(key)
                    .setCounter(triggers.get(key))
                    .build());
        }

        return Any.pack(joinBuilder.build());
    }

    private Any buildTimerNodeInstance(TimerNodeInstance nodeInstance) {
        return Any.pack(TimerNodeInstanceContent.newBuilder().setTimerId(nodeInstance.getTimerId()).build());
    }

    private Any buildEventNodeInstance() {
        return Any.pack(EventNodeInstanceContent.newBuilder().build());
    }

    private Any buildMilestoneNodeInstance(MilestoneNodeInstance nodeInstance) {
        MilestoneNodeInstanceContent.Builder builder = MilestoneNodeInstanceContent.newBuilder();

        List<String> timerInstances = nodeInstance.getTimerInstances();
        if (timerInstances != null) {
            builder.addAllTimerInstanceId(timerInstances);
        }
        return Any.pack(builder.build());
    }

    private Any buildDynamicNodeInstance(DynamicNodeInstance nodeInstance) {

        DynamicNodeInstanceContent.Builder builder = DynamicNodeInstanceContent.newBuilder();
        List<String> timerInstances = nodeInstance.getTimerInstances();
        if (timerInstances != null) {
            builder.addAllTimerInstanceId(timerInstances);
        }

        builder.setContext(buildWorkflowContext(nodeInstance));

        return Any.pack(builder.build());
    }

    private Any buildEventSubProcessNodeInstance(EventSubProcessNodeInstance nodeInstance) {

        EventSubProcessNodeInstanceContent.Builder builder = EventSubProcessNodeInstanceContent.newBuilder();
        List<String> timerInstances = nodeInstance.getTimerInstances();
        if (timerInstances != null) {
            builder.addAllTimerInstanceId(timerInstances);
        }

        builder.setContext(buildWorkflowContext(nodeInstance));

        return Any.pack(builder.build());
    }

    private Any buildCompositeContextNodeInstance(CompositeContextNodeInstance nodeInstance) {

        CompositeContextNodeInstanceContent.Builder builder = CompositeContextNodeInstanceContent.newBuilder();
        List<String> timerInstances = nodeInstance.getTimerInstances();
        if (timerInstances != null) {
            builder.addAllTimerInstanceId(timerInstances);
        }

        builder.setContext(buildWorkflowContext(nodeInstance));

        return Any.pack(builder.build());
    }

    private WorkflowContext buildWorkflowContext(CompositeContextNodeInstance nodeInstance) {
        List<NodeInstance> nodeInstances = new ArrayList<>(nodeInstance.getNodeInstances());
        List<ContextInstance> exclusiveGroupInstances = nodeInstance.getContextInstances(ExclusiveGroup.EXCLUSIVE_GROUP);
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) nodeInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
        List<Map.Entry<String, Object>> variables = (variableScopeInstance != null) ? new ArrayList<>(variableScopeInstance.getVariables().entrySet()) : Collections.emptyList();
        List<Map.Entry<String, Integer>> iterationlevels = new ArrayList<>(nodeInstance.getIterationLevels().entrySet());
        return buildWorkflowContext(nodeInstances, exclusiveGroupInstances, variables, iterationlevels);
    }

    private Any buildWorkItemNodeInstance(WorkItemNodeInstance nodeInstance) {
        return Any.pack(buildWorkItemNodeInstanceBuilder(nodeInstance).build());
    }

    private WorkItemNodeInstanceContent.Builder buildWorkItemNodeInstanceBuilder(WorkItemNodeInstance nodeInstance) {
        WorkItemNodeInstanceContent.Builder builder = WorkItemNodeInstanceContent.newBuilder();

        builder.setWorkItemId(nodeInstance.getWorkItemId());
        List<String> timerInstances = nodeInstance.getTimerInstances();
        if (timerInstances != null) {
            builder.addAllTimerInstanceId(timerInstances);
        }
        if (nodeInstance.getExceptionHandlingProcessInstanceId() != null) {
            builder.setErrorHandlingProcessInstanceId(nodeInstance.getExceptionHandlingProcessInstanceId());
        }
        KogitoWorkItem workItem = nodeInstance.getWorkItem();

        builder.setName(workItem.getName())
                .setState(workItem.getState())
                .setPhaseId(workItem.getPhaseId())
                .setPhaseStatus(workItem.getPhaseStatus())
                .setStartDate(workItem.getStartDate().getTime())
                .addAllVariable(varWriter.buildVariables(new ArrayList<>(workItem.getParameters().entrySet())))
                .addAllResult(varWriter.buildVariables(new ArrayList<>(workItem.getResults().entrySet())));

        if (workItem.getCompleteDate() != null) {
            builder.setCompleteDate(workItem.getCompleteDate().getTime());
        }
        return builder;
    }

    private Any buildHumanTaskNodeInstance(HumanTaskNodeInstance nodeInstance) {
        WorkItemNodeInstanceContent.Builder builder = buildWorkItemNodeInstanceBuilder(nodeInstance);
        builder.setWorkItemData(Any.pack(buildHumanTaskWorkItemData(nodeInstance, (HumanTaskWorkItem) nodeInstance.getWorkItem())));
        return Any.pack(builder.build());
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

    private HumanTaskWorkItemData buildHumanTaskWorkItemData(HumanTaskNodeInstance nodeInstance, HumanTaskWorkItem workItem) {
        HumanTaskWorkItemData.Builder builder = HumanTaskWorkItemData.newBuilder();

        if (workItem.getTaskPriority() != null) {
            builder.setTaskPriority(workItem.getTaskPriority());
        }

        if (workItem.getReferenceName() != null) {
            builder.setTaskReferenceName(workItem.getReferenceName());
        }
        if (workItem.getTaskDescription() != null) {
            builder.setTaskDescription(workItem.getTaskDescription());
        }

        if (workItem.getActualOwner() != null) {
            builder.setActualOwner(workItem.getActualOwner());
        }

        if (workItem.getTaskName() != null) {
            builder.setTaskName(workItem.getTaskName());
        }

        if (workItem.getPotentialUsers() != null) {
            builder.addAllPotUsers(workItem.getPotentialUsers());
        }

        if (workItem.getPotentialGroups() != null) {
            builder.addAllPotGroups(workItem.getPotentialGroups());
        }

        if (workItem.getExcludedUsers() != null) {
            builder.addAllExcludedUsers(workItem.getExcludedUsers());
        }

        if (workItem.getAdminUsers() != null) {
            builder.addAllAdminUsers(workItem.getAdminUsers());
        }

        if (workItem.getAdminGroups() != null) {
            builder.addAllAdminGroups(workItem.getAdminGroups());
        }

        if (workItem.getComments() != null) {
            builder.addAllComments(buildComments(workItem.getComments().values()));
        }

        if (workItem.getAttachments() != null) {
            builder.addAllAttachments(buildAttachments(workItem.getAttachments().values()));
        }

        if (nodeInstance.getNotStartedDeadlineTimers() != null) {
            builder.putAllStartDeadlines(buildDeadlines(nodeInstance.getNotStartedDeadlineTimers()));
        }

        if (nodeInstance.getNotStartedReassignments() != null) {
            builder.putAllStartReassigments(buildReassignments(nodeInstance.getNotStartedReassignments()));
        }

        if (nodeInstance.getNotCompletedDeadlineTimers() != null) {
            builder.putAllCompletedDeadlines(buildDeadlines(nodeInstance.getNotCompletedDeadlineTimers()));
        }

        if (nodeInstance.getNotCompletedReassigments() != null) {
            builder.putAllCompletedReassigments(buildReassignments(nodeInstance.getNotCompletedReassigments()));
        }

        return builder.build();
    }

    private List<KogitoWorkItemsProtobuf.Comment> buildComments(Iterable<Comment> comments) {
        List<KogitoWorkItemsProtobuf.Comment> commentsProtobuf = new ArrayList<>();
        for (Comment comment : comments) {
            KogitoWorkItemsProtobuf.Comment workItemComment = KogitoWorkItemsProtobuf.Comment.newBuilder()
                    .setId(comment.getId().toString())
                    .setContent(comment.getContent())
                    .setUpdatedBy(comment.getUpdatedBy())
                    .setUpdatedAt(comment.getUpdatedAt().getTime())
                    .build();
            commentsProtobuf.add(workItemComment);
        }
        return commentsProtobuf;
    }

    private List<KogitoWorkItemsProtobuf.Attachment> buildAttachments(Iterable<Attachment> attachments) {
        List<KogitoWorkItemsProtobuf.Attachment> attachmentProtobuf = new ArrayList<>();
        for (Attachment attachment : attachments) {
            KogitoWorkItemsProtobuf.Attachment workItemAttachment = KogitoWorkItemsProtobuf.Attachment.newBuilder()
                    .setId(attachment.getId().toString()).setContent(attachment.getContent().toString())
                    .setUpdatedBy(attachment.getUpdatedBy()).setUpdatedAt(attachment.getUpdatedAt().getTime())
                    .setName(attachment.getName())
                    .build();
            attachmentProtobuf.add(workItemAttachment);
        }
        return attachmentProtobuf;
    }

    private Map<String, KogitoWorkItemsProtobuf.Deadline> buildDeadlines(Map<String, Map<String, Object>> deadlines) {
        Map<String, KogitoWorkItemsProtobuf.Deadline> deadlinesProtobuf = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : deadlines.entrySet()) {
            Map<String, String> data = new HashMap<>();
            entry.getValue().forEach((k, v) -> data.put(k, v.toString()));
            KogitoWorkItemsProtobuf.Deadline.Builder builder = KogitoWorkItemsProtobuf.Deadline.newBuilder();
            builder.getContentMap().putAll(data);
            deadlinesProtobuf.put(entry.getKey(), builder.build());
        }
        return deadlinesProtobuf;
    }

    private Map<String, KogitoWorkItemsProtobuf.Reassignment> buildReassignments(Map<String, Reassignment> reassignments) {
        Map<String, KogitoWorkItemsProtobuf.Reassignment> reassignmentsProtobuf = new HashMap<>();
        for (Map.Entry<String, Reassignment> entry : reassignments.entrySet()) {
            KogitoWorkItemsProtobuf.Reassignment.Builder builder = KogitoWorkItemsProtobuf.Reassignment.newBuilder();
            builder.addAllGroups(entry.getValue().getPotentialGroups());
            builder.addAllUsers(entry.getValue().getPotentialUsers());
            reassignmentsProtobuf.put(entry.getKey(), builder.build());
        }
        return reassignmentsProtobuf;
    }

}
