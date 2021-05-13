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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;
import org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel;
import org.kie.kogito.trusty.storage.api.model.LIMEExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LIMEExplainerServiceHandlerTest extends BaseExplainerServiceHandlerTest<LIMEExplainerServiceHandler, LIMEExplainabilityResult, LIMEExplainabilityResultDto> {

    @Override
    protected LIMEExplainerServiceHandler getHandler() {
        return new LIMEExplainerServiceHandler(storageService);
    }

    @Override
    protected Class<LIMEExplainabilityResult> getResult() {
        return LIMEExplainabilityResult.class;
    }

    @Override
    protected Class<LIMEExplainabilityResultDto> getResultDto() {
        return LIMEExplainabilityResultDto.class;
    }

    @Override
    protected void setupMockStorage() {
        when(storageService.getLIMEResultStorage()).thenReturn(storage);
    }

    @Test
    @Override
    public void testGetExplainabilityResultById_WhenStored() {
        when(storage.containsKey(anyString())).thenReturn(true);
        when(storage.get(eq(EXECUTION_ID))).thenReturn(result);

        assertEquals(result, handler.getExplainabilityResultById(EXECUTION_ID));
    }

    @Test
    @Override
    public void testGetExplainabilityResultById_WhenNotStored() {
        when(storage.containsKey(anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> handler.getExplainabilityResultById(EXECUTION_ID));
    }

    @Test
    @Override
    public void testStoreExplainabilityResult_WhenAlreadyStored() {
        when(storage.containsKey(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> handler.storeExplainabilityResult(EXECUTION_ID, result));
    }

    @Test
    @Override
    public void testStoreExplainabilityResultById_WhenNotAlreadyStored() {
        when(storage.containsKey(anyString())).thenReturn(false);

        handler.storeExplainabilityResult(EXECUTION_ID, result);

        verify(storage).put(eq(EXECUTION_ID), eq(result));
    }

    @Test
    @Override
    public void testExplainabilityResultFrom_Success() {
        LIMEExplainabilityResultDto dto = LIMEExplainabilityResultDto.buildSucceeded(EXECUTION_ID,
                Map.of("key1",
                        new SaliencyDto(List.of(new FeatureImportanceDto("feature1a", 1.0),
                                new FeatureImportanceDto("feature1b", 2.0))),
                        "key2",
                        new SaliencyDto(List.of(new FeatureImportanceDto("feature2", 3.0)))));

        LIMEExplainabilityResult result = handler.explainabilityResultFrom(dto, decision);

        assertNotNull(result);
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
        assertEquals(2, result.getSaliencies().size());

        Optional<SaliencyModel> oSaliencyModel1 = result.getSaliencies().stream().filter(sm -> sm.getOutcomeName().equals("key1")).findFirst();
        assertTrue(oSaliencyModel1.isPresent());
        SaliencyModel saliencyModel1 = oSaliencyModel1.get();
        assertEquals(2, saliencyModel1.getFeatureImportance().size());

        Optional<FeatureImportanceModel> oFeatureImportance1Model1 = saliencyModel1.getFeatureImportance().stream().filter(fim -> fim.getFeatureName().equals("feature1a")).findFirst();
        assertTrue(oFeatureImportance1Model1.isPresent());
        assertEquals(1.0, oFeatureImportance1Model1.get().getFeatureScore());
        Optional<FeatureImportanceModel> oFeatureImportance2Model1 = saliencyModel1.getFeatureImportance().stream().filter(fim -> fim.getFeatureName().equals("feature1b")).findFirst();
        assertTrue(oFeatureImportance2Model1.isPresent());
        assertEquals(2.0, oFeatureImportance2Model1.get().getFeatureScore());

        Optional<SaliencyModel> oSaliencyModel2 = result.getSaliencies().stream().filter(sm -> sm.getOutcomeName().equals("key2")).findFirst();
        assertTrue(oSaliencyModel2.isPresent());
        SaliencyModel saliencyModel2 = oSaliencyModel2.get();
        assertEquals(1, saliencyModel2.getFeatureImportance().size());

        Optional<FeatureImportanceModel> oFeatureImportance1Model2 = saliencyModel2.getFeatureImportance().stream().filter(fim -> fim.getFeatureName().equals("feature2")).findFirst();
        assertTrue(oFeatureImportance1Model2.isPresent());
        assertEquals(3.0, oFeatureImportance1Model2.get().getFeatureScore());
    }

    @Test
    @Override
    public void testExplainabilityResultFrom_Failure() {
        LIMEExplainabilityResultDto dto = LIMEExplainabilityResultDto.buildFailed(EXECUTION_ID, FAILURE_MESSAGE);

        LIMEExplainabilityResult result = handler.explainabilityResultFrom(dto, decision);

        assertNotNull(result);
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(ExplainabilityStatus.FAILED, result.getStatus());
        assertEquals(FAILURE_MESSAGE, result.getStatusDetails());
        assertTrue(result.getSaliencies().isEmpty());
    }
}
