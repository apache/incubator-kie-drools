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
package org.kie.kogito.trusty.service.common.handlers;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CounterfactualExplainabilityResultsManagerDuplicatesTest {

    private static final String EXECUTION_ID = "executionId";

    private static final String COUNTERFACTUAL_ID = "counterfactualId";

    private Storage<String, CounterfactualExplainabilityResult> storage;

    private Query query;

    private CounterfactualExplainabilityResultsManagerDuplicates manager;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        this.storage = mock(Storage.class);
        this.query = mock(Query.class);
        this.manager = new CounterfactualExplainabilityResultsManagerDuplicates(new ObjectMapper());

        when(storage.query()).thenReturn(query);
        when(query.sort(any())).thenReturn(query);
        when(query.filter(any())).thenReturn(query);
    }

    @Test
    public void testPurgeWhenResultSetSizeIsSmallerThanMinimum() {
        when(query.execute()).thenReturn(Collections.emptyList());

        manager.purge(COUNTERFACTUAL_ID, storage);

        verify(storage, never()).remove(anyString());
    }

    @Test
    public void testPurgeWhenResultSetSizeIsGreaterThanMinimum_WithDuplicateInputs() {
        CounterfactualExplainabilityResult result0 = makeResult(0, Collections.emptyList(), Collections.emptyList());
        CounterfactualExplainabilityResult result1 = makeResult(1, Collections.emptyList(), Collections.emptyList());
        CounterfactualExplainabilityResult result2 = makeResult(2, List.of(makeCounterfactualInput("a")), Collections.emptyList());
        CounterfactualExplainabilityResult result3 = makeResult(3, List.of(makeCounterfactualInput("a")), Collections.emptyList());
        when(query.execute()).thenReturn(List.of(result0, result1, result2, result3));

        manager.purge(COUNTERFACTUAL_ID, storage);

        verify(storage).remove(anyString());
        verify(storage).remove(result3.getSolutionId());
    }

    @Test
    public void testPurgeWhenResultSetSizeIsGreaterThanMinimum_WithDuplicateInputs_FinalLast() {
        CounterfactualExplainabilityResult result0 = makeResult(0, Collections.emptyList(), Collections.emptyList());
        CounterfactualExplainabilityResult result1 = makeResult(1, Collections.emptyList(), Collections.emptyList());
        CounterfactualExplainabilityResult result2 = makeResult(2, List.of(makeCounterfactualInput("a")), Collections.emptyList());
        CounterfactualExplainabilityResult result3 = makeResult(3, CounterfactualExplainabilityResult.Stage.FINAL, List.of(makeCounterfactualInput("a")), Collections.emptyList());
        when(query.execute()).thenReturn(List.of(result0, result1, result2, result3));

        manager.purge(COUNTERFACTUAL_ID, storage);

        verify(storage).remove(anyString());
        verify(storage).remove(result2.getSolutionId());
    }

    @Test
    public void testPurgeWhenResultSetSizeIsGreaterThanMinimum_WithDuplicateInputs_FinalPenultimate() {
        CounterfactualExplainabilityResult result0 = makeResult(0, Collections.emptyList(), Collections.emptyList());
        CounterfactualExplainabilityResult result1 = makeResult(1, Collections.emptyList(), Collections.emptyList());
        CounterfactualExplainabilityResult result2 = makeResult(2, CounterfactualExplainabilityResult.Stage.FINAL, List.of(makeCounterfactualInput("a")), Collections.emptyList());
        CounterfactualExplainabilityResult result3 = makeResult(3, List.of(makeCounterfactualInput("a")), Collections.emptyList());
        when(query.execute()).thenReturn(List.of(result0, result1, result2, result3));

        manager.purge(COUNTERFACTUAL_ID, storage);

        verify(storage).remove(anyString());
        verify(storage).remove(result3.getSolutionId());
    }

    @Test
    public void testPurgeWhenResultSetSizeIsGreaterThanMinimum_WithoutDuplicateInputs() {
        CounterfactualExplainabilityResult result0 = makeResult(0, List.of(makeCounterfactualInput("0")), Collections.emptyList());
        CounterfactualExplainabilityResult result1 = makeResult(1, List.of(makeCounterfactualInput("1")), Collections.emptyList());
        CounterfactualExplainabilityResult result2 = makeResult(2, List.of(makeCounterfactualInput("2")), Collections.emptyList());
        CounterfactualExplainabilityResult result3 = makeResult(3, List.of(makeCounterfactualInput("3")), Collections.emptyList());
        when(query.execute()).thenReturn(List.of(result0, result1, result2, result3));

        manager.purge(COUNTERFACTUAL_ID, storage);

        verify(storage, never()).remove(anyString());
    }

    @Test
    public void testPurgeWhenResultSetSizeIsGreaterThanMinimum_WithDuplicateOutputs() {
        CounterfactualExplainabilityResult result0 = makeResult(0, Collections.emptyList(), Collections.emptyList());
        CounterfactualExplainabilityResult result1 = makeResult(1, Collections.emptyList(), Collections.emptyList());
        CounterfactualExplainabilityResult result2 = makeResult(2, Collections.emptyList(), List.of(makeCounterfactualOutput("a")));
        CounterfactualExplainabilityResult result3 = makeResult(3, Collections.emptyList(), List.of(makeCounterfactualOutput("a")));
        when(query.execute()).thenReturn(List.of(result0, result1, result2, result3));

        manager.purge(COUNTERFACTUAL_ID, storage);

        verify(storage).remove(anyString());
        verify(storage).remove(result3.getSolutionId());
    }

    @Test
    public void testPurgeWhenResultSetSizeIsGreaterThanMinimum_WithoutDuplicateOutputs() {
        CounterfactualExplainabilityResult result0 = makeResult(0, Collections.emptyList(), List.of(makeCounterfactualOutput("0")));
        CounterfactualExplainabilityResult result1 = makeResult(1, Collections.emptyList(), List.of(makeCounterfactualOutput("1")));
        CounterfactualExplainabilityResult result2 = makeResult(2, Collections.emptyList(), List.of(makeCounterfactualOutput("2")));
        CounterfactualExplainabilityResult result3 = makeResult(3, Collections.emptyList(), List.of(makeCounterfactualOutput("3")));
        when(query.execute()).thenReturn(List.of(result0, result1, result2, result3));

        manager.purge(COUNTERFACTUAL_ID, storage);

        verify(storage, never()).remove(anyString());
    }

    private CounterfactualExplainabilityResult makeResult(long sequenceId,
            Collection<NamedTypedValue> inputs,
            Collection<NamedTypedValue> outputs) {
        return makeResult(sequenceId,
                CounterfactualExplainabilityResult.Stage.INTERMEDIATE,
                inputs,
                outputs);
    }

    private CounterfactualExplainabilityResult makeResult(long sequenceId,
            CounterfactualExplainabilityResult.Stage stage,
            Collection<NamedTypedValue> inputs,
            Collection<NamedTypedValue> outputs) {
        return new CounterfactualExplainabilityResult(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                UUID.randomUUID().toString(),
                sequenceId,
                ExplainabilityStatus.SUCCEEDED,
                null,
                true,
                stage,
                inputs,
                outputs);
    }

    private NamedTypedValue makeCounterfactualInput(final String name) {
        return new NamedTypedValue(name, new UnitValue("typeRef", "typeRef", new TextNode("value")));
    }

    private NamedTypedValue makeCounterfactualOutput(final String name) {
        return new NamedTypedValue(name, new UnitValue("typeRef", "typeRef", new TextNode("value")));
    }
}
