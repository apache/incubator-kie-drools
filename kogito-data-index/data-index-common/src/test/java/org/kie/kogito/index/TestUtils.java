/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.kie.kogito.event.process.AttachmentEventBody;
import org.kie.kogito.event.process.CommentEventBody;
import org.kie.kogito.event.process.MilestoneEventBody;
import org.kie.kogito.event.process.NodeInstanceEventBody;
import org.kie.kogito.event.process.ProcessErrorEventBody;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceEventBody;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceEventBody;
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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyMap;
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
            return buf.toString(StandardCharsets.UTF_8.name());
        }
    }

    public static ProcessInstanceDataEvent getProcessCloudEvent(String processId, String processInstanceId, ProcessInstanceState status, String rootProcessInstanceId, String rootProcessId,
            String parentProcessInstanceId) {

        ProcessInstanceEventBody body = ProcessInstanceEventBody.create()
                .id(processInstanceId)
                .parentInstanceId(parentProcessInstanceId)
                .rootInstanceId(rootProcessInstanceId)
                .processId(processId)
                .rootProcessId(rootProcessId)
                .processName(RandomStringUtils.randomAlphabetic(10))
                .startDate(new Date())
                .endDate(status == ProcessInstanceState.COMPLETED ? Date.from(Instant.now().plus(1, ChronoUnit.HOURS)) : null)
                .state(status.ordinal())
                .businessKey(RandomStringUtils.randomAlphabetic(10))
                .variables(getProcessInstanceVariablesMap())
                .milestones(Set.of(
                        MilestoneEventBody.create()
                                .id(MILESTONE_ID)
                                .name("SimpleMilestone")
                                .status(MilestoneStatus.AVAILABLE.name())
                                .build()))
                .roles("admin")
                .nodeInstance(NodeInstanceEventBody.create()
                        .id(processInstanceId + "-1")
                        .triggerTime(new Date())
                        .nodeName("Start")
                        .nodeType("StartNode")
                        .nodeId("1")
                        .nodeDefinitionId("StartEvent_1")
                        .leaveTime(status == ProcessInstanceState.COMPLETED ? Date.from(Instant.now().plus(1, ChronoUnit.HOURS)) : null)
                        .build())
                .error(status == ProcessInstanceState.ERROR ? ProcessErrorEventBody.create()
                        .nodeDefinitionId("StartEvent_1")
                        .errorMessage("Something went wrong")
                        .build() : null)
                .build();

        return new ProcessInstanceDataEvent(URI.create("http://localhost:8080/" + processId).toString(), "jobs-management,prometheus-monitoring,process-management", null, body.metaData(), body);
    }

    public static ProcessInstance getProcessInstance(String processId, String processInstanceId, Integer status, String rootProcessInstanceId, String rootProcessId) {
        ProcessInstance pi = new ProcessInstance();
        pi.setId(processInstanceId);
        pi.setProcessId(processId);
        pi.setProcessName(RandomStringUtils.randomAlphabetic(10));
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
        pi.setBusinessKey(RandomStringUtils.randomAlphabetic(10));
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
        return newArrayList(ni);
    }

    private static List<Milestone> getMilestones() {
        return newArrayList(
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

    public static UserTaskInstanceDataEvent getUserTaskCloudEvent(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId, String state) {
        return getUserTaskCloudEvent(taskId, processId, processInstanceId, rootProcessInstanceId, rootProcessId, state, "kogito");
    }

    public static UserTaskInstanceDataEvent getUserTaskCloudEvent(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId, String state,
            String actualOwner) {

        UserTaskInstanceEventBody body = UserTaskInstanceEventBody.create()
                .id(taskId)
                .state(state)
                .taskName("TaskName")
                .taskDescription("TaskDescription")
                .taskPriority("High")
                .actualOwner(actualOwner)
                .startDate(new Date())
                .completeDate(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .adminGroups(singleton("admin"))
                .adminUsers(singleton("kogito"))
                .excludedUsers(singleton("excluded"))
                .potentialGroups(singleton("potentialGroup"))
                .potentialUsers(singleton("potentialUser"))
                .processInstanceId(processInstanceId)
                .rootProcessInstanceId(rootProcessInstanceId)
                .processId(processId)
                .rootProcessId(rootProcessId)
                .comments(singleton(getTaskComment("commentId" + taskId, "kogito", "Comment 1")))
                .attachments(singleton(getTaskAttachment("attachmentId" + taskId, "kogito", "http://linltodoc.com/1", "doc1")))
                .inputs(emptyMap())
                .outputs(emptyMap())
                .build();

        return new UserTaskInstanceDataEvent(URI.create("http://localhost:8080/" + processId).toString(), null, null, body.metaData(), body);
    }

    public static AttachmentEventBody getTaskAttachment(String id, String user, String name, String content) {
        return AttachmentEventBody.create()
                .id(id)
                .updatedBy(user)
                .name(name)
                .content(content == null ? null : URI.create(content))
                .updatedAt(new Date())
                .build();
    }

    public static CommentEventBody getTaskComment(String id, String user, String comment) {
        return CommentEventBody.create()
                .id(id)
                .content(comment)
                .updatedBy(user)
                .updatedAt(new Date())
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
