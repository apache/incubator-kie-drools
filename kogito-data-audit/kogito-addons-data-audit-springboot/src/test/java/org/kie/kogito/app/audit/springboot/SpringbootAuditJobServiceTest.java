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
package org.kie.kogito.app.audit.springboot;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.kie.kogito.app.audit.api.SubsystemConstants;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.kie.kogito.app.audit.quarkus.DataAuditTestUtils.deriveNewState;
import static org.kie.kogito.app.audit.quarkus.DataAuditTestUtils.newJobEvent;
import static org.kie.kogito.app.audit.quarkus.DataAuditTestUtils.wrapQuery;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server_port=0")
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SpringbootAuditJobServiceTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    EventPublisher publisher;

    @BeforeAll
    public void init() throws Exception {

        JobInstanceDataEvent jobEvent;
        jobEvent = newJobEvent("job1", "nodeInstanceId1", 1, "processId1", "processInstanceId1", 100L, 10, "rootProcessId1", "rootProcessInstanceId1", JobStatus.SCHEDULED, 0);
        publisher.publish(jobEvent);

        jobEvent = deriveNewState(jobEvent, 1, JobStatus.EXECUTED);
        publisher.publish(jobEvent);

        jobEvent = newJobEvent("job2", "nodeInstanceId1", 1, "processId1", "processInstanceId2", 100L, 10, "rootProcessId1", "rootProcessInstanceId1", JobStatus.SCHEDULED, 0);
        publisher.publish(jobEvent);

        jobEvent = newJobEvent("job3", "nodeInstanceId1", 1, "processId1", "processInstanceId3", 100L, 10, "rootProcessId1", "rootProcessInstanceId1", JobStatus.SCHEDULED, 0);
        publisher.publish(jobEvent);

        jobEvent = deriveNewState(jobEvent, 1, JobStatus.CANCELED);
        publisher.publish(jobEvent);

        jobEvent = newJobEvent("job4", "nodeInstanceId1", 1, "processId1", "processInstanceId4", 100L, 10, "rootProcessId1", "rootProcessInstanceId1", JobStatus.SCHEDULED, 0);
        publisher.publish(jobEvent);

        jobEvent = deriveNewState(jobEvent, 1, JobStatus.RETRY);
        publisher.publish(jobEvent);

        jobEvent = deriveNewState(jobEvent, 2, JobStatus.EXECUTED);
        publisher.publish(jobEvent);

        jobEvent = newJobEvent("job5", "nodeInstanceId1", 1, "processId1", "processInstanceI51", 100L, 10, "rootProcessId1", "rootProcessInstanceId1", JobStatus.SCHEDULED, 0);
        publisher.publish(jobEvent);

        jobEvent = deriveNewState(jobEvent, 1, JobStatus.ERROR, "java.lang.RuntimeException: Job execution failed",
                "java.lang.RuntimeException: Job execution failed\n\tat org.example.JobExecutor.execute(JobExecutor.java:42)");
        publisher.publish(jobEvent);

        // Job with retry that has exception details and eventually succeeds
        jobEvent = newJobEvent("job6", "nodeInstanceId1", 1, "processId1", "processInstanceId6", 100L, 10, "rootProcessId1", "rootProcessInstanceId1", JobStatus.SCHEDULED, 0);
        publisher.publish(jobEvent);

        jobEvent = deriveNewState(jobEvent, 1, JobStatus.RETRY, "java.lang.IllegalStateException: Temporary failure",
                "java.lang.IllegalStateException: Temporary failure\n\tat org.example.Service.process(Service.java:123)");
        publisher.publish(jobEvent);

        jobEvent = deriveNewState(jobEvent, 2, JobStatus.EXECUTED);
        publisher.publish(jobEvent);

        // Job with retry that eventually fails with ERROR
        jobEvent = newJobEvent("job7", "nodeInstanceId1", 1, "processId1", "processInstanceId7", 100L, 10, "rootProcessId1", "rootProcessInstanceId1", JobStatus.SCHEDULED, 0);
        publisher.publish(jobEvent);

        jobEvent = deriveNewState(jobEvent, 1, JobStatus.RETRY, "java.net.ConnectException: Connection refused",
                "java.net.ConnectException: Connection refused\n\tat org.example.Client.connect(Client.java:89)");
        publisher.publish(jobEvent);

        jobEvent = deriveNewState(jobEvent, 2, JobStatus.ERROR, "java.net.ConnectException: Max retries exceeded",
                "java.net.ConnectException: Max retries exceeded\n\tat org.example.Client.connect(Client.java:95)\n\tCaused by: Connection refused");
        publisher.publish(jobEvent);
    }

    @Test
    public void testGetAllScheduledJobs() {
        String query =
                "{ GetAllScheduledJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);

        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllScheduledJobs");

        assertThat(response)
                .hasSize(1)
                .extracting(e -> e.get("jobId"), e -> e.get("processInstanceId"), e -> e.get("status"))
                .containsExactlyInAnyOrder(tuple("job2", "processInstanceId2", "SCHEDULED"));

    }

    @Test
    public void testGetJobById() {
        String query =
                "{ GetJobById ( jobId : \\\"job1\\\") { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobById");

        assertThat(response)
                .hasSize(1)
                .first()
                .satisfies(e -> {
                    assertThat(e.get("jobId")).isEqualTo("job1");
                    assertThat(e.get("priority")).isEqualTo(1);
                    assertThat(e.get("processInstanceId")).isEqualTo("processInstanceId1");
                    assertThat(e.get("nodeInstanceId")).isEqualTo("nodeInstanceId1");
                    assertThat(e.get("repeatInterval")).isEqualTo(100);
                    assertThat(e.get("repeatLimit")).isEqualTo(10);
                    assertThat(e.get("scheduledId")).isEqualTo("my scheduler");
                    assertThat(e.get("retries")).isEqualTo(1);
                    assertThat(e.get("status")).isEqualTo("EXECUTED");
                    assertThat(e.get("executionCounter")).isEqualTo(1);
                    assertThat(e.get("expirationTime")).isNotNull();
                });
    }

    @Test
    public void testGetJobHistoryById() {
        String query =
                "{ GetJobHistoryById ( jobId : \\\"job4\\\") { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobHistoryById");

        assertThat(response)
                .hasSize(3)
                .allMatch(e -> "job4".equals(e.get("jobId")))
                .extracting(e -> e.get("status"))
                .containsExactlyInAnyOrder("SCHEDULED", "RETRY", "EXECUTED");

        // Validate retries field progression through job lifecycle
        assertThat(response)
                .extracting(e -> e.get("status"), e -> e.get("retries"), e -> e.get("executionCounter"))
                .containsExactlyInAnyOrder(
                        tuple("SCHEDULED", 0, 0),
                        tuple("RETRY", 1, 1),
                        tuple("EXECUTED", 2, 2));

        // Validate all common fields are present and consistent
        assertThat(response)
                .allMatch(e -> e.get("jobId").equals("job4"))
                .allMatch(e -> e.get("priority").equals(1))
                .allMatch(e -> e.get("processInstanceId").equals("processInstanceId4"))
                .allMatch(e -> e.get("nodeInstanceId").equals("nodeInstanceId1"))
                .allMatch(e -> e.get("repeatInterval").equals(100))
                .allMatch(e -> e.get("repeatLimit").equals(10))
                .allMatch(e -> e.get("scheduledId").equals("my scheduler"))
                .allMatch(e -> e.get("expirationTime") != null);
    }

    @Test
    public void testGetJobHistoryByProcessInstanceId() {
        String query =
                "{ GetJobHistoryByProcessInstanceId ( processInstanceId : \\\"processInstanceId4\\\") { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobHistoryByProcessInstanceId");

        assertThat(data)
                .hasSize(3)
                .allMatch(e -> "job4".equals(e.get("jobId")))
                .allMatch(e -> "processInstanceId4".equals(e.get("processInstanceId")))
                .extracting(e -> e.get("status"))
                .containsExactlyInAnyOrder("SCHEDULED", "RETRY", "EXECUTED");
    }

    @Test
    public void testGetAllPendingJobs() {
        String query =
                "{ GetAllPendingJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllPendingJobs");

        assertThat(data).hasSize(1);
    }

    @Test
    public void testGetAllEligibleJobsForExecution() {
        String query =
                "{ GetAllEligibleJobsForExecution { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails }  }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllEligibleJobsForExecution");

        assertThat(data).hasSize(1);

    }

    @Test
    public void testGetAllEligibleJobsForRetry() {
        String query =
                "{ GetAllEligibleJobsForRetry { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllEligibleJobsForRetry");

        assertThat(data).hasSize(2)
                .extracting(e -> e.get("jobId"))
                .containsExactlyInAnyOrder("job5", "job7");
    }

    @Test
    public void testGetAllJobs() {
        String query =
                "{ GetAllJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllJobs");

        assertThat(data)
                .hasSize(7)
                .extracting(e -> e.get("jobId"))
                .containsExactlyInAnyOrder("job1", "job2", "job3", "job4", "job5", "job6", "job7");
    }

    @Test
    public void testGetAllCompletedJobs() {
        String query =
                "{ GetAllCompletedJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllCompletedJobs");

        assertThat(data)
                .hasSize(3)
                .allMatch(e -> "EXECUTED".equals(e.get("status")))
                .extracting(e -> e.get("jobId"))
                .containsExactlyInAnyOrder("job1", "job4", "job6");
    }

    @Test
    public void testGetAllInErrorJobs() {

        String query =
                "{ GetAllInErrorJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllInErrorJobs");

        assertThat(data)
                .hasSize(2)
                .allMatch(e -> "ERROR".equals(e.get("status")))
                .extracting(e -> e.get("jobId"))
                .containsExactlyInAnyOrder("job5", "job7");
    }

    @Test
    public void testGetAllJobsByStatus() {

        String query =
                "{ GetAllJobsByStatus (status : \\\"EXECUTED\\\") { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllJobsByStatus");

        assertThat(data)
                .allMatch(e -> "EXECUTED".equals(e.get("status")))
                .extracting(e -> e.get("jobId"))
                .containsExactlyInAnyOrder("job1", "job4", "job6");
    }

    @Test
    public void testGetJobByProcessInstanceId() {

        String query =
                "{ GetJobByProcessInstanceId (processInstanceId : \\\"processInstanceId1\\\") { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobByProcessInstanceId");

        assertThat(data).first()
                .hasFieldOrPropertyWithValue("processInstanceId", "processInstanceId1");

    }

    @Test
    public void testGetJobWithExceptionDetails() {
        String query =
                "{ GetAllInErrorJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllInErrorJobs");

        assertThat(data)
                .hasSize(2)
                .allMatch(e -> "ERROR".equals(e.get("status")))
                .allMatch(e -> e.get("exceptionMessage") != null)
                .allMatch(e -> e.get("exceptionDetails") != null)
                .extracting(e -> e.get("jobId"), e -> e.get("exceptionMessage"))
                .containsExactlyInAnyOrder(
                        tuple("job5", "java.lang.RuntimeException: Job execution failed"),
                        tuple("job7", "java.net.ConnectException: Max retries exceeded"));
    }

    @Test
    public void testGetJobHistoryWithExceptionDetails() {
        String query =
                "{ GetJobHistoryById ( jobId : \\\"job6\\\") { jobId, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobHistoryById");

        assertThat(response)
                .hasSize(3)
                .allMatch(e -> "job6".equals(e.get("jobId")))
                .extracting(e -> e.get("status"), e -> e.get("exceptionMessage"))
                .containsExactlyInAnyOrder(
                        tuple("SCHEDULED", null),
                        tuple("RETRY", "java.lang.IllegalStateException: Temporary failure"),
                        tuple("EXECUTED", null));
    }

    @Test
    public void testGetJobHistoryRetryToError() {
        String query =
                "{ GetJobHistoryById ( jobId : \\\"job7\\\") { jobId, status, executionCounter, exceptionMessage, exceptionDetails } }";
        query = wrapQuery(query);
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobHistoryById");

        assertThat(response)
                .hasSize(3)
                .allMatch(e -> "job7".equals(e.get("jobId")))
                .extracting(e -> e.get("status"), e -> e.get("exceptionMessage"))
                .containsExactlyInAnyOrder(
                        tuple("SCHEDULED", null),
                        tuple("RETRY", "java.net.ConnectException: Connection refused"),
                        tuple("ERROR", "java.net.ConnectException: Max retries exceeded"));

        // Verify exception details are captured for both RETRY and ERROR states
        assertThat(response)
                .filteredOn(e -> "RETRY".equals(e.get("status")))
                .hasSize(1)
                .first()
                .satisfies(e -> {
                    assertThat(e.get("exceptionMessage")).isEqualTo("java.net.ConnectException: Connection refused");
                    assertThat(e.get("exceptionDetails")).asString().contains("org.example.Client.connect");
                });

        assertThat(response)
                .filteredOn(e -> "ERROR".equals(e.get("status")))
                .hasSize(1)
                .first()
                .satisfies(e -> {
                    assertThat(e.get("exceptionMessage")).isEqualTo("java.net.ConnectException: Max retries exceeded");
                    assertThat(e.get("exceptionDetails")).asString().contains("Max retries exceeded");
                    assertThat(e.get("exceptionDetails")).asString().contains("Caused by");
                });
    }

    @Test
    public void testGetJobHistoryByIdWithPagination() {
        // Test with limit of 2 - should return only 2 most recent entries
        String query =
                "{ GetJobHistoryById ( jobId : \\\"job4\\\", pagination: { limit: 2, offset: 0 }) { jobId, status, retries, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobHistoryById");

        // Should return only 2 results (most recent due to ORDER BY event_date DESC)
        assertThat(response)
                .hasSize(2)
                .allMatch(e -> "job4".equals(e.get("jobId")))
                .extracting(e -> e.get("status"))
                .containsExactlyInAnyOrder("EXECUTED", "RETRY");

        // Test with higher limit to get all results
        query = "{ GetJobHistoryById ( jobId : \\\"job4\\\", pagination: { limit: 100, offset: 0 }) { jobId, status, retries, executionCounter } }";
        query = wrapQuery(query);
        response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobHistoryById");

        // Should return all 3 results
        assertThat(response)
                .hasSize(3)
                .allMatch(e -> "job4".equals(e.get("jobId")))
                .extracting(e -> e.get("status"))
                .containsExactlyInAnyOrder("SCHEDULED", "RETRY", "EXECUTED");

        // Test with offset - ORDER BY event_date DESC means newest first
        // So offset=1 skips EXECUTED (newest) and returns RETRY and SCHEDULED
        query = "{ GetJobHistoryById ( jobId : \\\"job4\\\", pagination: { limit: 2, offset: 1 }) { jobId, status, retries, executionCounter } }";
        query = wrapQuery(query);
        response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobHistoryById");

        // Should skip first result (EXECUTED) and return next 2 (RETRY, SCHEDULED)
        assertThat(response)
                .hasSize(2)
                .allMatch(e -> "job4".equals(e.get("jobId")))
                .extracting(e -> e.get("status"))
                .containsExactlyInAnyOrder("RETRY", "SCHEDULED");
    }

}
