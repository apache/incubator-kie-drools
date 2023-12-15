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
package org.kie.kogito.integrationtests.springboot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.ChannelType;
import org.kie.kogito.event.Topic;
import org.kie.kogito.testcontainers.springboot.KafkaSpringBootTestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers =  { KafkaSpringBootTestResource.class })
public class PingPongMessageTest extends BaseRestTest {

    @Test
    void testPingPongBetweenProcessInstances() throws InterruptedException {
        String pId = given().body("{ \"message\": \"hello\" }")
                .contentType(ContentType.JSON)
                .when()
                .post("/ping_message")
                .then()
                .statusCode(201)
                .extract().body().path("id");

        validateSubProcess();

        await().atMost(Duration.ofSeconds(10))
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


    private void validateSubProcess(){
        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/pong_message")
                        .then()
                        .statusCode(200)
                        .body("$.size()", equalTo(1)));

        String pId = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/pong_message")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .extract().body().path("[0].id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/pong_message/{pId}/end", pId)
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/pong_message/{pId}", pId)
                .then()
                .statusCode(404);
    }
    
    @Test
    void verifyTopicsInformation() {
        final List<Topic> topics = Arrays.asList(given().get("/messaging/topics").as(Topic[].class));
        assertThat(topics).isNotEmpty();
        assertThat(topics.stream().anyMatch(t -> t.getType() == ChannelType.INCOMING && t.getName().equals("pingpong"))).isTrue();
    }
}
