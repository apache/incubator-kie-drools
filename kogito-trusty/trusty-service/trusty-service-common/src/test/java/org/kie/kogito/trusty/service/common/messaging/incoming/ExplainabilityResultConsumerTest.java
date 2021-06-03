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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.cloudevents.CloudEventUtils;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResultDto;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.TrustyServiceTestUtils;
import org.kie.kogito.trusty.service.common.handlers.CounterfactualExplainerServiceHandler;
import org.kie.kogito.trusty.service.common.handlers.ExplainerServiceHandler;
import org.kie.kogito.trusty.service.common.handlers.ExplainerServiceHandlerRegistry;
import org.kie.kogito.trusty.service.common.handlers.LIMEExplainerServiceHandler;
import org.kie.kogito.trusty.storage.api.StorageExceptionsProvider;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel;
import org.kie.kogito.trusty.storage.api.model.LIMEExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;
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

    private static final String TEST_COUNTERFACTUAL_ID = "counterfactualId";
    private static final String TEST_SOLUTION_ID = "solutionId";

    private static final Decision TEST_DECISION = new Decision(
            TEST_EXECUTION_ID, null, null, null, true, null, null, null,
            List.of(
                    new DecisionInput(TEST_FEATURE_1_ID, TEST_FEATURE_1_NAME, null),
                    new DecisionInput(TEST_FEATURE_2_ID, TEST_FEATURE_2_NAME, null)),
            List.of(
                    new DecisionOutcome(TEST_OUTCOME_1_ID, TEST_OUTCOME_1_NAME, null, null, null, null)));

    private static final FeatureImportanceDto TEST_FEATURE_IMPORTANCE_DTO_1 = new FeatureImportanceDto(TEST_FEATURE_1_NAME, 1d);
    private static final FeatureImportanceDto TEST_FEATURE_IMPORTANCE_DTO_2 = new FeatureImportanceDto(TEST_FEATURE_2_NAME, -1d);
    private static final SaliencyDto TEST_SALIENCY_DTO = new SaliencyDto(asList(TEST_FEATURE_IMPORTANCE_DTO_1, TEST_FEATURE_IMPORTANCE_DTO_2));
    private static final LIMEExplainabilityResultDto TEST_RESULT_DTO = LIMEExplainabilityResultDto.buildSucceeded(TEST_EXECUTION_ID, singletonMap(TEST_OUTCOME_1_NAME, TEST_SALIENCY_DTO));

    private TrustyService trustyService;
    private StorageExceptionsProvider storageExceptionsProvider;
    private ExplainabilityResultConsumer consumer;
    private LIMEExplainerServiceHandler limeExplainerServiceHandler;
    private CounterfactualExplainerServiceHandler counterfactualExplainerServiceHandler;
    private Instance<ExplainerServiceHandler<?, ?>> explanationHandlers;
    private ExplainerServiceHandlerRegistry explainerServiceHandlerRegistry;
    private TrustyStorageService trustyStorage;

    private static CloudEvent buildLIMEExplainabilityCloudEvent(LIMEExplainabilityResultDto resultDto) {
        return CloudEventUtils.build(
                resultDto.getExecutionId(),
                URI.create("explainabilityResult/test"),
                resultDto,
                LIMEExplainabilityResultDto.class).get();
    }

    private static String buildLIMECloudEventJsonString(LIMEExplainabilityResultDto resultDto) {
        return CloudEventUtils.encode(buildLIMEExplainabilityCloudEvent(resultDto)).orElseThrow(IllegalStateException::new);
    }

    private static CloudEvent buildCounterfactualExplainabilityCloudEvent(CounterfactualExplainabilityResultDto resultDto) {
        return CloudEventUtils.build(
                resultDto.getExecutionId(),
                URI.create("explainabilityResult/test"),
                resultDto,
                CounterfactualExplainabilityResultDto.class).get();
    }

    private static String buildCounterfactualCloudEventJsonString(CounterfactualExplainabilityResultDto resultDto) {
        return CloudEventUtils.encode(buildCounterfactualExplainabilityCloudEvent(resultDto)).orElseThrow(IllegalStateException::new);
    }

    private static CloudEvent buildUnknownExplainabilityCloudEvent(BaseExplainabilityResultDto resultDto) {
        return CloudEventUtils.build(
                resultDto.getExecutionId(),
                URI.create("explainabilityResult/test"),
                resultDto,
                BaseExplainabilityResultDto.class).get();
    }

    private static String buildUnknownExplainabilityCloudEventJsonString(BaseExplainabilityResultDto resultDto) {
        return CloudEventUtils.encode(buildUnknownExplainabilityCloudEvent(resultDto)).orElseThrow(IllegalStateException::new);
    }

    private static int compareFeatureImportance(FeatureImportanceModel expected, FeatureImportanceModel actual) {
        return new CompareToBuilder()
                .append(expected.getFeatureName(), actual.getFeatureName())
                .toComparison();
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        trustyService = mock(TrustyService.class);
        storageExceptionsProvider = mock(StorageExceptionsProvider.class);
        trustyStorage = mock(TrustyStorageService.class);
        limeExplainerServiceHandler = new LIMEExplainerServiceHandler(trustyStorage);
        counterfactualExplainerServiceHandler = new CounterfactualExplainerServiceHandler(trustyStorage);
        explanationHandlers = mock(Instance.class);
        when(explanationHandlers.stream()).thenReturn(Stream.of(limeExplainerServiceHandler, counterfactualExplainerServiceHandler));
        explainerServiceHandlerRegistry = new ExplainerServiceHandlerRegistry(explanationHandlers);
        consumer = new ExplainabilityResultConsumer(trustyService, explainerServiceHandlerRegistry, TrustyServiceTestUtils.MAPPER, storageExceptionsProvider);
    }

    @Test
    void testCorrectLIMECloudEvent() {
        Message<String> message = mockMessage(buildLIMECloudEventJsonString(LIMEExplainabilityResultDto.buildSucceeded(TEST_EXECUTION_ID, emptyMap())));
        doNothing().when(trustyService).storeExplainabilityResult(any(String.class), any(BaseExplainabilityResult.class));

        testNumberOfInvocations(message, 1);
    }

    @Test
    void testCorrectCounterfactualCloudEvent() {
        Message<String> message = mockMessage(buildCounterfactualCloudEventJsonString(CounterfactualExplainabilityResultDto.buildSucceeded(TEST_EXECUTION_ID,
                TEST_COUNTERFACTUAL_ID,
                TEST_SOLUTION_ID,
                Boolean.TRUE,
                CounterfactualExplainabilityResultDto.Stage.FINAL,
                Collections.emptyMap(),
                Collections.emptyMap())));
        doNothing().when(trustyService).storeExplainabilityResult(any(String.class), any(BaseExplainabilityResult.class));

        testNumberOfInvocations(message, 1);
    }

    @Test
    void testInvalidPayload() {
        Message<String> message = mockMessage("Not a cloud event");
        testNumberOfInvocations(message, 0);
    }

    @Test
    void testInvalidPayloadUnknownExplanationType() {
        BaseExplainabilityResultDto result = new BaseExplainabilityResultDto() {
            @Override
            public String getExecutionId() {
                return TEST_EXECUTION_ID;
            }
        };
        Message<String> message = mockMessage(buildUnknownExplainabilityCloudEventJsonString(result));
        testNumberOfInvocations(message, 0);
    }

    @Test
    void testExceptionsAreCaught() {
        Message<String> message = mockMessage(buildLIMECloudEventJsonString(LIMEExplainabilityResultDto.buildSucceeded(TEST_EXECUTION_ID, emptyMap())));

        doThrow(new RuntimeException("Something really bad")).when(trustyService).storeExplainabilityResult(any(String.class), any(BaseExplainabilityResult.class));
        Assertions.assertDoesNotThrow(() -> consumer.handleMessage(message));
    }

    private void testExplainabilityResultFromWith(Decision decision, String expectedOutcomeId) {
        LIMEExplainabilityResult explainabilityResult = (LIMEExplainabilityResult) consumer.explainabilityResultFrom(TEST_RESULT_DTO, decision);
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
        assertEquals(expected0.getScore(), actual0.getFeatureScore());

        FeatureImportanceDto expected1 = TEST_SALIENCY_DTO.getFeatureImportance().get(1);
        FeatureImportanceModel actual1 = featureImportanceModels.get(1);
        assertEquals(expected1.getFeatureName(), actual1.getFeatureName());
        assertEquals(expected1.getScore(), actual1.getFeatureScore());
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
        assertNull(consumer.explainabilityResultFrom(null, null));
    }

    private Message<String> mockMessage(String payload) {
        @SuppressWarnings("unchecked")
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
