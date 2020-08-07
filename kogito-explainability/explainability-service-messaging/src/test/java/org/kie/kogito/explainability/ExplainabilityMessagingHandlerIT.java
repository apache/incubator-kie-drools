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

package org.kie.kogito.explainability;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.ExplainabilityRequestDto;
import org.kie.kogito.explainability.api.ExplainabilityResultDto;
import org.kie.kogito.explainability.models.ExplainabilityRequest;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class ExplainabilityMessagingHandlerIT {

    private static final String TOPIC_REQUEST = "trusty-explainability-request-test";
    private static final String TOPIC_RESULT = "trusty-explainability-result-test";
    private static Logger LOGGER = LoggerFactory.getLogger(ExplainabilityMessagingHandlerIT.class);

    @InjectMock
    ExplanationService explanationService;

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @Inject
    private ObjectMapper objectMapper;

    @Test
    public void explainabilityRequestIsProcessedAndAResultMessageIsSent() throws Exception {
        KafkaClient kafkaClient = new KafkaClient(kafkaBootstrapServers);

        String executionId = "idException";
        ExplainabilityRequestDto request = new ExplainabilityRequestDto(executionId);
        when(explanationService.explainAsync(any(ExplainabilityRequest.class))).thenReturn(CompletableFuture.completedFuture(new ExplainabilityResultDto(executionId)));

        kafkaClient.produce(ExplainabilityCloudEventBuilder.buildCloudEventJsonString(request), TOPIC_REQUEST);

        verify(explanationService, timeout(1000).times(1)).explainAsync(any(ExplainabilityRequest.class));

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        kafkaClient.consume(TOPIC_RESULT, s -> {
            LOGGER.info("Received from kafka: {}", s);
            try {
                ExplainabilityResultDto event = objectMapper.readValue(s, ExplainabilityResultDto.class);
                assertNotNull(event);
                countDownLatch.countDown();
            } catch (JsonProcessingException e) {
                LOGGER.error("Error parsing {}", s, e);
                throw new RuntimeException(e);
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);
        assertEquals(countDownLatch.getCount(), 0);
    }
}
