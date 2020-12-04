/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.integrationtests.springboot;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.testcontainers.springboot.InfinispanSpringBootTestResource;
import org.kie.kogito.testcontainers.springboot.KafkaSpringBootTestResource;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers =  { KafkaSpringBootTestResource.class, InfinispanSpringBootTestResource.Conditional.class })
public class PingPongMessageTest extends BaseRestTest {

    @Autowired
    @Qualifier(KogitoEventStreams.PUBLISHER)
    Publisher<String> publisher;

    @Test
    void testPingPongBetweenProcessInstances() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.from(publisher)
                .map(x -> {
                    try {
                        return (Map<String, String>) new ObjectMapper().readValue(x, Map.class);
                    } catch (JsonProcessingException e) {
                        throw new Error(e);
                    }
                })
                .filter(m -> "hello world".equals(m.get("data")) &&
                        m.getOrDefault("source", "").startsWith("/process/pong_message/"))
                .subscribe(x -> latch.countDown());

        String pId = given().body("{ \"message\": \"hello\" }")
                .contentType(ContentType.JSON)
                .when()
                .post("/ping_message")
                .then()
                .statusCode(201)
                .extract().body().path("id");

        validateSubProcess();

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

        latch.await(5, TimeUnit.SECONDS);

    }


    private void validateSubProcess(){
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/pong_message/")
                        .then()
                        .statusCode(200)
                        .body("$.size", equalTo(1)));

        String pId = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/pong_message/")
                .then()
                .statusCode(200)
                .body("$.size", equalTo(1))
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
}
