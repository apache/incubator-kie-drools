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
package org.kie.kogito.trusty.service.common.handlers;

import java.util.Collections;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResultDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.LIMEExplainabilityResult;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExplainerServiceHandlerRegistryTest {

    private static final String EXECUTION_ID = "executionId";

    private static final String COUNTERFACTUAL_ID = "counterfactualId";

    private LIMEExplainerServiceHandler limeExplainerServiceHandler;
    private CounterfactualExplainerServiceHandler counterfactualExplainerServiceHandler;
    private Storage<String, LIMEExplainabilityResult> storageLIME;
    private Storage<String, CounterfactualExplainabilityResult> storageCounterfactual;
    private Decision decision;

    private ExplainerServiceHandlerRegistry registry;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        TrustyStorageService trustyStorage = mock(TrustyStorageService.class);
        limeExplainerServiceHandler = spy(new LIMEExplainerServiceHandler(trustyStorage));
        counterfactualExplainerServiceHandler = spy(new CounterfactualExplainerServiceHandler(trustyStorage));
        Instance<ExplainerServiceHandler<?, ?>> explanationHandlers = mock(Instance.class);
        when(explanationHandlers.stream()).thenReturn(Stream.of(limeExplainerServiceHandler, counterfactualExplainerServiceHandler));
        registry = new ExplainerServiceHandlerRegistry(explanationHandlers);

        storageLIME = mock(Storage.class);
        storageCounterfactual = mock(Storage.class);
        when(trustyStorage.getLIMEResultStorage()).thenReturn(storageLIME);
        when(trustyStorage.getCounterfactualResultStorage()).thenReturn(storageCounterfactual);

        decision = mock(Decision.class);
    }

    @Test
    public void testLIME_getExplainabilityResultById() {
        when(storageLIME.containsKey(eq(EXECUTION_ID))).thenReturn(true);

        registry.getExplainabilityResultById(EXECUTION_ID, LIMEExplainabilityResult.class);

        verify(limeExplainerServiceHandler).getExplainabilityResultById(eq(EXECUTION_ID));
    }

    @Test
    public void testLIME_storeExplainabilityResult() {
        when(storageLIME.containsKey(eq(EXECUTION_ID))).thenReturn(false);
        LIMEExplainabilityResult result = mock(LIMEExplainabilityResult.class);

        registry.storeExplainabilityResult(EXECUTION_ID, result);

        verify(limeExplainerServiceHandler).storeExplainabilityResult(eq(EXECUTION_ID), eq(result));
    }

    @Test
    public void testLIME_explainabilityResultFrom() {
        LIMEExplainabilityResultDto dto = LIMEExplainabilityResultDto.buildSucceeded(EXECUTION_ID, Collections.emptyMap());

        assertTrue(registry.explainabilityResultFrom(dto, decision) instanceof LIMEExplainabilityResult);

        verify(limeExplainerServiceHandler).explainabilityResultFrom(eq(dto), eq(decision));
    }

    @Test
    public void testCounterfactual_getExplainabilityResultById() {
        when(storageCounterfactual.containsKey(eq(EXECUTION_ID))).thenReturn(true);

        registry.getExplainabilityResultById(EXECUTION_ID, CounterfactualExplainabilityResult.class);

        verify(counterfactualExplainerServiceHandler).getExplainabilityResultById(eq(EXECUTION_ID));
    }

    @Test
    public void testCounterfactual_storeExplainabilityResult() {
        when(storageCounterfactual.containsKey(eq(EXECUTION_ID))).thenReturn(false);
        CounterfactualExplainabilityResult result = mock(CounterfactualExplainabilityResult.class);

        registry.storeExplainabilityResult(EXECUTION_ID, result);

        verify(counterfactualExplainerServiceHandler).storeExplainabilityResult(eq(EXECUTION_ID), eq(result));
    }

    @Test
    public void testCounterfactual_explainabilityResultFrom() {
        CounterfactualExplainabilityResultDto dto = CounterfactualExplainabilityResultDto.buildSucceeded(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                true,
                Collections.emptyMap(),
                Collections.emptyMap());

        assertTrue(registry.explainabilityResultFrom(dto, decision) instanceof CounterfactualExplainabilityResult);

        verify(counterfactualExplainerServiceHandler).explainabilityResultFrom(eq(dto), eq(decision));
    }

}
