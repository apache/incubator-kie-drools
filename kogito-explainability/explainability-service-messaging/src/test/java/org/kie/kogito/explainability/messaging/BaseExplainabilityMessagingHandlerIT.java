/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability.messaging;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.kie.kogito.cloudevents.CloudEventUtils;
import org.kie.kogito.explainability.ExplanationService;
import org.kie.kogito.explainability.api.BaseExplainabilityRequestDto;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.ModelIdentifierDto;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.models.BaseExplainabilityRequest;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.quarkus.test.junit.mockito.InjectMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

abstract class BaseExplainabilityMessagingHandlerIT {

    private static final String TOPIC_REQUEST = "trusty-explainability-request-test";
    private static final String TOPIC_RESULT = "trusty-explainability-result-test";

    protected static final String EXECUTION_ID = "idException";
    protected static final String COUNTERFACTUAL_ID = "idCounterfactual";
    protected static final String SERVICE_URL = "http://localhost:8080";
    protected static final ModelIdentifierDto MODEL_IDENTIFIER_DTO = new ModelIdentifierDto("dmn", "namespace:name");

    private static Logger LOGGER = LoggerFactory.getLogger(BaseExplainabilityMessagingHandlerIT.class);

    @InjectMock
    ExplanationService explanationService;

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @Inject
    private ObjectMapper objectMapper;

    @Test
    void explainabilityRequestIsProcessedAndAResultMessageIsSent() throws Exception {
        KafkaClient kafkaClient = new KafkaClient(kafkaBootstrapServers);

        BaseExplainabilityRequestDto request = buildRequest();
        BaseExplainabilityResultDto result = buildResult();

        when(explanationService.explainAsync(any(BaseExplainabilityRequest.class), any(PredictionProvider.class)))
                .thenReturn(CompletableFuture.completedFuture(result));

        kafkaClient.produce(ExplainabilityCloudEventBuilder.buildCloudEventJsonString(request), TOPIC_REQUEST);

        verify(explanationService, timeout(1000).times(1)).explainAsync(any(BaseExplainabilityRequest.class), any(PredictionProvider.class));

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        kafkaClient.consume(TOPIC_RESULT, s -> {
            LOGGER.info("Received from kafka: {}", s);
            CloudEventUtils.decode(s).ifPresent((CloudEvent cloudEvent) -> {
                try {
                    BaseExplainabilityResultDto event = objectMapper.readValue(cloudEvent.getData(), BaseExplainabilityResultDto.class);
                    assertNotNull(event);
                    assertResult(event);
                    countDownLatch.countDown();
                } catch (IOException e) {
                    LOGGER.error("Error parsing {}", s, e);
                    throw new RuntimeException(e);
                }
            });
        });

        assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));

        kafkaClient.shutdown();
    }

    protected abstract BaseExplainabilityRequestDto buildRequest();

    protected abstract BaseExplainabilityResultDto buildResult();

    protected abstract void assertResult(BaseExplainabilityResultDto result);
}
