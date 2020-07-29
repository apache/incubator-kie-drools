/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.service.messaging;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.TrustyInfinispanServerTestResource;
import org.kie.kogito.trusty.service.TrustyKafkaTestResource;
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.storage.api.TrustyStorageService;
import org.kie.kogito.trusty.storage.api.model.Decision;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.CLOUDEVENT_WITH_ERRORS_ID;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.CORRECT_CLOUDEVENT_ID;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCloudEventJsonString;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectDecision;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectTraceEvent;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildDecisionWithErrors;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildTraceEventWithErrors;
import static org.kie.kogito.trusty.service.messaging.KafkaUtils.generateProducer;
import static org.kie.kogito.trusty.service.messaging.KafkaUtils.sendToKafkaAndWaitForCompletion;

@QuarkusTest
@QuarkusTestResource(TrustyInfinispanServerTestResource.class)
@QuarkusTestResource(TrustyKafkaTestResource.class)
class TraceEventConsumerInfinispanIT {

    @Inject
    TrustyService trustyService;

    @Inject
    TrustyStorageService trustyStorageService;

    KafkaProducer<String, String> producer;

    @BeforeEach
    public void setup() {
        trustyStorageService.getDecisionsStorage().clear();
        producer = generateProducer();
    }

    @Test
    void testCorrectCloudEvent() throws Exception {
        sendToKafkaAndWaitForCompletion(buildCloudEventJsonString(buildCorrectTraceEvent(CORRECT_CLOUDEVENT_ID)), producer);
        Decision storedDecision = trustyService.getDecisionById(CORRECT_CLOUDEVENT_ID);
        assertNotNull(storedDecision);
        TraceEventTestUtils.assertDecision(buildCorrectDecision(CORRECT_CLOUDEVENT_ID), storedDecision);
    }

    @Test
    void testCloudEventWithErrors() throws Exception {
        sendToKafkaAndWaitForCompletion(buildCloudEventJsonString(buildTraceEventWithErrors()), producer);
        Decision storedDecision = trustyService.getDecisionById(CLOUDEVENT_WITH_ERRORS_ID);
        assertNotNull(storedDecision);
        TraceEventTestUtils.assertDecision(buildDecisionWithErrors(), storedDecision);
    }
}
