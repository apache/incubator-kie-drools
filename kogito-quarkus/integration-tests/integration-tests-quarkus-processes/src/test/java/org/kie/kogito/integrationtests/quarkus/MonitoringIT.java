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
package org.kie.kogito.integrationtests.quarkus;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

@QuarkusIntegrationTest
public class MonitoringIT {

    private static final String ARTIFACT_ID = "integration-tests-quarkus-processes";
    private static final String VERSION = System.getProperty("kogito.version", "999-SNAPSHOT");

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void test() {
        String pId = given().contentType(ContentType.JSON)
                .when()
                .post("/monitoring/")
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .extract().path("id");

        String response = given().contentType(ContentType.JSON)
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(200)
                .extract().body().asString();

        assertThat(response).contains(format("kogito_process_instance_started_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"monitoring\",version=\"%s\"} 1.0",
                ARTIFACT_ID, VERSION))
                .contains(format("kogito_process_instance_running_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"monitoring\",version=\"%s\"} 1.0",
                        ARTIFACT_ID, VERSION));

        String taskId = given()
                .contentType(ContentType.JSON)
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("pId", pId)
                .when()
                .get("/monitoring/{pId}/tasks")
                .then()
                .statusCode(200)
                .extract()
                .path("[0].id");

        given().contentType(ContentType.JSON)
                .pathParam("pId", pId)
                .pathParam("taskId", taskId)
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .body("{}")
                .when()
                .post("/monitoring/{pId}/MonitoringTask/{taskId}/phases/complete")
                .then()
                .statusCode(200);

        response = given().contentType(ContentType.JSON)
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(200)
                .extract().body().asString();

        assertThat(response).contains(format("kogito_work_item_duration_seconds_max{artifactId=\"%s\",name=\"MonitoringTask\",version=\"%s\"}", ARTIFACT_ID, VERSION))
                .contains(format("kogito_work_item_duration_seconds_count{artifactId=\"%s\",name=\"MonitoringTask\",version=\"%s\"} 1.0", ARTIFACT_ID, VERSION))
                .contains(format("kogito_work_item_duration_seconds_sum{artifactId=\"%s\",name=\"MonitoringTask\",version=\"%s\"}", ARTIFACT_ID, VERSION))
                .contains(format(
                        "kogito_process_instance_completed_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"monitoring\",process_state=\"Completed\",version=\"%s\"} 1.0",
                        ARTIFACT_ID, VERSION))
                .contains(format("kogito_process_instance_running_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"monitoring\",version=\"%s\"} 0.0",
                        ARTIFACT_ID, VERSION))
                .contains(format("kogito_process_instance_duration_seconds_max{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"monitoring\",version=\"%s\"}",
                        ARTIFACT_ID, VERSION))
                .contains(format("kogito_process_instance_duration_seconds_count{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"monitoring\",version=\"%s\"} 1.0",
                        ARTIFACT_ID, VERSION))
                .contains(format("kogito_process_instance_duration_seconds_sum{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"monitoring\",version=\"%s\"}",
                        ARTIFACT_ID, VERSION));
    }

}
