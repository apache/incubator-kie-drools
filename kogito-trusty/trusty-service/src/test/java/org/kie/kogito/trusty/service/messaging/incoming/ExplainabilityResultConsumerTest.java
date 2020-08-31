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

package org.kie.kogito.trusty.service.messaging.incoming;

import java.net.URI;

import io.cloudevents.v1.CloudEventImpl;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.ExplainabilityResultDto;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.tracing.decision.event.CloudEventUtils;
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.FeatureImportance;
import org.kie.kogito.trusty.storage.api.model.Saliency;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExplainabilityResultConsumerTest {

    private TrustyService trustyService;
    private ExplainabilityResultConsumer consumer;

    private FeatureImportanceDto featureImportanceDto1 = new FeatureImportanceDto("feature1", 1d);
    private FeatureImportanceDto featureImportanceDto2 = new FeatureImportanceDto("feature2", 1d);
    private SaliencyDto saliencyDto = new SaliencyDto(asList(featureImportanceDto1, featureImportanceDto2));
    private ExplainabilityResultDto resultDto = new ExplainabilityResultDto("executionId", singletonMap("saliency", saliencyDto));

    @BeforeEach
    void setup() {
        trustyService = mock(TrustyService.class);
        consumer = new ExplainabilityResultConsumer(trustyService);
    }

    @Test
    void testCorrectCloudEvent() {
        Message<String> message = mockMessage(buildCloudEventJsonString(new ExplainabilityResultDto("test", emptyMap())));
        doNothing().when(trustyService).storeExplainabilityResult(any(String.class), any(ExplainabilityResult.class));

        testNumberOfInvocations(message, 1);
    }

    @Test
    void testInvalidPayload() {
        Message<String> message = mockMessage("Not a cloud event");
        testNumberOfInvocations(message, 0);
    }

    @Test
    void testExceptionsAreCatched() {
        Message<String> message = mockMessage(buildCloudEventJsonString(new ExplainabilityResultDto("test", emptyMap())));

        doThrow(new RuntimeException("Something really bad")).when(trustyService).storeExplainabilityResult(any(String.class), any(ExplainabilityResult.class));
        Assertions.assertDoesNotThrow(() -> consumer.handleMessage(message));
    }

    @Test
    public void explainabilityResultFrom() {
        Assertions.assertNull(ExplainabilityResultConsumer.explainabilityResultFrom(null));

        ExplainabilityResult explainabilityResult = ExplainabilityResultConsumer.explainabilityResultFrom(resultDto);

        Assertions.assertNotNull(explainabilityResult);
        Assertions.assertEquals(resultDto.getExecutionId(), explainabilityResult.getExecutionId());
        Assertions.assertEquals(resultDto.getSaliencies().size(), explainabilityResult.getSaliencies().size());
        Assertions.assertTrue(resultDto.getSaliencies().containsKey("saliency"));
        Assertions.assertEquals(resultDto.getSaliencies().get("saliency").getFeatureImportance().size(),
                explainabilityResult.getSaliencies().get("saliency").getFeatureImportance().size());
    }

    @Test
    public void featureImportanceFrom() {
        Assertions.assertNull(ExplainabilityResultConsumer.featureImportanceFrom(null));

        FeatureImportance featureImportance = ExplainabilityResultConsumer.featureImportanceFrom(featureImportanceDto1);

        Assertions.assertNotNull(featureImportance);
        Assertions.assertEquals(featureImportanceDto1.getFeatureId(), featureImportance.getFeatureId());
        Assertions.assertEquals(featureImportanceDto1.getScore(), featureImportance.getScore());
    }

    @Test
    public void saliencyFrom() {
        Assertions.assertNull(ExplainabilityResultConsumer.saliencyFrom(null));

        Saliency saliency = ExplainabilityResultConsumer.saliencyFrom(saliencyDto);

        Assertions.assertNotNull(saliency);
        Assertions.assertEquals(saliencyDto.getFeatureImportance().size(), saliency.getFeatureImportance().size());
        Assertions.assertEquals(saliencyDto.getFeatureImportance().get(0).getFeatureId(),
                saliency.getFeatureImportance().get(0).getFeatureId());
        Assertions.assertEquals(saliencyDto.getFeatureImportance().get(0).getScore(),
                saliency.getFeatureImportance().get(0).getScore(), 0.1);
    }

    private Message<String> mockMessage(String payload) {
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payload);
        return message;
    }

    private void testNumberOfInvocations(Message<String> message, int wantedNumberOfServiceInvocations) {
        consumer.handleMessage(message);
        verify(trustyService, times(wantedNumberOfServiceInvocations)).storeExplainabilityResult(any(), any());
        verify(message, times(1)).ack();
    }

    public static CloudEventImpl<ExplainabilityResultDto> buildExplainabilityCloudEvent(ExplainabilityResultDto resultDto) {
        return CloudEventUtils.build(
                resultDto.getExecutionId(),
                URI.create("explainabilityResult/test"),
                resultDto,
                ExplainabilityResultDto.class
        );
    }

    public static String buildCloudEventJsonString(ExplainabilityResultDto resultDto) {
        return CloudEventUtils.encode(buildExplainabilityCloudEvent(resultDto));
    }
}
