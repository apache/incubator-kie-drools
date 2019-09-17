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

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.kie.kogito.index.event.KogitoCloudEvent;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceState;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singleton;

public final class TestUtils {

    private TestUtils() {
    }

    public static String getTravelsProtoBufferFile() throws Exception {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource("travels.proto").toURI());
        return new String(Files.readAllBytes(path));
    }

    public static KogitoCloudEvent getTravelsCloudEvent(String processId, String processInstanceId, String type, ProcessInstanceState status, String rootProcessInstanceId, String rootProcessId) throws Exception {
        return KogitoCloudEvent.builder()
                .id(UUID.randomUUID().toString())
                .rootProcessInstanceId(rootProcessInstanceId)
                .rootProcessId(rootProcessId)
                .processId(processId)
                .state(status.ordinal())
                .contentType("application/json")
                .processInstanceId(processInstanceId)
                .type(type)
                .source(URI.create("http://localhost:8080/"))
                .time(new Date())
                .data(getProcessInstance(processId, processInstanceId, status.ordinal(), rootProcessInstanceId, rootProcessId))
                .build();
    }

    private static ProcessInstance getProcessInstance(String processId, String processInstanceId, Integer status, String rootProcessInstanceId, String rootProcessId) throws Exception {
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
        return newArrayList(ni);
    }

    private static String getProcessInstanceVariables() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        Map<String, Object> traveller = new HashMap<>();
        traveller.put("firstName", "Maciej");
        builder.add("traveller", Json.createObjectBuilder(traveller).build());
        Map<String, Object> hotel = new HashMap<>();
        hotel.put("name", "Meriton");
        builder.add("hotel", Json.createObjectBuilder(hotel).build());
        Map<String, Object> flight = new HashMap<>();
        flight.put("flightNumber", "MX555");
        flight.put("arrival", "2019-08-20T22:12:57.340Z");
        flight.put("departure", "2019-08-20T07:12:57.340Z");
        builder.add("flight", Json.createObjectBuilder(flight).build());
        return builder.build().toString();
    }
}
