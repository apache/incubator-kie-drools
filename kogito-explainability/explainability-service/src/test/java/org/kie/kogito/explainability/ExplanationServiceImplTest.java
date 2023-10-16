/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.explainability;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.FeatureImportanceModel;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.explainability.api.SaliencyModel;
import org.kie.kogito.explainability.handlers.CounterfactualExplainerServiceHandler;
import org.kie.kogito.explainability.handlers.LimeExplainerServiceHandler;
import org.kie.kogito.explainability.handlers.LocalExplainerServiceHandlerRegistry;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;

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
    Consumer<BaseExplainabilityResult> callbackMock;

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
    void testLIMEExplainAsyncSuccess(ThrowingSupplier<BaseExplainabilityResult> invocation) {
        when(instance.stream()).thenReturn(Stream.of(limeExplainerServiceHandlerMock));
        when(limeExplainerMock.explainAsync(any(Prediction.class),
                eq(predictionProviderMock),
                any(Consumer.class)))
                        .thenReturn(CompletableFuture.completedFuture(SALIENCY_MAP));

        BaseExplainabilityResult result = assertDoesNotThrow(invocation);

        assertNotNull(result);
        assertTrue(result instanceof LIMEExplainabilityResult);
        LIMEExplainabilityResult limeResult = (LIMEExplainabilityResult) result;

        assertEquals(EXECUTION_ID, limeResult.getExecutionId());
        assertSame(ExplainabilityStatus.SUCCEEDED, limeResult.getStatus());
        assertNull(limeResult.getStatusDetails());
        assertEquals(SALIENCY_MAP.size(), limeResult.getSaliencies().size());

        SaliencyModel saliency = limeResult.getSaliencies().iterator().next();
        assertEquals(SALIENCY.getPerFeatureImportance().size(), saliency.getFeatureImportance().size());

        FeatureImportanceModel featureImportance1 = saliency.getFeatureImportance().get(0);
        assertEquals(FEATURE_IMPORTANCE_1.getFeature().getName(), featureImportance1.getFeatureName());
        assertEquals(FEATURE_IMPORTANCE_1.getScore(), featureImportance1.getFeatureScore(), 0.01);
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
    void testCounterfactualsExplainAsyncSuccess(ThrowingSupplier<BaseExplainabilityResult> invocation) {
        when(instance.stream()).thenReturn(Stream.of(cfExplainerServiceHandlerMock));

        when(cfExplainerMock.explainAsync(any(Prediction.class),
                eq(predictionProviderMock),
                any(Consumer.class))).thenReturn(CompletableFuture.completedFuture(COUNTERFACTUAL_RESULT));

        BaseExplainabilityResult result = assertDoesNotThrow(invocation);

        assertNotNull(result);
        assertTrue(result instanceof CounterfactualExplainabilityResult);
        CounterfactualExplainabilityResult counterfactualResult = (CounterfactualExplainabilityResult) result;

        assertEquals(EXECUTION_ID, counterfactualResult.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, counterfactualResult.getCounterfactualId());
        assertSame(ExplainabilityStatus.SUCCEEDED, counterfactualResult.getStatus());
        assertNull(counterfactualResult.getStatusDetails());
        assertEquals(COUNTERFACTUAL_RESULT.getEntities().size(), counterfactualResult.getInputs().size());
        assertEquals(COUNTERFACTUAL_RESULT.getOutput().size(), counterfactualResult.getOutputs().size());
        assertTrue(counterfactualResult.getOutputs().stream().anyMatch(o -> o.getName().equals("output1")));

        NamedTypedValue value = counterfactualResult.getOutputs().iterator().next();
        assertTrue(value.getValue().isUnit());
        assertEquals(Double.class.getSimpleName(), value.getValue().toUnit().getType());
        assertEquals(555.0, value.getValue().toUnit().getValue().asDouble());
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

        BaseExplainabilityResult result = assertDoesNotThrow(() -> explanationService.explainAsync(LIME_REQUEST, callbackMock)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));

        assertNotNull(result);
        assertTrue(result instanceof LIMEExplainabilityResult);
        LIMEExplainabilityResult exceptionResult = (LIMEExplainabilityResult) result;

        assertEquals(EXECUTION_ID, exceptionResult.getExecutionId());
        assertSame(ExplainabilityStatus.FAILED, exceptionResult.getStatus());
        assertEquals(errorMessage, exceptionResult.getStatusDetails());
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

        BaseExplainabilityResult result = assertDoesNotThrow(() -> explanationService.explainAsync(COUNTERFACTUAL_REQUEST, callbackMock)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));

        assertNotNull(result);
        assertTrue(result instanceof CounterfactualExplainabilityResult);
        CounterfactualExplainabilityResult exceptionResult = (CounterfactualExplainabilityResult) result;

        assertEquals(EXECUTION_ID, exceptionResult.getExecutionId());
        assertSame(ExplainabilityStatus.FAILED, exceptionResult.getStatus());
        assertEquals(errorMessage, exceptionResult.getStatusDetails());
    }

    @Test
    void testServiceHandlerLookupLIME() {
        when(instance.stream()).thenReturn(Stream.of(limeExplainerServiceHandlerMock, cfExplainerServiceHandlerMock));

        when(limeExplainerMock.explainAsync(any(), any(), any())).thenReturn(CompletableFuture.completedFuture(SALIENCY_MAP));

        BaseExplainabilityResult result = assertDoesNotThrow(() -> explanationService.explainAsync(LIME_REQUEST, callbackMock)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));

        assertNotNull(result);
        assertTrue(result instanceof LIMEExplainabilityResult);
    }

    @Test
    void testServiceHandlerLookupCounterfactuals() {
        when(instance.stream()).thenReturn(Stream.of(limeExplainerServiceHandlerMock, cfExplainerServiceHandlerMock));

        when(cfExplainerMock.explainAsync(any(), any(), any())).thenReturn(CompletableFuture.completedFuture(COUNTERFACTUAL_RESULT));

        BaseExplainabilityResult result = assertDoesNotThrow(() -> explanationService.explainAsync(COUNTERFACTUAL_REQUEST, callbackMock)
                .toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit()));

        assertNotNull(result);
        assertTrue(result instanceof CounterfactualExplainabilityResult);
    }
}
