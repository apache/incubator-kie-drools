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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.cloudevents.CloudEvent;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.ExplainabilityResultDto;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.tracing.decision.event.CloudEventUtils;
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.service.TrustyServiceTestUtils;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.FeatureImportance;
import org.kie.kogito.trusty.storage.api.model.Saliency;
import org.testcontainers.shaded.org.apache.commons.lang.builder.CompareToBuilder;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExplainabilityResultConsumerTest {

    private static final String TEST_EXECUTION_ID = "test";
    private static final String TEST_FEATURE_1_ID = "f1-id";
    private static final String TEST_FEATURE_1_NAME = "feature1";
    private static final String TEST_FEATURE_2_ID = "f2-id";
    private static final String TEST_FEATURE_2_NAME = "feature2";
    private static final String TEST_OUTCOME_1_ID = "o1-id";
    private static final String TEST_OUTCOME_1_NAME = "outcome1";

    private static final Decision TEST_DECISION = new Decision(
            TEST_EXECUTION_ID, null, null, true, null, null, null,
            List.of(
                    new DecisionInput(TEST_FEATURE_1_ID, TEST_FEATURE_1_NAME, null),
                    new DecisionInput(TEST_FEATURE_2_ID, TEST_FEATURE_2_NAME, null)
            ),
            List.of(
                    new DecisionOutcome(TEST_OUTCOME_1_ID, TEST_OUTCOME_1_NAME, null, null, null, null)
            )
    );

    private static final FeatureImportanceDto TEST_FEATURE_IMPORTANCE_DTO_1 = new FeatureImportanceDto(TEST_FEATURE_1_NAME, 1d);
    private static final FeatureImportanceDto TEST_FEATURE_IMPORTANCE_DTO_2 = new FeatureImportanceDto(TEST_FEATURE_2_NAME, -1d);
    private static final SaliencyDto TEST_SALIENCY_DTO = new SaliencyDto(asList(TEST_FEATURE_IMPORTANCE_DTO_1, TEST_FEATURE_IMPORTANCE_DTO_2));
    private static final ExplainabilityResultDto TEST_RESULT_DTO = ExplainabilityResultDto.buildSucceeded(TEST_EXECUTION_ID, singletonMap(TEST_OUTCOME_1_NAME, TEST_SALIENCY_DTO));

    private TrustyService trustyService;
    private ExplainabilityResultConsumer consumer;

    @BeforeEach
    void setup() {
        trustyService = mock(TrustyService.class);
        consumer = new ExplainabilityResultConsumer(trustyService, TrustyServiceTestUtils.MAPPER);
    }

    @Test
    void testCorrectCloudEvent() {
        Message<String> message = mockMessage(buildCloudEventJsonString(ExplainabilityResultDto.buildSucceeded(TEST_EXECUTION_ID, emptyMap())));
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
        Message<String> message = mockMessage(buildCloudEventJsonString(ExplainabilityResultDto.buildSucceeded(TEST_EXECUTION_ID, emptyMap())));

        doThrow(new RuntimeException("Something really bad")).when(trustyService).storeExplainabilityResult(any(String.class), any(ExplainabilityResult.class));
        Assertions.assertDoesNotThrow(() -> consumer.handleMessage(message));
    }

    private void testExplainabilityResultFromWith(Decision decision, String expectedOutcomeId) {
        ExplainabilityResult explainabilityResult = ExplainabilityResultConsumer.explainabilityResultFrom(TEST_RESULT_DTO, decision);
        assertNotNull(explainabilityResult);
        assertEquals(TEST_RESULT_DTO.getExecutionId(), explainabilityResult.getExecutionId());
        assertNotNull(TEST_RESULT_DTO.getSaliencies());
        assertEquals(TEST_RESULT_DTO.getSaliencies().size(), explainabilityResult.getSaliencies().size());

        Optional<Saliency> optSaliency = explainabilityResult.getSaliencies().stream()
                .filter(s -> s.getOutcomeName().equals(TEST_OUTCOME_1_NAME))
                .findFirst();

        assertFalse(optSaliency.isEmpty());

        Saliency saliency = optSaliency.get();
        assertEquals(expectedOutcomeId, saliency.getOutcomeId());
        assertNotNull(saliency.getFeatureImportance());
        assertEquals(TEST_SALIENCY_DTO.getFeatureImportance().size(), saliency.getFeatureImportance().size());

        List<FeatureImportance> featureImportances = saliency.getFeatureImportance().stream()
                .sorted(ExplainabilityResultConsumerTest::compareFeatureImportance)
                .collect(Collectors.toList());

        FeatureImportanceDto expected0 = TEST_SALIENCY_DTO.getFeatureImportance().get(0);
        FeatureImportance actual0 = featureImportances.get(0);
        assertEquals(expected0.getFeatureName(), actual0.getFeatureName());
        assertEquals(expected0.getScore(), actual0.getScore());

        FeatureImportanceDto expected1 = TEST_SALIENCY_DTO.getFeatureImportance().get(1);
        FeatureImportance actual1 = featureImportances.get(1);
        assertEquals(expected1.getFeatureName(), actual1.getFeatureName());
        assertEquals(expected1.getScore(), actual1.getScore());
    }

    @Test
    void testExplainabilityResultFromWithValidParams() {
        testExplainabilityResultFromWith(TEST_DECISION, TEST_OUTCOME_1_ID);
    }

    @Test
    void testExplainabilityResultFromWithNullDecision() {
        testExplainabilityResultFromWith(null, null);
    }

    @Test
    void testExplainabilityResultFromWithNullDto() {
        assertNull(ExplainabilityResultConsumer.explainabilityResultFrom(null, null));
    }

    @Test
    void testFeatureImportanceFromWithValidParams() {
        FeatureImportance featureImportance = ExplainabilityResultConsumer.featureImportanceFrom(TEST_FEATURE_IMPORTANCE_DTO_1);
        assertNotNull(featureImportance);
        assertEquals(TEST_FEATURE_IMPORTANCE_DTO_1.getFeatureName(), featureImportance.getFeatureName());
        assertEquals(TEST_FEATURE_IMPORTANCE_DTO_1.getScore(), featureImportance.getScore());
    }

    @Test
    void testFeatureImportanceFromWithNullDto() {
        assertNull(ExplainabilityResultConsumer.featureImportanceFrom(null));
    }

    @Test
    void testSaliencyFromWithValidParams() {
        Saliency saliency = ExplainabilityResultConsumer.saliencyFrom(TEST_OUTCOME_1_ID, TEST_OUTCOME_1_NAME, TEST_SALIENCY_DTO);

        assertNotNull(saliency);
        assertEquals(TEST_SALIENCY_DTO.getFeatureImportance().size(), saliency.getFeatureImportance().size());
        assertEquals(TEST_SALIENCY_DTO.getFeatureImportance().get(0).getFeatureName(),
                saliency.getFeatureImportance().get(0).getFeatureName());
        assertEquals(TEST_SALIENCY_DTO.getFeatureImportance().get(0).getScore(),
                saliency.getFeatureImportance().get(0).getScore(), 0.1);
    }

    @Test
    void testSaliencyFromWithNullDto() {
        assertNull(ExplainabilityResultConsumer.saliencyFrom(null, null, null));
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

    public static CloudEvent buildExplainabilityCloudEvent(ExplainabilityResultDto resultDto) {
        return CloudEventUtils.build(
                resultDto.getExecutionId(),
                URI.create("explainabilityResult/test"),
                resultDto,
                ExplainabilityResultDto.class
        ).get();
    }

    public static String buildCloudEventJsonString(ExplainabilityResultDto resultDto) {
        return CloudEventUtils.encode(buildExplainabilityCloudEvent(resultDto));
    }

    private static int compareFeatureImportance(FeatureImportance expected, FeatureImportance actual) {
        return new CompareToBuilder()
                .append(expected.getFeatureName(), actual.getFeatureName())
                .toComparison();
    }
}
