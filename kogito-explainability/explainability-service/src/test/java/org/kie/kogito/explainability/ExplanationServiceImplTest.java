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
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResultDto;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.explainability.handlers.CounterfactualExplainerServiceHandler;
import org.kie.kogito.explainability.handlers.LimeExplainerServiceHandler;
import org.kie.kogito.explainability.handlers.LocalExplainerServiceHandlerRegistry;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.tracing.typedvalue.TypedValue;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.explainability.TestUtils.COUNTERFACTUAL_ID;
import static org.kie.kogito.explainability.TestUtils.COUNTERFACTUAL_REQUEST;
import static org.kie.kogito.explainability.TestUtils.COUNTERFACTUAL_RESULT;
import static org.kie.kogito.explainability.TestUtils.EXECUTION_ID;
import static org.kie.kogito.explainability.TestUtils.FEATURE_IMPORTANCE_1;
import static org.kie.kogito.explainability.TestUtils.LIME_REQUEST;
import static org.kie.kogito.explainability.TestUtils.SALIENCY;
import static org.kie.kogito.explainability.TestUtils.SALIENCY_MAP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ExplanationServiceImplTest {

    private static final Long MAX_RUNNING_TIME_SECONDS = 60L;

    @SuppressWarnings("rawtype")
    Instance instance;
    ExplanationServiceImpl explanationService;
    LimeExplainer limeExplainerMock;
    LimeExplainerServiceHandler limeExplainerServiceHandlerMock;
    CounterfactualExplainer cfExplainerMock;
    CounterfactualExplainerServiceHandler cfExplainerServiceHandlerMock;
    LocalExplainerServiceHandlerRegistry explainerServiceHandlerRegistryMock;
    PredictionProvider predictionProviderMock;
    Consumer<BaseExplainabilityResultDto> callbackMock;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void init() {
        instance = mock(Instance.class);
        limeExplainerMock = mock(LimeExplainer.class);
        cfExplainerMock = mock(CounterfactualExplainer.class);
        PredictionProviderFactory predictionProviderFactory = mock(PredictionProviderFactory.class);
        explainerServiceHandlerRegistryMock = new LocalExplainerServiceHandlerRegistry(instance);
        limeExplainerServiceHandlerMock = spy(new LimeExplainerServiceHandler(limeExplainerMock,
                predictionProviderFactory));
        cfExplainerServiceHandlerMock = spy(new CounterfactualExplainerServiceHandler(cfExplainerMock,
                predictionProviderFactory,
                MAX_RUNNING_TIME_SECONDS));

        predictionProviderMock = mock(PredictionProvider.class);
        callbackMock = mock(Consumer.class);
        explanationService = new ExplanationServiceImpl(explainerServiceHandlerRegistryMock);
        when(predictionProviderFactory.createPredictionProvider(any(), any(), any())).thenReturn(predictionProviderMock);
    }

    @Test
    void testLIMEExplainAsyncSucceeded() {
        testLIMEExplainAsyncSuccess(() -> explanationService.explainAsync(LIME_REQUEST, callbackMock)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));
    }

    @Test
    void testLIMEExplainAsyncSucceededWithoutCallback() {
        testLIMEExplainAsyncSuccess(() -> explanationService.explainAsync(LIME_REQUEST)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));
    }

    @SuppressWarnings("unchecked")
    void testLIMEExplainAsyncSuccess(ThrowingSupplier<BaseExplainabilityResultDto> invocation) {
        when(instance.stream()).thenReturn(Stream.of(limeExplainerServiceHandlerMock));
        when(limeExplainerMock.explainAsync(any(Prediction.class),
                eq(predictionProviderMock),
                any(Consumer.class)))
                        .thenReturn(CompletableFuture.completedFuture(SALIENCY_MAP));

        BaseExplainabilityResultDto resultDto = assertDoesNotThrow(invocation);

        assertNotNull(resultDto);
        assertTrue(resultDto instanceof LIMEExplainabilityResultDto);
        LIMEExplainabilityResultDto limeResultDto = (LIMEExplainabilityResultDto) resultDto;

        assertEquals(EXECUTION_ID, limeResultDto.getExecutionId());
        assertSame(ExplainabilityStatus.SUCCEEDED, limeResultDto.getStatus());
        assertNull(limeResultDto.getStatusDetails());
        assertEquals(SALIENCY_MAP.size(), limeResultDto.getSaliencies().size());
        assertTrue(limeResultDto.getSaliencies().containsKey("key"));

        SaliencyDto saliencyDto = limeResultDto.getSaliencies().get("key");
        assertEquals(SALIENCY.getPerFeatureImportance().size(), saliencyDto.getFeatureImportance().size());

        FeatureImportanceDto featureImportanceDto1 = saliencyDto.getFeatureImportance().get(0);
        assertEquals(FEATURE_IMPORTANCE_1.getFeature().getName(), featureImportanceDto1.getFeatureName());
        assertEquals(FEATURE_IMPORTANCE_1.getScore(), featureImportanceDto1.getScore(), 0.01);
    }

    @Test
    void testCounterfactualsExplainAsyncSucceeded() {
        testCounterfactualsExplainAsyncSuccess(() -> explanationService.explainAsync(COUNTERFACTUAL_REQUEST, callbackMock)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));
    }

    @Test
    void testCounterfactualsExplainAsyncSucceededWithoutCallback() {
        testCounterfactualsExplainAsyncSuccess(() -> explanationService.explainAsync(COUNTERFACTUAL_REQUEST)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));
    }

    @SuppressWarnings("unchecked")
    void testCounterfactualsExplainAsyncSuccess(ThrowingSupplier<BaseExplainabilityResultDto> invocation) {
        when(instance.stream()).thenReturn(Stream.of(cfExplainerServiceHandlerMock));

        when(cfExplainerMock.explainAsync(any(Prediction.class),
                eq(predictionProviderMock),
                any(Consumer.class))).thenReturn(CompletableFuture.completedFuture(COUNTERFACTUAL_RESULT));

        BaseExplainabilityResultDto resultDto = assertDoesNotThrow(invocation);

        assertNotNull(resultDto);
        assertTrue(resultDto instanceof CounterfactualExplainabilityResultDto);
        CounterfactualExplainabilityResultDto counterfactualResultDto = (CounterfactualExplainabilityResultDto) resultDto;

        assertEquals(EXECUTION_ID, counterfactualResultDto.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, counterfactualResultDto.getCounterfactualId());
        assertSame(ExplainabilityStatus.SUCCEEDED, counterfactualResultDto.getStatus());
        assertNull(counterfactualResultDto.getStatusDetails());
        assertEquals(COUNTERFACTUAL_RESULT.getEntities().size(), counterfactualResultDto.getInputs().size());
        assertEquals(COUNTERFACTUAL_RESULT.getOutput().size(), counterfactualResultDto.getOutputs().size());
        assertTrue(counterfactualResultDto.getOutputs().containsKey("output1"));

        TypedValue value = counterfactualResultDto.getOutputs().get("output1");
        assertTrue(value.isUnit());
        assertEquals(Double.class.getSimpleName(), value.toUnit().getType());
        assertEquals(555.0, value.toUnit().getValue().asDouble());
    }

    @Test
    void testServiceCallFailed() {
        String errorMessage = "Something bad happened";
        RuntimeException exception = new RuntimeException(errorMessage);

        when(instance.stream()).thenReturn(Stream.of(limeExplainerServiceHandlerMock));
        doThrow(exception).when(limeExplainerServiceHandlerMock).supports(any());

        assertThrows(RuntimeException.class,
                () -> explanationService.explainAsync(LIME_REQUEST, callbackMock)
                        .toCompletableFuture()
                        .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));
    }

    @Test
    void testServiceCallFailedNoMatchingServiceHandlers() {
        when(instance.stream()).thenReturn(Stream.of());

        assertThrows(IllegalArgumentException.class,
                () -> explanationService.explainAsync(LIME_REQUEST, callbackMock)
                        .toCompletableFuture()
                        .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testLIMEExplainAsyncFailed() {
        String errorMessage = "Something bad happened";
        RuntimeException exception = new RuntimeException(errorMessage);

        when(instance.stream()).thenReturn(Stream.of(limeExplainerServiceHandlerMock));
        when(limeExplainerMock.explainAsync(any(Prediction.class),
                eq(predictionProviderMock),
                any(Consumer.class)))
                        .thenThrow(exception);

        BaseExplainabilityResultDto resultDto = assertDoesNotThrow(() -> explanationService.explainAsync(LIME_REQUEST, callbackMock)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));

        assertNotNull(resultDto);
        assertTrue(resultDto instanceof LIMEExplainabilityResultDto);
        LIMEExplainabilityResultDto exceptionResultDto = (LIMEExplainabilityResultDto) resultDto;

        assertEquals(EXECUTION_ID, exceptionResultDto.getExecutionId());
        assertSame(ExplainabilityStatus.FAILED, exceptionResultDto.getStatus());
        assertEquals(errorMessage, exceptionResultDto.getStatusDetails());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCounterfactualsxplainAsyncFailed() {
        String errorMessage = "Something bad happened";
        RuntimeException exception = new RuntimeException(errorMessage);

        when(instance.stream()).thenReturn(Stream.of(cfExplainerServiceHandlerMock));
        when(cfExplainerMock.explainAsync(any(Prediction.class),
                eq(predictionProviderMock),
                any(Consumer.class)))
                        .thenThrow(exception);

        BaseExplainabilityResultDto resultDto = assertDoesNotThrow(() -> explanationService.explainAsync(COUNTERFACTUAL_REQUEST, callbackMock)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));

        assertNotNull(resultDto);
        assertTrue(resultDto instanceof CounterfactualExplainabilityResultDto);
        CounterfactualExplainabilityResultDto exceptionResultDto = (CounterfactualExplainabilityResultDto) resultDto;

        assertEquals(EXECUTION_ID, exceptionResultDto.getExecutionId());
        assertSame(ExplainabilityStatus.FAILED, exceptionResultDto.getStatus());
        assertEquals(errorMessage, exceptionResultDto.getStatusDetails());
    }

    @Test
    void testServiceHandlerLookupLIME() {
        when(instance.stream()).thenReturn(Stream.of(limeExplainerServiceHandlerMock, cfExplainerServiceHandlerMock));

        when(limeExplainerMock.explainAsync(any(), any(), any())).thenReturn(CompletableFuture.completedFuture(SALIENCY_MAP));

        BaseExplainabilityResultDto resultDto = assertDoesNotThrow(() -> explanationService.explainAsync(LIME_REQUEST, callbackMock)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));

        assertNotNull(resultDto);
        assertTrue(resultDto instanceof LIMEExplainabilityResultDto);
    }

    @Test
    void testServiceHandlerLookupCounterfactuals() {
        when(instance.stream()).thenReturn(Stream.of(limeExplainerServiceHandlerMock, cfExplainerServiceHandlerMock));

        when(cfExplainerMock.explainAsync(any(), any(), any())).thenReturn(CompletableFuture.completedFuture(COUNTERFACTUAL_RESULT));

        BaseExplainabilityResultDto resultDto = assertDoesNotThrow(() -> explanationService.explainAsync(COUNTERFACTUAL_REQUEST, callbackMock)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));

        assertNotNull(resultDto);
        assertTrue(resultDto instanceof CounterfactualExplainabilityResultDto);
    }
}
