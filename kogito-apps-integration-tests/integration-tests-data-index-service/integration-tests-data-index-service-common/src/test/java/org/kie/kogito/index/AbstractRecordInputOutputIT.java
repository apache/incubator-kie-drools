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
package org.kie.kogito.index;

import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

/**
 * Tests verify that service task input/output arguments are correctly recorded and indexed
 * based on the three-level hierarchy: node-level > process-level > global-level.
 *
 * The global property kogito.processes.nodes.record-io=true is set in application.properties.
 */
public abstract class AbstractRecordInputOutputIT {

    protected static final Duration TIMEOUT = Duration.ofSeconds(30);

    public abstract String getDataIndexURL();

    protected RequestSpecification dataIndexSpec() {
        return given()
                .baseUri(getDataIndexURL())
                .contentType(ContentType.JSON);
    }

    protected String startProcess(String processId) {
        return given()
                .contentType(ContentType.JSON)
                .body(Collections.emptyMap())
                .when()
                .post("/" + processId)
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private void verifyArgsRecorded(String processId) {
        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{ProcessInstances(where: {id: {equal: \\\"" + processId + "\\\"}}) " +
                                "{ nodes { name, type, inputArgs, outputArgs } } }\" }")
                        .when().post("/graphql")
                        .then().statusCode(200)
                        .body("data.ProcessInstances[0].nodes.find { it.type == 'WorkItemNode' }.name", is("Task"))
                        .body("data.ProcessInstances[0].nodes.find { it.type == 'WorkItemNode' }.inputArgs", notNullValue())
                        .body("data.ProcessInstances[0].nodes.find { it.type == 'WorkItemNode' }.outputArgs", notNullValue()));
    }

    private void verifyArgsNotRecorded(String processId) {
        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{ProcessInstances(where: {id: {equal: \\\"" + processId + "\\\"}}) " +
                                "{ nodes { name, type, inputArgs, outputArgs } } }\" }")
                        .when().post("/graphql")
                        .then().statusCode(200)
                        .body("data.ProcessInstances[0].nodes.find { it.type == 'WorkItemNode' }.name", is("Task"))
                        .body("data.ProcessInstances[0].nodes.find { it.type == 'WorkItemNode' }.inputArgs", nullValue())
                        .body("data.ProcessInstances[0].nodes.find { it.type == 'WorkItemNode' }.outputArgs", nullValue()));
    }

    /**
     * Configuration: Node-level recordArgs=true
     * Expected: inputArgs and outputArgs are recorded
     */
    @Test
    public void testNodeLevelTrue() {
        verifyArgsRecorded(startProcess("greeting_node_true"));
    }

    /**
     * Configuration: Node-level recordArgs=false
     * Expected: inputArgs and outputArgs are null
     */
    @Test
    public void testNodeLevelFalse() {
        verifyArgsNotRecorded(startProcess("greeting_node_false"));
    }

    /**
     * Configuration: Process-level recordArgs=true
     * Expected: inputArgs and outputArgs are recorded
     */
    @Test
    public void testProcessLevelTrue() {
        verifyArgsRecorded(startProcess("greeting_process_true"));
    }

    /**
     * Configuration: Process-level recordArgs=false
     * Expected: inputArgs and outputArgs are null
     */
    @Test
    public void testProcessLevelFalse() {
        verifyArgsNotRecorded(startProcess("greeting_process_false"));
    }

    /**
     * Configuration: Process-level recordArgs=true, Node-level recordArgs=false
     * Expected: inputArgs and outputArgs are null (node-level wins)
     */
    @Test
    public void testNodeFalseOverridesProcessTrue() {
        verifyArgsNotRecorded(startProcess("greeting_process_true_node_false"));
    }

    /**
     * Configuration: Process-level recordArgs=false, Node-level recordArgs=true
     * Expected: inputArgs and outputArgs are recorded (node-level wins)
     */
    @Test
    public void testNodeTrueOverridesProcessFalse() {
        verifyArgsRecorded(startProcess("greeting_process_false_node_true"));
    }

    /**
     * Configuration: No recordArgs metadata (falls back to global property)
     * Global Property: kogito.processes.nodes.record-io=true
     * Expected: inputArgs and outputArgs are recorded (global property is true)
     */
    @Test
    public void testNoMetadataUsesGlobalProperty() {
        verifyArgsRecorded(startProcess("greeting_no_args"));
    }

    /**
     * Tests a process with multiple service tasks having different recordArgs settings.
     * Verifies that each task respects its own configuration independently.
     *
     * Configuration:
     * - Global-level: kogito.processes.nodes.record-io=true
     * - Process-level: recordArgs=true
     * - Service Task 1: recordArgs=true (explicit node-level)
     * - Service Task 2: recordArgs=false (explicit node-level)
     * Expected:
     * - Service Task 1: inputArgs and outputArgs are recorded
     * - Service Task 2: inputArgs and outputArgs are null
     */
    @Test
    public void testMultipleTasksMixedSettings() {
        String processId = startProcess("greeting_multiple");

        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> {
                    given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                            .body("{ \"query\" : \"{ProcessInstances(where: {id: {equal: \\\"" + processId + "\\\"}}) " +
                                    "{ nodes { name, type, inputArgs, outputArgs } } }\" }")
                            .when().post("/graphql")
                            .then().statusCode(200)
                            // First task has explicit node-level true
                            .body("data.ProcessInstances[0].nodes.find { it.name == 'Service Task 1' }.inputArgs", notNullValue())
                            .body("data.ProcessInstances[0].nodes.find { it.name == 'Service Task 1' }.outputArgs", notNullValue())
                            // Second task has explicit node-level false
                            .body("data.ProcessInstances[0].nodes.find { it.name == 'Service Task 2' }.inputArgs", nullValue())
                            .body("data.ProcessInstances[0].nodes.find { it.name == 'Service Task 2' }.outputArgs", nullValue());
                });
    }
}
