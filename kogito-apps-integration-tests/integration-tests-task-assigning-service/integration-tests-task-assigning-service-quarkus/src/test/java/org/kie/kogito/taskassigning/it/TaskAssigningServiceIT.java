/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.it;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.kogito.taskassigning.ClientServices;
import org.kie.kogito.taskassigning.auth.NoAuthenticationCredentials;
import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClient;
import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClientConfig;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClient;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClientConfig;
import org.kie.kogito.taskassigning.resources.TaskAssigningServiceQuarkusTestResource;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
@QuarkusTestResource(TaskAssigningServiceQuarkusTestResource.class)
class TaskAssigningServiceIT {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TaskAssigningServiceIT.class);

    private static final String RESERVED_STATUS = "Reserved";
    private static final String COMPLETED_STATUS = "Completed";
    private static final String COMPLETE_PHASE = "complete";

    private static final String CREDIT_DISPUTE_PROCESS = "CreditDispute";

    private static final String RESOLVE_DISPUTE_TASK = "ResolveDispute";
    private static final String NOTIFY_CUSTOMER_TASK = "NotifyCustomer";
    private static final String CREDIT_ANALYST_GROUP = "CreditAnalyst";
    private static final String CLIENT_RELATIONS_GROUP = "ClientRelations";

    private static final String USER_EMILY = "emily";
    private static final String USER_BOB = "bob";

    private static final int TASK_QUERY_TIMOUT_IN_SECONDS = 2 * 60;
    private static final int TASK_QUERY_POLL_INTERVAL_IN_MILLISECONDS = 500;
    private static final int HEALTH_CHECK_QUERY_TIMEOUT_IN_SECONDS = 7 * 60;
    private static final int HEALTH_CHECK_POLL_INTERVAL_IN_MILLISECONDS = 500;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Predicate<List<UserTaskInstance>> HAS_TASKS = list -> !list.isEmpty();

    private static final Predicate<String> IS_UP = "UP"::equals;

    @ConfigProperty(name = TaskAssigningServiceQuarkusTestResource.KOGITO_TASK_ASSIGNING_SERVICE_URL)
    private String taskAssigningServiceUrl;

    @ConfigProperty(name = TaskAssigningServiceQuarkusTestResource.KOGITO_DATA_INDEX_SERVICE_URL)
    private String dataIndexServiceUrl;

    @ConfigProperty(name = TaskAssigningServiceQuarkusTestResource.KOGITO_PROCESSES_SERVICE_URL)
    private String processesServiceUrl;

    @Inject
    private ClientServices clientServices;

    @Test
    @Timeout(value = 10,
            unit = TimeUnit.MINUTES)
    void taskAssignments() {
        LOGGER.debug("taskAssigningServiceURL: " + taskAssigningServiceUrl);
        LOGGER.debug("dataIndexServiceUrl: " + dataIndexServiceUrl);
        LOGGER.debug("processesServiceUrl: " + processesServiceUrl);

        DataIndexServiceClient dataIndexClient = clientServices.dataIndexClientFactory().newClient(DataIndexServiceClientConfig.newBuilder()
                .serviceUrl(dataIndexServiceUrl)
                .build(),
                NoAuthenticationCredentials.INSTANCE);

        ProcessServiceClient processServiceClient = clientServices.processServiceClientFactory().newClient(ProcessServiceClientConfig.newBuilder()
                .serviceUrl(processesServiceUrl)
                .build(),
                NoAuthenticationCredentials.INSTANCE);

        createProcessInstance(CREDIT_DISPUTE_PROCESS, createCreditDisputeParams("VISA", "Spanish"));

        // ResolveDispute task must have been assigned to emily since the creditCard is VISA
        List<UserTaskInstance> tasks = waitForResults(() -> userTasksQuery(USER_EMILY, Collections.singletonList(RESERVED_STATUS), 0, 10, dataIndexClient),
                HAS_TASKS,
                TASK_QUERY_POLL_INTERVAL_IN_MILLISECONDS,
                TASK_QUERY_TIMOUT_IN_SECONDS);
        assertThat(tasks).hasSize(1);

        UserTaskInstance userTaskInstance = tasks.get(0);
        assertTaskWithOwnerAndStatus(userTaskInstance, RESOLVE_DISPUTE_TASK, USER_EMILY, RESERVED_STATUS);

        processServiceClient.transitionTask(userTaskInstance.getProcessId(),
                userTaskInstance.getProcessInstanceId(),
                userTaskInstance.getName(),
                userTaskInstance.getId(),
                COMPLETE_PHASE,
                USER_EMILY,
                Collections.singletonList(CREDIT_ANALYST_GROUP));

        tasks = waitForResults(() -> userTasksQuery(USER_EMILY, Collections.singletonList(COMPLETED_STATUS), 0, 10, dataIndexClient),
                HAS_TASKS,
                TASK_QUERY_POLL_INTERVAL_IN_MILLISECONDS,
                TASK_QUERY_TIMOUT_IN_SECONDS);
        assertThat(tasks).hasSize(1);

        userTaskInstance = tasks.get(0);
        assertTaskWithOwnerAndStatus(userTaskInstance, RESOLVE_DISPUTE_TASK, USER_EMILY, COMPLETED_STATUS);

        // NotifyCustomer task must have been assigned to bob since the language is Spanish
        tasks = waitForResults(() -> userTasksQuery(USER_BOB, Collections.singletonList(RESERVED_STATUS), 0, 10, dataIndexClient),
                HAS_TASKS,
                TASK_QUERY_POLL_INTERVAL_IN_MILLISECONDS,
                TASK_QUERY_TIMOUT_IN_SECONDS);
        assertThat(tasks).hasSize(1);

        userTaskInstance = tasks.get(0);
        assertTaskWithOwnerAndStatus(userTaskInstance, NOTIFY_CUSTOMER_TASK, USER_BOB, RESERVED_STATUS);

        processServiceClient.transitionTask(userTaskInstance.getProcessId(),
                userTaskInstance.getProcessInstanceId(),
                userTaskInstance.getName(),
                userTaskInstance.getId(),
                COMPLETE_PHASE,
                USER_BOB,
                Collections.singletonList(CLIENT_RELATIONS_GROUP));

        tasks = waitForResults(() -> userTasksQuery(USER_BOB, Collections.singletonList(COMPLETED_STATUS), 0, 10, dataIndexClient),
                HAS_TASKS,
                TASK_QUERY_POLL_INTERVAL_IN_MILLISECONDS,
                TASK_QUERY_TIMOUT_IN_SECONDS);
        assertThat(tasks).hasSize(1);

        userTaskInstance = tasks.get(0);
        assertTaskWithOwnerAndStatus(userTaskInstance, NOTIFY_CUSTOMER_TASK, USER_BOB, COMPLETED_STATUS);
    }

    @Test
    @Timeout(value = 10,
            unit = TimeUnit.MINUTES)
    void livenessHealthCheck() {
        waitForResults(() -> executeHealthCheck(taskAssigningServiceUrl + "/q/health/live"),
                IS_UP,
                HEALTH_CHECK_POLL_INTERVAL_IN_MILLISECONDS,
                HEALTH_CHECK_QUERY_TIMEOUT_IN_SECONDS);
    }

    @Test
    @Timeout(value = 10,
            unit = TimeUnit.MINUTES)
    void readinessHealthCheck() {
        waitForResults(() -> executeHealthCheck(taskAssigningServiceUrl + "/q/health/ready"),
                IS_UP,
                HEALTH_CHECK_POLL_INTERVAL_IN_MILLISECONDS,
                HEALTH_CHECK_QUERY_TIMEOUT_IN_SECONDS);
    }

    private void assertTaskWithOwnerAndStatus(UserTaskInstance userTaskInstance, String expectedName, String expectedOwner, String expectedStatus) {
        assertThat(userTaskInstance.getName())
                .withFailMessage("Task with name: %s is expected", userTaskInstance.getName())
                .isEqualTo(expectedName);
        assertThat(userTaskInstance.getActualOwner())
                .withFailMessage("Task: %s is expected to be assigned to: %s", userTaskInstance.getName(), expectedOwner)
                .isEqualTo(expectedOwner);
        assertThat(userTaskInstance.getState())
                .withFailMessage("Task: %s is expected to be in status: %s", userTaskInstance.getName(), expectedStatus)
                .isEqualTo(expectedStatus);
    }

    private String createCreditDisputeParams(String cardType, String language) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        if (cardType != null) {
            objectNode.put("cardType", cardType);
        }
        if (language != null) {
            objectNode.put("language", language);
        }
        return objectNode.toString();
    }

    private String createProcessInstance(String processId, String jsonParams) {
        return given()
                .contentType(ContentType.JSON)
                .body(jsonParams)
                .when()
                .post(processesServiceUrl + "/" + processId)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    private List<UserTaskInstance> userTasksQuery(List<String> status, int offset, int limit, DataIndexServiceClient dataIndexClient) {
        return dataIndexClient.findTasks(status, null, UserTaskInstance.Field.STARTED.name(), true, offset, limit);
    }

    private List<UserTaskInstance> userTasksQuery(String user, List<String> status, int offset, int limit, DataIndexServiceClient dataIndexClient) {
        List<UserTaskInstance> tasks = userTasksQuery(status, offset, limit, dataIndexClient);
        return tasks.stream()
                .filter(userTaskInstance -> user.equals(userTaskInstance.getActualOwner()))
                .collect(Collectors.toList());
    }

    private String executeHealthCheck(String path) {
        return given()
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .body("status", notNullValue())
                .extract()
                .path("status");
    }

    private <T> T waitForResults(Supplier<T> resultProducer, Predicate<T> condition, int pollIntervalInMillis, int timoutInSeconds) {
        AtomicReference<T> output = new AtomicReference<>();
        await()
                .pollInterval(pollIntervalInMillis, MILLISECONDS)
                .timeout(timoutInSeconds, SECONDS)
                .until(() -> {
                    T result = resultProducer.get();
                    boolean exit = condition.test(result);
                    if (exit) {
                        output.set(result);
                    }
                    return exit;
                });
        return output.get();
    }
}
