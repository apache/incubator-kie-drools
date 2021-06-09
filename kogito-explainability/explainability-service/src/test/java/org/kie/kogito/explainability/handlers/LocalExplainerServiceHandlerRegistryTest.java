/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.handlers;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.PredictionProviderFactory;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequestDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequestDto;
import org.kie.kogito.explainability.api.ModelIdentifierDto;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.models.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.models.LIMEExplainabilityRequest;
import org.kie.kogito.explainability.models.ModelIdentifier;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocalExplainerServiceHandlerRegistryTest {

    private static final String EXECUTION_ID = "executionId";

    private static final String SERVICE_URL = "serviceURL";

    private static final ModelIdentifier MODEL_IDENTIFIER = new ModelIdentifier("resourceType", "resourceId");

    private static final ModelIdentifierDto MODEL_IDENTIFIER_DTO = new ModelIdentifierDto("resourceType", "resourceId");

    private static final String COUNTERFACTUAL_ID = "counterfactualId";

    private LimeExplainerServiceHandler limeExplainerServiceHandler;
    private CounterfactualExplainerServiceHandler counterfactualExplainerServiceHandler;
    private PredictionProvider predictionProvider;
    private Consumer<BaseExplainabilityResultDto> callback;

    private LocalExplainerServiceHandlerRegistry registry;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        LimeExplainer limeExplainer = mock(LimeExplainer.class);
        CounterfactualExplainer counterfactualExplainer = mock(CounterfactualExplainer.class);
        PredictionProviderFactory predictionProviderFactory = mock(PredictionProviderFactory.class);
        limeExplainerServiceHandler = spy(new LimeExplainerServiceHandler(limeExplainer, predictionProviderFactory));
        counterfactualExplainerServiceHandler = spy(new CounterfactualExplainerServiceHandler(counterfactualExplainer, predictionProviderFactory));
        predictionProvider = mock(PredictionProvider.class);
        callback = mock(Consumer.class);

        when(predictionProviderFactory.createPredictionProvider(any(), any(), any())).thenReturn(predictionProvider);
        Instance<LocalExplainerServiceHandler<?, ?, ?>> explanationHandlers = mock(Instance.class);
        when(explanationHandlers.stream()).thenReturn(Stream.of(limeExplainerServiceHandler, counterfactualExplainerServiceHandler));
        registry = new LocalExplainerServiceHandlerRegistry(explanationHandlers);
    }

    @Test
    public void testLIME_explainabilityRequestFrom() {
        LIMEExplainabilityRequestDto request = new LIMEExplainabilityRequestDto(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER_DTO,
                Collections.emptyMap(),
                Collections.emptyMap());

        assertTrue(registry.explainabilityRequestFrom(request) instanceof LIMEExplainabilityRequest);

        verify(limeExplainerServiceHandler).explainabilityRequestFrom(eq(request));
    }

    @Test
    public void testLIME_explainAsyncWithResults() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap());

        registry.explainAsyncWithResults(request, callback);

        verify(limeExplainerServiceHandler).explainAsyncWithResults(eq(request), eq(callback));
    }

    @Test
    public void testCounterfactual_explainabilityRequestFrom() {
        CounterfactualExplainabilityRequestDto request = new CounterfactualExplainabilityRequestDto(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER_DTO,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap());

        assertTrue(registry.explainabilityRequestFrom(request) instanceof CounterfactualExplainabilityRequest);

        verify(counterfactualExplainerServiceHandler).explainabilityRequestFrom(eq(request));
    }

    @Test
    public void testCounterfactual_explainAsyncWithResults() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap());

        registry.explainAsyncWithResults(request, callback);

        verify(counterfactualExplainerServiceHandler).explainAsyncWithResults(eq(request), eq(callback));
    }

}
