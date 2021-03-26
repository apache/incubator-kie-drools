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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.cloudevents.CloudEventUtils;
import org.kie.kogito.explainability.api.ExplainabilityResultDto;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.TrustyServiceTestUtils;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;
import org.testcontainers.shaded.org.apache.commons.lang.builder.CompareToBuilder;

import io.cloudevents.CloudEvent;

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
                    new DecisionInput(TEST_FEATURE_2_ID, TEST_FEATURE_2_NAME, null)),
            List.of(
                    new DecisionOutcome(TEST_OUTCOME_1_ID, TEST_OUTCOME_1_NAME, null, null, null, null)));

    private static final FeatureImportanceDto TEST_FEATURE_IMPORTANCE_DTO_1 = new FeatureImportanceDto(TEST_FEATURE_1_NAME, 1d);
    private static final FeatureImportanceDto TEST_FEATURE_IMPORTANCE_DTO_2 = new FeatureImportanceDto(TEST_FEATURE_2_NAME, -1d);
    private static final SaliencyDto TEST_SALIENCY_DTO = new SaliencyDto(asList(TEST_FEATURE_IMPORTANCE_DTO_1, TEST_FEATURE_IMPORTANCE_DTO_2));
    private static final ExplainabilityResultDto TEST_RESULT_DTO = ExplainabilityResultDto.buildSucceeded(TEST_EXECUTION_ID, singletonMap(TEST_OUTCOME_1_NAME, TEST_SALIENCY_DTO));

    private TrustyService trustyService;
    private ExplainabilityResultConsumer consumer;

    public static CloudEvent buildExplainabilityCloudEvent(ExplainabilityResultDto resultDto) {
        return CloudEventUtils.build(
                resultDto.getExecutionId(),
                URI.create("explainabilityResult/test"),
                resultDto,
                ExplainabilityResultDto.class).get();
    }

    public static String buildCloudEventJsonString(ExplainabilityResultDto resultDto) {
        return CloudEventUtils.encode(buildExplainabilityCloudEvent(resultDto)).orElseThrow(IllegalStateException::new);
    }

    private static int compareFeatureImportance(FeatureImportanceModel expected, FeatureImportanceModel actual) {
        return new CompareToBuilder()
                .append(expected.getFeatureName(), actual.getFeatureName())
                .toComparison();
    }

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

        Optional<SaliencyModel> optSaliency = explainabilityResult.getSaliencies().stream()
                .filter(s -> s.getOutcomeName().equals(TEST_OUTCOME_1_NAME))
                .findFirst();

        assertFalse(optSaliency.isEmpty());

        SaliencyModel saliencyModel = optSaliency.get();
        assertEquals(expectedOutcomeId, saliencyModel.getOutcomeId());
        assertNotNull(saliencyModel.getFeatureImportance());
        assertEquals(TEST_SALIENCY_DTO.getFeatureImportance().size(), saliencyModel.getFeatureImportance().size());

        List<FeatureImportanceModel> featureImportanceModels = saliencyModel.getFeatureImportance().stream()
                .sorted(ExplainabilityResultConsumerTest::compareFeatureImportance)
                .collect(Collectors.toList());

        FeatureImportanceDto expected0 = TEST_SALIENCY_DTO.getFeatureImportance().get(0);
        FeatureImportanceModel actual0 = featureImportanceModels.get(0);
        assertEquals(expected0.getFeatureName(), actual0.getFeatureName());
        assertEquals(expected0.getScore(), actual0.getScore());

        FeatureImportanceDto expected1 = TEST_SALIENCY_DTO.getFeatureImportance().get(1);
        FeatureImportanceModel actual1 = featureImportanceModels.get(1);
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
        FeatureImportanceModel featureImportanceModel = ExplainabilityResultConsumer.featureImportanceFrom(TEST_FEATURE_IMPORTANCE_DTO_1);
        assertNotNull(featureImportanceModel);
        assertEquals(TEST_FEATURE_IMPORTANCE_DTO_1.getFeatureName(), featureImportanceModel.getFeatureName());
        assertEquals(TEST_FEATURE_IMPORTANCE_DTO_1.getScore(), featureImportanceModel.getScore());
    }

    @Test
    void testFeatureImportanceFromWithNullDto() {
        assertNull(ExplainabilityResultConsumer.featureImportanceFrom(null));
    }

    @Test
    void testSaliencyFromWithValidParams() {
        SaliencyModel saliencyModel = ExplainabilityResultConsumer.saliencyFrom(TEST_OUTCOME_1_ID, TEST_OUTCOME_1_NAME, TEST_SALIENCY_DTO);

        assertNotNull(saliencyModel);
        assertEquals(TEST_SALIENCY_DTO.getFeatureImportance().size(), saliencyModel.getFeatureImportance().size());
        assertEquals(TEST_SALIENCY_DTO.getFeatureImportance().get(0).getFeatureName(),
                saliencyModel.getFeatureImportance().get(0).getFeatureName());
        assertEquals(TEST_SALIENCY_DTO.getFeatureImportance().get(0).getScore(),
                saliencyModel.getFeatureImportance().get(0).getScore(), 0.1);
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
}
