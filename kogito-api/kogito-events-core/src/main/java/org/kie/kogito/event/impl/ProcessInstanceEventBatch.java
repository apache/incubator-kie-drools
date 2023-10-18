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
package org.kie.kogito.event.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessNodeEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.event.usertask.UserTaskAssignmentEvent;
import org.kie.api.event.usertask.UserTaskAttachmentEvent;
import org.kie.api.event.usertask.UserTaskCommentEvent;
import org.kie.api.event.usertask.UserTaskDeadlineEvent;
import org.kie.api.event.usertask.UserTaskEvent;
import org.kie.api.event.usertask.UserTaskStateEvent;
import org.kie.api.event.usertask.UserTaskVariableEvent;
import org.kie.kogito.Addons;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventBatch;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorEventBody;
import org.kie.kogito.event.process.ProcessInstanceEventMetadata;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLAEventBody;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceEventMetadata;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableEventBody;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;

public class ProcessInstanceEventBatch implements EventBatch {

    private String service;
    private Addons addons;
    Collection<DataEvent<?>> processedEvents;

    public ProcessInstanceEventBatch(String service, Addons addons) {
        this.service = service;
        this.addons = addons != null ? addons : Addons.EMTPY;
        this.processedEvents = new ArrayList<>();
    }

    @Override
    public void append(Object rawEvent) {
        if (rawEvent instanceof ProcessEvent) {
            addDataEvent((ProcessEvent) rawEvent);
        } else if (rawEvent instanceof UserTaskEvent) {
            addDataEvent((UserTaskEvent) rawEvent);
        }
    }

    @Override
    public Collection<DataEvent<?>> events() {
        return processedEvents;
    }

    private void addDataEvent(ProcessEvent event) {
        // process events
        if (event instanceof ProcessStartedEvent) {
            handleProcessStateEvent((ProcessStartedEvent) event);
        } else if (event instanceof ProcessCompletedEvent) {
            handleProcessStateEvent((ProcessCompletedEvent) event);
        } else if (event instanceof ProcessNodeTriggeredEvent) {
            handleProcesssNodeEvent((ProcessNodeTriggeredEvent) event);
        } else if (event instanceof ProcessNodeLeftEvent) {
            handleProcesssNodeEvent((ProcessNodeLeftEvent) event);
        } else if (event instanceof SLAViolatedEvent) {
            handleProcesssNodeEvent((SLAViolatedEvent) event);
        } else if (event instanceof ProcessVariableChangedEvent) {
            handleProcesssVariableEvent((ProcessVariableChangedEvent) event);
        }
    }

    private void handleProcesssVariableEvent(ProcessVariableChangedEvent event) {
        // custom data fields for this event that are not there
        // private String nodeContainerDefinitionId;
        // private String nodeContainerInstanceId;

        Map<String, Object> metadata = buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance());

        ProcessInstanceVariableEventBody.Builder builder = ProcessInstanceVariableEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventIdentity())
                .processId(event.getProcessInstance().getProcessId())
                .processVersion(event.getProcessInstance().getProcessVersion())
                .processInstanceId(event.getProcessInstance().getId())
                .variableId(event.getVariableInstanceId())
                .variableName(event.getVariableId())
                .variableValue(event.getNewValue());

        ProcessInstanceVariableEventBody body = builder.build();
        processedEvents.add(new ProcessInstanceVariableDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), event.getEventIdentity(), metadata, body));
    }

    private void handleProcesssNodeEvent(SLAViolatedEvent event) {
        Map<String, Object> metadata = buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance());

        ProcessInstanceSLAEventBody.Builder builder = ProcessInstanceSLAEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventIdentity())
                .processId(event.getProcessInstance().getProcessId())
                .processVersion(event.getProcessInstance().getProcessVersion())
                .processInstanceId(event.getProcessInstance().getId());

        if (event.getNodeInstance() != null) {
            builder.nodeDefinitionId(event.getNodeInstance().getNode().getNodeUniqueId())
                    .nodeInstanceId(event.getNodeInstance().getId());
        }

        ProcessInstanceSLAEventBody body = builder.build();
        processedEvents.add(new ProcessInstanceSLADataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), event.getEventIdentity(), metadata, body));
    }

    private void handleProcesssNodeEvent(ProcessNodeLeftEvent event) {
        processedEvents.add(toProcessInstanceNodeEvent(event, ProcessInstanceNodeEventBody.EVENT_TYPE_EXIT));
    }

    private void handleProcesssNodeEvent(ProcessNodeTriggeredEvent event) {
        processedEvents.add(toProcessInstanceNodeEvent(event, ProcessInstanceNodeEventBody.EVENT_TYPE_ENTER));

    }

    private ProcessInstanceNodeDataEvent toProcessInstanceNodeEvent(ProcessNodeEvent event, int eventType) {
        Map<String, Object> metadata = buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance());

        ProcessInstanceNodeEventBody.Builder builder = ProcessInstanceNodeEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventIdentity())
                .eventType(eventType)
                .processId(event.getProcessInstance().getProcessId())
                .processVersion(event.getProcessInstance().getProcessVersion())
                .processInstanceId(event.getProcessInstance().getId())
                .nodeName(event.getNodeInstance().getNodeName())
                .nodeType(event.getNodeInstance().getNode().getClass().getSimpleName())
                .nodeInstanceId(event.getNodeInstance().getId())
                .nodeDefinitionId(event.getNodeInstance().getNode().getNodeUniqueId());

        ProcessInstanceNodeEventBody body = builder.build();
        return new ProcessInstanceNodeDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), event.getEventIdentity(), metadata, body);
    }

    private void handleProcessStateEvent(ProcessCompletedEvent event) {

        processedEvents.add(toProcessInstanceStateEvent(event, ProcessInstanceStateEventBody.EVENT_TYPE_ENDED));

        KogitoWorkflowProcessInstance pi = (KogitoWorkflowProcessInstance) event.getProcessInstance();
        if (pi.getState() == KogitoProcessInstance.STATE_ERROR) {
            ProcessInstanceErrorEventBody errorBody = ProcessInstanceErrorEventBody.create()
                    .eventDate(new Date())
                    .eventUser(event.getEventIdentity())
                    .processInstanceId(pi.getId())
                    .processId(pi.getProcessId())
                    .processVersion(pi.getProcessVersion())
                    .nodeDefinitionId(pi.getNodeIdInError())
                    .errorMessage(pi.getErrorMessage())
                    .build();
            Map<String, Object> metadata = buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance());
            processedEvents.add(new ProcessInstanceErrorDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), event.getEventIdentity(), metadata, errorBody));
        }
    }

    private void handleProcessStateEvent(ProcessStartedEvent event) {
        processedEvents.add(toProcessInstanceStateEvent(event, ProcessInstanceStateEventBody.EVENT_TYPE_STARTED));

        KogitoWorkflowProcessInstance pi = (KogitoWorkflowProcessInstance) event.getProcessInstance();
        if (pi.getState() == KogitoProcessInstance.STATE_ERROR) {
            ProcessInstanceErrorEventBody errorBody = ProcessInstanceErrorEventBody.create()
                    .eventDate(new Date())
                    .eventUser(event.getEventIdentity())
                    .processInstanceId(pi.getId())
                    .processId(pi.getProcessId())
                    .processVersion(pi.getProcessVersion())
                    .nodeDefinitionId(pi.getNodeIdInError())
                    .errorMessage(pi.getErrorMessage())
                    .build();
            Map<String, Object> metadata = buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance());
            processedEvents.add(new ProcessInstanceErrorDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), event.getEventIdentity(), metadata, errorBody));
        }

    }

    private ProcessInstanceStateDataEvent toProcessInstanceStateEvent(ProcessEvent event, int eventType) {
        Map<String, Object> metadata = buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance());

        KogitoWorkflowProcessInstance pi = (KogitoWorkflowProcessInstance) event.getProcessInstance();

        ProcessInstanceStateEventBody.Builder builder = ProcessInstanceStateEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventIdentity())
                .eventType(eventType)
                .processId(event.getProcessInstance().getProcessId())
                .processVersion(event.getProcessInstance().getProcessVersion())
                .processInstanceId(event.getProcessInstance().getId())
                .processName(event.getProcessInstance().getProcessName())
                .processVersion(event.getProcessInstance().getProcessVersion())
                .processType(event.getProcessInstance().getProcess().getType())
                .parentInstanceId(pi.getParentProcessInstanceId())
                .rootProcessId(pi.getRootProcessId())
                .rootProcessInstanceId(pi.getRootProcessInstanceId())
                .state(event.getProcessInstance().getState());

        String securityRoles = (String) event.getProcessInstance().getProcess().getMetaData().get("securityRoles");
        if (securityRoles != null) {
            builder.roles(securityRoles.split(","));
        }

        ProcessInstanceStateEventBody body = builder.build();
        return new ProcessInstanceStateDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), event.getEventIdentity(), metadata, body);
    }

    private Map<String, Object> buildProcessMetadata(KogitoWorkflowProcessInstance pi) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, pi.getId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, pi.getProcessVersion());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, pi.getProcessId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA, String.valueOf(pi.getState()));
        metadata.put(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA, pi.getProcess().getType());
        metadata.put(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA, pi.getParentProcessInstanceId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA, pi.getRootProcessId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA, pi.getRootProcessInstanceId());
        return metadata;
    }

    private void addDataEvent(UserTaskEvent event) {
        // this should go in another event types
        if (event instanceof UserTaskStateEvent) {
            handleUserTaskStateEvent((UserTaskStateEvent) event);
        } else if (event instanceof UserTaskDeadlineEvent) {
            handleUserTaskDeadlineEvent((UserTaskDeadlineEvent) event);
        } else if (event instanceof UserTaskAssignmentEvent) {
            handleUserTaskAssignmentEvent((UserTaskAssignmentEvent) event);
        } else if (event instanceof UserTaskVariableEvent) {
            handleUserTaskVariableEvent((UserTaskVariableEvent) event);
        } else if (event instanceof UserTaskAttachmentEvent) {
            handleUserTaskAttachmentEvent((UserTaskAttachmentEvent) event);
        } else if (event instanceof UserTaskCommentEvent) {
            handleUserTaskCommentEvent((UserTaskCommentEvent) event);
        }
    }

    private void handleUserTaskCommentEvent(UserTaskCommentEvent event) {
        Map<String, Object> metadata = buildUserTaskMetadata((HumanTaskWorkItem) event.getWorkItem());
        metadata.putAll(buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance()));

        int eventType = UserTaskInstanceCommentEventBody.EVENT_TYPE_ADDED;
        if (event.getOldComment() != null && event.getNewComment() == null) {
            eventType = UserTaskInstanceCommentEventBody.EVENT_TYPE_DELETED;
        } else if (event.getOldComment() != null && event.getNewComment() != null) {
            eventType = UserTaskInstanceCommentEventBody.EVENT_TYPE_CHANGE;
        }

        UserTaskInstanceCommentEventBody.Builder builder = UserTaskInstanceCommentEventBody.create()
                .eventType(eventType)
                .userTaskDefinitionId(event.getUserTaskDefinitionId())
                .userTaskInstanceId(((HumanTaskWorkItem) event.getWorkItem()).getStringId())
                .userTaskName(((HumanTaskWorkItem) event.getWorkItem()).getTaskName());

        String updatedBy = null;
        switch (eventType) {
            case UserTaskInstanceCommentEventBody.EVENT_TYPE_ADDED:
            case UserTaskInstanceCommentEventBody.EVENT_TYPE_CHANGE:
                builder.commentContent(event.getNewComment().getCommentContent())
                        .commentId(event.getNewComment().getCommentId())
                        .eventDate(event.getNewComment().getUpdatedAt())
                        .eventUser(event.getNewComment().getUpdatedBy());
                updatedBy = event.getNewComment().getUpdatedBy();
                break;
            case UserTaskInstanceCommentEventBody.EVENT_TYPE_DELETED:
                builder.commentId(event.getOldComment().getCommentId())
                        .eventDate(event.getOldComment().getUpdatedAt())
                        .eventUser(event.getOldComment().getUpdatedBy());

                updatedBy = event.getOldComment().getUpdatedBy();
                break;
        }

        UserTaskInstanceCommentEventBody body = builder.build();
        processedEvents.add(new UserTaskInstanceCommentDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), updatedBy, metadata, body));

    }

    private void handleUserTaskAttachmentEvent(UserTaskAttachmentEvent event) {
        Map<String, Object> metadata = buildUserTaskMetadata((HumanTaskWorkItem) event.getWorkItem());
        metadata.putAll(buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance()));

        int eventType = UserTaskInstanceAttachmentEventBody.EVENT_TYPE_ADDED;
        if (event.getOldAttachment() != null && event.getNewAttachment() == null) {
            eventType = UserTaskInstanceAttachmentEventBody.EVENT_TYPE_DELETED;
        } else if (event.getOldAttachment() != null && event.getNewAttachment() != null) {
            eventType = UserTaskInstanceAttachmentEventBody.EVENT_TYPE_CHANGE;
        }

        UserTaskInstanceAttachmentEventBody.Builder builder = UserTaskInstanceAttachmentEventBody.create()
                .eventType(eventType)
                .userTaskDefinitionId(event.getUserTaskDefinitionId())
                .userTaskInstanceId(((HumanTaskWorkItem) event.getWorkItem()).getStringId())
                .userTaskName(((HumanTaskWorkItem) event.getWorkItem()).getTaskName());

        String updatedBy = null;
        switch (eventType) {
            case UserTaskInstanceAttachmentEventBody.EVENT_TYPE_ADDED:
            case UserTaskInstanceAttachmentEventBody.EVENT_TYPE_CHANGE:
                builder.attachmentName(event.getNewAttachment().getAttachmentName())
                        .attachmentId(event.getNewAttachment().getAttachmentId())
                        .attachmentURI(event.getNewAttachment().getAttachmentURI())
                        .eventDate(event.getNewAttachment().getUpdatedAt())
                        .eventUser(event.getNewAttachment().getUpdatedBy());
                updatedBy = event.getNewAttachment().getUpdatedBy();

                break;
            case UserTaskInstanceAttachmentEventBody.EVENT_TYPE_DELETED:
                builder.attachmentId(event.getOldAttachment().getAttachmentId())
                        .eventDate(event.getOldAttachment().getUpdatedAt())
                        .eventUser(event.getOldAttachment().getUpdatedBy());
                updatedBy = event.getOldAttachment().getUpdatedBy();
                break;
        }

        UserTaskInstanceAttachmentEventBody body = builder.build();
        processedEvents
                .add(new UserTaskInstanceAttachmentDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), updatedBy, metadata, body));

    }

    private void handleUserTaskAssignmentEvent(UserTaskAssignmentEvent event) {
        Map<String, Object> metadata = buildUserTaskMetadata((HumanTaskWorkItem) event.getWorkItem());
        metadata.putAll(buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance()));

        UserTaskInstanceAssignmentEventBody.Builder builder = UserTaskInstanceAssignmentEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventUser())
                .userTaskDefinitionId(event.getUserTaskDefinitionId())
                .userTaskInstanceId(((HumanTaskWorkItem) event.getWorkItem()).getStringId())
                .userTaskName(((HumanTaskWorkItem) event.getWorkItem()).getTaskName())
                .assignmentType(event.getAssignmentType())
                .users(event.getNewUsersId());

        UserTaskInstanceAssignmentEventBody body = builder.build();
        processedEvents.add(new UserTaskInstanceAssignmentDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), event.getEventUser(), metadata, body));
    }

    private void handleUserTaskDeadlineEvent(UserTaskDeadlineEvent event) {
        Map<String, Object> metadata = buildUserTaskMetadata((HumanTaskWorkItem) event.getWorkItem());
        metadata.putAll(buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance()));

        UserTaskInstanceDeadlineEventBody.Builder builder = UserTaskInstanceDeadlineEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventUser())
                .userTaskDefinitionId(event.getUserTaskDefinitionId())
                .userTaskInstanceId(((HumanTaskWorkItem) event.getWorkItem()).getStringId())
                .userTaskName(((HumanTaskWorkItem) event.getWorkItem()).getTaskName())
                .notification(event.getNotification());

        UserTaskInstanceDeadlineEventBody body = builder.build();
        processedEvents.add(new UserTaskInstanceDeadlineDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), event.getEventUser(), metadata, body));
    }

    private void handleUserTaskStateEvent(UserTaskStateEvent event) {
        Map<String, Object> metadata = buildUserTaskMetadata((HumanTaskWorkItem) event.getWorkItem());
        metadata.putAll(buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance()));

        UserTaskInstanceStateEventBody.Builder builder = UserTaskInstanceStateEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventUser())
                .userTaskDefinitionId(event.getUserTaskDefinitionId())
                .userTaskInstanceId(((HumanTaskWorkItem) event.getWorkItem()).getStringId())
                .userTaskName(((HumanTaskWorkItem) event.getWorkItem()).getTaskName())
                .userTaskDescription(((HumanTaskWorkItem) event.getWorkItem()).getTaskDescription())
                .userTaskPriority(((HumanTaskWorkItem) event.getWorkItem()).getTaskPriority())
                .userTaskReferenceName(((HumanTaskWorkItem) event.getWorkItem()).getReferenceName())
                .state(((HumanTaskWorkItem) event.getWorkItem()).getPhaseStatus())
                .actualOwner(((HumanTaskWorkItem) event.getWorkItem()).getActualOwner())
                .processInstanceId(event.getProcessInstance().getId());

        UserTaskInstanceStateEventBody body = builder.build();
        processedEvents.add(new UserTaskInstanceStateDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), event.getEventUser(), metadata, body));
    }

    private void handleUserTaskVariableEvent(UserTaskVariableEvent event) {
        Map<String, Object> metadata = buildUserTaskMetadata((HumanTaskWorkItem) event.getWorkItem());
        metadata.putAll(buildProcessMetadata((KogitoWorkflowProcessInstance) event.getProcessInstance()));

        UserTaskInstanceVariableEventBody.Builder builder = UserTaskInstanceVariableEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getEventUser())
                .userTaskDefinitionId(event.getUserTaskDefinitionId())
                .userTaskInstanceId(((HumanTaskWorkItem) event.getWorkItem()).getStringId())
                .userTaskName(((HumanTaskWorkItem) event.getWorkItem()).getTaskName())
                .variableId(event.getVariableName())
                .variableName(event.getVariableName())
                .variableValue(event.getNewValue())
                .variableType(event.getVariableType().name());

        UserTaskInstanceVariableEventBody body = builder.build();

        processedEvents.add(new UserTaskInstanceVariableDataEvent(buildSource(event.getProcessInstance().getProcessId()), addons.toString(), event.getEventUser(), metadata, body));

    }

    private Map<String, Object> buildUserTaskMetadata(HumanTaskWorkItem pi) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_ID_META_DATA, pi.getStringId());
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_REFERENCE_ID_META_DATA, pi.getReferenceName());
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_STATE_META_DATA, pi.getPhaseStatus());

        return metadata;
    }

    protected String extractRuntimeSource(Map<String, String> metadata) {
        return buildSource(metadata.get(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA));
    }

    private String buildSource(String processId) {
        if (processId == null) {
            return null;
        } else {
            return service + "/" + (processId.contains(".") ? processId.substring(processId.lastIndexOf('.') + 1) : processId);
        }
    }
}
