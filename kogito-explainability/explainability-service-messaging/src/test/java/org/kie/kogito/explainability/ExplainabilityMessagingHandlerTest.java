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
import java.util.concurrent.Executors;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.ExplainabilityRequestDto;
import org.kie.kogito.explainability.messaging.ExplainabilityMessagingHandler;
import org.kie.kogito.explainability.models.ExplainabilityRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExplainabilityMessagingHandlerTest {

    private ExplanationService explanationService;
    private ExplainabilityMessagingHandler handler;

    @BeforeEach
    void setup() {
        explanationService = mock(ExplanationService.class);
        handler = new ExplainabilityMessagingHandler(explanationService, Executors.newFixedThreadPool(2));
    }

    @Test
    void testCorrectCloudEvent() {
        Message<String> message = mockMessage(buildCorrectExplainabilityRequestEvent());
        when(explanationService.explainAsync(any(ExplainabilityRequest.class))).thenReturn(CompletableFuture.completedFuture(null));
        testNumberOfInvocations(message, 1);
    }

    @Test
    void testMalformedCloudEvent() {
        Message<String> message = mockMessage("I'm a malformed cloud event");
        testNumberOfInvocations(message, 0);
    }

    @Test
    void testExceptionsAreCatched() {
        Message<String> message = mockMessage(buildCorrectExplainabilityRequestEvent());

        doThrow(new RuntimeException("Something really bad")).when(explanationService).explainAsync(any(ExplainabilityRequest.class));
        Assertions.assertDoesNotThrow(() -> handler.handleMessage(message));
    }

    private Message<String> mockMessage(String payload) {
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payload);
        return message;
    }

    private void testNumberOfInvocations(Message<String> message, int wantedNumberOfServiceInvocations) {
        handler.handleMessage(message);
        verify(explanationService, timeout(3000).times(wantedNumberOfServiceInvocations)).explainAsync(any(ExplainabilityRequest.class));
        verify(message, times(1)).ack();
    }

    private String buildCorrectExplainabilityRequestEvent() {
        return ExplainabilityCloudEventBuilder.buildCloudEventJsonString(new ExplainabilityRequestDto("test"));
    }
}
