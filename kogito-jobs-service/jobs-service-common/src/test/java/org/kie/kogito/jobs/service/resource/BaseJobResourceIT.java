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
package org.kie.kogito.jobs.service.resource;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;
import org.kie.kogito.jobs.service.scheduler.impl.VertxTimerServiceScheduler;
import org.kie.kogito.jobs.service.utils.DateUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class BaseJobResourceIT {

    private static final String HEALTH_ENDPOINT = "/q/health";
    private static final String CALLBACK_ENDPOINT = "http://localhost:%d/callback";
    public static final String PROCESS_ID = "processId";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String ROOT_PROCESS_ID = "rootProcessId";
    public static final String ROOT_PROCESS_INSTANCE_ID = "rootProcessInstanceId";
    public static final String NODE_INSTANCE_ID = "nodeInstanceId";
    public static final int PRIORITY = 1;

    @ConfigProperty(name = "quarkus.http.test-port")
    int port;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    TimerDelegateJobScheduler scheduler;

    @Inject
    VertxTimerServiceScheduler timer;

    @BeforeEach
    void init() {
        //health check - wait to be ready
        await()
                .atMost(1, MINUTES)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get(HEALTH_ENDPOINT)
                        .then()
                        .statusCode(200));
    }

    @AfterEach
    void tearDown() {
        scheduler.setForceExecuteExpiredJobs(false);
    }

    @Test
    void create() throws Exception {
        final Job job = getJob("1");
        final ScheduledJob response = create(jobToJson(job))
                .statusCode(200)
                .extract()
                .as(ScheduledJob.class);
        assertEquals(job, response);
    }

    @Test
    void createWithMissingAttributes() throws Exception {
        final Job job = JobBuilder
                .builder()
                .id("1")
                .callbackEndpoint(getCallbackEndpoint())
                .build();
        create(jobToJson(job))
                .statusCode(500);
    }

    private String getCallbackEndpoint() {
        return String.format(CALLBACK_ENDPOINT, port);
    }

    private ValidatableResponse create(String body) {
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(JobResource.JOBS_PATH)
                .then();
    }

    private String jobToJson(Job job) throws JsonProcessingException {
        return objectMapper.writeValueAsString(job);
    }

    private Job getJob(String id) {
        return getJob(id, DateUtil.now().plusSeconds(10));
    }

    private Job getJob(String id, ZonedDateTime expirationTime, Integer repeatLimit, Long repeatInterval) {
        return JobBuilder
                .builder()
                .id(id)
                .expirationTime(expirationTime)
                .repeatInterval(repeatInterval)
                .repeatLimit(repeatLimit)
                .callbackEndpoint(getCallbackEndpoint())
                .processId(PROCESS_ID)
                .processInstanceId(PROCESS_INSTANCE_ID)
                .rootProcessId(ROOT_PROCESS_ID)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                .nodeInstanceId(NODE_INSTANCE_ID)
                .priority(PRIORITY)
                .build();
    }

    private Job getJob(String id, ZonedDateTime expirationTime) {
        return getJob(id, expirationTime, null, null);
    }

    @Test
    void deleteAfterCreate() throws Exception {
        final String id = "2";
        final Job job = getJob(id);
        create(jobToJson(job));
        final ScheduledJob response = given().pathParam("id", id)
                .when()
                .delete(JobResource.JOBS_PATH + "/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(ScheduledJob.class);
        assertEquals(job, response);
    }

    @Test
    void getAfterCreate() throws Exception {
        final String id = "3";
        final Job job = getJob(id);
        create(jobToJson(job));
        assertGetScheduledJob(id);
    }

    @Test
    void executeTest() throws Exception {
        final String id = "4";
        final Job job = getJob(id);
        create(jobToJson(job));
        final ScheduledJob scheduledJob = assertGetScheduledJob(id);
        assertEquals(scheduledJob.getId(), job.getId());
        assertEquals(0, scheduledJob.getRetries());
        assertEquals(JobStatus.SCHEDULED, scheduledJob.getStatus());
        assertNotNull(scheduledJob.getScheduledId());
    }

    @Test
    void cancelRunningNonPeriodicJobTest() throws Exception {
        final String id = UUID.randomUUID().toString();
        final Job job = getJob(id, DateUtil.now().plus(10, ChronoUnit.SECONDS));
        create(jobToJson(job));

        assertGetScheduledJob(id);

        //guarantee the job is scheduled on vertx
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {

            //canceled the running job
            ScheduledJob scheduledJob = assertCancelScheduledJob(id);

            //assert the job was deleted from the api perspective
            assertJobNotFound(id);

            //ensure the job was indeed canceled on vertx
            assertJobNotScheduledOnVertx(scheduledJob);
        });
    }

    @Test
    //@Disabled("see https://issues.redhat.com/browse/KOGITO-1941")
    void cancelRunningPeriodicJobTest() throws Exception {
        final String id = UUID.randomUUID().toString();
        int timeMillis = 1000;
        final Job job = getJob(id, DateUtil.now().plus(timeMillis, ChronoUnit.MILLIS), 10, 500l);
        create(jobToJson(job));

        //check the job was created
        assertGetScheduledJob(id);

        //guarantee the job is running
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    //assert executed at least once
                    ScheduledJob scheduledJob = assertGetScheduledJob(id);
                    assertThat(scheduledJob.getExecutionCounter()).isPositive();

                    //canceled the running job
                    assertCancelScheduledJob(id);

                    //assert the job was deleted from the api perspective
                    assertJobNotFound(id);

                    //ensure the job was indeed canceled on vertx
                    assertJobNotScheduledOnVertx(scheduledJob);
                });
    }

    private void assertJobNotScheduledOnVertx(ScheduledJob scheduledJob) {
        assertJobScheduledOnVertx(scheduledJob, false);
    }

    private void assertJobScheduledOnVertx(ScheduledJob scheduledJob, boolean wasScheduled) {
        Long scheduledId = Long.valueOf(scheduledJob.getScheduledId());
        boolean timerCanceled = timer.getVertx().cancelTimer(scheduledId);
        assertThat(timerCanceled).isEqualTo(wasScheduled);
    }

    private void assertJobNotFound(String id) {
        given()
                .pathParam("id", id)
                .when()
                .get(JobResource.JOBS_PATH + "/{id}")
                .then()
                .statusCode(404);
    }

    private ScheduledJob assertCancelScheduledJob(String id) {
        ScheduledJob scheduledJob = given()
                .pathParam("id", id)
                .when()
                .delete(JobResource.JOBS_PATH + "/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .extract()
                .as(ScheduledJob.class);

        assertEquals(scheduledJob.getId(), id);
        assertEquals(JobStatus.CANCELED, scheduledJob.getStatus());
        assertThat(scheduledJob.getScheduledId()).isNotBlank();
        return scheduledJob;
    }

    private ScheduledJob assertGetScheduledJob(String id) {
        return assertGetScheduledJob(id, true);
    }

    private ScheduledJob assertGetScheduledJob(String id, boolean wasScheduled) {
        ScheduledJob scheduledJob = given()
                .pathParam("id", id)
                .when()
                .get(JobResource.JOBS_PATH + "/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .extract()
                .as(ScheduledJob.class);

        assertThat(scheduledJob.getId()).isEqualTo(id);
        assertThat(scheduledJob.getPriority()).isEqualTo(PRIORITY);
        assertThat(scheduledJob.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(scheduledJob.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(scheduledJob.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(scheduledJob.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(scheduledJob.getNodeInstanceId()).isEqualTo(NODE_INSTANCE_ID);
        assertThat(scheduledJob.getCallbackEndpoint()).isEqualTo(getCallbackEndpoint());
        assertThat(scheduledJob.getStatus()).isEqualTo(JobStatus.SCHEDULED);
        if (wasScheduled) {
            assertThat(scheduledJob.getScheduledId()).isNotBlank();
        } else {
            assertThat(scheduledJob.getScheduledId()).isBlank();
        }
        return scheduledJob;
    }

    @Test
    void testCreateExpiredJob() throws Exception {
        createExpiredJob().statusCode(500);
    }

    @Test
    void testForcingCreateExpiredJob() throws Exception {
        scheduler.setForceExecuteExpiredJobs(true);
        createExpiredJob().statusCode(200);
    }

    private ValidatableResponse createExpiredJob() throws JsonProcessingException {
        final Job job =
                JobBuilder
                        .builder()
                        .id(UUID.randomUUID().toString())
                        .expirationTime(DateUtil.now().minusMinutes(10))
                        .callbackEndpoint(getCallbackEndpoint())
                        .processId(PROCESS_ID)
                        .processInstanceId(PROCESS_INSTANCE_ID)
                        .rootProcessId(ROOT_PROCESS_ID)
                        .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                        .nodeInstanceId(NODE_INSTANCE_ID)
                        .priority(PRIORITY)
                        .build();
        return create(jobToJson(job));
    }

    @Test
    void patchInvalidAttributesTest() throws Exception {
        final String id = UUID.randomUUID().toString();
        final Job job = getJob(id);
        create(jobToJson(job));

        final String newCallbackEndpoint = "http://localhost/newcallback";
        Job toPatch = JobBuilder.builder().callbackEndpoint(newCallbackEndpoint).build();

        assertPatch(id, toPatch, 500);

        toPatch = JobBuilder.builder().processId(UUID.randomUUID().toString()).build();
        assertPatch(id, toPatch, 500);

        toPatch = JobBuilder.builder().rootProcessId(UUID.randomUUID().toString()).build();
        assertPatch(id, toPatch, 500);

        toPatch = JobBuilder.builder().rootProcessInstanceId(UUID.randomUUID().toString()).build();
        assertPatch(id, toPatch, 500);

        toPatch = JobBuilder.builder().processInstanceId(UUID.randomUUID().toString()).build();
        assertPatch(id, toPatch, 500);

        toPatch = JobBuilder.builder().priority(10).build();
        assertPatch(id, toPatch, 500);

        toPatch = JobBuilder.builder().repeatLimit(1).repeatInterval(1l).build();
        assertPatch(id, toPatch, 200);
    }

    private void assertPatch(String id, Job toPatch, int i) throws JsonProcessingException {
        given()
                .pathParam("id", id)
                .contentType(ContentType.JSON)
                .body(jobToJson(toPatch))
                .when()
                .patch(JobResource.JOBS_PATH + "/{id}")
                .then()
                .statusCode(i);
    }

    @Test
    void patchInvalidIdPathTest() throws Exception {
        final String id = UUID.randomUUID().toString();
        final Job job = getJob(id);
        create(jobToJson(job));

        Job toPatch = JobBuilder.builder().expirationTime(DateUtil.now()).build();

        //not found id on path
        assertPatch("invalid", toPatch, 404);

        //different id on the job object from path id
        toPatch = JobBuilder.builder().id("differentId").build();
        assertPatch(id, toPatch, 500);
    }

    @Test
    void patchReschedulingTest() throws Exception {
        final String id = UUID.randomUUID().toString();
        final Job job = getJob(id, DateUtil.now().plusHours(1));
        create(jobToJson(job));

        assertGetScheduledJob(id, false);

        Job toPatch = JobBuilder.builder().expirationTime(DateUtil.now().plusSeconds(20)).build();

        assertPatch(id, toPatch, 200);

        //ensure the job was scheduled in vertx
        assertJobScheduledOnVertx(assertGetScheduledJob(id), true);
    }
}
