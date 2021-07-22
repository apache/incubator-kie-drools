/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.time.Duration;
import java.util.Collection;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.DataIndexStorageService;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.persistence.protobuf.ProtobufService;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isA;
import static org.kie.kogito.index.TestUtils.readFileContent;

public abstract class AbstractDomainMessagingConsumerKafkaIT {

    Duration timeout = Duration.ofSeconds(30);

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY, defaultValue = "localhost:9092")
    public String kafkaBootstrapServers;

    @Inject
    public ProtobufService protobufService;

    @Inject
    public DataIndexStorageService cacheService;

    KafkaClient kafkaClient;

    @BeforeEach
    void setup() throws Exception {
        kafkaClient = new KafkaClient(kafkaBootstrapServers);
        protobufService.registerProtoBufferType(getTestProtobufFileContent());
        cacheService.getProcessInstancesCache().clear();
        cacheService.getUserTaskInstancesCache().clear();
        if (cacheService.getDomainModelCache("travels") != null) {
            cacheService.getDomainModelCache("travels").clear();
        }
    }

    @AfterEach
    void close() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
        cacheService.getProcessInstancesCache().clear();
        cacheService.getUserTaskInstancesCache().clear();
        if (cacheService.getDomainModelCache("travels") != null) {
            cacheService.getDomainModelCache("travels").clear();
        }
    }

    @Test
    void testProcessInstanceEvent() throws Exception {
        sendProcessInstanceEvent();

        String processInstanceId = "c2fa5c5e-3002-44c7-aef7-bce82297e3fe";

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels { id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels", isA(Collection.class));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{ Travels { id, metadata { processInstances { id } } } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.Travels[0].id", is("f8868a2e-1bbb-47eb-93cf-fa46ff9dbfee"))
                        .body("data.Travels[0].metadata.processInstances[0].id", is(processInstanceId)));

    }

    @Test
    void testUserTaskInstanceEvent() throws Exception {
        sendUserTaskInstanceEvent();

        String taskId = "228d5922-5e88-4bfa-8329-7116a5cbe58b";

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels { id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels", isA(Collection.class));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{ Travels { id, metadata { userTasks { id } } } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.Travels[0].id", is("f78fb147-ec22-4478-a592-3063add9f956"))
                        .body("data.Travels[0].metadata.userTasks[0].id", is(taskId)));
    }

    private void sendUserTaskInstanceEvent() throws Exception {
        send("user_task_instance_event.json", "kogito-usertaskinstances-events");
    }

    private void sendProcessInstanceEvent() throws Exception {
        send("process_instance_event.json", "kogito-processinstances-events");
    }

    private void send(String file, String topic) throws Exception {
        String json = readFileContent(file);
        kafkaClient.produce(json, topic);
    }

    protected abstract String getTestProtobufFileContent() throws Exception;
}
