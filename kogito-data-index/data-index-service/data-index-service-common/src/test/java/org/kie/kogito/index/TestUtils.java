/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.MilestoneStatus;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceError;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.model.UserTaskInstance;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singleton;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

public final class TestUtils {

    private TestUtils() {
    }

    public static int getPortFromConfig() {
        return ConfigProvider.getConfig().getOptionalValue("quarkus.https.test-port", Integer.class).orElse(8081);
    }

    public static String getDealsProtoBufferFile() throws Exception {
        return readFileContent("deals.proto");
    }

    public static String getTravelsProtoBufferFile() throws Exception {
        return readFileContent("travels.proto");
    }

    public static String readFileContent(String file) throws URISyntaxException, IOException {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource(file).toURI());
        return new String(Files.readAllBytes(path));
    }

    public static KogitoProcessCloudEvent getProcessCloudEvent(String processId, String processInstanceId, ProcessInstanceState status, String rootProcessInstanceId, String rootProcessId, String parentProcessInstanceId) {
        return KogitoProcessCloudEvent.builder()
                .id(UUID.randomUUID().toString())
                .rootProcessInstanceId(rootProcessInstanceId)
                .rootProcessId(rootProcessId)
                .parentProcessInstanceId(parentProcessInstanceId)
                .processId(processId)
                .state(status.ordinal())
                .contentType("application/json")
                .processInstanceId(processInstanceId)
                .type("ProcessInstanceEvent")
                .data(getProcessInstance(processId, processInstanceId, status.ordinal(), rootProcessInstanceId, rootProcessId))
                .kogitoReferenceId(UUID.randomUUID().toString())
                .schemaURL(URI.create("kogito"))
                .source(URI.create("http://localhost:8080/" + processId))
                .kogitoAddons("jobs-management,prometheus-monitoring,process-management")
                .time(ZonedDateTime.now())
                .build();
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
        pi.setVariables(getProcessInstanceVariables());
        pi.setNodes(getNodeInstances());
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

    private static List<NodeInstance> getNodeInstances() {
        final NodeInstance ni = new NodeInstance();
        ni.setId(UUID.randomUUID().toString());
        ni.setEnter(ZonedDateTime.now());
        ni.setName("Start");
        ni.setType("StartNode");
        ni.setNodeId("1");
        ni.setDefinitionId("StartEvent_1");
        return newArrayList(ni);
    }

    private static List<Milestone> getMilestones() {
        return newArrayList(
                Milestone.builder()
                        .id(UUID.randomUUID().toString())
                        .name("SimpleMilestone")
                        .status(MilestoneStatus.AVAILABLE.name())
                        .build()
        );
    }

    private static JsonNode getProcessInstanceVariables() {
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
        return getObjectMapper().valueToTree(json);
    }

    public static KogitoUserTaskCloudEvent getUserTaskCloudEvent(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId, String state) {
        return KogitoUserTaskCloudEvent.builder()
                .id(UUID.randomUUID().toString())
                .userTaskInstanceId(taskId)
                .rootProcessInstanceId(rootProcessInstanceId)
                .rootProcessId(rootProcessId)
                .processId(processId)
                .contentType("application/json")
                .processInstanceId(processInstanceId)
                .type("UserTaskInstanceEvent")
                .data(getUserTaskInstance(taskId, processId, processInstanceId, rootProcessInstanceId, rootProcessId, state))
                .source(URI.create("http://localhost:8080/" + processId))
                .time(ZonedDateTime.now())
                .build();
    }

    public static KogitoJobCloudEvent getJobCloudEvent(String jobId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId, String status) {
        return KogitoJobCloudEvent.builder()
                .id(UUID.randomUUID().toString())
                .source(URI.create("http://localhost:8080/jobs"))
                .data(getJob(jobId, processId, processInstanceId, rootProcessId, rootProcessInstanceId, status))
                .build();
    }

    private static Job getJob(String jobId, String processId, String processInstanceId, String rootProcessId, String rootProcessInstanceId, String status) {
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

    private static UserTaskInstance getUserTaskInstance(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId, String state) {
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
        task.setActualOwner("kogito");
        task.setAdminUsers(singleton("kogito"));
        task.setAdminGroups(singleton("admin"));
        task.setExcludedUsers(singleton("excluded"));
        task.setPotentialUsers(singleton("potentialUser"));
        task.setPotentialGroups(singleton("potentialGroup"));
        return task;
    }
}
