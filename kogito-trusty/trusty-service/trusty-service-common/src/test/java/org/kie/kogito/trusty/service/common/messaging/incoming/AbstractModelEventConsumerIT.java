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

package org.kie.kogito.trusty.service.common.messaging.incoming;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.TrustyServiceTestUtils;
import org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class AbstractModelEventConsumerIT {

    @Inject
    TrustyService trustyService;

    @Inject
    TrustyStorageService trustyStorageService;

    KafkaClient kafkaClient;

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @BeforeEach
    public void setup() {
        trustyStorageService.getModelStorage().clear();
    }

    @Test
    void testCorrectCloudEvent() {
        kafkaClient = new KafkaClient(kafkaBootstrapServers);

        kafkaClient.produce(TrustyServiceTestUtils.buildCloudEventJsonString(TrustyServiceTestUtils.buildCorrectModelEvent()),
                KafkaConstants.KOGITO_TRACING_MODEL_TOPIC);
        await()
                .atMost(5, SECONDS)
                .untilAsserted(() -> assertDoesNotThrow(() -> trustyService.getModelById(TrustyServiceTestUtils.getModelIdentifier())));

        DMNModelWithMetadata storedDefinition = trustyService.getModelById(TrustyServiceTestUtils.getModelIdentifier());
        assertNotNull(storedDefinition);
        assertEquals("definition", storedDefinition.getModel());
        assertEquals("name", storedDefinition.getName());
        assertEquals("namespace", storedDefinition.getNamespace());
    }
}
