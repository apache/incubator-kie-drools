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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.jobs.service.api.serialization.SerializationUtils;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.cloudevents.CloudEvent;
import io.restassured.RestAssured;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kie.kogito.it.jobs.JobRecipientMock.FAILING_JOB_RECIPIENT_MOCK;
import static org.kie.kogito.it.jobs.JobRecipientMock.JOB_RECIPIENT_MOCK;
import static org.kie.kogito.it.jobs.JobRecipientMock.JOB_RECIPIENT_MOCK_URL_PROPERTY;
import static org.kie.kogito.it.jobs.JobRecipientMock.verifyFailingJobWasExecutedAtLeastCount;
import static org.kie.kogito.it.jobs.JobRecipientMock.verifyJobWasExecuted;
import static org.kie.kogito.test.TestUtils.JOB_STATUS_FIELD;
import static org.kie.kogito.test.TestUtils.assertJobExists;
import static org.kie.kogito.test.TestUtils.assertJobInDataIndexAndReturn;
import static org.kie.kogito.test.resources.JobServiceCompositeQuarkusTestResource.DATA_INDEX_SERVICE_URL;
import static org.kie.kogito.test.resources.JobServiceCompositeQuarkusTestResource.JOBS_SERVICE_URL;

public abstract class BaseIndependentJobsIT implements JobRecipientMock.JobRecipientMockAware, JobServiceHealthAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseIndependentJobsIT.class);
    private static final String KOGITO_JOBS_EVENTS_TOPIC = "kogito-jobs-events";

    private static final Function<String, Predicate<ScheduledJob>> IS_RETRY_EVENT_FOR_JOB = jobId -> {
        return scheduledJob -> jobId.equals(scheduledJob.getId()) && JobStatus.RETRY == scheduledJob.getStatus();
    };

    private static final Function<String, Predicate<ScheduledJob>> IS_SCHEDULED_EVENT_FOR_JOB = jobId -> {
        return scheduledJob -> jobId.equals(scheduledJob.getId()) && JobStatus.SCHEDULED == scheduledJob.getStatus();
    };

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
        String jobRecipientUrl = jobRecipientMockUrl() + "/" + FAILING_JOB_RECIPIENT_MOCK;
        String jobId = UUID.randomUUID().toString();
        Job job = Job.builder()
                .id(jobId)
                .correlationId(jobId)
                .schedule(TimerSchedule.builder()
                        .startTime(getNowPlusSeconds(5))
                        .build())
                .recipient(HttpRecipient.builder().forStringPayload()
                        .url(jobRecipientUrl)
                        .method("POST")
                        .payload(HttpRecipientStringPayloadData.from("Irrelevant"))
                        .header("jobId", jobId)
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
        assertJobExists(jobServiceUrl(), jobId, false, 60);

        // For the default configurations we should expect that at least 30 reties where produced.
        int minExpectedRetries = 30;
        verifyFailingJobWasExecutedAtLeastCount(jobRecipient, 30, minExpectedRetries, jobId, 0);

        // For the default configurations we should expect at least 30 events where produced notifying every retry.
        waitForAtLeastJobEvents(createKafkaTestClient(jobId, kafkaBootstrapServers()), KOGITO_JOBS_EVENTS_TOPIC,
                60, IS_RETRY_EVENT_FOR_JOB.apply(jobId), minExpectedRetries, true);
    }

    @Test
    void testSimpleJob() throws Exception {
        String jobRecipientUrl = jobRecipientMockUrl() + "/" + JOB_RECIPIENT_MOCK;
        String jobId = UUID.randomUUID().toString();
        Job job = Job.builder()
                .id(jobId)
                .correlationId(jobId)
                .schedule(TimerSchedule.builder()
                        .startTime(getNowPlusSeconds(5))
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

        // Verify the job was executed.
        verifyJobWasExecuted(jobRecipient, 120, jobId, 0);

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
        assertJobExists(jobServiceUrl(), jobId, false, 60);

        waitForAtLeastJobEvents(createKafkaTestClient(jobId, kafkaBootstrapServers()), KOGITO_JOBS_EVENTS_TOPIC,
                60, IS_SCHEDULED_EVENT_FOR_JOB.apply(jobId), 1, true);
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
                        .startTime(getNowPlusSeconds(5))
                        .repeatCount(repeatCount)
                        .delay(5L)
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

        LOGGER.debug("Verifying the repetitive job programmed executions are produced jobId: {}", jobId);
        for (int i = 1; i <= expectedExecutions; i++) {
            // limit goes 2,1,0
            int limit = expectedExecutions - i;
            verifyJobWasExecuted(jobRecipient, 120, jobId, limit);
        }

        LOGGER.debug("Verifying repetitive job reaches the EXECUTED state jobId: {}", jobId);
        // Verify the job is registered as executed in the Data Index.
        Awaitility.await()
                .atMost(120, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    Map<String, Object> dataIndexJob = assertJobInDataIndexAndReturn(dataIndexUrl(), jobId);
                    assertThat(dataIndexJob).hasFieldOrPropertyWithValue(JOB_STATUS_FIELD, Job.State.EXECUTED.name());
                });

        // Ensure the job as removed from the jobs service.
        assertJobExists(jobServiceUrl(), jobId, false, 60);

        // Ensure status changes where produced incrementing the execution counter.
        // Note, the SCHEDULED event is produced many times.
        List<ScheduledJob> scheduledJobs = waitForAtLeastJobEvents(createKafkaTestClient(jobId, kafkaBootstrapServers()), KOGITO_JOBS_EVENTS_TOPIC,
                60, IS_SCHEDULED_EVENT_FOR_JOB.apply(jobId), 9, true);
        for (int i = 0; i <= expectedExecutions; i++) {
            int execution = i;
            assertThat(scheduledJobs.stream().anyMatch(scheduledJob -> scheduledJob.getExecutionCounter().equals(execution))).isTrue();
        }
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

    public String kafkaBootstrapServers() {
        return System.getProperty(KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY);
    }

    public String jobServiceJobsUrl() {
        return jobServiceUrl() + "/v2/jobs";
    }

    public static List<ScheduledJob> waitForAtLeastJobEvents(KafkaTestClient kafkaClient, String topic, int atMostTimeoutInSeconds,
            Predicate<ScheduledJob> filter, int atLeastCount,
            boolean shutdownAfterConsume) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(atLeastCount);
        final List<ScheduledJob> scheduledJobs = new ArrayList<>();
        kafkaClient.consume(topic, rawCloudEvent -> {
            CloudEvent cloudEvent;
            ScheduledJob scheduledJob;
            try {
                cloudEvent = SerializationUtils.DEFAULT_OBJECT_MAPPER.readValue(rawCloudEvent, CloudEvent.class);
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Failed to parse %s from rawCloudEvent: %s.", CloudEvent.class, rawCloudEvent));
            }
            try {
                scheduledJob = SerializationUtils.DEFAULT_OBJECT_MAPPER.readValue(cloudEvent.getData().toBytes(), ScheduledJob.class);
                if (filter.test(scheduledJob)) {
                    scheduledJobs.add(scheduledJob);
                    countDownLatch.countDown();
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Failed to parse %s from cloudEventData: %s.", ScheduledJob.class, cloudEvent.getData().toString()));
            }
        });
        // consume events during configured time.
        Assertions.assertThat(countDownLatch.await(atMostTimeoutInSeconds, TimeUnit.SECONDS))
                .withFailMessage("At least %d events where expected but %d where produced in the configured time frame", atLeastCount, atLeastCount - countDownLatch.getCount())
                .isTrue();
        if (shutdownAfterConsume) {
            kafkaClient.shutdown();
        }
        return scheduledJobs;
    }

    private static KafkaTestClient createKafkaTestClient(String jobId, String kafkaBoostrapServers) {
        Properties additionalProperties = new Properties();
        additionalProperties.put(ConsumerConfig.GROUP_ID_CONFIG, jobEventConsumerGroup(jobId));
        return new KafkaTestClient(kafkaBoostrapServers, additionalProperties);
    }

    private static String jobEventConsumerGroup(String jobId) {
        return jobId + "Consumer";
    }
}
