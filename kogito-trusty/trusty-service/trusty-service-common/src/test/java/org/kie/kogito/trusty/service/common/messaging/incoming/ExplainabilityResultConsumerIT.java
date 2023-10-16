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
package org.kie.kogito.trusty.service.common.messaging.incoming;

import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.trusty.service.common.TrustyService;

import io.cloudevents.CloudEvent;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class ExplainabilityResultConsumerIT {

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;

    @InjectMock
    TrustyService trustyService;

    KafkaTestClient kafkaClient;

    public static CloudEvent buildExplainabilityCloudEvent(BaseExplainabilityResult result) {
        return CloudEventUtils.build(
                result.getExecutionId(),
                URI.create("explainabilityResult/test"),
                result,
                BaseExplainabilityResult.class).orElseThrow(IllegalStateException::new);
    }

    public static String buildCloudEventJsonString(BaseExplainabilityResult result) {
        return CloudEventUtils.encode(buildExplainabilityCloudEvent(result)).orElseThrow(IllegalStateException::new);
    }

    @BeforeEach
    public void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
    }

    @AfterEach
    public void tearDown() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
    }

    @Test
    public void explainabilityResultIsProcessedAndStored() {
        String executionId = "executionId";

        doNothing().when(trustyService).storeExplainabilityResult(eq(executionId), any(BaseExplainabilityResult.class));

        kafkaClient.produce(buildCloudEventJsonString(LIMEExplainabilityResult.buildSucceeded(executionId, Collections.emptyList())),
                KafkaConstants.TRUSTY_EXPLAINABILITY_RESULT_TOPIC);

        verify(trustyService, timeout(3000).times(1)).storeExplainabilityResult(any(String.class), any(BaseExplainabilityResult.class));
    }
}
