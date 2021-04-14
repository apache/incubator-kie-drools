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

package org.kie.kogito.explainability.messaging;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.ExplanationService;
import org.kie.kogito.explainability.PredictionProviderFactory;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequestDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.explainability.api.ModelIdentifierDto;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.models.ExplainabilityRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.jackson.JsonFormat;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExplainabilityMessagingHandlerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule());
    private ExplanationService explanationService;
    private ExplainabilityMessagingHandler handler;

    @BeforeEach
    void setup() {
        explanationService = mock(ExplanationService.class);
        PredictionProviderFactory predictionProviderFactory = mock(PredictionProviderFactory.class);
        PredictionProvider predictionProvider = mock(PredictionProvider.class);
        when(predictionProviderFactory.createPredictionProvider(any(ExplainabilityRequest.class)))
                .thenReturn(predictionProvider);
        handler = new ExplainabilityMessagingHandler(explanationService, predictionProviderFactory);
        handler.objectMapper = MAPPER;
    }

    @Test
    void testCorrectCloudEvent() throws InterruptedException, ExecutionException, TimeoutException {
        Message<String> message = mockMessage(buildCorrectExplainabilityRequestEvent());
        when(explanationService.explainAsync(any(ExplainabilityRequest.class), any(PredictionProvider.class)))
                .thenReturn(completedFuture(mockExplainabilityResultDto()));
        testNumberOfInvocations(message, 1);
    }

    @Test
    void testMalformedCloudEvent() throws InterruptedException, ExecutionException, TimeoutException {
        Message<String> message = mockMessage("I'm a malformed cloud event");
        testNumberOfInvocations(message, 0);
    }

    @Test
    void testExceptionsAreCatched() {
        Message<String> message = mockMessage(buildCorrectExplainabilityRequestEvent());

        doThrow(new RuntimeException("Something really bad")).when(explanationService)
                .explainAsync(any(ExplainabilityRequest.class), any(PredictionProvider.class));
        Assertions.assertDoesNotThrow(() -> handler.handleMessage(message));
    }

    private Message<String> mockMessage(String payload) {
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payload);
        when(message.ack()).thenReturn(completedFuture(null));
        return message;
    }

    private BaseExplainabilityResultDto mockExplainabilityResultDto() {
        return LIMEExplainabilityResultDto.buildSucceeded(UUID.randomUUID().toString(), Collections.emptyMap());
    }

    private void testNumberOfInvocations(Message<String> message, int wantedNumberOfServiceInvocations) throws InterruptedException, ExecutionException, TimeoutException {
        handler.handleMessage(message)
                .toCompletableFuture()
                .get(1, TimeUnit.SECONDS);
        verify(explanationService, timeout(3000).times(wantedNumberOfServiceInvocations))
                .explainAsync(any(ExplainabilityRequest.class), any(PredictionProvider.class));
        verify(message, times(1)).ack();
    }

    private String buildCorrectExplainabilityRequestEvent() {
        ModelIdentifierDto modelIdentifierDto = new ModelIdentifierDto("dmn", "namespace:name");
        return ExplainabilityCloudEventBuilder
                .buildCloudEventJsonString(new LIMEExplainabilityRequestDto("test", "http://localhost:8080", modelIdentifierDto, Collections.emptyMap(), Collections.emptyMap()));
    }
}
