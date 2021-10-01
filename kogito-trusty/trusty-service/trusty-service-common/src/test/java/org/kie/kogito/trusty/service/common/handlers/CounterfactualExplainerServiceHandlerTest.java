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
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResultDto;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CounterfactualExplainerServiceHandlerTest
        extends BaseExplainerServiceHandlerTest<CounterfactualExplainerServiceHandler, CounterfactualExplainabilityResult, CounterfactualExplainabilityResultDto> {

    private static final String COUNTERFACTUAL_ID = "counterfactualId";

    private static final String SOLUTION_ID = "solutionId";

    private static final Long SEQUENCE_ID = 1L;

    private CounterfactualExplainabilityResultsManagerSlidingWindow explainabilityResultsManagerSlidingWindow;
    private CounterfactualExplainabilityResultsManagerDuplicates explainabilityResultsManagerDuplicates;

    @SuppressWarnings("rawtypes")
    private final Query query = mock(Query.class);

    @Override
    protected CounterfactualExplainerServiceHandler getHandler() {
        explainabilityResultsManagerSlidingWindow = mock(CounterfactualExplainabilityResultsManagerSlidingWindow.class);
        explainabilityResultsManagerDuplicates = mock(CounterfactualExplainabilityResultsManagerDuplicates.class);
        return new CounterfactualExplainerServiceHandler(storageService,
                explainabilityResultsManagerSlidingWindow,
                explainabilityResultsManagerDuplicates);
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
    @SuppressWarnings("unchecked")
    protected void setupMockStorage() {
        when(storageService.getCounterfactualResultStorage()).thenReturn(storage);
        when(query.filter(any())).thenReturn(query);
        when(storage.query()).thenReturn(query);
    }

    @BeforeEach
    @Override
    public void setup() {
        super.setup();
        when(result.getStage()).thenReturn(CounterfactualExplainabilityResult.Stage.FINAL);
        when(result.getCounterfactualId()).thenReturn(COUNTERFACTUAL_ID);
        when(result.getSolutionId()).thenReturn(SOLUTION_ID);
    }

    @Test
    @Override
    public void testGetExplainabilityResultById_WhenStored() {
        when(query.execute()).thenReturn(List.of(result));

        assertEquals(result, handler.getExplainabilityResultById(EXECUTION_ID));
    }

    @Test
    public void testGetExplainabilityResultById_WhenMultipleStored() {
        CounterfactualExplainabilityResult intermediate = mock(CounterfactualExplainabilityResult.class);
        when(intermediate.getExecutionId()).thenReturn(EXECUTION_ID);
        when(intermediate.getSolutionId()).thenReturn(SOLUTION_ID);
        when(intermediate.getStage()).thenReturn(CounterfactualExplainabilityResult.Stage.INTERMEDIATE);

        when(query.execute()).thenReturn(List.of(intermediate, result));

        assertEquals(result, handler.getExplainabilityResultById(EXECUTION_ID));
    }

    @Test
    public void testGetExplainabilityResultById_WhenOnlyIntermediateStored() {
        CounterfactualExplainabilityResult intermediate = mock(CounterfactualExplainabilityResult.class);
        when(intermediate.getExecutionId()).thenReturn(EXECUTION_ID);
        when(intermediate.getSolutionId()).thenReturn(SOLUTION_ID);
        when(intermediate.getStage()).thenReturn(CounterfactualExplainabilityResult.Stage.INTERMEDIATE);

        when(query.execute()).thenReturn(List.of(intermediate));

        assertThrows(IllegalArgumentException.class, () -> handler.getExplainabilityResultById(EXECUTION_ID));
    }

    @Test
    @Override
    public void testGetExplainabilityResultById_WhenNotStored() {
        when(query.execute()).thenReturn(Collections.emptyList());

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

        verify(storage).put(eq(SOLUTION_ID), eq(result));
        verify(explainabilityResultsManagerSlidingWindow).purge(eq(COUNTERFACTUAL_ID), eq(storage));
    }

    @Test
    @Override
    public void testExplainabilityResultFrom_Success() {
        CounterfactualExplainabilityResultDto dto = CounterfactualExplainabilityResultDto.buildSucceeded(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SOLUTION_ID,
                SEQUENCE_ID,
                true,
                CounterfactualExplainabilityResultDto.Stage.FINAL,
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualExplainabilityResult result = handler.explainabilityResultFrom(dto, decision);

        assertNotNull(result);
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, result.getCounterfactualId());
        assertEquals(SOLUTION_ID, result.getSolutionId());
        assertEquals(SEQUENCE_ID, result.getSequenceId());
        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
        assertTrue(result.isValid());
        assertEquals(CounterfactualExplainabilityResult.Stage.FINAL, result.getStage());
        assertTrue(result.getInputs().isEmpty());
        assertTrue(result.getOutputs().isEmpty());
    }

    @Test
    public void testExplainabilityResultFrom_SuccessIntermediate() {
        CounterfactualExplainabilityResultDto dto = CounterfactualExplainabilityResultDto.buildSucceeded(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SOLUTION_ID,
                SEQUENCE_ID,
                true,
                CounterfactualExplainabilityResultDto.Stage.INTERMEDIATE,
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualExplainabilityResult result = handler.explainabilityResultFrom(dto, decision);

        assertNotNull(result);
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, result.getCounterfactualId());
        assertEquals(SOLUTION_ID, result.getSolutionId());
        assertEquals(SEQUENCE_ID, result.getSequenceId());
        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
        assertTrue(result.isValid());
        assertEquals(CounterfactualExplainabilityResult.Stage.INTERMEDIATE, result.getStage());
        assertTrue(result.getInputs().isEmpty());
        assertTrue(result.getOutputs().isEmpty());
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
