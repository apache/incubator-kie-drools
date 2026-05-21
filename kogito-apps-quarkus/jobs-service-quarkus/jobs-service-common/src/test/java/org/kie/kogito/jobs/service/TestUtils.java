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
package org.kie.kogito.jobs.service;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Function;

import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.serialization.SerializationUtils;
import org.kie.kogito.jobs.service.resource.RestApiConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class TestUtils {

    public static final String CREATE_PATH_V2 = RestApiConstants.V2 + RestApiConstants.JOBS_PATH;

    public static Function<String, String> GET_JOB_V2_QUERY = (jobId) -> String.format(RestApiConstants.V2 + RestApiConstants.JOBS_PATH + "/%s", jobId);

    private TestUtils() {
    }

    public static byte[] readFileContent(String classPathResource) throws Exception {
        URL url = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(classPathResource),
                "Required test resource was not found in class path: " + classPathResource);
        Path path = Paths.get(url.toURI());
        return Files.readAllBytes(path);
    }

    public static Job createJobV2(Job job, ObjectMapper objectMapper) throws Exception {
        String body = objectMapper.writeValueAsString(job);
        String response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(CREATE_PATH_V2)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
        return objectMapper.readValue(response, Job.class);
    }

    public static Job createJobV2(Job job) throws Exception {
        return createJobV2(job, SerializationUtils.DEFAULT_OBJECT_MAPPER);
    }

    public static void assertJobHasFinishedV2(String jobId, long atMostTimeoutInSeconds) {
        await()
                .atMost(atMostTimeoutInSeconds, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get(GET_JOB_V2_QUERY.apply(jobId))
                        .then()
                        .statusCode(404));
    }
}
