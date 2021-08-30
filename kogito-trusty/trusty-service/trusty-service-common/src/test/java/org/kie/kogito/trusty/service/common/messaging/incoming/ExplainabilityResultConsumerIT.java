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

import java.net.URI;
import java.util.Collections;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.cloudevents.CloudEventUtils;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;

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

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;

    @InjectMock
    TrustyService trustyService;

    KafkaTestClient kafkaClient;

    public static CloudEvent buildExplainabilityCloudEvent(BaseExplainabilityResultDto resultDto) {
        return CloudEventUtils.build(
                resultDto.getExecutionId(),
                URI.create("explainabilityResult/test"),
                resultDto,
                BaseExplainabilityResultDto.class).orElseThrow(IllegalStateException::new);
    }

    public static String buildCloudEventJsonString(BaseExplainabilityResultDto resultDto) {
        return CloudEventUtils.encode(buildExplainabilityCloudEvent(resultDto)).orElseThrow(IllegalStateException::new);
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

        kafkaClient.produce(buildCloudEventJsonString(LIMEExplainabilityResultDto.buildSucceeded(executionId, Collections.emptyMap())),
                KafkaConstants.TRUSTY_EXPLAINABILITY_RESULT_TOPIC);

        verify(trustyService, timeout(3000).times(1)).storeExplainabilityResult(any(String.class), any(BaseExplainabilityResult.class));
    }

}
