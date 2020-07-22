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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.restassured.http.ContentType;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.KafkaTestResource;
import org.kie.kogito.persistence.protobuf.ProtobufService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isA;
import static org.kie.kogito.index.TestUtils.readFileContent;

public abstract class AbstractReactiveMessagingEventConsumerKafkaIT {

    @Inject
    ProtobufService protobufService;

    KafkaProducer<String, String> producer;

    @BeforeEach
    void setup() {
        String kafka = System.getProperty(KafkaTestResource.KAFKA_BOOTSTRAP_SERVERS, "localhost:9092");
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", kafka);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("acks", "all");
        producer = KafkaProducer.create(Vertx.vertx(), config);
    }

    @AfterEach
    void close() {
        if (producer != null) {
            producer.close();
        }
    }

    @Test
    void testProcessInstanceEvent() throws Exception {
        sendProcessInstanceEvent().get(1, TimeUnit.MINUTES);

        String processInstanceId = "c2fa5c5e-3002-44c7-aef7-bce82297e3fe";

        protobufService.registerProtoBufferType(getTestProtobufFileContent());

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels", isA(Collection.class));

        sendProcessInstanceEvent().get(1, TimeUnit.MINUTES);

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.ProcessInstances.size()", is(1))
                .body("data.ProcessInstances[0].id", is(processInstanceId));

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].id", is("f8868a2e-1bbb-47eb-93cf-fa46ff9dbfee"));

        given()
                .when().get("/metrics")
                .then().log().ifValidationFails().statusCode(200)
                .body(containsString("application_mp_messaging_message_count_total{channel=\"kogito-processdomain-events\"} 2.0"),
                      containsString("application_mp_messaging_message_count_total{channel=\"kogito-processinstances-events\"} 2.0"));
    }

    private CompletableFuture<Void> sendProcessInstanceEvent() throws Exception {
        String json = readFileContent("process_instance_event.json");
        CompletableFuture<Void> future = new CompletableFuture<>();
        producer.write(KafkaProducerRecord.create("kogito-processinstances-events", json), event -> {
            if (event.succeeded()) {
                future.complete(null);
            } else {
                future.completeExceptionally(event.cause());
            }
        });
        return future;
    }

    protected abstract String getTestProtobufFileContent() throws Exception;
}
