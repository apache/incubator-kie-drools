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
package org.kie.kogito;

import java.time.Duration;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;

public final class FailingServiceTestUtil {

    public static final String FAILING_SYNC_SERVICE_TASK_PROCESS_ID = "FailingSyncServiceTask";
    public static final String FAILING_ASYNC_SERVICE_TASK_PROCESS_ID = "FailingAsyncServiceTask";

    public static final String SERVICE_TASK_NODE_DEFINTION_ID = "_C02E8C5F-5134-47A5-9784-45821DB66B5D";
    public static final String USER_TASK_NODE_DEFINTION_ID = "_E2424AB3-6F2A-4624-B451-C126EE9EABA1";

    public static final Duration TIMEOUT = Duration.ofSeconds(10);
    public static final Duration POLL_DELAY = Duration.ofSeconds(5);
    public static final Duration POLL_INTERVAL = Duration.ofMillis(500);

    private FailingServiceTestUtil() {
    }

    public static JsonPath executeGraphQLQuery(String query) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(query)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
    }

    public static String getQuery(String processInstanceId) {
        return String.format(
                "{ \"query\": \"{ " +
                        "ProcessInstances(where: { id: { equal: \\\"%s\\\" } }) { " +
                        "id, processId, state, " +
                        "error { nodeDefinitionId, nodeInstanceId, message }, " +
                        "nodes { id, type, enter, exit, definitionId, cancelType, errorMessage } " +
                        "}, " +
                        "Jobs(where: { processInstanceId: { equal: \\\"%s\\\" } }) { " +
                        "processInstanceId, nodeInstanceId, status " +
                        "} " +
                        "}\" }",
                processInstanceId, processInstanceId);
    }
}
