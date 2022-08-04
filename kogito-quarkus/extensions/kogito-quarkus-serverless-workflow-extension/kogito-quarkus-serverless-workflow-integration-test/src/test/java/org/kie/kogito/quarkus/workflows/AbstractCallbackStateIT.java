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

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.restassured.path.json.JsonPath;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.quarkus.workflows.ExternalServiceMock.GENERATE_ERROR_QUERY;
import static org.kie.kogito.quarkus.workflows.ExternalServiceMock.SUCCESSFUL_QUERY;
import static org.kie.kogito.quarkus.workflows.WorkflowTestUtils.assertProcessInstanceExists;
import static org.kie.kogito.quarkus.workflows.WorkflowTestUtils.assertProcessInstanceHasFinished;
import static org.kie.kogito.quarkus.workflows.WorkflowTestUtils.assertProcessInstanceNotExists;
import static org.kie.kogito.quarkus.workflows.WorkflowTestUtils.newProcessInstance;
import static org.kie.kogito.quarkus.workflows.WorkflowTestUtils.newProcessInstanceAndGetId;

@QuarkusTestResource(KafkaCompanionResource.class)
abstract class AbstractCallbackStateIT {

    static final String ANSWER = "ANSWER";

    ObjectMapper objectMapper;

    @InjectKafkaCompanion
    KafkaCompanion kafkaCompanion;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(JsonFormat.getCloudEventJacksonModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    void executeCallbackStateSuccessfulPath(String callbackProcessPostUrl,
            String callbackProcessGetByIdUrl,
            String answer,
            String callbackEventType,
            String callbackEventTopic) throws Exception {
        // start a new process instance by sending the post query and collect the process instance id.
        String processInput = buildProcessInput(SUCCESSFUL_QUERY);
        String processInstanceId = newProcessInstanceAndGetId(callbackProcessPostUrl, processInput);

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
        kafkaCompanion.produceStrings().usingGenerator(i -> new ProducerRecord<>(callbackEventTopic, response));

        // give some time for the event to be processed and the process to finish.
        assertProcessInstanceHasFinished(callbackProcessGetByIdUrl, processInstanceId, 1, 180);
    }

    void executeCallbackStateWithErrorPath(String callbackProcessPostUrl, String callbackProcessGetByIdUrl) {
        // start a new process instance and collect the results.
        String processInput = buildProcessInput(GENERATE_ERROR_QUERY);
        JsonPath result = newProcessInstance(callbackProcessPostUrl, processInput);
        String processInstanceId = result.get("id");
        // ensure the process has failed as expected since GENERATE_ERROR_QUERY was used.
        String lastExecutedState = result.getString("workflowdata.lastExecutedState");
        assertThat(lastExecutedState).isEqualTo("FinalizeWithError");
        // the process instance should not be there since an end state was reached.
        assertProcessInstanceNotExists(callbackProcessGetByIdUrl, processInstanceId);
    }

    @AfterEach
    void cleanUp() {
        kafkaCompanion.close();
    }

    protected static String buildProcessInput(String query) {
        return "{\"workflowdata\": {\"query\": \"" + query + "\"} }";
    }
}
