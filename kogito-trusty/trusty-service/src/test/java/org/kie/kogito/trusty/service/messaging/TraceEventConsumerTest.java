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

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.TrustyService;

import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCloudEventJsonString;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCloudEventWithoutDataJsonString;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectTraceEvent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TraceEventConsumerTest {

    @Test
    void testCorrectCloudEvent() {
        String payload = buildCloudEventJsonString(buildCorrectTraceEvent());
        doTest(payload, 1);
    }

    @Test
    void testCloudEventWithoutData() {
        String payload = buildCloudEventWithoutDataJsonString();
        doTest(payload, 0);
    }

    @Test
    void testGibberishPayload() {
        String payload = "DefinitelyNotASerializedCloudEvent123456";
        doTest(payload, 0);
    }

    private void doTest(String payload, int wantedNumberOfServiceInvocations) {
        TrustyService service = mock(TrustyService.class);

        TraceEventConsumer consumer = new TraceEventConsumer(service);

        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payload);

        consumer.handleMessage(message);

        verify(service, times(wantedNumberOfServiceInvocations)).storeDecision(any(), any());
        verify(message, times(1)).ack();
    }

}
