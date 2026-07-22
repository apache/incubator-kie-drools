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

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusIntegrationTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class PingPongMessageIT {

    public KafkaTestClient kafkaClient;

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @BeforeEach
    public void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
    }

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testPingPongBetweenProcessInstances() {
        String pId = given().body("{ \"message\": \"hello\" }")
                .contentType(ContentType.JSON)
                .when()
                .post("/ping_message")
                .then()
                .statusCode(201)
                .extract().body().path("id");

        validateSubProcess("pong_message");

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/ping_message/{pId}", pId)
                        .then()
                        .statusCode(200)
                        .body("message", equalTo("hello world")));

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/ping_message/{pId}/end", pId)
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/ping_message/{pId}", pId)
                .then()
                .statusCode(404);
    }

    private void validateSubProcess(String subProcessName) {
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/" + subProcessName + "/")
                        .then()
                        .statusCode(200)
                        .body("$.size()", equalTo(1)));

        String pId = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + subProcessName + "/")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .extract().body().path("[0].id");

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/" + subProcessName + "/{pId}", pId)
                        .then()
                        .statusCode(200)
                        .body("message", equalTo("hello world")));

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/" + subProcessName + "/{pId}/end", pId)
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + subProcessName + "/{pId}", pId)
                .then()
                .statusCode(404);
    }

    @Test
    void testPongWithValidMessage() throws InterruptedException {
        kafkaClient.produce(
                "{\"specversion\": \"1.0\", \"id\":\"id1\", \"source\": \"junit\", \"type\": \"pong_start\",  \"data\": \"hello\" }",
                "kogito_it_test");

        validateSubProcess("pong_message");
    }

    @Test
    void testPongAfterInvalidMessage() throws InterruptedException {
        // sending invalid message
        kafkaClient.produce(
                "{ \"event\": \"Hello World\" }",
                "kogito_it_test");
        // sending valid message
        kafkaClient.produce(
                "{\"specversion\": \"1.0\", \"id\":\"id1\", \"source\": \"junit\", \"type\": \"pong_start\",  \"data\": \"hello\" }",
                "kogito_it_test");

        validateSubProcess("pong_message");
    }

    @Test
    void testPongWithValidMessageAfterSignal() throws InterruptedException {
        String pId = given().body("{ \"message\": \"hello\" }")
                .contentType(ContentType.JSON)
                .when()
                .post("/pong_message_signal")
                .then()
                .statusCode(201)
                .extract().body().path("id");

        kafkaClient.produce(
                "{\"specversion\": \"1.0\", \"id\":\"id1\", \"source\": \"junit\", \"type\": \"pong_signal\",  \"data\": \"hello\" }",
                "kogito_it_test");

        validateSubProcess("pong_message_signal");
    }

}
