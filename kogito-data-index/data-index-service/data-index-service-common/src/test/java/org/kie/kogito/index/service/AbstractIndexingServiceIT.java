/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.model.MilestoneStatus;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasItems;
import static org.kie.kogito.index.DateTimeUtils.formatDateTime;
import static org.kie.kogito.index.DateTimeUtils.formatOffsetDateTime;
import static org.kie.kogito.index.DateTimeUtils.formatZonedDateTime;
import static org.kie.kogito.index.TestUtils.getJobCloudEvent;
import static org.kie.kogito.index.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.TestUtils.getProcessInstanceVariablesMap;
import static org.kie.kogito.index.TestUtils.getUserTaskCloudEvent;
import static org.kie.kogito.index.model.ProcessInstanceState.ACTIVE;
import static org.kie.kogito.index.model.ProcessInstanceState.COMPLETED;
import static org.kie.kogito.index.model.ProcessInstanceState.ERROR;
import static org.kie.kogito.index.service.GraphQLUtils.getJobById;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByBusinessKey;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceById;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndAddon;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndErrorNode;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndMilestoneName;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndMilestoneStatus;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndNullParentProcessInstanceId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndNullRootProcessInstanceId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndParentProcessInstanceId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndProcessId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndStart;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndState;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByParentProcessInstanceId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByRootProcessInstanceId;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceById;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndActualOwner;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndCompleted;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndPotentialGroups;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndPotentialUsers;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndProcessId;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndStarted;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndState;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdNoActualOwner;

public abstract class AbstractIndexingServiceIT extends AbstractIndexingIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIndexingServiceIT.class);

    Duration timeout = Duration.ofSeconds(30);

    @Inject
    public DataIndexStorageService cacheService;

    @BeforeAll
    static void setup() {
        RestAssured.config = RestAssured.config()
                .encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));
    }

    @AfterEach
    @Transactional
    void tearDown() {
        cacheService.getJobsCache().clear();
        cacheService.getProcessInstancesCache().clear();
        cacheService.getUserTaskInstancesCache().clear();
    }

    @Test
    //Reproducer for KOGITO-334
    void testDefaultGraphqlTypes() {
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.ProcessInstances", isA(Collection.class));

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{UserTaskInstances{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.UserTaskInstances", isA(Collection.class));

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{Jobs{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Jobs", isA(Collection.class));
    }

    protected void validateProcessInstance(String query, ProcessInstanceDataEvent event, String childProcessInstanceId) {
        LOGGER.debug("GraphQL query: {}", query);
        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body(query)
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.ProcessInstances[0].id", is(event.getData().getId()))
                        .body("data.ProcessInstances[0].processId", is(event.getData().getProcessId()))
                        .body("data.ProcessInstances[0].processName", is(event.getData().getProcessName()))
                        .body("data.ProcessInstances[0].rootProcessId", is(event.getData().getRootProcessId()))
                        .body("data.ProcessInstances[0].rootProcessInstanceId", is(event.getData().getRootInstanceId()))
                        .body("data.ProcessInstances[0].parentProcessInstanceId", is(event.getData().getParentInstanceId()))
                        .body("data.ProcessInstances[0].parentProcessInstance.id", event.getData().getParentInstanceId() == null ? is(nullValue()) : is(event.getData().getParentInstanceId()))
                        .body("data.ProcessInstances[0].parentProcessInstance.processName", event.getData().getParentInstanceId() == null ? is(nullValue()) : is(not(emptyOrNullString())))
                        .body("data.ProcessInstances[0].start", is(formatDateTime(event.getData().getStartDate())))
                        .body("data.ProcessInstances[0].end", event.getData().getEndDate() == null ? is(nullValue()) : is(formatDateTime(event.getData().getEndDate())))
                        .body("data.ProcessInstances[0].childProcessInstances[0].id", childProcessInstanceId == null ? is(nullValue()) : is(childProcessInstanceId))
                        .body("data.ProcessInstances[0].childProcessInstances[0].processName", childProcessInstanceId == null ? is(nullValue()) : is(not(emptyOrNullString())))
                        .body("data.ProcessInstances[0].endpoint", is(event.getSource().toString()))
                        .body("data.ProcessInstances[0].serviceUrl", event.getSource().toString().equals("/" + event.getData().getProcessId()) ? is(nullValue()) : is("http://localhost:8080"))
                        .body("data.ProcessInstances[0].addons", event.getKogitoAddons() == null ? is(nullValue()) : hasItems(event.getKogitoAddons().split(",")))
                        .body("data.ProcessInstances[0].error.message", event.getData().getError() == null ? is(nullValue()) : is(event.getData().getError().getErrorMessage()))
                        .body("data.ProcessInstances[0].error.nodeDefinitionId", event.getData().getError() == null ? is(nullValue()) : is(event.getData().getError().getNodeDefinitionId()))
                        .body("data.ProcessInstances[0].lastUpdate", is(formatOffsetDateTime(event.getTime())))
                        .body("data.ProcessInstances[0].nodes.size()", is(event.getData().getNodeInstances().size()))
                        .body("data.ProcessInstances[0].nodes[0].id", is(event.getData().getNodeInstances().stream().findFirst().get().getId()))
                        .body("data.ProcessInstances[0].nodes[0].name", is(event.getData().getNodeInstances().stream().findFirst().get().getNodeName()))
                        .body("data.ProcessInstances[0].nodes[0].nodeId", is(event.getData().getNodeInstances().stream().findFirst().get().getNodeId()))
                        .body("data.ProcessInstances[0].nodes[0].type", is(event.getData().getNodeInstances().stream().findFirst().get().getNodeType()))
                        .body("data.ProcessInstances[0].nodes[0].definitionId", is(event.getData().getNodeInstances().stream().findFirst().get().getNodeDefinitionId()))
                        .body("data.ProcessInstances[0].nodes[0].enter", is(formatDateTime(event.getData().getNodeInstances().stream().findFirst().get().getTriggerTime())))
                        .body("data.ProcessInstances[0].nodes[0].exit",
                                event.getData().getNodeInstances().stream().findFirst().get().getLeaveTime() == null ? is(nullValue())
                                        : is(formatDateTime(event.getData().getNodeInstances().stream().findFirst().get().getLeaveTime())))
                        .body("data.ProcessInstances[0].milestones.size()", is(event.getData().getMilestones().size()))
                        .body("data.ProcessInstances[0].milestones[0].id", is(event.getData().getMilestones().stream().findFirst().get().getId()))
                        .body("data.ProcessInstances[0].milestones[0].name", is(event.getData().getMilestones().stream().findFirst().get().getName()))
                        .body("data.ProcessInstances[0].milestones[0].status", is(event.getData().getMilestones().stream().findFirst().get().getStatus())));
    }

    protected void validateProcessInstance(String query, ProcessInstanceDataEvent event) {
        validateProcessInstance(query, event, null);
    }

    @Test
    void testProcessInstancePagination() {
        String processId = "travels";
        List<String> pIds = new ArrayList<>();

        IntStream.range(0, 100).forEach(i -> {
            String pId = UUID.randomUUID().toString();
            ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, pId, ACTIVE, null, null, null);
            indexProcessCloudEvent(startEvent);
            pIds.add(pId);
            await()
                    .atMost(timeout)
                    .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances { id } }\" }")
                            .when().post("/graphql")
                            .then().log().ifValidationFails().statusCode(200)
                            .body("data.ProcessInstances.size()", is(pIds.size())));
        });

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances(orderBy : {start: ASC}, pagination: {offset: 50, limit: 50}) { id, start } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.ProcessInstances.size()", is(50))
                        .body("data.ProcessInstances[0].id", is(pIds.get(50)))
                        .body("data.ProcessInstances[49].id", is(pIds.get(99))));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances(orderBy : {start: ASC}, pagination: {offset: 0, limit: 50}) { id, start } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.ProcessInstances.size()", is(50))
                        .body("data.ProcessInstances[0].id", is(pIds.get(0)))
                        .body("data.ProcessInstances[49].id", is(pIds.get(49))));
    }

    @Test
    void testUserTaskInstancePagination() {
        String processId = "deals";
        List<String> taskIds = new ArrayList<>();

        IntStream.range(0, 100).forEach(i -> {
            String taskId = UUID.randomUUID().toString();
            UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, UUID.randomUUID().toString(), null, null, "InProgress");
            indexUserTaskCloudEvent(event);
            taskIds.add(taskId);
            await()
                    .atMost(timeout)
                    .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{UserTaskInstances { id } }\" }")
                            .when().post("/graphql")
                            .then().log().ifValidationFails().statusCode(200)
                            .body("data.UserTaskInstances.size()", is(taskIds.size())));
        });

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{UserTaskInstances(orderBy : {started: ASC}, pagination: {offset: 0, limit: 50}) { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(50))
                        .body("data.UserTaskInstances[0].id", is(taskIds.get(0)))
                        .body("data.UserTaskInstances[49].id", is(taskIds.get(49))));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{UserTaskInstances(orderBy : {started: ASC}, pagination: {offset: 50, limit: 50}) { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(50))
                        .body("data.UserTaskInstances[0].id", is(taskIds.get(50)))
                        .body("data.UserTaskInstances[49].id", is(taskIds.get(99))));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{UserTaskInstances(orderBy : {started: ASC}, pagination: {offset: 0, limit: 100}) { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(taskIds.size()))
                        .body("data.UserTaskInstances[0].id", is(taskIds.get(0)))
                        .body("data.UserTaskInstances[99].id", is(taskIds.get(99))));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{UserTaskInstances(where: {state: {in: [\\\"InProgress\\\"]}}, orderBy : {started: ASC}, pagination: {offset: 0, limit: 100}) { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(taskIds.size()))
                        .body("data.UserTaskInstances[0].id", is(taskIds.get(0)))
                        .body("data.UserTaskInstances[99].id", is(taskIds.get(99))));
    }

    @Test
    void testProcessInstanceIndex() throws Exception {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();
        String subProcessId = processId + "_sub";
        String subProcessInstanceId = UUID.randomUUID().toString();

        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        validateProcessInstance(getProcessInstanceById(processInstanceId), startEvent);
        validateProcessInstance(getProcessInstanceByIdAndState(processInstanceId, ACTIVE), startEvent);
        validateProcessInstance(getProcessInstanceByIdAndProcessId(processInstanceId, processId), startEvent);
        validateProcessInstance(
                getProcessInstanceByIdAndStart(processInstanceId, formatDateTime(startEvent.getData().getStartDate())),
                startEvent);
        validateProcessInstance(getProcessInstanceByIdAndAddon(processInstanceId, "process-management"), startEvent);
        validateProcessInstance(getProcessInstanceByIdAndMilestoneName(processInstanceId, "SimpleMilestone"), startEvent);
        validateProcessInstance(getProcessInstanceByIdAndMilestoneStatus(processInstanceId, MilestoneStatus.AVAILABLE.name()),
                startEvent);
        validateProcessInstance(getProcessInstanceByBusinessKey(startEvent.getData().getBusinessKey()), startEvent);

        ProcessInstanceDataEvent endEvent = getProcessCloudEvent(processId, processInstanceId, COMPLETED, null, null, null);
        endEvent.getData().update().endDate(new Date());
        Map<String, Object> variablesMap = getProcessInstanceVariablesMap();
        ((Map<String, Object>) variablesMap.get("hotel")).put("name", "Ibis");
        ((Map<String, Object>) variablesMap.get("flight")).put("flightNumber", "QF444");
        endEvent.getData().update().variables(variablesMap);
        endEvent.getData().getMilestones().stream().findFirst().get().update().status(MilestoneStatus.COMPLETED.name());
        indexProcessCloudEvent(endEvent);

        validateProcessInstance(getProcessInstanceByIdAndState(processInstanceId, COMPLETED), endEvent);
        validateProcessInstance(getProcessInstanceByIdAndMilestoneStatus(processInstanceId, MilestoneStatus.COMPLETED.name()), endEvent);

        ProcessInstanceDataEvent event = getProcessCloudEvent(subProcessId, subProcessInstanceId, ACTIVE, processInstanceId,
                processId, processInstanceId);
        indexProcessCloudEvent(event);

        validateProcessInstance(getProcessInstanceByParentProcessInstanceId(processInstanceId), event);
        validateProcessInstance(getProcessInstanceByIdAndNullParentProcessInstanceId(processInstanceId, true), endEvent,
                subProcessInstanceId);
        validateProcessInstance(getProcessInstanceByRootProcessInstanceId(processInstanceId), event);
        validateProcessInstance(getProcessInstanceByIdAndNullRootProcessInstanceId(processInstanceId, true), endEvent,
                subProcessInstanceId);
        validateProcessInstance(getProcessInstanceById(processInstanceId), endEvent, subProcessInstanceId);
        validateProcessInstance(getProcessInstanceByIdAndParentProcessInstanceId(subProcessInstanceId, processInstanceId),
                event);

        ProcessInstanceDataEvent errorEvent = getProcessCloudEvent(subProcessId, subProcessInstanceId, ERROR, processInstanceId,
                processId, processInstanceId);
        indexProcessCloudEvent(errorEvent);

        validateProcessInstance(
                getProcessInstanceByIdAndErrorNode(subProcessInstanceId, errorEvent.getData().getError().getNodeDefinitionId()),
                errorEvent);
    }

    @Test
    void testUserTaskInstanceIndex() throws Exception {
        String taskId = UUID.randomUUID().toString();
        String state = "InProgress";
        String processId = "deals";
        String processInstanceId = UUID.randomUUID().toString();

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state);
        indexUserTaskCloudEvent(event);

        validateUserTaskInstance(getUserTaskInstanceById(taskId), event);
        validateUserTaskInstance(getUserTaskInstanceByIdAndActualOwner(taskId, "kogito"), event);
        validateUserTaskInstance(getUserTaskInstanceByIdAndProcessId(taskId, processId), event);
        validateUserTaskInstance(
                getUserTaskInstanceByIdAndPotentialGroups(taskId, new ArrayList<>(event.getData().getPotentialGroups())),
                event);
        validateUserTaskInstance(
                getUserTaskInstanceByIdAndPotentialUsers(taskId, new ArrayList<>(event.getData().getPotentialUsers())), event);
        validateUserTaskInstance(getUserTaskInstanceByIdAndState(taskId, event.getData().getState()), event);
        validateUserTaskInstance(getUserTaskInstanceByIdAndStarted(taskId, formatDateTime(event.getData().getStartDate())),
                event);
        validateUserTaskInstance(
                getUserTaskInstanceByIdAndCompleted(taskId, formatDateTime(event.getData().getCompleteDate())), event);

        event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state);
        event.getData().update().completeDate(new Date());
        event.getData().update().taskPriority("Low");
        event.getData().update().actualOwner("admin");
        event.getData().update().state("Completed");

        indexUserTaskCloudEvent(event);

        validateUserTaskInstance(getUserTaskInstanceByIdAndActualOwner(taskId, "admin"), event);

        event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state, null);
        indexUserTaskCloudEvent(event);

        validateUserTaskInstance(getUserTaskInstanceByIdNoActualOwner(taskId), event);
    }

    @Test
    void testJobIndex() {
        String jobId = UUID.randomUUID().toString();
        String processId = "deals";
        String processInstanceId = UUID.randomUUID().toString();

        KogitoJobCloudEvent event = getJobCloudEvent(jobId, processId, processInstanceId, null, null, "EXECUTED");

        indexJobCloudEvent(event);

        validateJob(getJobById(jobId), event);
    }

    protected void validateJob(String query, KogitoJobCloudEvent event) {
        LOGGER.debug("GraphQL query: {}", query);
        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body(query)
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.Jobs[0].id", is(event.getData().getId()))
                        .body("data.Jobs[0].processId", is(event.getData().getProcessId()))
                        .body("data.Jobs[0].processInstanceId", is(event.getData().getProcessInstanceId()))
                        .body("data.Jobs[0].nodeInstanceId", is(event.getData().getNodeInstanceId()))
                        .body("data.Jobs[0].rootProcessId", is(event.getData().getRootProcessId()))
                        .body("data.Jobs[0].rootProcessInstanceId", is(event.getData().getRootProcessInstanceId()))
                        .body("data.Jobs[0].status", is(event.getData().getStatus()))
                        .body("data.Jobs[0].expirationTime", is(formatZonedDateTime(event.getData().getExpirationTime())))
                        .body("data.Jobs[0].priority", is(event.getData().getPriority()))
                        .body("data.Jobs[0].callbackEndpoint", is(event.getData().getCallbackEndpoint()))
                        .body("data.Jobs[0].repeatInterval", is(event.getData().getRepeatInterval().intValue()))
                        .body("data.Jobs[0].repeatLimit", is(event.getData().getRepeatLimit()))
                        .body("data.Jobs[0].scheduledId", is(event.getData().getScheduledId()))
                        .body("data.Jobs[0].retries", is(event.getData().getRetries()))
                        .body("data.Jobs[0].lastUpdate", is(formatZonedDateTime(event.getData().getLastUpdate())))
                        .body("data.Jobs[0].executionCounter", is(event.getData().getExecutionCounter()))
                        .body("data.Jobs[0].endpoint", is(event.getData().getEndpoint())));
    }

    protected void validateUserTaskInstance(String query, UserTaskInstanceDataEvent event) {
        LOGGER.debug("GraphQL query: {}", query);
        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body(query)
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances[0].id", is(event.getData().getId()))
                        .body("data.UserTaskInstances[0].processId", is(event.getData().getProcessId()))
                        .body("data.UserTaskInstances[0].rootProcessId", is(event.getData().getRootProcessId()))
                        .body("data.UserTaskInstances[0].rootProcessInstanceId", is(event.getData().getRootProcessInstanceId()))
                        .body("data.UserTaskInstances[0].description", is(event.getData().getTaskDescription()))
                        .body("data.UserTaskInstances[0].name", is(event.getData().getTaskName()))
                        .body("data.UserTaskInstances[0].priority", is(event.getData().getTaskPriority()))
                        .body("data.UserTaskInstances[0].actualOwner", is(event.getData().getActualOwner()))
                        .body("data.UserTaskInstances[0].excludedUsers", hasItems(event.getData().getExcludedUsers().toArray()))
                        .body("data.UserTaskInstances[0].potentialUsers", hasItems(event.getData().getPotentialUsers().toArray()))
                        .body("data.UserTaskInstances[0].potentialGroups", hasItems(event.getData().getPotentialGroups().toArray()))
                        .body("data.UserTaskInstances[0].started", is(formatDateTime(event.getData().getStartDate())))
                        .body("data.UserTaskInstances[0].completed", is(formatDateTime(event.getData().getCompleteDate())))
                        .body("data.UserTaskInstances[0].lastUpdate", is(formatOffsetDateTime(event.getTime())))
                        .body("data.UserTaskInstances[0].endpoint",
                                is(event.getSource().toString() + "/" + event.getData().getProcessInstanceId() + "/" + event.getData().getTaskName() + "/" + event.getData().getId())));
    }

}
