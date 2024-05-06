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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.flow.serialization.MarshallerWriterContext;
import org.jbpm.flow.serialization.NodeInstanceWriter;
import org.jbpm.flow.serialization.impl.ProtobufVariableWriter;
import org.jbpm.flow.serialization.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent;
import org.jbpm.flow.serialization.protobuf.KogitoWorkItemsProtobuf;
import org.jbpm.flow.serialization.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData;
import org.jbpm.process.instance.impl.humantask.Reassignment;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.Comment;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3.Builder;

public class WorkItemNodeInstanceWriter implements NodeInstanceWriter {

    @Override
    public boolean accept(NodeInstance value) {
        return value instanceof WorkItemNodeInstance;
    }

    @Override
    public Builder<?> write(MarshallerWriterContext context, NodeInstance value) {
        ProtobufVariableWriter varWriter = new ProtobufVariableWriter(context);
        WorkItemNodeInstance nodeInstance = (WorkItemNodeInstance) value;
        WorkItemNodeInstanceContent.Builder builder = WorkItemNodeInstanceContent.newBuilder();

        List<String> timerInstances = nodeInstance.getTimerInstances();
        if (timerInstances != null) {
            builder.addAllTimerInstanceId(timerInstances);
        }
        if (nodeInstance.getTimerInstancesReference() != null) {
            builder.putAllTimerInstanceReference(nodeInstance.getTimerInstancesReference());
        }
        if (nodeInstance.getExceptionHandlingProcessInstanceId() != null) {
            builder.setErrorHandlingProcessInstanceId(nodeInstance.getExceptionHandlingProcessInstanceId());
        }
        KogitoWorkItem workItem = nodeInstance.getWorkItem();

        builder.setWorkItemId(nodeInstance.getWorkItemId())
                .setName(workItem.getName())
                .setState(workItem.getState())
                .setPhaseId(workItem.getPhaseId())
                .setPhaseStatus(workItem.getPhaseStatus())
                .setStartDate(workItem.getStartDate().getTime())
                .addAllVariable(varWriter.buildVariables(new ArrayList<>(workItem.getParameters().entrySet())))
                .addAllResult(varWriter.buildVariables(new ArrayList<>(workItem.getResults().entrySet())));

        if (workItem.getCompleteDate() != null) {
            builder.setCompleteDate(workItem.getCompleteDate().getTime());
        }

        if (nodeInstance instanceof HumanTaskNodeInstance) {
            builder.setWorkItemData(Any.pack(buildHumanTaskWorkItemData((HumanTaskNodeInstance) nodeInstance, (HumanTaskWorkItem) nodeInstance.getWorkItem())));
        }
        return builder;
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
            KogitoWorkItemsProtobuf.Deadline.Builder builder = KogitoWorkItemsProtobuf.Deadline.newBuilder();
            entry.getValue().forEach((k, v) -> builder.putContent(k, v.toString()));
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
