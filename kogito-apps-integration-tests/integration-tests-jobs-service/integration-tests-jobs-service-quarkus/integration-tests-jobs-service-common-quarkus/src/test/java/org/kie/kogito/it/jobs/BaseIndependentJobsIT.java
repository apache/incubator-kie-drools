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
import java.util.Map;
import java.util.UUID;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.jobs.service.api.serialization.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.restassured.RestAssured;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kie.kogito.it.jobs.JobRecipientMock.JOB_RECIPIENT_MOCK;
import static org.kie.kogito.it.jobs.JobRecipientMock.JOB_RECIPIENT_MOCK_URL_PROPERTY;
import static org.kie.kogito.it.jobs.JobRecipientMock.verifyJobWasExecuted;
import static org.kie.kogito.test.TestUtils.JOB_EXECUTION_COUNTER_FIELD;
import static org.kie.kogito.test.TestUtils.JOB_RETRIES_FIELD;
import static org.kie.kogito.test.TestUtils.JOB_STATUS_FIELD;
import static org.kie.kogito.test.TestUtils.assertJobExists;
import static org.kie.kogito.test.TestUtils.assertJobInDataIndexAndReturn;
import static org.kie.kogito.test.resources.JobServiceCompositeQuarkusTestResource.DATA_INDEX_SERVICE_URL;
import static org.kie.kogito.test.resources.JobServiceCompositeQuarkusTestResource.JOBS_SERVICE_URL;

public abstract class BaseIndependentJobsIT implements JobRecipientMock.JobRecipientMockAware, JobServiceHealthAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseIndependentJobsIT.class);

    protected WireMockServer jobRecipient;

    private static OffsetDateTime getNowPlusSeconds(long seconds) {
        return OffsetDateTime.now().plus(seconds, ChronoUnit.SECONDS);
    }

    @Override
    public void setWireMockServer(WireMockServer jobRecipient) {
        this.jobRecipient = jobRecipient;
    }

    @Test
    void testFailingJob() throws Exception {
        String jobId = UUID.randomUUID().toString();
        Job job = Job.builder()
                .id(jobId)
                .correlationId(jobId)
                .schedule(TimerSchedule.builder()
                        .startTime(getNowPlusSeconds(5))
                        .build())
                .recipient(HttpRecipient.builder().forStringPayload()
                        .url("http://never.existing.kogito.service")
                        .method("POST")
                        .payload(HttpRecipientStringPayloadData.from("Irrelevant"))
                        .build())
                .build();

        String serializedJob = SerializationUtils.DEFAULT_OBJECT_MAPPER.writeValueAsString(job);
        LOGGER.debug("Creating failing job: {}", serializedJob);
        // Create the job.
        RestAssured.given()
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(serializedJob)
                .post(jobServiceJobsUrl())
                .then()
                .statusCode(200);

        LOGGER.debug("Verifying failing job retrials in Data Index, jobId: {}", jobId);
        // Ensure the job has been retrying for some time and properly notifying the DI with the correct status while
        // retrying.
        Awaitility.await()
                .atMost(120, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    Map<String, Object> dataIndexJob = assertJobInDataIndexAndReturn(dataIndexUrl(), jobId);
                    int retries = (Integer) dataIndexJob.get(JOB_RETRIES_FIELD);
                    assertThat(retries).isGreaterThan(10);
                    assertThat(dataIndexJob).hasFieldOrPropertyWithValue(JOB_STATUS_FIELD, Job.State.RETRY.name());
                });

        LOGGER.debug("Verifying failing job reaches the ERROR state, jobId: {}", jobId);
        // Ensure the job finalizes the failing execution and properly notifies the DI with the correct status.
        Awaitility.await()
                .atMost(120, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    Map<String, Object> dataIndexJob = assertJobInDataIndexAndReturn(dataIndexUrl(), jobId);
                    assertThat(dataIndexJob).hasFieldOrPropertyWithValue(JOB_STATUS_FIELD, Job.State.ERROR.name());
                });

        LOGGER.debug("Verifying failing job is removed from the Job Service, jobId: {}", jobId);
        // Ensure the job as removed from the jobs service.
        assertJobExists(jobServiceUrl(), jobId, false, 120);
    }

    @Test
    void testSimpleJob() throws Exception {
        String jobRecipientUrl = jobRecipientMockUrl() + "/" + JOB_RECIPIENT_MOCK;
        String jobId = UUID.randomUUID().toString();
        Job job = Job.builder()
                .id(jobId)
                .correlationId(jobId)
                .schedule(TimerSchedule.builder()
                        .startTime(getNowPlusSeconds(50))
                        .build())
                .recipient(HttpRecipient.builder().forStringPayload()
                        .url(jobRecipientUrl)
                        .method("POST")
                        .payload(HttpRecipientStringPayloadData.from("Irrelevant"))
                        .header("Content-type", APPLICATION_JSON)
                        .header("jobId", jobId)
                        .build())
                .build();

        String serializedJob = SerializationUtils.DEFAULT_OBJECT_MAPPER.writeValueAsString(job);
        LOGGER.debug("Creating simple job: {}", serializedJob);
        // Create the job.
        RestAssured.given()
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(serializedJob)
                .post(jobServiceJobsUrl())
                .then()
                .statusCode(200);

        LOGGER.debug("Verifying the simple job was scheduled in the Data Index, jobId: {}", jobId);
        // Verify the job is registered as scheduled in the Data Index.
        Awaitility.await()
                .atMost(180, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    Map<String, Object> dataIndexJob = assertJobInDataIndexAndReturn(dataIndexUrl(), jobId);
                    assertThat(dataIndexJob).hasFieldOrPropertyWithValue(JOB_STATUS_FIELD, Job.State.SCHEDULED.name());
                });

        // Verify the job was executed.
        verifyJobWasExecuted(jobRecipient, jobId, 0);

        LOGGER.debug("Verifying simple job reaches the EXECUTED state jobId: {}", jobId);
        // Verify the job is registered as executed in the Data Index.
        Awaitility.await()
                .atMost(120, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    Map<String, Object> dataIndexJob = assertJobInDataIndexAndReturn(dataIndexUrl(), jobId);
                    assertThat(dataIndexJob).hasFieldOrPropertyWithValue(JOB_STATUS_FIELD, Job.State.EXECUTED.name());
                });

        // Ensure the job as removed from the jobs service.
        assertJobExists(jobServiceUrl(), jobId, false, 120);
    }

    @Test
    void testRepetitiveJob() throws Exception {
        String jobRecipientUrl = jobRecipientMockUrl() + "/" + JOB_RECIPIENT_MOCK;
        String jobId = UUID.randomUUID().toString();
        int repeatCount = 2;
        // initial execution + 2 repetitions
        int expectedExecutions = repeatCount + 1;
        Job job = Job.builder()
                .id(jobId)
                .correlationId(jobId)
                .schedule(TimerSchedule.builder()
                        .startTime(getNowPlusSeconds(30))
                        .repeatCount(2)
                        .delay(30L)
                        .delayUnit(TemporalUnit.SECONDS)
                        .build())
                .recipient(HttpRecipient.builder().forStringPayload()
                        .url(jobRecipientUrl)
                        .method("POST")
                        .payload(HttpRecipientStringPayloadData.from("Irrelevant"))
                        .header("Content-type", APPLICATION_JSON)
                        .header("jobId", jobId)
                        .build())
                .build();

        String serializedJob = SerializationUtils.DEFAULT_OBJECT_MAPPER.writeValueAsString(job);
        LOGGER.debug("Creating repetitive job: {}", serializedJob);
        // Create the job.
        RestAssured.given()
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(serializedJob)
                .post(jobServiceJobsUrl())
                .then()
                .statusCode(200);

        // Verify the job is registered as scheduled in the Data Index.
        Awaitility.await()
                .atMost(120, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    Map<String, Object> dataIndexJob = assertJobInDataIndexAndReturn(dataIndexUrl(), jobId);
                    assertThat(dataIndexJob).hasFieldOrPropertyWithValue(JOB_STATUS_FIELD, Job.State.SCHEDULED.name());
                });

        LOGGER.debug("Verifying the repetitive job programmed executions are produced jobId: {}", jobId);
        for (int i = 1; i <= expectedExecutions; i++) {
            // executions goes 1,2,3
            final int execution = i;
            // limit goes 2,1,0
            int limit = expectedExecutions - execution;
            // Verify the job was executed.
            verifyJobWasExecuted(jobRecipient, jobId, limit);
            // Verify the given execution was produced, and the expected status registered in the DI.
            Awaitility.await()
                    .atMost(120, SECONDS)
                    .with().pollInterval(1, SECONDS)
                    .untilAsserted(() -> {
                        Map<String, Object> dataIndexJob = assertJobInDataIndexAndReturn(dataIndexUrl(), jobId);
                        assertThat(dataIndexJob).hasFieldOrPropertyWithValue(JOB_EXECUTION_COUNTER_FIELD, execution);
                        if (execution < expectedExecutions) {
                            assertThat(dataIndexJob).hasFieldOrPropertyWithValue(JOB_STATUS_FIELD, Job.State.SCHEDULED.name());
                        } else {
                            assertThat(dataIndexJob).hasFieldOrPropertyWithValue(JOB_STATUS_FIELD, Job.State.EXECUTED.name());
                        }
                    });
        }
        // Ensure the job as removed from the jobs service.
        assertJobExists(jobServiceUrl(), jobId, false, 120);
    }

    public String jobServiceJobsUrl() {
        return jobServiceUrl() + "/v2/jobs";
    }

    public String jobServiceUrl() {
        return System.getProperty(JOBS_SERVICE_URL);
    }

    public String dataIndexUrl() {
        return System.getProperty(DATA_INDEX_SERVICE_URL);
    }

    public String jobRecipientMockUrl() {
        return System.getProperty(JOB_RECIPIENT_MOCK_URL_PROPERTY);
    }
}
