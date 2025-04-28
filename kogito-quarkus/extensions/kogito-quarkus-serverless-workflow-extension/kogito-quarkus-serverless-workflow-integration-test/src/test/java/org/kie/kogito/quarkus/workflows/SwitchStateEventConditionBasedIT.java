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

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.path.json.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceHasFinished;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstanceAndGetId;

@QuarkusIntegrationTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
class SwitchStateEventConditionBasedIT extends AbstractSwitchStateIT {

    private static final String SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL = "/switch_state_event_condition_timeouts_transition";
    private static final String SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL_GET_BY_ID_URL = SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL + "/{id}";

    private static final String SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL = "/switch_state_event_condition_timeouts_transition2";
    private static final String SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL_GET_BY_ID_URL = SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL + "/{id}";

    private static final String SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_END_URL = "/switch_state_event_condition_timeouts_end";
    private static final String SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_END_GET_BY_ID_URL = SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_END_URL + "/{id}";

    /**
     * Topics and event types for the switch_state_event_condition_timeouts_transition SW.
     */
    private static final String VISA_APPROVED_EVENT_TOPIC_TRANSITION = "visa_approved_topic_transition";
    private static final String VISA_DENIED_EVENT_TOPIC_TRANSITION = "visa_denied_topic_transition";
    private static final String VISA_APPROVED_EVENT_TYPE_TRANSITION = "visa_approved_in_transition";
    private static final String VISA_DENIED_EVENT_TYPE_TRANSITION = "visa_denied_in_transition";
    private static final String PROCESS_RESULT_EVENT_TYPE_TRANSITION = "process_result_event_transition";

    /**
     * Topics and event types for the switch_state_event_condition_timeouts_transition2 SW.
     */
    private static final String VISA_APPROVED_EVENT_TOPIC_TRANSITION2 = "visa_approved_topic_transition2";
    private static final String VISA_DENIED_EVENT_TOPIC_TRANSITION2 = "visa_denied_topic_transition2";
    private static final String VISA_APPROVED_EVENT_TYPE_TRANSITION2 = "visa_approved_in_transition2";
    private static final String VISA_DENIED_EVENT_TYPE_TRANSITION2 = "visa_denied_in_transition2";
    private static final String PROCESS_RESULT_EVENT_TYPE_TRANSITION2 = "process_result_event_transition2";

    /**
     * Topics and event types for the switch-state-event-condition-timeouts-end.
     */
    private static final String VISA_APPROVED_EVENT_TOPIC_CONDITION_END = "visa_approved_topic_condition_end";
    private static final String VISA_DENIED_EVENT_TOPIC_CONDITION_END = "visa_denied_topic_condition_end";
    private static final String VISA_APPROVED_EVENT_TYPE_CONDITION_END = "visa_approved_in_condition_end";
    private static final String VISA_DENIED_EVENT_TYPE_CONDITION_END = "visa_denied_in_condition_end";
    private static final String PROCESS_RESULT_EVENT_TYPE_CONDITION_END = "process_result_event_condition_end";

    private static final String EVENT_DECISION_PATH = "data.decision";
    private static final String EVENT_PROCESS_INSTANCE_ID_PATH = "kogitoprocinstanceid";
    private static final String EVENT_TYPE_PATH = "type";

    private static final String KOGITO_OUTGOING_STREAM_TOPIC = "kogito-sw-out-events";

    private static final String EMPTY_WORKFLOW_DATA = "{\"workflowdata\" : \"\"}";

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;

    ObjectMapper objectMapper;

    KafkaTestClient kafkaClient;

    @BeforeEach
    void setup() throws Exception {
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

    @Test
    void switchStateEventConditionTimeoutsTransitionApproved() throws Exception {
        switchStateEventConditionTimeoutsTransitionBasedWithEvent(SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL,
                SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL_GET_BY_ID_URL,
                VISA_APPROVED_EVENT_TYPE_TRANSITION,
                VISA_APPROVED_EVENT_TOPIC_TRANSITION,
                PROCESS_RESULT_EVENT_TYPE_TRANSITION,
                DECISION_APPROVED);
    }

    @Test
    void switchStateEventConditionTimeoutsTransitionDenied() throws Exception {
        switchStateEventConditionTimeoutsTransitionBasedWithEvent(SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL,
                SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL_GET_BY_ID_URL,
                VISA_DENIED_EVENT_TYPE_TRANSITION,
                VISA_DENIED_EVENT_TOPIC_TRANSITION,
                PROCESS_RESULT_EVENT_TYPE_TRANSITION,
                DECISION_DENIED);
    }

    @Test
    void switchStateEventConditionTimeoutsTransitionTimeoutsExceeded() throws Exception {
        switchStateEventConditionTimeoutsTransitionBasedWithTimeoutsExceeded(SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL,
                SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL_GET_BY_ID_URL,
                PROCESS_RESULT_EVENT_TYPE_TRANSITION,
                DECISION_NO_DECISION);
    }

    @Test
    void switchStateEventConditionTimeoutsTransition2Approved() throws Exception {
        switchStateEventConditionTimeoutsTransitionBasedWithEvent(SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL,
                SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL_GET_BY_ID_URL,
                VISA_APPROVED_EVENT_TYPE_TRANSITION2,
                VISA_APPROVED_EVENT_TOPIC_TRANSITION2,
                PROCESS_RESULT_EVENT_TYPE_TRANSITION2,
                DECISION_APPROVED);
    }

    @Test
    void switchStateEventConditionTimeoutsTransition2Denied() throws Exception {
        switchStateEventConditionTimeoutsTransitionBasedWithEvent(SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL,
                SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL_GET_BY_ID_URL,
                VISA_DENIED_EVENT_TYPE_TRANSITION2,
                VISA_DENIED_EVENT_TOPIC_TRANSITION2,
                PROCESS_RESULT_EVENT_TYPE_TRANSITION2,
                DECISION_DENIED);
    }

    @Test
    void switchStateEventConditionTimeoutsTransition2TimeoutsExceeded() throws Exception {
        switchStateEventConditionTimeoutsTransitionBasedWithTimeoutsExceeded(SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL,
                SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL_GET_BY_ID_URL,
                PROCESS_RESULT_EVENT_TYPE_TRANSITION2,
                DECISION_DENIED);
    }

    @Test
    void switchStateEventConditionTimeoutsEndTApproved() throws Exception {
        switchStateEventConditionTimeoutsTransitionBasedWithEvent(SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_END_URL,
                SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_END_GET_BY_ID_URL,
                VISA_APPROVED_EVENT_TYPE_CONDITION_END,
                VISA_APPROVED_EVENT_TOPIC_CONDITION_END,
                PROCESS_RESULT_EVENT_TYPE_CONDITION_END,
                DECISION_APPROVED);
    }

    @Test
    void switchStateEventConditionTimeoutsEndDenied() throws Exception {
        switchStateEventConditionTimeoutsTransitionBasedWithEvent(SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_END_URL,
                SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_END_GET_BY_ID_URL,
                VISA_DENIED_EVENT_TYPE_CONDITION_END,
                VISA_DENIED_EVENT_TOPIC_CONDITION_END,
                PROCESS_RESULT_EVENT_TYPE_CONDITION_END,
                DECISION_DENIED);
    }

    @Test
    void switchStateEventConditionTimeoutsEndTimeoutsExceeded() throws Exception {
        // Start a new process instance.
        String processInstanceId = newProcessInstanceAndGetId(SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_END_URL, EMPTY_WORKFLOW_DATA);
        // Give enough time for the timeout to exceed.
        assertProcessInstanceHasFinished(SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_END_GET_BY_ID_URL, processInstanceId, 1, 180);
        // When the process has finished the default case event must arrive.
        JsonPath result = waitForEvent(KOGITO_OUTGOING_STREAM_TOPIC, PROCESS_RESULT_EVENT_TYPE_CONDITION_END, 50);
        assertThat(result.getString("data")).isNotEmpty();
    }

    /**
     * Executes the happy path for the SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL and the
     * SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL processes.
     */
    private void switchStateEventConditionTimeoutsTransitionBasedWithEvent(String processUrl,
            String processGetByIdUrl,
            String eventTypeToSend,
            String eventTopicToSend,
            String expectedDecisionEventType,
            String expectedDecision) throws Exception {
        // Start a new process instance.
        String processInstanceId = newProcessInstanceAndGetId(processUrl, EMPTY_WORKFLOW_DATA);

        // Send the event to activate the switch state.
        String response = objectMapper.writeValueAsString(CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType(eventTypeToSend)
                .withTime(OffsetDateTime.now())
                .withExtension("kogitoprocrefid", processInstanceId)
                .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode()))
                .build());
        kafkaClient.produce(response, eventTopicToSend);
        // Give some time for the event to be processed and the process to finish.
        assertProcessInstanceHasFinished(processGetByIdUrl, processInstanceId, 1, 180);

        // Give some time to consume the event and very the expected decision was made.
        JsonPath result = waitForEvent(KOGITO_OUTGOING_STREAM_TOPIC, expectedDecisionEventType, 50);
        assertDecisionEvent(result, processInstanceId, expectedDecisionEventType, expectedDecision);
    }

    /**
     * Executes timeout exceeded path for the SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION_URL and the
     * SWITCH_STATE_EVENT_CONDITION_TIMEOUTS_TRANSITION2_URL processes.
     */
    private void switchStateEventConditionTimeoutsTransitionBasedWithTimeoutsExceeded(String processUrl,
            String processGetByIdUrl,
            String expectedDecisionEventType,
            String expectedDecision) throws Exception {
        // Start a new process instance.
        String processInstanceId = newProcessInstanceAndGetId(processUrl, EMPTY_WORKFLOW_DATA);
        // Give enough time for the timeout to exceed.
        assertProcessInstanceHasFinished(processGetByIdUrl, processInstanceId, 1, 180);
        // When the process has finished the default case event must arrive.
        JsonPath result = waitForEvent(KOGITO_OUTGOING_STREAM_TOPIC, expectedDecisionEventType, 50);
        assertDecisionEvent(result, processInstanceId, expectedDecisionEventType, expectedDecision);
    }

    protected JsonPath waitForEvent(String topic, String eventType, long seconds) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference<String> cloudEvent = new AtomicReference<>();
        kafkaClient.consume(topic, rawCloudEvent -> {
            cloudEvent.set(rawCloudEvent);
            countDownLatch.countDown();
        });
        // give some time to consume the event and verify the expected decision was made.
        assertThat(countDownLatch.await(seconds, TimeUnit.SECONDS)).isTrue();
        JsonPath jsonPath = new JsonPath(cloudEvent.get());
        assertThat(jsonPath.getString(EVENT_TYPE_PATH)).isEqualTo(eventType);
        return new JsonPath(cloudEvent.get());
    }

    protected static void assertDecisionEvent(JsonPath cloudEventJsonPath,
            String expectedProcessInstanceId,
            String expectedEventType,
            String expectedDecision) {
        assertThat(cloudEventJsonPath.getString(EVENT_PROCESS_INSTANCE_ID_PATH)).isEqualTo(expectedProcessInstanceId);
        assertThat(cloudEventJsonPath.getString(EVENT_TYPE_PATH)).isEqualTo(expectedEventType);
        assertThat(cloudEventJsonPath.getString(EVENT_DECISION_PATH)).isEqualTo(expectedDecision);
    }
}
