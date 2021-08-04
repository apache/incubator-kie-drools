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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.DataIndexStorageService;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.model.MilestoneStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

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
import static org.hamcrest.Matchers.hasSize;
import static org.kie.kogito.index.GraphQLUtils.getJobById;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByBusinessKey;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceById;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByIdAndAddon;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByIdAndErrorNode;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByIdAndMilestoneName;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByIdAndMilestoneStatus;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByIdAndNullParentProcessInstanceId;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByIdAndNullRootProcessInstanceId;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByIdAndParentProcessInstanceId;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByIdAndProcessId;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByIdAndStart;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByIdAndState;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByParentProcessInstanceId;
import static org.kie.kogito.index.GraphQLUtils.getProcessInstanceByRootProcessInstanceId;
import static org.kie.kogito.index.GraphQLUtils.getUserTaskInstanceById;
import static org.kie.kogito.index.GraphQLUtils.getUserTaskInstanceByIdAndActualOwner;
import static org.kie.kogito.index.GraphQLUtils.getUserTaskInstanceByIdAndCompleted;
import static org.kie.kogito.index.GraphQLUtils.getUserTaskInstanceByIdAndPotentialGroups;
import static org.kie.kogito.index.GraphQLUtils.getUserTaskInstanceByIdAndPotentialUsers;
import static org.kie.kogito.index.GraphQLUtils.getUserTaskInstanceByIdAndProcessId;
import static org.kie.kogito.index.GraphQLUtils.getUserTaskInstanceByIdAndStarted;
import static org.kie.kogito.index.GraphQLUtils.getUserTaskInstanceByIdAndState;
import static org.kie.kogito.index.GraphQLUtils.getUserTaskInstanceByIdNoActualOwner;
import static org.kie.kogito.index.TestUtils.getJobCloudEvent;
import static org.kie.kogito.index.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.TestUtils.getUserTaskCloudEvent;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;
import static org.kie.kogito.index.model.ProcessInstanceState.ACTIVE;
import static org.kie.kogito.index.model.ProcessInstanceState.COMPLETED;
import static org.kie.kogito.index.model.ProcessInstanceState.ERROR;

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

    protected String formatZonedDateTime(ZonedDateTime time) {
        return time.truncatedTo(ChronoUnit.MILLIS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
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

    protected void validateProcessInstance(String query, KogitoProcessCloudEvent event, String childProcessInstanceId) {
        LOGGER.debug("GraphQL query: {}", query);
        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body(query)
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.ProcessInstances[0].id", is(event.getProcessInstanceId()))
                        .body("data.ProcessInstances[0].processId", is(event.getProcessId()))
                        .body("data.ProcessInstances[0].processName", is(event.getData().getProcessName()))
                        .body("data.ProcessInstances[0].rootProcessId", is(event.getRootProcessId()))
                        .body("data.ProcessInstances[0].rootProcessInstanceId", is(event.getRootProcessInstanceId()))
                        .body("data.ProcessInstances[0].parentProcessInstanceId",
                                is(event.getParentProcessInstanceId()))
                        .body("data.ProcessInstances[0].parentProcessInstance.id",
                                event.getParentProcessInstanceId() == null ? is(nullValue()) : is(event.getParentProcessInstanceId()))
                        .body("data.ProcessInstances[0].parentProcessInstance.processName",
                                event.getParentProcessInstanceId() == null ? is(nullValue()) : is(not(emptyOrNullString())))
                        .body("data.ProcessInstances[0].start",
                                is(formatZonedDateTime(event.getData().getStart().withZoneSameInstant(ZoneOffset.UTC))))
                        .body("data.ProcessInstances[0].end",
                                event.getData().getEnd() == null ? is(nullValue()) : is(formatZonedDateTime(event.getData().getEnd().withZoneSameInstant(ZoneOffset.UTC))))
                        .body("data.ProcessInstances[0].childProcessInstances[0].id",
                                childProcessInstanceId == null ? is(nullValue()) : is(childProcessInstanceId))
                        .body("data.ProcessInstances[0].childProcessInstances[0].processName",
                                childProcessInstanceId == null ? is(nullValue()) : is(not(emptyOrNullString())))
                        .body("data.ProcessInstances[0].endpoint", is(event.getSource().toString()))
                        .body("data.ProcessInstances[0].serviceUrl",
                                event.getSource().toString().equals("/" + event.getProcessId()) ? is(nullValue()) : is("http://localhost:8080"))
                        .body("data.ProcessInstances[0].addons", hasItems(event.getData().getAddons().toArray()))
                        .body("data.ProcessInstances[0].error.message",
                                event.getData().getError() == null ? is(nullValue()) : is(event.getData().getError().getMessage()))
                        .body("data.ProcessInstances[0].error.nodeDefinitionId", event.getData().getError() == null ? is(nullValue()) : is(event.getData().getError().getNodeDefinitionId()))
                        .body("data.ProcessInstances[0].lastUpdate",
                                is(formatZonedDateTime(
                                        event.getData().getLastUpdate().withZoneSameInstant(ZoneOffset.UTC))))
                        .body("data.ProcessInstances[0].nodes", hasSize(event.getData().getNodes().size()))
                        .body("data.ProcessInstances[0].nodes[0].id", is(event.getData().getNodes().get(0).getId()))
                        .body("data.ProcessInstances[0].nodes[0].name", is(event.getData().getNodes().get(0).getName()))
                        .body("data.ProcessInstances[0].nodes[0].nodeId", is(event.getData().getNodes().get(0).getNodeId()))
                        .body("data.ProcessInstances[0].nodes[0].type", is(event.getData().getNodes().get(0).getType()))
                        .body("data.ProcessInstances[0].nodes[0].definitionId", is(event.getData().getNodes().get(0).getDefinitionId()))
                        .body("data.ProcessInstances[0].nodes[0].enter", is(formatZonedDateTime(event.getData().getNodes().get(0).getEnter().withZoneSameInstant(ZoneOffset.UTC))))
                        .body("data.ProcessInstances[0].nodes[0].exit",
                                event.getData().getNodes().get(0).getExit() == null ? is(nullValue())
                                        : is(formatZonedDateTime(event.getData().getNodes().get(0).getExit().withZoneSameInstant(ZoneOffset.UTC))))
                        .body("data.ProcessInstances[0].milestones", hasSize(event.getData().getMilestones().size()))
                        .body("data.ProcessInstances[0].milestones[0].id",
                                is(event.getData().getMilestones().get(0).getId()))
                        .body("data.ProcessInstances[0].milestones[0].name",
                                is(event.getData().getMilestones().get(0).getName()))
                        .body("data.ProcessInstances[0].milestones[0].status",
                                is(event.getData().getMilestones().get(0).getStatus())));
    }

    protected void validateProcessInstance(String query, KogitoProcessCloudEvent event) {
        validateProcessInstance(query, event, null);
    }

    @Test
    void testProcessInstancePagination() {
        String processId = "travels";
        List<String> pIds = new ArrayList<>();

        IntStream.range(0, 200).forEach(i -> {
            String pId = UUID.randomUUID().toString();
            KogitoProcessCloudEvent startEvent = getProcessCloudEvent(processId, pId, ACTIVE, null, null, null);
            indexProcessCloudEvent(startEvent);
            pIds.add(pId);
        });

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.ProcessInstances.size()", is(pIds.size())));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances(orderBy : {start: ASC}, pagination: {offset: 100, limit: 100}) { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.ProcessInstances.size()", is(100))
                        .body("data.ProcessInstances[0].id", is(pIds.get(100)))
                        .body("data.ProcessInstances[99].id", is(pIds.get(199))));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances(orderBy : {start: ASC}, pagination: {offset: 0, limit: 100}) { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.ProcessInstances.size()", is(100))
                        .body("data.ProcessInstances[0].id", is(pIds.get(0)))
                        .body("data.ProcessInstances[99].id", is(pIds.get(99))));
    }

    @Test
    void testUserTaskInstancePagination() {
        String processId = "deals";
        List<String> taskIds = new ArrayList<>();

        IntStream.range(0, 200).forEach(i -> {
            System.out.println("index = " + i);
            String taskId = UUID.randomUUID().toString();
            KogitoUserTaskCloudEvent event = getUserTaskCloudEvent(taskId, processId, UUID.randomUUID().toString(), null, null, "InProgress");
            indexUserTaskCloudEvent(event);
            taskIds.add(taskId);
        });

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{UserTaskInstances { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(taskIds.size())));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{UserTaskInstances(orderBy : {started: ASC}, pagination: {offset: 0, limit: 100}) { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(100))
                        .body("data.UserTaskInstances[0].id", is(taskIds.get(0)))
                        .body("data.UserTaskInstances[99].id", is(taskIds.get(99))));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{UserTaskInstances(orderBy : {started: ASC}, pagination: {offset: 100, limit: 100}) { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(100))
                        .body("data.UserTaskInstances[0].id", is(taskIds.get(100)))
                        .body("data.UserTaskInstances[99].id", is(taskIds.get(199))));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{UserTaskInstances(orderBy : {started: ASC}, pagination: {offset: 0, limit: 200}) { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(taskIds.size()))
                        .body("data.UserTaskInstances[0].id", is(taskIds.get(0)))
                        .body("data.UserTaskInstances[199].id", is(taskIds.get(199))));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{UserTaskInstances(where: {state: {in: [\\\"InProgress\\\"]}}, orderBy : {started: ASC}, pagination: {offset: 0, limit: 200}) { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(taskIds.size()))
                        .body("data.UserTaskInstances[0].id", is(taskIds.get(0)))
                        .body("data.UserTaskInstances[199].id", is(taskIds.get(199))));
    }

    @Test
    void testProcessInstanceIndex() throws Exception {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();
        String subProcessId = processId + "_sub";
        String subProcessInstanceId = UUID.randomUUID().toString();

        KogitoProcessCloudEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        validateProcessInstance(getProcessInstanceById(processInstanceId), startEvent);
        validateProcessInstance(getProcessInstanceByIdAndState(processInstanceId, ACTIVE), startEvent);
        validateProcessInstance(getProcessInstanceByIdAndProcessId(processInstanceId, processId), startEvent);
        validateProcessInstance(
                getProcessInstanceByIdAndStart(processInstanceId, formatZonedDateTime(startEvent.getData().getStart())),
                startEvent);
        validateProcessInstance(getProcessInstanceByIdAndAddon(processInstanceId, "process-management"), startEvent);
        validateProcessInstance(getProcessInstanceByIdAndMilestoneName(processInstanceId, "SimpleMilestone"), startEvent);
        validateProcessInstance(getProcessInstanceByIdAndMilestoneStatus(processInstanceId, MilestoneStatus.AVAILABLE.name()),
                startEvent);
        validateProcessInstance(getProcessInstanceByBusinessKey(startEvent.getData().getBusinessKey()), startEvent);

        KogitoProcessCloudEvent endEvent = getProcessCloudEvent(processId, processInstanceId, COMPLETED, null, null, null);
        endEvent.getData().setEnd(ZonedDateTime.now());
        endEvent.getData().setVariables((ObjectNode) getObjectMapper().readTree(
                "{ \"traveller\":{\"firstName\":\"Maciej\"},\"hotel\":{\"name\":\"Ibis\"},\"flight\":{\"arrival\":\"2019-08-20T22:12:57.340Z\",\"departure\":\"2019-08-20T07:12:57.340Z\",\"flightNumber\":\"QF444\"} }"));
        indexProcessCloudEvent(endEvent);

        validateProcessInstance(getProcessInstanceByIdAndState(processInstanceId, COMPLETED), endEvent);

        KogitoProcessCloudEvent event = getProcessCloudEvent(subProcessId, subProcessInstanceId, ACTIVE, processInstanceId,
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

        KogitoProcessCloudEvent errorEvent = getProcessCloudEvent(subProcessId, subProcessInstanceId, ERROR, processInstanceId,
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

        KogitoUserTaskCloudEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state);
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
        validateUserTaskInstance(getUserTaskInstanceByIdAndStarted(taskId, formatZonedDateTime(event.getData().getStarted())),
                event);
        validateUserTaskInstance(
                getUserTaskInstanceByIdAndCompleted(taskId, formatZonedDateTime(event.getData().getCompleted())), event);

        event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state);
        event.getData().setCompleted(ZonedDateTime.now());
        event.getData().setPriority("Low");
        event.getData().setActualOwner("admin");
        event.getData().setState("Completed");

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
                        .body("data.Jobs[0].expirationTime",
                                is(formatZonedDateTime(
                                        event.getData().getExpirationTime().withZoneSameInstant(ZoneOffset.UTC))))
                        .body("data.Jobs[0].priority", is(event.getData().getPriority()))
                        .body("data.Jobs[0].callbackEndpoint", is(event.getData().getCallbackEndpoint()))
                        .body("data.Jobs[0].repeatInterval", is(event.getData().getRepeatInterval().intValue()))
                        .body("data.Jobs[0].repeatLimit", is(event.getData().getRepeatLimit()))
                        .body("data.Jobs[0].scheduledId", is(event.getData().getScheduledId()))
                        .body("data.Jobs[0].retries", is(event.getData().getRetries()))
                        .body("data.Jobs[0].lastUpdate",
                                is(formatZonedDateTime(event.getData().getLastUpdate().withZoneSameInstant(ZoneOffset.UTC))))
                        .body("data.Jobs[0].executionCounter", is(event.getData().getExecutionCounter()))
                        .body("data.Jobs[0].endpoint", is(event.getData().getEndpoint())));
    }

    protected void validateUserTaskInstance(String query, KogitoUserTaskCloudEvent event) {
        LOGGER.debug("GraphQL query: {}", query);
        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body(query)
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances[0].id", is(event.getUserTaskInstanceId()))
                        .body("data.UserTaskInstances[0].processId", is(event.getProcessId()))
                        .body("data.UserTaskInstances[0].rootProcessId", is(event.getRootProcessId()))
                        .body("data.UserTaskInstances[0].rootProcessInstanceId", is(event.getRootProcessInstanceId()))
                        .body("data.UserTaskInstances[0].description", is(event.getData().getDescription()))
                        .body("data.UserTaskInstances[0].name", is(event.getData().getName()))
                        .body("data.UserTaskInstances[0].priority", is(event.getData().getPriority()))
                        .body("data.UserTaskInstances[0].actualOwner", is(event.getData().getActualOwner()))
                        .body("data.UserTaskInstances[0].excludedUsers",
                                hasItems(event.getData().getExcludedUsers().toArray()))
                        .body("data.UserTaskInstances[0].potentialUsers",
                                hasItems(event.getData().getPotentialUsers().toArray()))
                        .body("data.UserTaskInstances[0].potentialGroups",
                                hasItems(event.getData().getPotentialGroups().toArray()))
                        .body("data.UserTaskInstances[0].started",
                                is(formatZonedDateTime(
                                        event.getData().getStarted().withZoneSameInstant(ZoneOffset.UTC))))
                        .body("data.UserTaskInstances[0].completed",
                                is(formatZonedDateTime(
                                        event.getData().getCompleted().withZoneSameInstant(ZoneOffset.UTC))))
                        .body("data.UserTaskInstances[0].lastUpdate",
                                is(formatZonedDateTime(event.getTime().withZoneSameInstant(ZoneOffset.UTC))))
                        .body("data.UserTaskInstances[0].endpoint", is(event.getData().getEndpoint())));
    }

}
