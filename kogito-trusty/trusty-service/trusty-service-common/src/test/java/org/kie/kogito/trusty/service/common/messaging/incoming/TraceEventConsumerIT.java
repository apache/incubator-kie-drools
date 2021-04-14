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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.TrustyServiceTestUtils;
import org.kie.kogito.trusty.storage.api.model.Decision;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class TraceEventConsumerIT {

    @InjectMock
    TrustyService trustyService;

    KafkaClient kafkaClient;

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @Test
    public void eventLoopIsNotStoppedWithException() {
        kafkaClient = new KafkaClient(kafkaBootstrapServers);

        String executionIdException = "idException";
        String executionIdNoException = "idNoException";
        doThrow(new RuntimeException("Something really bad")).when(trustyService).processDecision(eq(executionIdException), any(Decision.class));
        doNothing().when(trustyService).processDecision(eq(executionIdNoException), any(Decision.class));

        kafkaClient.produce(TrustyServiceTestUtils.buildCloudEventJsonString(TrustyServiceTestUtils.buildCorrectTraceEvent(executionIdException)),
                KafkaConstants.KOGITO_TRACING_TOPIC);

        kafkaClient.produce(TrustyServiceTestUtils.buildCloudEventJsonString(TrustyServiceTestUtils.buildCorrectTraceEvent(executionIdNoException)),
                KafkaConstants.KOGITO_TRACING_TOPIC);

        verify(trustyService, timeout(3000).times(2)).processDecision(any(String.class), any(Decision.class));
    }
}
