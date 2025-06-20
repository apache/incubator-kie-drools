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
package org.kie.kogito.it.jobs;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.test.resources.JobServiceTestResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;

import io.cloudevents.SpecVersion;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.kie.kogito.it.jobs.JobRecipientMock.JOB_RECIPIENT_MOCK;
import static org.kie.kogito.it.jobs.JobRecipientMock.JOB_RECIPIENT_MOCK_URL_PROPERTY;
import static org.kie.kogito.it.jobs.JobRecipientMock.verifyJobWasExecuted;
import static org.kie.kogito.test.TestUtils.assertJobExists;
import static org.kie.kogito.test.resources.JobServiceCompositeQuarkusTestResource.JOBS_SERVICE_URL;

@QuarkusIntegrationTest
@QuarkusTestResource(JobRecipientMock.class)
@JobServiceTestResource(knativeEventingEnabled = true)
class HttpJobExecutionIT implements JobRecipientMock.JobRecipientMockAware, JobServiceHealthAware {

    private static final String APPLICATION_CLOUD_EVENTS = "application/cloudevents+json";
    private static final String SPECVERSION = "specversion";
    private static final String ID = "id";
    private static final String SOURCE = "source";
    private static final String TYPE = "type";
    private static final String TIME = "time";
    private static final String CE_SPECVERSION_HEADER = "ce-specversion";
    private static final String CE_ID_HEADER = "ce-id";
    private static final String CE_SOURCE_HEADER = "ce-source";
    private static final String CE_TYPE_HEADER = "ce-type";
    private static final String CE_TIME_HEADER = "ce-time";

    private WireMockServer jobRecipient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setWireMockServer(WireMockServer jobRecipient) {
        this.jobRecipient = jobRecipient;
    }

    @Test
    void createAndExecuteJobWithBinaryModeEventsApi() {
        String eventsUrl = jobServiceEventsUrl();
        String jobRecipientUrl = jobRecipientMockUrl() + "/" + JOB_RECIPIENT_MOCK;

        String jobId = UUID.randomUUID().toString();
        String startTimeStr = getNowPlusSeconds(5);

        ObjectNode job = createJob(jobId, startTimeStr, jobRecipientUrl);
        String body = job.toPrettyString();

        RestAssured.given()
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header(CE_ID_HEADER, UUID.randomUUID().toString())
                .header(CE_SPECVERSION_HEADER, SpecVersion.V1.toString())
                .header(CE_TYPE_HEADER, CreateJobEvent.TYPE)
                .header(CE_SOURCE_HEADER, "http://binary.test.com")
                .header(CE_TIME_HEADER, getNowPlusSeconds(0))
                .body(body)
                .post(eventsUrl)
                .then()
                .statusCode(202);

        verifyJobWasExecuted(jobRecipient, 120, jobId, 0);
        assertJobExists(jobServiceUrl(), jobId, false, 60);
    }

    @Test
    void deleteJobWithBinaryModeEventsApi() {
        String eventsUrl = jobServiceEventsUrl();
        String jobRecipientUrl = jobRecipientMockUrl() + "/" + JOB_RECIPIENT_MOCK;

        String jobId = UUID.randomUUID().toString();
        String startTimeStr = getNowPlusSeconds(60 * 60);

        ObjectNode job = createJob(jobId, startTimeStr, jobRecipientUrl);
        String body = job.toPrettyString();

        RestAssured.given()
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header(CE_ID_HEADER, UUID.randomUUID().toString())
                .header(CE_SPECVERSION_HEADER, SpecVersion.V1.toString())
                .header(CE_TYPE_HEADER, CreateJobEvent.TYPE)
                .header(CE_SOURCE_HEADER, "http://binary.test.com")
                .header(CE_TIME_HEADER, getNowPlusSeconds(0))
                .body(body)
                .post(eventsUrl)
                .then()
                .statusCode(202);

        assertJobExists(jobServiceUrl(), jobId, true, 60);

        ObjectNode delete = objectMapper.createObjectNode()
                .put("id", jobId);
        String deleteBody = delete.toPrettyString();

        RestAssured.given()
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header(CE_ID_HEADER, UUID.randomUUID().toString())
                .header(CE_SPECVERSION_HEADER, SpecVersion.V1.toString())
                .header(CE_TYPE_HEADER, DeleteJobEvent.TYPE)
                .header(CE_SOURCE_HEADER, "http://binary.test.com")
                .header(CE_TIME_HEADER, getNowPlusSeconds(0))
                .body(deleteBody)
                .post(eventsUrl)
                .then()
                .statusCode(202);

        assertJobExists(jobServiceUrl(), jobId, false, 60);
    }

    @Test
    void executeJobWithStructuredModeEventsApi() {
        String eventsUrl = jobServiceEventsUrl();
        String jobRecipientUrl = jobRecipientMockUrl() + "/" + JOB_RECIPIENT_MOCK;

        String jobId = UUID.randomUUID().toString();
        String startTimeStr = getNowPlusSeconds(5);

        ObjectNode job = createJob(jobId, startTimeStr, jobRecipientUrl);

        ObjectNode cloudEvent = objectMapper.createObjectNode()
                .put(ID, UUID.randomUUID().toString())
                .put(SPECVERSION, SpecVersion.V1.toString())
                .put(TYPE, CreateJobEvent.TYPE)
                .put(SOURCE, "http://structured.test.com")
                .put(TIME, getNowPlusSeconds(0));
        cloudEvent.set("data", job);

        String body = cloudEvent.toPrettyString();

        RestAssured.given()
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_CLOUD_EVENTS)
                .body(body)
                .post(eventsUrl)
                .then()
                .statusCode(202);

        verifyJobWasExecuted(jobRecipient, 120, jobId, 0);
    }

    @Test
    void deleteJobWithStructuredModeEventsApi() {
        String eventsUrl = jobServiceEventsUrl();
        String jobRecipientUrl = jobRecipientMockUrl() + "/" + JOB_RECIPIENT_MOCK;

        String jobId = UUID.randomUUID().toString();
        String startTimeStr = getNowPlusSeconds(60 * 60);

        ObjectNode job = createJob(jobId, startTimeStr, jobRecipientUrl);

        ObjectNode cloudEvent = objectMapper.createObjectNode()
                .put(ID, UUID.randomUUID().toString())
                .put(SPECVERSION, SpecVersion.V1.toString())
                .put(TYPE, CreateJobEvent.TYPE)
                .put(SOURCE, "http://structured.test.com")
                .put(TIME, getNowPlusSeconds(0));
        cloudEvent.set("data", job);

        String body = cloudEvent.toPrettyString();

        RestAssured.given()
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_CLOUD_EVENTS)
                .body(body)
                .post(eventsUrl)
                .then()
                .statusCode(202);

        assertJobExists(jobServiceUrl(), jobId, true, 60);

        ObjectNode deleteCloudEvent = objectMapper.createObjectNode()
                .put(ID, UUID.randomUUID().toString())
                .put(SPECVERSION, SpecVersion.V1.toString())
                .put(TYPE, DeleteJobEvent.TYPE)
                .put(SOURCE, "http://structured.test.com")
                .put(TIME, getNowPlusSeconds(0));

        ObjectNode delete = objectMapper.createObjectNode()
                .put("id", jobId);
        deleteCloudEvent.set("data", delete);

        String deleteBody = deleteCloudEvent.toPrettyString();

        RestAssured.given()
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_CLOUD_EVENTS)
                .body(deleteBody)
                .post(eventsUrl)
                .then()
                .statusCode(202);

        assertJobExists(jobServiceUrl(), jobId, false, 60);
    }

    private ObjectNode createJob(String jobId, String startTimeStr, String jobRecipientUrl) {
        ObjectNode job = objectMapper.createObjectNode();
        job.put("id", jobId);
        job.putObject("schedule")
                .put("type", "timer")
                .put("startTime", startTimeStr);
        job.putObject("recipient")
                .put("type", "http")
                .put("url", jobRecipientUrl)
                .put("method", "POST")
                .putObject("headers")
                .put("Content-Type", "application/json")
                .put("jobId", jobId);
        return job;
    }

    public String jobServiceEventsUrl() {
        return jobServiceUrl() + "/v2/jobs/events";
    }

    @Override
    public String jobServiceUrl() {
        return System.getProperty(JOBS_SERVICE_URL);
    }

    private static String jobRecipientMockUrl() {
        return System.getProperty(JOB_RECIPIENT_MOCK_URL_PROPERTY);
    }

    private static String getNowPlusSeconds(long seconds) {
        return OffsetDateTime.now().plus(seconds, ChronoUnit.SECONDS).toString();
    }
}
