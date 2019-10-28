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

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.time.ZonedDateTime;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.JobBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

@QuarkusTest
class JobResourceTest {

    @Inject
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void create() throws Exception {
        final String body = getJob("1");
        create(body);
    }

    private ValidatableResponse create(String body) {
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/job")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .body(equalTo(body))
                .log()
                .body();
    }

    private String getJob(String id) throws JsonProcessingException {
        final Job job = JobBuilder
                .builder()
                .id(id)
                .expirationTime(ZonedDateTime.now().plusMinutes(1))
                .priority(1)
                .build();
        String str = mapper.writeValueAsString(job);
        System.out.println(str);
        return str;
    }

    @Test
    void deleteAfterCreate() throws Exception {
        final String id = "2";
        final String body = getJob(id);
        create(body);
        given().pathParam("id", id)
                .when()
                .delete("/job/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .body(equalTo(body))
                .log()
                .body();
    }

    @Test
    void getAfterCreate() throws Exception {
        final String id = "3";
        final String body = getJob(id);
        create(body);
        given()
                .pathParam("id", id)
                .when()
                .get("/job/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .body(equalTo(body))
                .log()
                .body();
    }
}