/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.resource;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.scheduler.impl.VertxJobScheduler;
import org.kie.kogito.jobs.service.utils.DateUtil;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(InfinispanServerTestResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobResourceIT {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    Vertx vertx;

    @Inject
    VertxJobScheduler scheduler;

    @Test
    void create() throws Exception {
        final Job job = getJob("1");
        final ScheduledJob response = create(jobToJson(job))
                .statusCode(200)
                .extract()
                .as(ScheduledJob.class);
        assertEquals(job, response);
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
        return JobBuilder
                .builder()
                .id(id)
                .expirationTime(DateUtil.now().plusSeconds(10))
                .callbackEndpoint("http://localhost:8081/callback")
                .priority(1)
                .build();
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
        final Job job = JobBuilder
                .builder()
                .id(id)
                .expirationTime(DateUtil.now().plus(10, ChronoUnit.SECONDS))
                .callbackEndpoint("http://localhost:8081/callback")
                .priority(1)
                .build();
        create(jobToJson(job));

        assertGetScheduledJob(id);

        //guarantee the job is scheduled on vertx
        Thread.sleep(500);

        //canceled the running job
        ScheduledJob scheduledJob = assertCancelScheduledJob(id);

        //assert the job was deleted from the api perspective
        assertJobNotFound(id);

        //ensure the job was indeed canceled on vertx
        assertJobNotScheduledOnVertx(scheduledJob);
    }

    @Test
    @Disabled("see https://issues.redhat.com/browse/KOGITO-1941")
    void cancelRunningPeriodicJobTest() throws Exception {
        final String id = UUID.randomUUID().toString();
        int timeMillis = 1000;
        final Job job = JobBuilder
                .builder()
                .id(id)
                .expirationTime(DateUtil.now().plus(timeMillis, ChronoUnit.MILLIS))
                .repeatLimit(10)
                .repeatInterval(500l)
                .callbackEndpoint("http://localhost:8081/callback")
                .priority(1)
                .build();
        create(jobToJson(job));

        //check the job was created
        assertGetScheduledJob(id);

        //guarantee the job is running
        Thread.sleep(timeMillis + 1);

        //canceled the running job
        ScheduledJob scheduledJob = assertCancelScheduledJob(id);
        assertThat(scheduledJob.getExecutionCounter()).isGreaterThan(0);

        //assert the job was deleted from the api perspective
        assertJobNotFound(id);

        //ensure the job was indeed canceled on vertx
        assertJobNotScheduledOnVertx(scheduledJob);
    }

    private void assertJobNotScheduledOnVertx(ScheduledJob scheduledJob) {
        Long scheduledId = Long.valueOf(scheduledJob.getScheduledId());
        boolean timerCanceled = vertx.cancelTimer(scheduledId);
        assertThat(timerCanceled).isFalse();
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
        assertThat(scheduledJob.getStatus()).isEqualTo(JobStatus.SCHEDULED);
        assertThat(scheduledJob.getScheduledId()).isNotBlank();
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
                        .callbackEndpoint("http://localhost:8081/callback")
                        .priority(1)
                        .build();
        return create(jobToJson(job));
    }

    @Test
    void patchCallbackEndpointTest() throws Exception {
        final String id = UUID.randomUUID().toString();
        final Job job = getJob(id);
        create(jobToJson(job));

        final String newCallbackEndpoint = "http://localhost/newcallback";
        final Job toPatch = JobBuilder.builder().callbackEndpoint(newCallbackEndpoint).build();

        final ScheduledJob scheduledJob = given()
                .pathParam("id", id)
                .contentType(ContentType.JSON)
                .body(jobToJson(toPatch))
                .when()
                .patch(JobResource.JOBS_PATH + "/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .extract()
                .as(ScheduledJob.class);

        assertThat(scheduledJob.getCallbackEndpoint()).isEqualTo(newCallbackEndpoint);
        assertThat(scheduledJob.getId()).isEqualTo(job.getId());
        assertThat(scheduledJob.getExpirationTime()).isEqualTo(job.getExpirationTime());
        assertThat(scheduledJob.getPriority()).isEqualTo(job.getPriority());
        assertThat(scheduledJob.getRepeatLimit()).isEqualTo(job.getRepeatLimit());
        assertThat(scheduledJob.getRepeatInterval()).isEqualTo(job.getRepeatInterval());
        assertThat(scheduledJob.getProcessId()).isEqualTo(job.getProcessId());
        assertThat(scheduledJob.getRootProcessInstanceId()).isEqualTo(job.getRootProcessInstanceId());
        assertThat(scheduledJob.getRootProcessId()).isEqualTo(job.getRootProcessId());
        assertThat(scheduledJob.getProcessInstanceId()).isEqualTo(job.getRootProcessInstanceId());
    }

    @Test
    void patchInvalidIdPathTest() throws Exception {
        final String id = UUID.randomUUID().toString();
        final Job job = getJob(id);
        create(jobToJson(job));

        final String newCallbackEndpoint = "http://localhost/newcallback";
        Job toPatch = JobBuilder.builder().callbackEndpoint(newCallbackEndpoint).build();

        //not found id on path
        given()
                .pathParam("id", "invalid")
                .contentType(ContentType.JSON)
                .body(jobToJson(toPatch))
                .when()
                .patch(JobResource.JOBS_PATH + "/{id}")
                .then()
                .statusCode(404);

        //different id on the job object from path id
        toPatch = JobBuilder.builder().id("differentId").callbackEndpoint(newCallbackEndpoint).build();
        given()
                .pathParam("id", id)
                .contentType(ContentType.JSON)
                .body(jobToJson(toPatch))
                .when()
                .patch(JobResource.JOBS_PATH + "/{id}")
                .then()
                .statusCode(500);
    }
}