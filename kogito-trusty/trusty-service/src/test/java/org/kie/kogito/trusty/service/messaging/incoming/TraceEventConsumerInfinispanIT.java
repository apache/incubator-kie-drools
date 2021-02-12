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
package org.kie.kogito.trusty.service.messaging.incoming;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.storage.api.TrustyStorageService;
import org.kie.kogito.trusty.storage.api.model.Decision;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.CLOUDEVENT_WITH_ERRORS_ID;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.CORRECT_CLOUDEVENT_ID;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCloudEventJsonString;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectDecision;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectTraceEvent;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildDecisionWithErrors;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildTraceEventWithErrors;

@QuarkusTest
@QuarkusTestResource(InfinispanQuarkusTestResource.class)
@QuarkusTestResource(KafkaQuarkusTestResource.class)
class TraceEventConsumerInfinispanIT {

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;

    @Inject
    TrustyService trustyService;

    @Inject
    TrustyStorageService trustyStorageService;

    KafkaClient kafkaClient;

    @BeforeEach
    public void setup() {
        trustyStorageService.getDecisionsStorage().clear();
        kafkaClient = new KafkaClient(kafkaBootstrapServers);
    }

    @Test
    void testCorrectCloudEvent() {
        kafkaClient.produce(buildCloudEventJsonString(buildCorrectTraceEvent(CORRECT_CLOUDEVENT_ID)),
                            KafkaConstants.KOGITO_TRACING_TOPIC);

        await()
                .atMost(5, SECONDS)
                .untilAsserted(() -> assertDoesNotThrow(() -> trustyService.getDecisionById(CORRECT_CLOUDEVENT_ID)));

        Decision storedDecision = trustyService.getDecisionById(CORRECT_CLOUDEVENT_ID);
        assertNotNull(storedDecision);
        TraceEventTestUtils.assertDecision(buildCorrectDecision(CORRECT_CLOUDEVENT_ID), storedDecision);
    }

    @Test
    void testCloudEventWithErrors() {
        kafkaClient.produce(buildCloudEventJsonString(buildTraceEventWithErrors()),
                            KafkaConstants.KOGITO_TRACING_TOPIC);

        await()
                .atMost(5, SECONDS)
                .untilAsserted(() -> assertDoesNotThrow(() -> trustyService.getDecisionById(CLOUDEVENT_WITH_ERRORS_ID)));

        Decision storedDecision = trustyService.getDecisionById(CLOUDEVENT_WITH_ERRORS_ID);
        assertNotNull(storedDecision);
        TraceEventTestUtils.assertDecision(buildDecisionWithErrors(), storedDecision);
    }
}
