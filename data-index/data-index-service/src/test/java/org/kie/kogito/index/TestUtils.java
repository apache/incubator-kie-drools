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
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.model.UserTaskInstance;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singleton;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

public final class TestUtils {

    private TestUtils() {
    }

    public static String getDealsProtoBufferFile() throws Exception {
        return getProtoFile("deals.proto");
    }

    public static String getTravelsProtoBufferFile() throws Exception {
        return getProtoFile("travels.proto");
    }

    private static String getProtoFile(String file) throws URISyntaxException, IOException {
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
                .source(URI.create("http://localhost:8080/"))
                .time(ZonedDateTime.now())
                .data(getProcessInstance(processId, processInstanceId, status.ordinal(), rootProcessInstanceId, rootProcessId))
                .build();
    }

    private static ProcessInstance getProcessInstance(String processId, String processInstanceId, Integer status, String rootProcessInstanceId, String rootProcessId) {
        ProcessInstance pi = new ProcessInstance();
        pi.setId(processInstanceId);
        pi.setProcessId(processId);
        pi.setRootProcessInstanceId(rootProcessInstanceId);
        pi.setParentProcessInstanceId(rootProcessInstanceId);
        pi.setRootProcessId(rootProcessId);
        pi.setRoles(singleton("admin"));
        pi.setVariables(getProcessInstanceVariables());
        pi.setNodes(getNodeInstances());
        pi.setEndpoint("http://localhost:8080/");
        pi.setState(status);
        pi.setStart(ZonedDateTime.now());
        pi.setEnd(status == ProcessInstanceState.COMPLETED.ordinal() ? ZonedDateTime.now().plus(1, ChronoUnit.HOURS) : null);
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

    public static KogitoUserTaskCloudEvent getUserTaskCloudEvent(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId) {
        return KogitoUserTaskCloudEvent.builder()
                .id(UUID.randomUUID().toString())
                .userTaskInstanceId(taskId)
                .rootProcessInstanceId(rootProcessInstanceId)
                .rootProcessId(rootProcessId)
                .processId(processId)
                .contentType("application/json")
                .processInstanceId(processInstanceId)
                .type("UserTaskInstanceEvent")
                .source(URI.create("http://localhost:8080/"))
                .time(ZonedDateTime.now())
                .data(getUserTaskInstance(taskId, processId, processInstanceId, rootProcessInstanceId, rootProcessId))
                .build();
    }

    private static UserTaskInstance getUserTaskInstance(String taskId, String processId, String processInstanceId, String rootProcessInstanceId, String rootProcessId) {
        UserTaskInstance task = new UserTaskInstance();
        task.setId(taskId);
        task.setProcessInstanceId(processInstanceId);
        task.setProcessId(processId);
        task.setRootProcessId(rootProcessId);
        task.setRootProcessInstanceId(rootProcessInstanceId);
        task.setName("TaskName");
        task.setDescription("TaskDescription");
        task.setState("InProgress");
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
