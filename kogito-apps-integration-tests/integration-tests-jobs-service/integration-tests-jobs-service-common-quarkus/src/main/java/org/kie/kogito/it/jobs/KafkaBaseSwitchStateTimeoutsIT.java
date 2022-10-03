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

package org.kie.kogito.it.jobs;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.restassured.path.json.JsonPath;

import static org.kie.kogito.test.TestUtils.waitForEvent;

public class KafkaBaseSwitchStateTimeoutsIT extends BaseSwitchStateTimeoutsIT {

    private KafkaTestClient kafkaClient;
    private static final String KOGITO_OUTGOING_STREAM_TOPIC = "kogito-sw-out-events";

    @BeforeEach
    void setup() {
        String kafkaBootstrapServers = ConfigProvider.getConfig().getValue(KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY, String.class);
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
    }

    @AfterEach
    void cleanUp() {
        kafkaClient.shutdown();
    }

    @Override
    protected void verifyNoDecisionEventWasProduced(String processInstanceId) throws Exception {
        JsonPath result = waitForEvent(kafkaClient, KOGITO_OUTGOING_STREAM_TOPIC, 50);
        assertDecisionEvent(result, processInstanceId, PROCESS_RESULT_EVENT_TYPE, DECISION_NO_DECISION);
    }
}
