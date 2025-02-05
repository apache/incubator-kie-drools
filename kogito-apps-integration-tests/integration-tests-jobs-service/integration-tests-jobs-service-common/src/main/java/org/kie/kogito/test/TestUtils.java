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

import org.awaitility.Awaitility;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class TestUtils {

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
}
