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
package org.kie.kogito.index.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorEventBody;
import org.kie.kogito.event.process.ProcessInstanceEventMetadata;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceEventMetadata;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateEventBody;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.model.Attachment;
import org.kie.kogito.index.model.Comment;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.MilestoneStatus;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceError;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.model.UserTaskInstance;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static java.util.Collections.singleton;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

public final class TestUtils {

    private static final String MILESTONE_ID = UUID.randomUUID().toString();

    private TestUtils() {
    }

    public static int getPortFromConfig() {
        return ConfigProvider.getConfig().getOptionalValue("quarkus.http.test-port", Integer.class).orElse(8081);
    }

    public static String getDealsProtoBufferFile() throws Exception {
        return readFileContent("deals.proto");
    }

    public static String getTravelsProtoBufferFile() throws Exception {
        return readFileContent("travels.proto");
    }

    public static String readFileContent(String file) throws IOException {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            Objects.requireNonNull(inputStream, "Could not resolve file path: " + file);
            int result = bis.read();
            while (result != -1) {
                buf.write((byte) result);
                result = bis.read();
            }
            return buf.toString(StandardCharsets.UTF_8);
        }
    }

    public static ProcessInstanceStateDataEvent getProcessCloudEvent(String processId, String processInstanceId, ProcessInstanceState status, String rootProcessInstanceId, String rootProcessId,
            String parentProcessInstanceId, String identity) {

        int eventType = status.equals(ProcessInstanceState.COMPLETED) ? ProcessInstanceStateEventBody.EVENT_TYPE_ENDED : ProcessInstanceStateEventBody.EVENT_TYPE_STARTED;
        ProcessInstanceStateEventBody body = ProcessInstanceStateEventBody.create()
                .processInstanceId(processInstanceId)
                .parentInstanceId(parentProcessInstanceId)
                .rootProcessInstanceId(rootProcessInstanceId)
                .rootProcessId(rootProcessId)
                .processId(processId)
                .processVersion("1.0")
                .processName(RandomStringUtils.randomAlphabetic(10))
                .eventDate(new Date())
                .state(status.ordinal())
                .businessKey(RandomStringUtils.randomAlphabetic(10))
                .roles("admin")
                .eventUser(identity)
                .eventType(eventType)
                .build();

        return new ProcessInstanceStateDataEvent(URI.create("http://localhost:8080/" + processId).toString(), "jobs-management,prometheus-monitoring,process-management", (String) identity,
                body.metaData(), body);

    }

    public static ProcessInstanceErrorDataEvent deriveErrorProcessCloudEvent(ProcessInstanceStateDataEvent event, String errorMessage, String nodeDefinition, String nodeInstanceId) {

        ProcessInstanceErrorEventBody body = ProcessInstanceErrorEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getData().getEventUser())
                .processId(event.getData().getProcessId())
                .processInstanceId(event.getData().getProcessInstanceId())
                .processVersion(event.getData().getProcessVersion())
                .errorMessage(errorMessage)
                .nodeDefinitionId(nodeDefinition)
                .nodeInstanceId(nodeInstanceId)
                .build();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, event.getKogitoProcessInstanceId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, event.getKogitoProcessInstanceVersion());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, event.getKogitoProcessId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA, event.getKogitoProcessInstanceState());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA, event.getKogitoProcessType());
        metadata.put(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA, event.getKogitoParentProcessInstanceId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA, event.getKogitoRootProcessId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA, event.getKogitoRootProcessInstanceId());

        return new ProcessInstanceErrorDataEvent(event.getSource().toString(), event.getKogitoAddons(), event.getKogitoIdentity(), metadata, body);

    }

    public static ProcessInstanceVariableDataEvent deriveProcessVariableCloudEvent(ProcessInstanceStateDataEvent event, String variableName, Object value) {

        ProcessInstanceVariableEventBody body = ProcessInstanceVariableEventBody.create()
                .eventDate(new Date())
                .eventUser(event.getData().getEventUser())
                .variableId(variableName)
                .variableName(variableName)
                .variableValue(value)
                .processId(event.getData().getProcessId())
                .processInstanceId(event.getData().getProcessInstanceId())
                .processVersion(event.getData().getProcessVersion())
                .build();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, event.getKogitoProcessInstanceId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, event.getKogitoProcessInstanceVersion());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, event.getKogitoProcessId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA, event.getKogitoProcessInstanceState());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA, event.getKogitoProcessType());
        metadata.put(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA, event.getKogitoParentProcessInstanceId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA, event.getKogitoRootProcessId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA, event.getKogitoRootProcessInstanceId());

        return new ProcessInstanceVariableDataEvent(event.getSource().toString(), event.getKogitoAddons(), event.getKogitoIdentity(), metadata, body);

    }

    public static ProcessInstance getProcessInstance(String processId, String processInstanceId, Integer status, String rootProcessInstanceId, String rootProcessId) {
        ProcessInstance pi = new ProcessInstance();
        pi.setId(processInstanceId);
        pi.setProcessId(processId);
        pi.setVersion("1.0");
        pi.setProcessName(String.format("%s-name", processId));
        pi.setRootProcessInstanceId(rootProcessInstanceId);
        pi.setParentProcessInstanceId(rootProcessInstanceId);
        pi.setRootProcessId(rootProcessId);
        pi.setRoles(singleton("admin"));
        pi.setVariables(getProcessInstanceVariablesJson());
        pi.setNodes(getNodeInstances(status));
        pi.setState(status);
        pi.setStart(ZonedDateTime.now());
        pi.setEnd(status == ProcessInstanceState.COMPLETED.ordinal() ? ZonedDateTime.now().plus(1, ChronoUnit.HOURS) : null);
        if (ProcessInstanceState.ERROR.ordinal() == status) {
            pi.setError(new ProcessInstanceError("StartEvent_1", "Something went wrong"));
        }
        pi.setMilestones(getMilestones());
        pi.setBusinessKey(String.format("%s-key", processId));
        pi.setCreatedBy("currentUser");
        pi.setUpdatedBy("currentUser");
        return pi;
    }

    private static List<NodeInstance> getNodeInstances(Integer status) {
        final NodeInstance ni = new NodeInstance();
        ni.setId(UUID.randomUUID().toString());
        ni.setEnter(ZonedDateTime.now());
        ni.setName("Start");
        ni.setType("StartNode");
        ni.setNodeId("1");
        ni.setDefinitionId("StartEvent_1");
        ni.setExit(status == ProcessInstanceState.COMPLETED.ordinal() ? ZonedDateTime.now().plus(1, ChronoUnit.HOURS) : null);
        return List.of(ni);
    }

    private static List<Milestone> getMilestones() {
        return List.of(
                Milestone.builder()
                        .id(MILESTONE_ID)
                        .name("SimpleMilestone")
                        .status(MilestoneStatus.AVAILABLE.name())
                        .build());
    }

    public static Map<String, Object> getProcessInstanceVariablesMap() {
        Map<String, Object> json = new HashMap<>();
        Map<String, Object> traveller = new HashMap<>();
        traveller.put("firstName", "Maciej");
        json.put("traveller", traveller);
        Map<String, Object> hotel = new HashMap<>();
        hotel.put("name", "Meriton");
        json.put("hotel", hotel);
        Map<String, Object> flight = new HashMap<>();
        flight.put("flightNumber", "MX555");
        flight.put("arrival", "2019-08-20T22:12:57.340Z");
        flight.put("departure", "2019-08-20T07:12:57.340Z");
        json.put("flight", flight);
        return json;
    }

    private static ObjectNode getProcessInstanceVariablesJson() {
        return getObjectMapper().valueToTree(getProcessInstanceVariablesMap());
    }

    public static UserTaskInstanceStateDataEvent getUserTaskCloudEvent(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId, String state) {
        return getUserTaskCloudEvent(taskId, processId, processInstanceId, rootProcessInstanceId, rootProcessId, state, "kogito", 1);
    }

    public static UserTaskInstanceStateDataEvent getUserTaskCloudEvent(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId, String state,
            String actualOwner, Integer eventType) {

        UserTaskInstanceStateEventBody body = UserTaskInstanceStateEventBody.create()
                .eventType(eventType)
                .userTaskInstanceId(taskId)
                .state(state)
                .userTaskName("TaskName")
                .userTaskDescription("TaskDescription")
                .userTaskPriority("High")
                .actualOwner(actualOwner)
                .eventDate(new Date())
                .processInstanceId(processInstanceId)
                .build();
        UserTaskInstanceStateDataEvent event = new UserTaskInstanceStateDataEvent(URI.create("http://localhost:8080/" + processId).toString(), null, null, body.metaData(), body);
        event.setKogitoProcessId(processId);
        event.setKogitoProcessInstanceId(processInstanceId);
        event.setKogitoRootProcessId(rootProcessId);
        event.setKogitoRootProcessInstanceId(rootProcessInstanceId);
        return event;
    }

    public static UserTaskInstanceAttachmentDataEvent getUserTaskAttachmentEvent(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId,
            String state,
            String actualOwner, String attachmentId, URI attachmentURI, String attachmentName, Integer eventType) {

        UserTaskInstanceAttachmentEventBody attachmentBody = UserTaskInstanceAttachmentEventBody.create()
                .attachmentId(attachmentId)
                .attachmentName(attachmentName)
                .attachmentURI(attachmentURI)
                .eventDate(new Date())
                .eventType(UserTaskInstanceAttachmentEventBody.EVENT_TYPE_ADDED)
                .userTaskInstanceId(taskId)
                .build();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_ID_META_DATA, taskId);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, processInstanceId);
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_STATE_META_DATA, state);

        UserTaskInstanceAttachmentDataEvent attachmentEvent =
                new UserTaskInstanceAttachmentDataEvent(URI.create("http://localhost:8080/" + processId).toString(), null, null, metadata, attachmentBody);
        attachmentEvent.setKogitoProcessId(processId);
        attachmentEvent.setKogitoProcessInstanceId(processInstanceId);
        attachmentEvent.setKogitoRootProcessId(rootProcessId);
        attachmentEvent.setKogitoRootProcessInstanceId(rootProcessInstanceId);
        return attachmentEvent;
    }

    public static UserTaskInstanceCommentDataEvent getUserTaskCommentEvent(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId,
            String state,
            String actualOwner, String commentId, String commentContent, Integer eventType) {

        UserTaskInstanceCommentEventBody attachmentBody = UserTaskInstanceCommentEventBody.create()
                .commentId(commentId)
                .commentContent(commentContent)
                .eventDate(new Date())
                .eventType(UserTaskInstanceAttachmentEventBody.EVENT_TYPE_ADDED)
                .userTaskInstanceId(taskId)
                .build();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_ID_META_DATA, taskId);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, processInstanceId);
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_STATE_META_DATA, state);

        UserTaskInstanceCommentDataEvent attachmentEvent =
                new UserTaskInstanceCommentDataEvent(URI.create("http://localhost:8080/" + processId).toString(), null, null, metadata, attachmentBody);
        attachmentEvent.setKogitoProcessId(processId);
        attachmentEvent.setKogitoProcessInstanceId(processInstanceId);
        attachmentEvent.setKogitoRootProcessId(rootProcessId);
        attachmentEvent.setKogitoRootProcessInstanceId(rootProcessInstanceId);
        return attachmentEvent;
    }

    public static UserTaskInstanceAttachmentEventBody getTaskAttachment(String id, String user, String name, String content) {
        return UserTaskInstanceAttachmentEventBody.create()
                .attachmentId(id)
                .eventUser(user)
                .attachmentName(name)
                .attachmentURI(content == null ? null : URI.create(content))
                .eventDate(new Date())
                .build();
    }

    public static UserTaskInstanceCommentEventBody getTaskComment(String id, String user, String comment) {
        return UserTaskInstanceCommentEventBody.create()
                .commentId(id)
                .commentContent(comment)
                .eventUser(user)
                .eventDate(new Date())
                .build();
    }

    public static KogitoJobCloudEvent getJobCloudEvent(String jobId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId, String status) {
        return KogitoJobCloudEvent.builder()
                .id(UUID.randomUUID().toString())
                .source(URI.create("http://localhost:8080/jobs"))
                .data(getJob(jobId, processId, processInstanceId, rootProcessId, rootProcessInstanceId, status))
                .build();
    }

    public static Job getJob(String jobId, String processId, String processInstanceId, String rootProcessId, String rootProcessInstanceId, String status) {
        Job job = new Job();
        job.setId(jobId);
        job.setProcessId(processId);
        job.setProcessInstanceId(processInstanceId);
        job.setNodeInstanceId(UUID.randomUUID().toString());
        job.setRootProcessId(rootProcessId);
        job.setRootProcessInstanceId(rootProcessInstanceId);
        job.setStatus(status);
        job.setExpirationTime(ZonedDateTime.now());
        job.setPriority(1);
        job.setCallbackEndpoint("http://service");
        job.setRepeatInterval(0l);
        job.setRepeatLimit(-1);
        job.setScheduledId(UUID.randomUUID().toString());
        job.setRetries(10);
        job.setLastUpdate(ZonedDateTime.now());
        job.setExecutionCounter(2);
        return job;
    }

    public static UserTaskInstance getUserTaskInstance(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId, String state,
            String actualOwner) {
        UserTaskInstance task = new UserTaskInstance();
        task.setId(taskId);
        task.setProcessInstanceId(processInstanceId);
        task.setProcessId(processId);
        task.setRootProcessId(rootProcessId);
        task.setRootProcessInstanceId(rootProcessInstanceId);
        task.setName("TaskName");
        task.setDescription("TaskDescription");
        task.setState(state);
        task.setPriority("High");
        task.setStarted(ZonedDateTime.now());
        task.setCompleted(ZonedDateTime.now().plus(1, ChronoUnit.HOURS));
        task.setActualOwner(actualOwner);
        task.setAdminUsers(singleton("kogito"));
        task.setAdminGroups(singleton("admin"));
        task.setExcludedUsers(singleton("excluded"));
        task.setPotentialUsers(singleton("potentialUser"));
        task.setPotentialGroups(singleton("potentialGroup"));
        task.setComments(List.of(Comment.builder().id("commentId" + taskId).content("Comment 1").updatedBy("kogito").build()));
        task.setAttachments(List.of(Attachment.builder().id("attachmentId" + taskId).content("http://linltodoc.com/1").name("doc1").updatedBy("kogito").build()));
        return task;
    }
}
