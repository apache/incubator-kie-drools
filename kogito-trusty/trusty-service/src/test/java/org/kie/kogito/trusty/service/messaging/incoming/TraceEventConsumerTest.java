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

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.service.TrustyServiceTestUtils;
import org.kie.kogito.trusty.storage.api.model.Decision;

import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.CORRECT_CLOUDEVENT_ID;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCloudEventJsonString;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCloudEventWithoutDataJsonString;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectTraceEvent;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildTraceEventWithNullType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TraceEventConsumerTest {

    private TrustyService trustyService;
    private TraceEventConsumer consumer;

    @BeforeEach
    void setup() {
        trustyService = mock(TrustyService.class);
        consumer = new TraceEventConsumer(trustyService, TrustyServiceTestUtils.MAPPER);
    }

    @Test
    void testCorrectCloudEvent() {
        Message<String> message = mockMessage(buildCloudEventJsonString(buildCorrectTraceEvent(CORRECT_CLOUDEVENT_ID)));
        testNumberOfInvocations(message, 1);
    }

    @Test
    void testCloudEventWithoutData() {
        Message<String> message = mockMessage(buildCloudEventWithoutDataJsonString());
        testNumberOfInvocations(message, 0);
    }

    @Test
    void testGibberishPayload() {
        Message<String> message = mockMessage("DefinitelyNotASerializedCloudEvent123456");
        testNumberOfInvocations(message, 0);
    }

    @Test
    void testExceptionsAreCatched() {
        Message<String> message = mockMessage(buildCloudEventJsonString(buildCorrectTraceEvent(CORRECT_CLOUDEVENT_ID)));

        doThrow(new RuntimeException("Something really bad")).when(trustyService).storeDecision(any(String.class), any(Decision.class));
        Assertions.assertDoesNotThrow(() -> consumer.handleMessage(message));
    }

    @Test
    void testUnsupportedExecutionTypesAreNotHandled() {
        Message<String> message = mockMessage(buildCloudEventJsonString(buildTraceEventWithNullType("test")));
        testNumberOfInvocations(message, 0);
    }

    private Message<String> mockMessage(String payload) {
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payload);
        return message;
    }

    private void testNumberOfInvocations(Message<String> message, int wantedNumberOfServiceInvocations) {
        consumer.handleMessage(message);
        verify(trustyService, times(wantedNumberOfServiceInvocations)).processDecision(any(), any(), any());
        verify(message, times(1)).ack();
    }
}
