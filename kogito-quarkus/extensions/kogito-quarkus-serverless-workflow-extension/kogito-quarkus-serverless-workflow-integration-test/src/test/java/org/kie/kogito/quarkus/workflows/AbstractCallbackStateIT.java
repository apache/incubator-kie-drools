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
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.JsonFormat;
import io.restassured.path.json.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.quarkus.workflows.ExternalServiceMock.GENERATE_ERROR_QUERY;
import static org.kie.kogito.quarkus.workflows.ExternalServiceMock.SUCCESSFUL_QUERY;
import static org.kie.kogito.quarkus.workflows.WorkflowTestUtils.waitForKogitoProcessInstanceEvent;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceExists;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceHasFinished;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceNotExists;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstance;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstanceAndGetId;

abstract class AbstractCallbackStateIT {

    static final String ANSWER = "ANSWER";

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;
    ObjectMapper objectMapper;
    KafkaTestClient kafkaClient;

    @BeforeEach
    void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(JsonFormat.getCloudEventJacksonModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @AfterEach
    void cleanUp() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
    }

    void executeCallbackStateSuccessfulPath(String callbackProcessPostUrl,
            String callbackProcessGetByIdUrl,
            String answer,
            String callbackEventType,
            String callbackEventTopic) throws Exception {
        // start a new process instance by sending the post query and collect the process instance id.
        String processInput = buildProcessInput(SUCCESSFUL_QUERY);
        String processInstanceId = newProcessInstanceAndGetId(callbackProcessPostUrl, processInput);

        JsonPath processInstanceEventContent = waitForKogitoProcessInstanceEvent(kafkaClient, true);
        Map workflowDataMap = processInstanceEventContent.getMap("data.variables.workflowdata");
        assertThat(workflowDataMap).hasSize(1);
        assertThat(workflowDataMap).containsEntry("query", SUCCESSFUL_QUERY);

        // double check that the process instance is there.
        assertProcessInstanceExists(callbackProcessGetByIdUrl, processInstanceId);

        // prepare and send the response to the created process via kafka
        String response = objectMapper.writeValueAsString(CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType(callbackEventType)
                .withTime(OffsetDateTime.now())
                .withExtension(
                        "kogitoprocrefid", processInstanceId)
                .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("answer", answer)))
                .build());
        kafkaClient.produce(response, callbackEventTopic);

        // give some time for the event to be processed and the process to finish.
        assertProcessInstanceHasFinished(callbackProcessGetByIdUrl, processInstanceId, 1, 180);
    }

    void executeCallbackStateWithErrorPath(String callbackProcessPostUrl, String callbackProcessGetByIdUrl) throws Exception {
        // start a new process instance and collect the results.
        String processInput = buildProcessInput(GENERATE_ERROR_QUERY);
        JsonPath result = newProcessInstance(callbackProcessPostUrl, processInput);
        String processInstanceId = result.get("id");
        // ensure the process has failed as expected since GENERATE_ERROR_QUERY was used.
        String lastExecutedState = result.getString("workflowdata.lastExecutedState");
        assertThat(lastExecutedState).isEqualTo("FinalizeWithError");

        JsonPath processInstanceEventContent = waitForKogitoProcessInstanceEvent(kafkaClient, true);
        Map workflowDataMap = processInstanceEventContent.getMap("data.variables.workflowdata");
        assertThat(workflowDataMap)
                .hasSize(2)
                .containsEntry("query", GENERATE_ERROR_QUERY)
                .containsEntry("lastExecutedState", "FinalizeWithError");

        // the process instance should not be there since an end state was reached.
        assertProcessInstanceNotExists(callbackProcessGetByIdUrl, processInstanceId);
    }

    protected static String buildProcessInput(String query) {
        return "{\"workflowdata\": {\"query\": \"" + query + "\"} }";
    }
}
