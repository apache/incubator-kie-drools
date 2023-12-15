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
package org.kie.kogito.quarkus.workflows;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.process.ProcessDefinitionDataEvent;
import org.kie.kogito.event.process.ProcessDefinitionEventBody;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusIntegrationTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class WorkflowEventIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowEventIT.class);

    public KafkaTestClient kafkaClient;
    private ObjectMapper mapper;

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @BeforeEach
    public void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
        mapper = new ObjectMapper()
                .registerModule(JsonFormat.getCloudEventJacksonModule())
                .registerModule(new JavaTimeModule());
    }

    @Test
    void testWorkflowEvents() throws Exception {
        final CompletableFuture<Void> future = new CompletableFuture<>();

        String username = "buddy";
        String password = "buddy";

        kafkaClient.consume(Set.of(EventPublisher.PROCESS_INSTANCES_TOPIC_NAME), s -> {
            LOGGER.debug("Received from kafka: {}", s);
            try {
                ProcessInstanceDataEvent<?> event = mapper.readValue(s, ProcessInstanceDataEvent.class);
                Map data = (Map) event.getData();
                if ("secure".equals(data.get("processId"))) {
                    if (event.getType().equals("ProcessInstanceStateDataEvent")) {
                        assertEquals("ProcessInstanceStateEvent", event.getType());
                        assertEquals("/secure", event.getSource().toString());
                        assertEquals("secure", event.getKogitoProcessId());
                        assertEquals("1.0", event.getKogitoProcessInstanceVersion());
                        assertEquals(username, event.getKogitoIdentity());
                    }
                    future.complete(null);
                }
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .auth().basic(username, password)
                .body("{\"workflowdata\" : {\"name\" : \"John\", \"language\":\"English\"}}").when()
                .post("/secure")
                .then()
                .statusCode(201)
                .body("workflowdata.greeting", is("Hello from JSON Workflow,"));

        future.get(10, TimeUnit.SECONDS);
    }

    @Test
    void testWorkflowDefinitionsEvents() {
        Collection<ProcessDefinitionDataEvent> definitionDataEvents = new ConcurrentLinkedQueue<>();
        kafkaClient.consume(Set.of(EventPublisher.PROCESS_DEFINITIONS_TOPIC_NAME), s -> {
            LOGGER.debug("Received from kafka: {}", s);
            try {
                ProcessDefinitionDataEvent event = mapper.readValue(s, ProcessDefinitionDataEvent.class);
                definitionDataEvents.add(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        List<String> processIds = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/management/processes")
                .then()
                .statusCode(200)
                .extract()
                .body().as(List.class);

        Awaitility.waitAtMost(10, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).untilAsserted(() -> assertThat(definitionDataEvents).hasSize(processIds.size()));

        List<String> processIdsFromEvent = definitionDataEvents.stream()
                .map(ProcessDefinitionDataEvent::getData)
                .map(ProcessDefinitionEventBody::getId)
                .collect(Collectors.toList());

        assertThat(processIdsFromEvent).containsAll(processIds);
    }
}
