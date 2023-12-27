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
package org.kie.kogito.app.audit.quarkus;

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

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import jakarta.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.kie.kogito.app.audit.quarkus.DataAuditTestUtils.deriveNewState;
import static org.kie.kogito.app.audit.quarkus.DataAuditTestUtils.newJobEvent;
import static org.kie.kogito.app.audit.quarkus.DataAuditTestUtils.wrapQuery;

@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
public class QuarkusAuditJobServiceTest {

    @Inject
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

        jobEvent = deriveNewState(jobEvent, 1, JobStatus.ERROR);
        publisher.publish(jobEvent);
    }

    @Test
    public void testGetAllScheduledJobs() {
        String query = "{ GetAllScheduledJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);

        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
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
                "{ GetJobById ( jobId : \\\"job1\\\") { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobById");

        assertThat(response)
                .hasSize(1);
    }

    @Test
    public void testGetJobHistoryById() {
        String query =
                "{ GetJobHistoryById ( jobId : \\\"job4\\\") { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> response = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
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

    }

    @Test
    public void testGetJobHistoryByProcessInstanceId() {
        String query =
                "{ GetJobHistoryByProcessInstanceId ( processInstanceId : \\\"processInstanceId4\\\") { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
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
        String query = "{ GetAllPendingJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
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
                "{ GetAllEligibleJobsForExecution { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter }  }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
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
                "{ GetAllEligibleJobsForRetry { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllEligibleJobsForRetry");

        assertThat(data).hasSize(1);
    }

    @Test
    public void testGetAllJobs() {
        String query = "{ GetAllJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllJobs");

        assertThat(data)
                .hasSize(5)
                .extracting(e -> e.get("jobId"))
                .containsExactlyInAnyOrder("job1", "job2", "job3", "job4", "job5");
    }

    @Test
    public void testGetAllCompletedJobs() {
        String query = "{ GetAllCompletedJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllCompletedJobs");

        assertThat(data)
                .hasSize(2)
                .allMatch(e -> "EXECUTED".equals(e.get("status")))
                .extracting(e -> e.get("jobId"))
                .containsExactlyInAnyOrder("job1", "job4");
    }

    @Test
    public void testGetAllInErrorJobs() {

        String query = "{ GetAllInErrorJobs { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllInErrorJobs");

        assertThat(data)
                .hasSize(1)
                .allMatch(e -> "ERROR".equals(e.get("status")))
                .extracting(e -> e.get("jobId"))
                .containsExactlyInAnyOrder("job5");
    }

    @Test
    public void testGetAllJobsByStatus() {

        String query =
                "{ GetAllJobsByStatus (status : \\\"EXECUTED\\\") { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllJobsByStatus");

        assertThat(data)
                .allMatch(e -> "EXECUTED".equals(e.get("status")))
                .extracting(e -> e.get("jobId"))
                .containsExactlyInAnyOrder("job1", "job4");
    }

    @Test
    public void testGetJobByProcessInstanceId() {

        String query =
                "{ GetJobByProcessInstanceId (processInstanceId : \\\"processInstanceId1\\\") { jobId, expirationTime, priority, processInstanceId, nodeInstanceId, repeatInterval, repeatLimit, scheduledId, retries, status, executionCounter } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetJobByProcessInstanceId");

        assertThat(data).first()
                .hasFieldOrPropertyWithValue("processInstanceId", "processInstanceId1");

    }

}
