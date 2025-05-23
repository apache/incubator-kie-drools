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
package org.kie.kogito.test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.awaitility.Awaitility;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class TestUtils {

    public static final String JOB_RETRIES_FIELD = "retries";
    public static final String JOB_STATUS_FIELD = "status";
    public static final String JOB_EXECUTION_COUNTER_FIELD = "executionCounter";

    private static final String JOB_BY_ID_QUERY = "{ \"query\": " +
            "\"{ Jobs (where: { id: { equal: \\\"%s\\\" } } ) " +
            " { id, expirationTime, status, scheduledId, lastUpdate, retries, repeatInterval, repeatLimit, executionCounter }" +
            "}\" }";

    private TestUtils() {
    }

    public static void assertJobsAndProcessOnDataIndex(String dataIndexURL, String processId, String processInstanceId, String processStatus, String jobStatus, Duration timeout) {
        if (dataIndexURL != null) {
            String query = "{  \"query\" : " +
                    "\"{ProcessInstances (where : {" +
                    "    id: {equal : \\\"" + processInstanceId + "\\\" }" +
                    "  }) {" +
                    "    id,processId,state" +
                    "  }" +
                    "}\"" +
                    "}";
            Awaitility.await()
                    .atMost(timeout)
                    .untilAsserted(() -> given()
                            .baseUri(dataIndexURL)
                            .contentType(ContentType.JSON)
                            .body(query)
                            .when().post("/graphql")
                            .then().statusCode(200)
                            .body("data.ProcessInstances.size()", is(1))
                            .body("data.ProcessInstances[0].id", is(processInstanceId))
                            .body("data.ProcessInstances[0].processId", is(processId))
                            .body("data.ProcessInstances[0].state", is(processStatus)));

            String queryJobs = "{  \"query\" : " +
                    "\"{Jobs (where : {" +
                    "    processInstanceId: {equal : \\\"" + processInstanceId + "\\\" }" +
                    "  }) {" +
                    "    status" +
                    "  }" +
                    "}\"" +
                    "}";

            given()
                    .baseUri(dataIndexURL)
                    .contentType(ContentType.JSON)
                    .body(queryJobs)
                    .when().post("/graphql")
                    .then().statusCode(200)
                    .body("data.Jobs.size()", is(1))
                    .body("data.Jobs[0].status", is(jobStatus));
        }
    }

    /**
     * Asserts that information for a job exists in the Data Index and returns the corresponding map representation.
     *
     * @param dataIndexUrl root url for the Data Index.
     * @param jobId identifier of the job.
     * @return the map representation of the job information stored in the Data Index.
     */
    public static Map<String, Object> assertJobInDataIndexAndReturn(String dataIndexUrl, String jobId) {
        JsonPath result = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(JOB_BY_ID_QUERY, jobId))
                .post(dataIndexUrl + "/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        List<Map<String, Object>> jobs = result.get("data.Jobs");
        assertThat(jobs).hasSize(1);
        return jobs.get(0);
    }

    /**
     * Asserts that a job exists or not in the Jobs Service.
     *
     * @param jobServiceUrl root url for the Jobs Service.
     * @param jobId identifier of the job.
     * @param exists true if the assertion must validate that the job exists, false for negative assertion.
     * @param atMostTimeoutInSeconds @see {@link Awaitility#await()}.
     */
    public static void assertJobExists(String jobServiceUrl,
            String jobId,
            boolean exists,
            long atMostTimeoutInSeconds) {

        String query = jobServiceUrl + "/v2/jobs/" + jobId;
        int expectedCode = exists ? 200 : 404;

        Awaitility.await()
                .atMost(atMostTimeoutInSeconds, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> RestAssured.given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get(query)
                        .then()
                        .statusCode(expectedCode));
    }
}
