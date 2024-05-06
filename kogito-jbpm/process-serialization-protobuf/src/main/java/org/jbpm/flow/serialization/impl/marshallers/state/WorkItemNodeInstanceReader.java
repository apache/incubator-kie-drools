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

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jbpm.flow.serialization.MarshallerContextName;
import org.jbpm.flow.serialization.MarshallerReaderContext;
import org.jbpm.flow.serialization.NodeInstanceReader;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerException;
import org.jbpm.flow.serialization.impl.ProtobufVariableReader;
import org.jbpm.flow.serialization.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent;
import org.jbpm.flow.serialization.protobuf.KogitoWorkItemsProtobuf;
import org.jbpm.flow.serialization.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData;
import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.process.instance.impl.humantask.InternalHumanTaskWorkItem;
import org.jbpm.process.instance.impl.humantask.Reassignment;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.Comment;
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
            WorkItemNodeInstance nodeInstance = instanceWorkItem(content);
            if (nodeInstance instanceof HumanTaskNodeInstance) {
                HumanTaskNodeInstance humanTaskNodeInstance = (HumanTaskNodeInstance) nodeInstance;
                InternalHumanTaskWorkItem workItem = humanTaskNodeInstance.getWorkItem();
                Any workItemDataMessage = content.getWorkItemData();
                if (workItemDataMessage.is(HumanTaskWorkItemData.class)) {
                    HumanTaskWorkItemData workItemData = workItemDataMessage.unpack(HumanTaskWorkItemData.class);
                    humanTaskNodeInstance.getNotCompletedDeadlineTimers().putAll(buildDeadlines(workItemData.getCompletedDeadlinesMap()));
                    humanTaskNodeInstance.getNotCompletedReassigments().putAll(buildReassignments(workItemData.getCompletedReassigmentsMap()));
                    humanTaskNodeInstance.getNotStartedDeadlineTimers().putAll(buildDeadlines(workItemData.getStartDeadlinesMap()));
                    humanTaskNodeInstance.getNotStartedReassignments().putAll(buildReassignments(workItemData.getStartReassigmentsMap()));

                    if (workItemData.hasTaskName()) {
                        workItem.setTaskName(workItemData.getTaskName());
                    }
                    if (workItemData.hasTaskDescription()) {
                        workItem.setTaskDescription(workItemData.getTaskDescription());
                    }
                    if (workItemData.hasTaskPriority()) {
                        workItem.setTaskPriority(workItemData.getTaskPriority());
                    }
                    if (workItemData.hasTaskReferenceName()) {
                        workItem.setReferenceName(workItemData.getTaskReferenceName());
                    }
                    if (workItemData.hasActualOwner()) {
                        workItem.setActualOwner(workItemData.getActualOwner());
                    }
                    workItem.getAdminUsers().addAll(workItemData.getAdminUsersList());
                    workItem.getAdminGroups().addAll(workItemData.getAdminGroupsList());
                    workItem.getPotentialUsers().addAll(workItemData.getPotUsersList());
                    workItem.getPotentialGroups().addAll(workItemData.getPotGroupsList());
                    workItem.getExcludedUsers().addAll(workItemData.getExcludedUsersList());
                    workItem.getComments().putAll(workItemData.getCommentsList().stream().map(this::buildComment).collect(Collectors.toMap(Comment::getId, Function.identity())));
                    workItem.getAttachments().putAll(workItemData.getAttachmentsList().stream().map(this::buildAttachment).collect(Collectors.toMap(Attachment::getId, Function.identity())));

                }

            }

            RuleFlowProcessInstance ruleFlowProcessInstance = context.get(MarshallerContextName.MARSHALLER_PROCESS_INSTANCE);
            nodeInstance.internalSetWorkItemId(content.getWorkItemId());
            InternalKogitoWorkItem workItem = (InternalKogitoWorkItem) nodeInstance.getWorkItem();
            workItem.setId(content.getWorkItemId());
            workItem.setProcessInstanceId(ruleFlowProcessInstance.getStringId());
            workItem.setName(content.getName());
            workItem.setState(content.getState());
            workItem.setDeploymentId(ruleFlowProcessInstance.getDeploymentId());
            workItem.setProcessInstance(ruleFlowProcessInstance);
            workItem.setPhaseId(content.getPhaseId());
            workItem.setPhaseStatus(content.getPhaseStatus());
            workItem.setStartDate(new Date(content.getStartDate()));
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

    private WorkItemNodeInstance instanceWorkItem(WorkItemNodeInstanceContent content) {
        if (content.hasWorkItemData()) {
            Any workItemDataMessage = content.getWorkItemData();
            if (workItemDataMessage.is(HumanTaskWorkItemData.class)) {
                HumanTaskNodeInstance nodeInstance = new HumanTaskNodeInstance();
                HumanTaskWorkItemImpl workItem = new HumanTaskWorkItemImpl();
                nodeInstance.internalSetWorkItem(workItem);
                return nodeInstance;
            } else {
                throw new ProcessInstanceMarshallerException("Don't know which type of work item is");
            }
        } else {
            WorkItemNodeInstance nodeInstance = new WorkItemNodeInstance();
            KogitoWorkItemImpl workItem = new KogitoWorkItemImpl();
            workItem.setId(UUID.randomUUID().toString());
            nodeInstance.internalSetWorkItem(workItem);
            return nodeInstance;
        }
    }

    private Comment buildComment(KogitoWorkItemsProtobuf.Comment comment) {
        Comment result = new Comment(comment.getId(), comment.getUpdatedBy());
        result.setContent(comment.getContent());
        result.setUpdatedAt(new Date(comment.getUpdatedAt()));
        return result;
    }

    private Attachment buildAttachment(KogitoWorkItemsProtobuf.Attachment attachment) {
        Attachment result = new Attachment(attachment.getId(), attachment.getUpdatedBy());
        result.setContent(URI.create(attachment.getContent()));
        result.setUpdatedAt(new Date(attachment.getUpdatedAt()));
        result.setName(attachment.getName());
        return result;
    }

    private Map<String, Map<String, Object>> buildDeadlines(Map<String, KogitoWorkItemsProtobuf.Deadline> deadlinesProtobuf) {
        Map<String, Map<String, Object>> deadlines = new HashMap<>();
        for (Map.Entry<String, KogitoWorkItemsProtobuf.Deadline> entry : deadlinesProtobuf.entrySet()) {
            Map<String, Object> notification = new HashMap<>();
            for (Map.Entry<String, String> pair : entry.getValue().getContentMap().entrySet()) {
                notification.put(pair.getKey(), pair.getValue());
            }
            deadlines.put(entry.getKey(), notification);
        }
        return deadlines;
    }

    private Map<String, Reassignment> buildReassignments(Map<String, KogitoWorkItemsProtobuf.Reassignment> reassignmentsProtobuf) {
        Map<String, Reassignment> reassignments = new HashMap<>();
        for (Map.Entry<String, KogitoWorkItemsProtobuf.Reassignment> entry : reassignmentsProtobuf.entrySet()) {
            reassignments.put(entry.getKey(), new Reassignment(entry.getValue().getUsersList().stream().collect(Collectors
                    .toSet()), entry.getValue().getGroupsList().stream().collect(Collectors.toSet())));
        }
        return reassignments;
    }
}
