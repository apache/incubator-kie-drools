/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quarkus.workflows;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import static org.awaitility.Awaitility.await;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceExists;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceHasFinished;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.getProcessInstance;

@QuarkusIntegrationTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class CorrelationIT {

    public static final String USER_ID = "userid";
    public static final String PROCESS_URL = "/correlation";
    public static final String PROCESS_GET_BY_ID_URL = PROCESS_URL + "/{id}";
    public static final String CORRELATION_EVENT_TYPE = "correlation_event_type";
    public static final String CORRELATION_EVENT_TOPIC = CORRELATION_EVENT_TYPE;
    public static final String START_EVENT_TYPE = "correlation_start_event_type";
    public static final String START_EVENT_TOPIC = START_EVENT_TYPE;
    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIT.class);

    private KafkaTestClient kafkaClient;

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(JsonFormat.getCloudEventJacksonModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @AfterEach
    public void cleanup() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
    }

    @Test
    void correlationEventTestDefault() throws Exception {
        correlationEventTest(null);
    }

    private void correlationEventTest(String startNode) throws JsonProcessingException {
        final String userId = UUID.randomUUID().toString();

        // start a new process instance by sending and event
        LOGGER.debug("Sending create correlation workflow event");
        io.cloudevents.core.v1.CloudEventBuilder cloudEventBuilder = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType(START_EVENT_TYPE)
                .withTime(OffsetDateTime.now())
                .withExtension(USER_ID, userId)
                .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("message", "Starting workflow using correlation")));

        //setting start node if present
        if (startNode != null) {
            cloudEventBuilder.withExtension(CloudEventExtensionConstants.PROCESS_START_FROM_NODE, startNode);
        }

        String request = objectMapper.writeValueAsString(cloudEventBuilder
                .build());
        kafkaClient.produce(request, START_EVENT_TOPIC);

        // double check that the process instance is there.
        AtomicReference<String> processInstanceId = new AtomicReference<>();
        await().with().pollDelay(2, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).atMost(2, TimeUnit.MINUTES).until(() -> {
            String id = getProcessInstance(PROCESS_URL);
            LOGGER.debug("Created workflow instance id = " + id);
            processInstanceId.set(id);
            return id != null;
        });

        assertProcessInstanceExists(PROCESS_GET_BY_ID_URL, processInstanceId.get());

        // prepare and send the response to the created process via kafka
        LOGGER.debug("Sending correlation event to complete Workflow {}", processInstanceId.get());
        String response = objectMapper.writeValueAsString(CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType(CORRELATION_EVENT_TYPE)
                .withTime(OffsetDateTime.now())
                .withExtension(USER_ID, userId)
                .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("message", "Hello using correlation")))
                .build());
        kafkaClient.produce(response, CORRELATION_EVENT_TOPIC);
        // give some time for the event to be processed and the process to finish.
        assertProcessInstanceHasFinished(PROCESS_GET_BY_ID_URL, processInstanceId.get(), 1, 180);
        LOGGER.debug("Workflow {} completed", processInstanceId.get());
    }

    @Test
    void correlationEventStartFromNodeTest() throws Exception {
        correlationEventTest("printWaitMessage");//printWaitMessage node
    }
}
