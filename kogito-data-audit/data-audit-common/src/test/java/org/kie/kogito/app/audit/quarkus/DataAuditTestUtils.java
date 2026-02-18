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
package org.kie.kogito.app.audit.quarkus;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorEventBody;
import org.kie.kogito.event.process.ProcessInstanceEventMetadata;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
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
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceEventMetadata;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableEventBody;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.process.ProcessInstance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class DataAuditTestUtils {

    public static final String ADDONS = "jobs-management,prometheus-monitoring,process-management";

    private static String toURIEndpoint(String processId) {
        return URI.create("http://localhost:8080/" + processId).toString();
    }

    public static String wrapQuery(String query) {
        return "{ \"query\" : \"" + query + " \"}";
    }

    public static JobInstanceDataEvent newJobEvent(String jobId, String nodeInstanceId, Integer priority,
            String processId, String procesInstanceId, Long repeatInterval, Integer repeatLimit, String rootProcessId, String rootProcessInstanceId,
            JobStatus state, Integer executionCounter) throws Exception {
        return newJobEvent(jobId, nodeInstanceId, priority, processId, procesInstanceId, repeatInterval, repeatLimit, rootProcessId, rootProcessInstanceId, state, executionCounter, null, null);
    }

    public static JobInstanceDataEvent newJobEvent(String jobId, String nodeInstanceId, Integer priority,
            String processId, String procesInstanceId, Long repeatInterval, Integer repeatLimit, String rootProcessId, String rootProcessInstanceId,
            JobStatus state, Integer executionCounter, String exceptionMessage, String exceptionDetails) throws Exception {

        ScheduledJob job = new ScheduledJob();
        job.setId(jobId);
        job.setNodeInstanceId(nodeInstanceId);
        job.setCallbackEndpoint("https://callback");
        job.setPriority(priority);
        job.setProcessId(processId);
        job.setProcessInstanceId(procesInstanceId);
        job.setRepeatInterval(repeatInterval);
        job.setRepeatLimit(repeatLimit);
        job.setRootProcessId(rootProcessId);
        job.setRootProcessInstanceId(rootProcessInstanceId);

        job = ScheduledJob.builder()
                .job(job)
                .status(state)
                .executionCounter(executionCounter)
                .retries(executionCounter) // Set retries to match executionCounter for testing
                .scheduledId("my scheduler")
                .expirationTime(ZonedDateTime.now())
                .exceptionMessage(exceptionMessage)
                .exceptionDetails(exceptionDetails)
                .build();

        JobInstanceDataEvent dataEvent =
                new JobInstanceDataEvent("JobEvent", toURIEndpoint(processId), new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsBytes(job), procesInstanceId,
                        rootProcessInstanceId, processId, rootProcessId, (String) "identity");
        return dataEvent;
    }

    public static JobInstanceDataEvent deriveNewState(JobInstanceDataEvent jobEvent, Integer executionCounter, JobStatus state) throws Exception {
        return deriveNewState(jobEvent, executionCounter, state, null, null);
    }

    public static JobInstanceDataEvent deriveNewState(JobInstanceDataEvent jobEvent, Integer executionCounter, JobStatus state, String exceptionMessage, String exceptionDetails) throws Exception {
        ScheduledJob job = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(jobEvent.getData(), ScheduledJob.class);
        job = ScheduledJob.builder()
                .job(job)
                .status(state)
                .executionCounter(executionCounter)
                .retries(executionCounter) // Set retries to match executionCounter for testing
                .scheduledId("my scheduler")
                .expirationTime(ZonedDateTime.now())
                .exceptionMessage(exceptionMessage)
                .exceptionDetails(exceptionDetails)
                .build();

        JobInstanceDataEvent dataEvent = new JobInstanceDataEvent("JobEvent", jobEvent.getSource().toString(), new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsBytes(job),
                jobEvent.getKogitoProcessInstanceId(),
                jobEvent.getKogitoRootProcessInstanceId(), jobEvent.getKogitoProcessId(), jobEvent.getKogitoRootProcessId(), (String) "identity");
        return dataEvent;
    }

    public static ProcessInstanceStateDataEvent newProcessInstanceStateEvent(
            String processId, String processInstanceId, Integer status, String rootProcessInstanceId, String rootProcessId,
            String parentProcessInstanceId, String identity, int eventType) {

        String processVersion = "1.0";
        String processType = "BPMN2";
        ProcessInstanceStateEventBody body = ProcessInstanceStateEventBody.create()
                .processInstanceId(processInstanceId)
                .parentInstanceId(parentProcessInstanceId)
                .rootProcessInstanceId(rootProcessInstanceId)
                .rootProcessId(rootProcessId)
                .processId(processId)
                .processType(processType)
                .processVersion(processVersion)
                .processName(UUID.randomUUID().toString())
                .eventDate(new Date())
                .state(status)
                .businessKey("BusinessKey" + processInstanceId)
                .roles("admin", "role2")
                .eventUser(identity)
                .eventType(eventType)
                .build();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, processInstanceId);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, processVersion);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, processId);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA, String.valueOf(status));
        metadata.put(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA, processType);
        metadata.put(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA, parentProcessInstanceId);
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA, rootProcessId);
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA, rootProcessInstanceId);

        ProcessInstanceStateDataEvent event =
                new ProcessInstanceStateDataEvent(toURIEndpoint(processId), ADDONS, (String) identity, metadata, body);

        event.setKogitoBusinessKey(body.getBusinessKey());
        return event;
    }

    public static ProcessInstanceStateDataEvent deriveProcessInstanceStateEvent(ProcessInstanceStateDataEvent event, String identity, int status, int eventType) {

        ProcessInstanceStateEventBody body = ProcessInstanceStateEventBody.create()
                .processInstanceId(event.getData().getProcessInstanceId())
                .parentInstanceId(event.getData().getParentInstanceId())
                .rootProcessInstanceId(event.getData().getRootProcessInstanceId())
                .rootProcessId(event.getData().getRootProcessId())
                .processId(event.getData().getProcessId())
                .processType(event.getData().getProcessType())
                .processVersion(event.getData().getProcessVersion())
                .processName(event.getData().getProcessName())
                .eventDate(new Date())
                .state(status)
                .businessKey(event.getData().getBusinessKey())
                .roles("admin")
                .eventUser(identity)
                .eventType(eventType)
                .build();

        ProcessInstanceStateDataEvent newEvent =
                new ProcessInstanceStateDataEvent(toURIEndpoint(body.getProcessId()), ADDONS, (String) identity,
                        extractProcessInstnaceEventMetadata(event), body);
        newEvent.setKogitoBusinessKey(body.getBusinessKey());
        return newEvent;
    }

    public static ProcessInstanceNodeDataEvent newProcessInstanceNodeEvent(
            ProcessInstanceStateDataEvent pEvent,
            String nodeType, String nodeDefintionId, String nodeInstanceId, String nodeName, String connection, String identity, int eventType) {

        ProcessInstanceNodeEventBody body = ProcessInstanceNodeEventBody.create()
                .processInstanceId(pEvent.getData().getProcessInstanceId())
                .processId(pEvent.getData().getProcessId())
                .processVersion(pEvent.getData().getProcessVersion())
                .eventDate(new Date())
                .eventUser(identity)
                .eventType(eventType)
                .nodeName(nodeName)
                .nodeType(nodeType)
                .nodeDefinitionId(nodeDefintionId)
                .nodeInstanceId(nodeInstanceId)
                .connectionNodeDefinitionId(connection)
                .build();

        ProcessInstanceNodeDataEvent event = new ProcessInstanceNodeDataEvent(toURIEndpoint(body.getProcessId()),
                ADDONS, (String) identity,
                extractProcessInstnaceEventMetadata(pEvent), body);

        event.setKogitoBusinessKey(pEvent.getKogitoBusinessKey());
        return event;
    }

    public static ProcessInstanceVariableDataEvent newProcessInstanceVariableEvent(
            ProcessInstanceStateDataEvent pEvent,
            String variableId, String variableName, Object variableValue, String identity) {

        ProcessInstanceVariableEventBody body = ProcessInstanceVariableEventBody.create()
                .eventDate(new Date())
                .eventUser(identity)
                .processId(pEvent.getData().getProcessId())
                .processVersion(pEvent.getData().getProcessVersion())
                .processInstanceId(pEvent.getData().getProcessInstanceId())
                .variableId(variableId)
                .variableName(variableName)
                .variableValue(variableValue)
                .build();

        ProcessInstanceVariableDataEvent event = new ProcessInstanceVariableDataEvent(toURIEndpoint(body.getProcessId()),
                ADDONS, (String) identity,
                extractProcessInstnaceEventMetadata(pEvent), body);

        event.setKogitoBusinessKey(pEvent.getKogitoBusinessKey());
        return event;
    }

    public static ProcessInstanceErrorDataEvent newProcessInstanceErrorEvent(
            ProcessInstanceStateDataEvent pEvent,
            String nodeDefintionId, String nodeInstanceId, String errorMessage, String identity) {

        ProcessInstanceErrorEventBody body = ProcessInstanceErrorEventBody.create()
                .eventDate(new Date())
                .eventUser(identity)
                .processId(pEvent.getData().getProcessId())
                .processVersion(pEvent.getData().getProcessVersion())
                .processInstanceId(pEvent.getData().getProcessInstanceId())
                .nodeDefinitionId(nodeDefintionId)
                .nodeInstanceId(nodeInstanceId)
                .errorMessage(errorMessage)
                .build();

        ProcessInstanceErrorDataEvent event = new ProcessInstanceErrorDataEvent(toURIEndpoint(body.getProcessId()),
                ADDONS, (String) identity,
                extractProcessInstnaceEventMetadata(pEvent), body);

        event.setKogitoBusinessKey(pEvent.getKogitoBusinessKey());
        return event;
    }

    private static Map<String, Object> extractProcessInstnaceEventMetadata(ProcessInstanceDataEvent<?> pEvent) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, pEvent.getKogitoProcessInstanceId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, pEvent.getKogitoProcessInstanceVersion());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, pEvent.getKogitoProcessId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA, pEvent.getKogitoProcessInstanceState());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA, pEvent.getKogitoProcessType());
        metadata.put(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA, pEvent.getKogitoParentProcessInstanceId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA, pEvent.getKogitoRootProcessId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA, pEvent.getKogitoRootProcessInstanceId());
        return metadata;
    }

    // user task instance stuff
    public static UserTaskInstanceStateDataEvent newUserTaskInstanceStateEvent(
            String eventUser, String userTaskDefinitionId, String userTaskInstanceId, String userTaskName, String eventType,
            String userTaskDescription, String userTaskPriority, String userTaskReferenceName, String state, String actualOwner, String processInstanceId) {

        String processId = UUID.randomUUID().toString();
        String processVersion = "1.0";
        String processType = "BPMN2";

        UserTaskInstanceStateEventBody body = UserTaskInstanceStateEventBody.create()
                .eventUser(eventUser)
                .eventDate(new Date())
                .userTaskDefinitionId(userTaskDefinitionId)
                .userTaskInstanceId(userTaskInstanceId)
                .userTaskName(userTaskName)
                .eventType(eventType)
                .userTaskDescription(userTaskDescription)
                .userTaskDescription(userTaskPriority)
                .userTaskReferenceName(userTaskReferenceName)
                .state(state)
                .actualOwner(actualOwner)
                .processInstanceId(processInstanceId)
                .build();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, processInstanceId);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, processVersion);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, processId);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA, String.valueOf(ProcessInstance.STATE_ACTIVE));
        metadata.put(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA, processType);
        metadata.put(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA, null);
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA, null);
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA, null);
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_ID_META_DATA, userTaskInstanceId);
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_STATE_META_DATA, state);

        UserTaskInstanceStateDataEvent event =
                new UserTaskInstanceStateDataEvent(toURIEndpoint(processId), ADDONS, (String) eventUser,
                        metadata, body);

        event.setKogitoBusinessKey(UUID.randomUUID().toString());
        return event;
    }

    public static UserTaskInstanceVariableDataEvent newUserTaskInstanceVariableEvent(UserTaskInstanceStateDataEvent pEvent,
            String eventUser, String variableId, String variableName, String variableType, Object variableValue) {

        UserTaskInstanceVariableEventBody body = UserTaskInstanceVariableEventBody.create()
                .eventUser(eventUser)
                .eventDate(new Date())
                .userTaskDefinitionId(pEvent.getData().getUserTaskDefinitionId())
                .userTaskInstanceId(pEvent.getData().getUserTaskInstanceId())
                .userTaskName(pEvent.getData().getUserTaskName())
                .variableId(variableId)
                .variableName(variableName)
                .variableType(variableType)
                .variableValue(variableValue)
                .build();

        UserTaskInstanceVariableDataEvent event =
                new UserTaskInstanceVariableDataEvent(toURIEndpoint(pEvent.getKogitoProcessId()), ADDONS,
                        (String) eventUser,
                        extractUserTaskInstnaceEventMetadata(pEvent), body);

        event.setKogitoBusinessKey(pEvent.getKogitoBusinessKey());
        return event;
    }

    public static UserTaskInstanceAssignmentDataEvent newUserTaskInstanceAssignmentEvent(UserTaskInstanceStateDataEvent pEvent,
            String eventUser, String assigmentType, String... users) {

        UserTaskInstanceAssignmentEventBody body = UserTaskInstanceAssignmentEventBody.create()
                .eventUser(eventUser)
                .eventDate(new Date())
                .userTaskDefinitionId(pEvent.getData().getUserTaskDefinitionId())
                .userTaskInstanceId(pEvent.getData().getUserTaskInstanceId())
                .userTaskName(pEvent.getData().getUserTaskName())
                .assignmentType(assigmentType)
                .users(users)
                .build();

        UserTaskInstanceAssignmentDataEvent event =
                new UserTaskInstanceAssignmentDataEvent(toURIEndpoint(pEvent.getKogitoProcessId()), ADDONS,
                        (String) eventUser,
                        extractUserTaskInstnaceEventMetadata(pEvent), body);

        event.setKogitoBusinessKey(pEvent.getKogitoBusinessKey());
        return event;
    }

    public static UserTaskInstanceAttachmentDataEvent newUserTaskInstanceAttachmentEvent(UserTaskInstanceStateDataEvent pEvent,
            String eventUser, String attachmentId, String attachmentName, URI attachmentURI, Integer eventType) {

        UserTaskInstanceAttachmentEventBody body = UserTaskInstanceAttachmentEventBody.create()
                .eventUser(eventUser)
                .eventDate(new Date())
                .userTaskDefinitionId(pEvent.getData().getUserTaskDefinitionId())
                .userTaskInstanceId(pEvent.getData().getUserTaskInstanceId())
                .userTaskName(pEvent.getData().getUserTaskName())
                .attachmentId(attachmentId)
                .attachmentName(attachmentName)
                .attachmentURI(attachmentURI)
                .eventType(eventType)
                .build();

        UserTaskInstanceAttachmentDataEvent event =
                new UserTaskInstanceAttachmentDataEvent(toURIEndpoint(pEvent.getKogitoProcessId()), ADDONS,
                        (String) eventUser,
                        extractUserTaskInstnaceEventMetadata(pEvent), body);

        event.setKogitoBusinessKey(pEvent.getKogitoBusinessKey());
        return event;
    }

    public static UserTaskInstanceDeadlineDataEvent newUserTaskInstanceDeadlineEvent(UserTaskInstanceStateDataEvent pEvent,
            String eventUser, Map<String, Object> inputs, Map<String, Object> notifications) {

        UserTaskInstanceDeadlineEventBody body = UserTaskInstanceDeadlineEventBody.create()
                .eventUser(eventUser)
                .eventDate(new Date())
                .userTaskDefinitionId(pEvent.getData().getUserTaskDefinitionId())
                .userTaskInstanceId(pEvent.getData().getUserTaskInstanceId())
                .userTaskName(pEvent.getData().getUserTaskName())
                .inputs(inputs)
                .notification(notifications)
                .build();

        UserTaskInstanceDeadlineDataEvent event =
                new UserTaskInstanceDeadlineDataEvent(toURIEndpoint(pEvent.getKogitoProcessId()), ADDONS,
                        (String) eventUser,
                        extractUserTaskInstnaceEventMetadata(pEvent), body);

        event.setKogitoBusinessKey(pEvent.getKogitoBusinessKey());
        return event;
    }

    public static UserTaskInstanceCommentDataEvent newUserTaskInstanceCommentEvent(UserTaskInstanceStateDataEvent pEvent,
            String eventUser, String commentId, String commentContent, Integer eventType) {

        UserTaskInstanceCommentEventBody body = UserTaskInstanceCommentEventBody.create()
                .eventUser(eventUser)
                .eventDate(new Date())
                .userTaskDefinitionId(pEvent.getData().getUserTaskDefinitionId())
                .userTaskInstanceId(pEvent.getData().getUserTaskInstanceId())
                .userTaskName(pEvent.getData().getUserTaskName())
                .commentId(commentId)
                .commentContent(commentContent)
                .eventType(eventType)
                .build();

        UserTaskInstanceCommentDataEvent event =
                new UserTaskInstanceCommentDataEvent(toURIEndpoint(pEvent.getKogitoProcessId()), ADDONS,
                        (String) eventUser,
                        extractUserTaskInstnaceEventMetadata(pEvent), body);

        event.setKogitoBusinessKey(pEvent.getKogitoBusinessKey());
        return event;
    }

    private static Map<String, Object> extractUserTaskInstnaceEventMetadata(UserTaskInstanceDataEvent<?> pEvent) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, pEvent.getKogitoProcessInstanceId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, pEvent.getKogitoProcessInstanceVersion());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, pEvent.getKogitoProcessId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA, pEvent.getKogitoProcessInstanceState());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA, pEvent.getKogitoProcessType());
        metadata.put(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA, pEvent.getKogitoParentProcessInstanceId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA, pEvent.getKogitoRootProcessId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA, pEvent.getKogitoRootProcessInstanceId());
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_ID_META_DATA, pEvent.getKogitoUserTaskInstanceId());
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_STATE_META_DATA, pEvent.getKogitoUserTaskInstanceState());
        return metadata;
    }
}
