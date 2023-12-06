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
package org.kie.kogito.index.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessDefinitionDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.index.test.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.kie.kogito.index.DateTimeUtils.formatDateTime;
import static org.kie.kogito.index.DateTimeUtils.formatZonedDateTime;
import static org.kie.kogito.index.model.ProcessInstanceState.ACTIVE;
import static org.kie.kogito.index.model.ProcessInstanceState.COMPLETED;
import static org.kie.kogito.index.model.ProcessInstanceState.PENDING;
import static org.kie.kogito.index.service.GraphQLUtils.getJobById;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessDefinitionByIdAndVersion;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByBusinessKey;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByCreatedBy;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceById;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndAddon;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndNullParentProcessInstanceId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndNullRootProcessInstanceId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndParentProcessInstanceId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndProcessId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndStart;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByIdAndState;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByParentProcessInstanceId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByRootProcessInstanceId;
import static org.kie.kogito.index.service.GraphQLUtils.getProcessInstanceByUpdatedBy;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceById;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndActualOwner;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndCompleted;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndProcessId;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndStarted;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdAndState;
import static org.kie.kogito.index.service.GraphQLUtils.getUserTaskInstanceByIdNoActualOwner;
import static org.kie.kogito.index.test.TestUtils.getJobCloudEvent;
import static org.kie.kogito.index.test.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.test.TestUtils.getProcessDefinitionDataEvent;
import static org.kie.kogito.index.test.TestUtils.getUserTaskCloudEvent;

public abstract class AbstractIndexingServiceIT extends AbstractIndexingIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIndexingServiceIT.class);
    public static final String CURRENT_USER = "currentUser";

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
        cacheService.getProcessDefinitionsCache().clear();
        cacheService.getProcessInstancesCache().clear();
        cacheService.getUserTaskInstancesCache().clear();
    }

    @Test
    //Reproducer for KOGITO-334
    void testDefaultGraphqlTypes() {
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessDefinitions{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.ProcessDefinitions", isA(Collection.class));

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

    protected void validateProcessDefinition(String query, ProcessDefinitionDataEvent event) {
        LOGGER.debug("GraphQL query: {}", query);
        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body(query)
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.ProcessDefinitions[0].id", is(event.getData().getId()))
                        .body("data.ProcessDefinitions[0].name", is(event.getData().getName()))
                        .body("data.ProcessDefinitions[0].version", is(event.getData().getVersion()))
                        .body("data.ProcessDefinitions[0].type", is(event.getData().getType()))
                        .body("data.ProcessDefinitions[0].description", is(event.getData().getDescription()))
                        .body("data.ProcessDefinitions[0].annotations", containsInAnyOrder(event.getData().getAnnotations().toArray()))
                        .body("data.ProcessDefinitions[0].metadata", equalTo(event.getData().getMetadata()))
                        .body("data.ProcessDefinitions[0].addons", containsInAnyOrder(event.getData().getAddons().toArray()))
                        .body("data.ProcessDefinitions[0].roles", containsInAnyOrder(event.getData().getRoles().toArray())));
    }

    protected void validateProcessInstance(String query, ProcessInstanceStateDataEvent event, String childProcessInstanceId) {
        LOGGER.debug("GraphQL query: {}", query);
        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body(query)
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.ProcessInstances[0].id", is(event.getData().getProcessInstanceId()))
                        .body("data.ProcessInstances[0].processId", is(event.getData().getProcessId()))
                        .body("data.ProcessInstances[0].processName", is(event.getData().getProcessName()))
                        .body("data.ProcessInstances[0].version", is(event.getData().getProcessVersion()))
                        .body("data.ProcessInstances[0].state", is(ProcessInstanceState.fromStatus(event.getData().getState()).name()))
                        .body("data.ProcessInstances[0].rootProcessId", is(event.getData().getRootProcessId()))
                        .body("data.ProcessInstances[0].rootProcessInstanceId", is(event.getData().getRootProcessInstanceId()))
                        .body("data.ProcessInstances[0].parentProcessInstanceId", is(event.getData().getParentInstanceId()))
                        .body("data.ProcessInstances[0].parentProcessInstance.id", event.getData().getParentInstanceId() == null ? is(nullValue()) : is(event.getData().getParentInstanceId()))
                        .body("data.ProcessInstances[0].parentProcessInstance.processName", event.getData().getParentInstanceId() == null ? is(nullValue()) : is(not(emptyOrNullString())))
                        .body("data.ProcessInstances[0].start", anything())
                        .body("data.ProcessInstances[0].childProcessInstances[0].id", childProcessInstanceId == null ? is(nullValue()) : is(childProcessInstanceId))
                        .body("data.ProcessInstances[0].childProcessInstances[0].processName", childProcessInstanceId == null ? is(nullValue()) : is(not(emptyOrNullString())))
                        .body("data.ProcessInstances[0].endpoint", is(event.getSource().toString()))
                        .body("data.ProcessInstances[0].serviceUrl", event.getSource().toString().equals("/" + event.getData().getProcessId()) ? is(nullValue()) : is("http://localhost:8080"))
                        .body("data.ProcessInstances[0].addons", event.getKogitoAddons() == null ? is(nullValue()) : hasItems(event.getKogitoAddons().split(",")))
                        .body("data.ProcessInstances[0].lastUpdate", anything()));

    }

    protected void validateProcessInstance(String query, ProcessInstanceStateDataEvent event) {
        validateProcessInstance(query, event, null);
    }

    @Test
    void testProcessInstancePagination() {
        String processId = "travels";
        List<String> pIds = new ArrayList<>();

        IntStream.range(0, 100).forEach(i -> {
            String pId = UUID.randomUUID().toString();

            ProcessInstanceDataEvent<?> startEvent = getProcessCloudEvent(processId, pId, ACTIVE, null, null, null, CURRENT_USER);

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
            UserTaskInstanceStateDataEvent event = getUserTaskCloudEvent(taskId, processId, UUID.randomUUID().toString(), null, null, "InProgress");
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
    void testConcurrentProcessInstanceIndex() throws Exception {
        String processId = "travels";
        ExecutorService executorService = new ScheduledThreadPoolExecutor(8);
        int max_instance_events = 10;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        String processInstanceId = UUID.randomUUID().toString();
        //indexing multiple events in parallel to the same process instance id
        for (int i = 0; i < max_instance_events; i++) {
            addFutureEvent(futures, processId, processInstanceId, ACTIVE, executorService, false);
            addFutureEvent(futures, processId, processInstanceId, PENDING, executorService, false);
            addFutureEvent(futures, processId, processInstanceId, ACTIVE, executorService, false);
            addFutureEvent(futures, processId, processInstanceId, COMPLETED, executorService, false);
        }
        //delay the last event to assert later the state
        addFutureEvent(futures, processId, processInstanceId, COMPLETED, executorService, true);
        //wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get(20, TimeUnit.SECONDS);
        ProcessInstanceStateDataEvent event = getProcessCloudEvent(processId, processInstanceId, COMPLETED, null, null, null, CURRENT_USER);
        validateProcessInstance(getProcessInstanceById(processInstanceId), event);
    }

    private void addFutureEvent(List<CompletableFuture<Void>> futures, String processId, String processInstanceId, ProcessInstanceState state, ExecutorService executorService, boolean delay) {
        futures.add(CompletableFuture.runAsync(() -> {
            if (delay) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            ProcessInstanceStateDataEvent event = getProcessCloudEvent(processId, processInstanceId, state, null, null, null, CURRENT_USER);
            indexProcessCloudEvent(event);
        }, executorService));
    }

    @Test
    void testProcessInstanceIndex() throws Exception {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();
        String subProcessId = processId + "_sub";
        String subProcessInstanceId = UUID.randomUUID().toString();

        ProcessDefinitionDataEvent definitionDataEvent = getProcessDefinitionDataEvent(processId);
        indexProcessCloudEvent(definitionDataEvent);
        validateProcessDefinition(getProcessDefinitionByIdAndVersion(processId, definitionDataEvent.getData().getVersion()), definitionDataEvent);

        ProcessInstanceStateDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null, CURRENT_USER);
        indexProcessCloudEvent(startEvent);

        validateProcessInstance(getProcessInstanceById(processInstanceId), startEvent);
        validateProcessInstance(getProcessInstanceByIdAndState(processInstanceId, ACTIVE), startEvent);
        validateProcessInstance(getProcessInstanceByIdAndProcessId(processInstanceId, processId), startEvent);
        validateProcessInstance(
                getProcessInstanceByIdAndStart(processInstanceId, formatDateTime(startEvent.getData().getEventDate())),
                startEvent);
        validateProcessInstance(getProcessInstanceByIdAndAddon(processInstanceId, "process-management"), startEvent);
        validateProcessInstance(getProcessInstanceByBusinessKey(startEvent.getData().getBusinessKey()), startEvent);
        validateProcessInstance(getProcessInstanceByCreatedBy(startEvent.getData().getEventUser()), startEvent);
        validateProcessInstance(getProcessInstanceByUpdatedBy(startEvent.getData().getEventUser()), startEvent);

        ProcessInstanceStateDataEvent endEvent = getProcessCloudEvent(processId, processInstanceId, COMPLETED, null, null, null, CURRENT_USER);

        indexProcessCloudEvent(endEvent);

        validateProcessInstance(getProcessInstanceByIdAndState(processInstanceId, COMPLETED), endEvent);

        ProcessInstanceStateDataEvent event = getProcessCloudEvent(subProcessId, subProcessInstanceId, ACTIVE, processInstanceId,
                processId, processInstanceId, CURRENT_USER);

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

        ProcessInstanceErrorDataEvent errorEvent = TestUtils.deriveErrorProcessCloudEvent(event, "error", "nodeDefintionId", "nodeInstanceId");

        indexProcessCloudEvent(errorEvent);

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body(getProcessInstanceById(event.getKogitoProcessInstanceId()))
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.ProcessInstances[0].id", is(event.getData().getProcessInstanceId()))
                        .body("data.ProcessInstances[0].error.message", errorEvent.getData().getErrorMessage() == null ? is(nullValue()) : is(errorEvent.getData().getErrorMessage()))
                        .body("data.ProcessInstances[0].error.nodeDefinitionId",
                                errorEvent.getData().getNodeDefinitionId() == null ? is(nullValue()) : is(errorEvent.getData().getNodeDefinitionId())));
    }

    @Test
    void testUserTaskInstanceIndex() throws Exception {
        String taskId = UUID.randomUUID().toString();
        String state = "InProgress";
        String processId = "deals";
        String processInstanceId = UUID.randomUUID().toString();

        UserTaskInstanceStateDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state);
        indexUserTaskCloudEvent(event);

        validateUserTaskInstance(getUserTaskInstanceById(taskId), event);
        validateUserTaskInstance(getUserTaskInstanceByIdAndActualOwner(taskId, "kogito"), event);
        validateUserTaskInstance(getUserTaskInstanceByIdAndProcessId(taskId, processId), event);

        validateUserTaskInstance(getUserTaskInstanceByIdAndState(taskId, event.getData().getState()), event);
        validateUserTaskInstance(getUserTaskInstanceByIdAndStarted(taskId, formatDateTime(event.getData().getEventDate())),
                event);

        state = "Completed";
        event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state, "kogito", 2);
        indexUserTaskCloudEvent(event);

        validateUserTaskInstance(
                getUserTaskInstanceByIdAndCompleted(taskId, formatDateTime(event.getData().getEventDate())), event);

        event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state, "admin", 2);
        indexUserTaskCloudEvent(event);

        validateUserTaskInstance(getUserTaskInstanceByIdAndActualOwner(taskId, "admin"), event);

        event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state, null, 2);
        LOGGER.info("event {}", event);
        indexUserTaskCloudEvent(event);

        LOGGER.info("value {}", given().contentType(ContentType.JSON).body(getUserTaskInstanceById(taskId))
                .when().post("/graphql")
                .then().statusCode(200).extract().asString());

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

    protected void validateUserTaskInstance(String query, UserTaskInstanceStateDataEvent event) {
        LOGGER.debug("GraphQL query: {}", query);
        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body(query)
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.UserTaskInstances[0].id", is(event.getData().getUserTaskInstanceId()))
                        .body("data.UserTaskInstances[0].processInstanceId", is(event.getData().getProcessInstanceId()))
                        .body("data.UserTaskInstances[0].description", is(event.getData().getUserTaskDescription()))
                        .body("data.UserTaskInstances[0].name", is(event.getData().getUserTaskName()))
                        .body("data.UserTaskInstances[0].priority", is(event.getData().getUserTaskPriority()))
                        .body("data.UserTaskInstances[0].actualOwner", event.getData().getActualOwner() != null ? is(event.getData().getActualOwner()) : anything())
                        .body("data.UserTaskInstances[0].started", anything())
                        .body("data.UserTaskInstances[0].lastUpdate", anything())
                        .body("data.UserTaskInstances[0].endpoint",
                                is(event.getSource().toString() + "/" + event.getData().getProcessInstanceId() + "/" + event.getData().getUserTaskName() + "/"
                                        + event.getData().getUserTaskInstanceId())));
    }

}
