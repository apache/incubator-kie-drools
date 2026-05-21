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
package org.kie.kogito;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.kie.kogito.FailingServiceTestUtil.FAILING_ASYNC_SERVICE_TASK_PROCESS_ID;
import static org.kie.kogito.FailingServiceTestUtil.POLL_DELAY;
import static org.kie.kogito.FailingServiceTestUtil.POLL_INTERVAL;
import static org.kie.kogito.FailingServiceTestUtil.SERVICE_TASK_NODE_DEFINTION_ID;
import static org.kie.kogito.FailingServiceTestUtil.TIMEOUT;
import static org.kie.kogito.FailingServiceTestUtil.USER_TASK_NODE_DEFINTION_ID;
import static org.kie.kogito.FailingServiceTestUtil.executeGraphQLQuery;
import static org.kie.kogito.FailingServiceTestUtil.getQuery;

@QuarkusIntegrationTest
class FailingAsyncServiceTaskIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testFailingAsyncServiceTask() {
        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/" + FAILING_ASYNC_SERVICE_TASK_PROCESS_ID)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .path("id");

        String query = getQuery(processInstanceId);
        JsonPath resultBefore = executeGraphQLQuery(query);

        // Verify initial state
        List<Map<String, Object>> processesBefore = resultBefore.get("data.ProcessInstances");
        assertThat(processesBefore).isNotEmpty();

        Map<String, Object> processBefore = processesBefore.get(0);
        assertThat(processBefore.get("id")).isEqualTo(processInstanceId);
        assertThat(processBefore.get("processId")).isEqualTo(FAILING_ASYNC_SERVICE_TASK_PROCESS_ID);
        assertThat(processBefore.get("state")).isEqualTo("ACTIVE");
        assertThat(processBefore.get("error")).isNull();

        List<Map<String, Object>> nodesBefore = resultBefore.get("data.ProcessInstances[0].nodes");
        assertThat(nodesBefore).isNotEmpty();

        Map<String, Object> userTaskNodeBefore = nodesBefore.stream()
                .filter(node -> USER_TASK_NODE_DEFINTION_ID.equals(node.get("definitionId")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("User task node not found"));
        String userTaskNodeInstanceId = (String) userTaskNodeBefore.get("id");

        List<Map<String, Object>> jobsBefore = resultBefore.get("data.Jobs");
        assertThat(jobsBefore).hasSize(1);

        Map<String, Object> jobBefore = jobsBefore.get(0);
        assertThat(jobBefore.get("processInstanceId")).isEqualTo(processInstanceId);
        assertThat(jobBefore.get("nodeInstanceId")).isEqualTo(userTaskNodeInstanceId);
        assertThat(jobBefore.get("status")).isIn("SCHEDULED", "RETRY");

        // After 4s Boundary Timer triggers
        JsonPath resultAfter = await()
                .pollDelay(POLL_DELAY)
                .atMost(TIMEOUT)
                .pollInterval(POLL_INTERVAL)
                .until(() -> {
                    JsonPath result = executeGraphQLQuery(query);

                    List<Map<String, Object>> processes = result.get("data.ProcessInstances");
                    if (processes != null && !processes.isEmpty() && "ERROR".equals(processes.get(0).get("state"))) {
                        return result;
                    }
                    return null;
                }, Objects::nonNull);

        // Verify final state
        List<Map<String, Object>> processesAfter = resultAfter.get("data.ProcessInstances");
        assertThat(processesAfter).isNotEmpty();

        Map<String, Object> processAfter = processesAfter.get(0);
        assertThat(processAfter.get("id")).isEqualTo(processInstanceId);
        assertThat(processAfter.get("state")).isEqualTo("ERROR");

        Map<String, Object> error = (Map<String, Object>) processAfter.get("error");
        assertThat(error).isNotNull();
        assertThat(error.get("nodeDefinitionId")).isEqualTo(SERVICE_TASK_NODE_DEFINTION_ID);
        assertThat(error.get("message")).asString().contains("Failed Service Task");

        List<Map<String, Object>> nodesAfter = resultAfter.get("data.ProcessInstances[0].nodes");
        assertThat(nodesAfter).isNotEmpty();

        // Verify service task node is NOT present after error due to transaction rollback
        boolean serviceTaskNodePresent = nodesAfter.stream()
                .anyMatch(node -> SERVICE_TASK_NODE_DEFINTION_ID.equals(node.get("definitionId")));
        assertThat(serviceTaskNodePresent)
                .as("Service task node should not be persisted due to transaction rollback")
                .isFalse();

        List<Map<String, Object>> jobsAfter = resultAfter.get("data.Jobs");
        assertThat(jobsAfter).hasSize(2);

        Map<String, Object> userTaskJob = jobsAfter.stream()
                .filter(job -> userTaskNodeInstanceId.equals(job.get("nodeInstanceId")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("User task job not found"));
        assertThat(userTaskJob.get("status")).isEqualTo("EXECUTED");
        assertThat(userTaskJob.get("processInstanceId")).isEqualTo(processInstanceId);

        Map<String, Object> errorJob = jobsAfter.stream()
                .filter(job -> "ERROR".equals(job.get("status")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Error job not found"));
        assertThat(errorJob.get("processInstanceId")).isEqualTo(processInstanceId);
    }
}
