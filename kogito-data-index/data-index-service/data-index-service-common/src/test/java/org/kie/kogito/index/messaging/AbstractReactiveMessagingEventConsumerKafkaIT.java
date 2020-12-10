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

package org.kie.kogito.index.messaging;

import java.util.Collection;

import javax.inject.Inject;

import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.persistence.protobuf.ProtobufService;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isA;
import static org.kie.kogito.index.TestUtils.readFileContent;

abstract class AbstractReactiveMessagingEventConsumerKafkaIT {

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;

    @Inject
    ProtobufService protobufService;

    KafkaClient kafkaClient;

    @BeforeEach
    void setup() {
        kafkaClient = new KafkaClient(kafkaBootstrapServers);
    }

    @AfterEach
    void close() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
    }

    @Test
    void testProcessInstanceEvent() throws Exception {
        protobufService.registerProtoBufferType(getTestProtobufFileContent());

        sendProcessInstanceEvent();

        String processInstanceId = "c2fa5c5e-3002-44c7-aef7-bce82297e3fe";

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels { id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels", isA(Collection.class));

        sendProcessInstanceEvent();

        await()
                .atMost(5, SECONDS)
                .untilAsserted(() -> {
                                   given().contentType(ContentType.JSON).body("{ \"query\" : \"{ ProcessInstances { id } }\" }")
                                           .when().post("/graphql")
                                           .then().log().ifValidationFails().statusCode(200)
                                           .body("data.ProcessInstances.size()", is(1))
                                           .body("data.ProcessInstances[0].id", is(processInstanceId));
                               }
                );

        await()
                .atMost(5, SECONDS)
                .untilAsserted(() -> {
                                   given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels { id } }\" }")
                                           .when().post("/graphql")
                                           .then().log().ifValidationFails().statusCode(200)
                                           .body("data.Travels[0].id", is("f8868a2e-1bbb-47eb-93cf-fa46ff9dbfee"));
                               }
                );

        await()
                .atMost(5, SECONDS)
                .untilAsserted(() -> {
                                   given()
                                           .when().get("/metrics")
                                           .then().log().ifValidationFails().statusCode(200)
                                           .body(containsString("base_mp_messaging_message_count_total{channel=\"kogito-processdomain-events\"} 2.0"),
                                                 containsString("base_mp_messaging_message_count_total{channel=\"kogito-processinstances-events\"} 2.0"));
                               }
                );
    }

    private void sendProcessInstanceEvent() throws Exception {
        String json = readFileContent("process_instance_event.json");
        kafkaClient.produce(json, "kogito-processinstances-events");
    }

    protected abstract String getTestProtobufFileContent() throws Exception;
}
