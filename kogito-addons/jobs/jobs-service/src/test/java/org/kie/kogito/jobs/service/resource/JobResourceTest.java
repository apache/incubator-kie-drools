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

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.utils.DateUtil;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(InfinispanServerTestResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobResourceTest {

    private JobResourceTest() {

    }

    @Inject
    private ObjectMapper objectMapper;

    @Test
    void create() throws Exception {
        final Job job = getJob("1");
        final Job response = create(jobToJson(job))
                .extract()
                .as(Job.class);
        assertEquals(job, response);
    }

    private ValidatableResponse create(String body) throws IOException {
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/job")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
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
        final Job response = given().pathParam("id", id)
                .when()
                .delete("/job/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Job.class);
        assertEquals(job, response);
    }

    @Test
    void getAfterCreate() throws Exception {
        final String id = "3";
        final Job job = getJob(id);
        create(jobToJson(job));
        final Job scheduledJob = given()
                .pathParam("id", id)
                .when()
                .get("/job/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .extract()
                .as(Job.class);
        assertEquals(scheduledJob, job);

    }

    @Test
    void executeTest() throws Exception {
        final String id = "4";
        final Job job = getJob(id);
        create(jobToJson(job));
        final ScheduledJob scheduledJob = given()
                .pathParam("id", id)
                .when()
                .get("/job/scheduled/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .extract()
                .as(ScheduledJob.class);
        assertEquals(scheduledJob.getJob(), job);
        assertEquals(scheduledJob.getRetries(), 0);
        assertEquals(scheduledJob.getStatus(), JobStatus.SCHEDULED);
        assertNotNull(scheduledJob.getScheduledId());
    }
}