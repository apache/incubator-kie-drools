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

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResultDto;
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class CounterfactualExplainerServiceHandlerTest
        extends BaseExplainerServiceHandlerTest<CounterfactualExplainerServiceHandler, CounterfactualExplainabilityResult, CounterfactualExplainabilityResultDto> {

    private static final String COUNTERFACTUAL_ID = "counterfactualId";

    @Override
    protected CounterfactualExplainerServiceHandler getHandler() {
        return new CounterfactualExplainerServiceHandler(storageService);
    }

    @Override
    protected Class<CounterfactualExplainabilityResult> getResult() {
        return CounterfactualExplainabilityResult.class;
    }

    @Override
    protected Class<CounterfactualExplainabilityResultDto> getResultDto() {
        return CounterfactualExplainabilityResultDto.class;
    }

    @Override
    protected void setupMockStorage() {
        when(storageService.getCounterfactualResultStorage()).thenReturn(storage);
    }

    @Test
    @Override
    public void testExplainabilityResultFrom_Success() {
        CounterfactualExplainabilityResultDto dto = CounterfactualExplainabilityResultDto.buildSucceeded(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                true,
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualExplainabilityResult result = handler.explainabilityResultFrom(dto, decision);

        assertNotNull(result);
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, result.getCounterfactualId());
        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
    }

    @Test
    @Override
    public void testExplainabilityResultFrom_Failure() {
        CounterfactualExplainabilityResultDto dto = CounterfactualExplainabilityResultDto.buildFailed(EXECUTION_ID, COUNTERFACTUAL_ID, FAILURE_MESSAGE);

        CounterfactualExplainabilityResult result = handler.explainabilityResultFrom(dto, decision);

        assertNotNull(result);
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, result.getCounterfactualId());
        assertEquals(ExplainabilityStatus.FAILED, result.getStatus());
        assertEquals(FAILURE_MESSAGE, result.getStatusDetails());
    }
}
