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

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;
import org.kie.kogito.index.model.Attachment;
import org.kie.kogito.index.model.Comment;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.MilestoneStatus;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceError;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

public class TestUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ProcessDefinition createProcessDefinition(String processId, String version, Set<String> roles) {
        ProcessDefinition pd = new ProcessDefinition();
        pd.setId(processId);
        pd.setName(RandomStringUtils.randomAlphabetic(10));
        pd.setVersion(version);
        pd.setAddons(singleton("kogito-events"));
        pd.setRoles(roles);
        return pd;
    }

    public static ProcessInstanceStateDataEvent createProcessInstanceEvent(String processInstance,
            String processId, String rootProcessInstanceId, String rootProcessId, Integer status) {
        ProcessInstanceStateDataEvent event = new ProcessInstanceStateDataEvent();
        event.setKogitoProcessId(processId);
        event.setKogitoRootProcessId(rootProcessId);
        event.setKogitoRootProcessInstanceId(rootProcessInstanceId);
        event.setKogitoProcessInstanceId(processInstance);
        event.setData(ProcessInstanceStateEventBody.create().processId(processId).rootProcessId(rootProcessId).rootProcessInstanceId(rootProcessInstanceId).processInstanceId(processInstance)
                .state(status).build());
        return event;
    }

    public static ProcessInstanceVariableDataEvent createProcessInstanceVariableEvent(String processInstance,
            String processId, String firstName, String lastName) {
        ProcessInstanceVariableDataEvent event = new ProcessInstanceVariableDataEvent();
        event.setKogitoProcessId(processId);
        event.setKogitoProcessInstanceId(processInstance);
        event.setData(ProcessInstanceVariableEventBody.create().processId(processId).processInstanceId(processInstance)
                .variableName("traveller").variableValue(ObjectMapperFactory.get().createObjectNode().put("firstName", firstName).put("lastName", lastName)).build());
        return event;
    }

    public static ProcessInstance createProcessInstance(String processInstanceId,
            String processId,
            String rootProcessInstanceId,
            String rootProcessId,
            Integer status,
            long timeInterval) {
        return createProcessInstance(processInstanceId,
                processId,
                rootProcessInstanceId,
                rootProcessId,
                status,
                timeInterval,
                "Bar",
                "Swi");
    }

    public static ProcessInstance createProcessInstance(String processInstanceId,
            String processId,
            String rootProcessInstanceId,
            String rootProcessId,
            Integer status,
            long timeInterval,
            String firstName,
            String lastName) {
        ProcessInstance pi = new ProcessInstance();
        pi.setId(processInstanceId);
        pi.setProcessId(processId);
        pi.setProcessName(RandomStringUtils.randomAlphabetic(10));
        pi.setRootProcessInstanceId(rootProcessInstanceId);
        pi.setParentProcessInstanceId(rootProcessInstanceId);
        pi.setRootProcessId(rootProcessId);
        pi.setRoles(singleton("admin"));
        pi.setVariables(createProcessInstanceVariables(firstName, lastName));
        pi.setNodes(createNodeInstances(timeInterval));
        pi.setState(status);
        pi.setStart(Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli() + timeInterval).atZone(ZoneOffset.UTC));
        pi.setEnd(status == ProcessInstanceState.COMPLETED.ordinal()
                ? Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli() + timeInterval).atZone(ZoneOffset.UTC).plus(1, ChronoUnit.HOURS)
                : null);
        if (ProcessInstanceState.ERROR.ordinal() == status) {
            pi.setError(new ProcessInstanceError("StartEvent_1", "Something went wrong"));
        }
        pi.setMilestones(singletonList(Milestone.builder().id(UUID.randomUUID().toString()).name("testMilestone").status(MilestoneStatus.COMPLETED.name()).build()));
        return pi;
    }

    private static List<NodeInstance> createNodeInstances(long timeInterval) {
        final NodeInstance ni1 = new NodeInstance();
        ni1.setId(UUID.randomUUID().toString());
        ni1.setEnter(Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli() + timeInterval).atZone(ZoneOffset.UTC));
        ni1.setName("StartProcess");
        ni1.setType("StartNode");
        ni1.setNodeId("1");
        ni1.setDefinitionId("StartEvent_1");
        final NodeInstance ni2 = new NodeInstance();
        ni2.setId(UUID.randomUUID().toString());
        ni2.setEnter(Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli() + timeInterval).atZone(ZoneOffset.UTC));
        ni2.setName("End Event 1");
        ni2.setType("EndNode");
        ni2.setNodeId("2");
        ni2.setDefinitionId("EndEvent_1");
        return List.of(ni1, ni2);
    }

    public static ObjectNode createDomainData(String id, String firstName, String lastName) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", id);
        Map<String, Object> traveller = new HashMap<>();
        traveller.put("firstName", firstName);
        traveller.put("lastName", lastName);
        traveller.put("email", "tek@email.com");
        traveller.put("age", 27);
        json.put("traveller", traveller);
        Map<String, Object> hotel = new HashMap<>();
        hotel.put("name", "Perfect hotel");
        hotel.put("phone", "09876543");
        hotel.put("bookingNumber", "XX-012345");
        json.put("hotel", hotel);
        Map<String, Object> flight = new HashMap<>();
        flight.put("flightNumber", "MX555");
        flight.put("arrival", "2019-08-20T22:12:57.340Z");
        flight.put("departure", "2019-08-20T07:12:57.340Z");
        json.put("flight", flight);
        Map<String, Object> trip = new HashMap<>();
        trip.put("begin", "2019-08-24T22:00:00Z");
        trip.put("end", "2019-08-30T22:00:00Z");
        trip.put("city", "Boston");
        trip.put("country", "US");
        json.put("trip", trip);
        return MAPPER.valueToTree(json);
    }

    private static ObjectNode createProcessInstanceVariables(String firstName, String lastName) {
        return createDomainData(null, firstName, lastName);
    }

    public static Job createJob(String jobId, String processInstanceId, String processId, String rootProcessInstanceId, String rootProcessId, String status, long timeInterval) {
        Job job = new Job();
        job.setId(jobId);
        job.setProcessId(processId);
        job.setProcessInstanceId(processInstanceId);
        job.setRootProcessId(rootProcessId);
        job.setRootProcessInstanceId(rootProcessInstanceId);
        job.setStatus(status);
        job.setExpirationTime(Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli() + timeInterval).atZone(ZoneOffset.UTC));
        job.setPriority(1);
        job.setCallbackEndpoint("http://service");
        job.setRepeatInterval(0L);
        job.setRepeatLimit(-1);
        job.setScheduledId(UUID.randomUUID().toString());
        job.setRetries(10);
        job.setLastUpdate(Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli() + timeInterval).atZone(ZoneOffset.UTC));
        job.setExecutionCounter(2);
        return job;
    }

    public static UserTaskInstance createUserTaskInstance(String taskId, String processInstanceId, String processId, String rootProcessInstanceId, String rootProcessId, String state,
            long timeInterval) {
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
        task.setStarted(Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli() + timeInterval).atZone(ZoneOffset.UTC));
        task.setCompleted("Completed".equals(state) ? Instant.ofEpochMilli(ZonedDateTime.now().toInstant().toEpochMilli() + timeInterval).atZone(ZoneOffset.UTC).plus(1, ChronoUnit.HOURS) : null);
        task.setActualOwner("kogito");
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
