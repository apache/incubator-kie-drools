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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.ExplainabilityResultDto;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.explainability.TestUtils.EXECUTION_ID;
import static org.kie.kogito.explainability.TestUtils.FEATURE_IMPORTANCE_1;
import static org.kie.kogito.explainability.TestUtils.REQUEST;
import static org.kie.kogito.explainability.TestUtils.SALIENCY;
import static org.kie.kogito.explainability.TestUtils.SALIENCY_MAP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExplanationServiceImplTest {

    ExplanationServiceImpl explanationService;
    LocalExplainer<Map<String, Saliency>> localExplainerMock;
    PredictionProvider predictionProviderMock;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void init() {
        localExplainerMock = mock(LocalExplainer.class);
        predictionProviderMock = mock(PredictionProvider.class);
        explanationService = new ExplanationServiceImpl(localExplainerMock);

        when(localExplainerMock.explainAsync(any(Prediction.class), eq(predictionProviderMock))).thenReturn(CompletableFuture.completedFuture(SALIENCY_MAP));
    }

    @Test
    void explainAsync() throws InterruptedException, ExecutionException, TimeoutException {
        CompletionStage<ExplainabilityResultDto> explainAsync = explanationService.explainAsync(REQUEST, predictionProviderMock);

        ExplainabilityResultDto resultDto = explainAsync.toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());

        assertNotNull(resultDto);
        assertEquals(EXECUTION_ID, resultDto.getExecutionId());
        assertEquals(SALIENCY_MAP.size(), resultDto.getSaliencies().size());
        assertTrue(resultDto.getSaliencies().containsKey("key"));

        SaliencyDto saliencyDto = resultDto.getSaliencies().get("key");
        assertEquals(SALIENCY.getPerFeatureImportance().size(), saliencyDto.getFeatureImportance().size());

        FeatureImportanceDto featureImportanceDto1 = saliencyDto.getFeatureImportance().get(0);
        assertEquals(FEATURE_IMPORTANCE_1.getFeature().getName(), featureImportanceDto1.getFeatureId());
        assertEquals(FEATURE_IMPORTANCE_1.getScore(), featureImportanceDto1.getScore(), 0.01);
    }
}