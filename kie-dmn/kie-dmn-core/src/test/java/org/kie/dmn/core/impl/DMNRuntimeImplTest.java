/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.impl;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.ast.InputDataNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DMNRuntimeImplTest {


    @Test
    void testPopulateContextUsingAliases() {
        Collection<Collection<List<String>>> importChainValues = Arrays.asList(
                List.of(Arrays.asList("Model B", "modelA")),
                List.of(List.of("Model B")));

        InputDataNode node1 = mock(InputDataNode.class);
        when(node1.getName()).thenReturn("Person name");
        Set<InputDataNode> inputs = new HashSet<>();
        inputs.add(node1);

        Map<String, Object> context = new HashMap<>();
        context.put("Person name", "Klaus");
        Map<String, Object> baseInputs = new HashMap<>();
        baseInputs.put("Person name", "Klaus");
        Map<String, Object> expectedContext = new HashMap<>();
        Map<String, Object> modelA = new HashMap<>();
        modelA.put("Person name", "Klaus");
        expectedContext.put("Person name", "Klaus");
        Map<String, Object> modelB = new HashMap<>();
        modelB.put("modelA", modelA);
        expectedContext.put("Model B", modelB);

        Map<String, Object> updatedContext = DMNRuntimeImpl.populateContextUsingAliases(inputs, importChainValues, context, baseInputs);
        assertThat(updatedContext).isEqualTo(expectedContext);
    }

    @Test
    void testGetFilteredInputs() {
        InputDataNode node1 = mock(InputDataNode.class);
        when(node1.getName()).thenReturn("Person name");
        Set<InputDataNode> inputs = new HashSet<>();
        inputs.add(node1);

        Map<String, Object> context = new HashMap<>();
        context.put("Person name", "Klaus");
        Map<String, Object> baseInputs = new HashMap<>();
        baseInputs.put("Person name", "Klaus");

        Map<String, Object> expected = Map.of("Person name", "Klaus");
        Map<String, Object> result = DMNRuntimeImpl.getFilteredInputs(inputs, context, baseInputs);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testGetFilteredInputsMultipleValue() {
        InputDataNode node1 = mock(InputDataNode.class);
        when(node1.getName()).thenReturn("Person age");
        Set<InputDataNode> inputs = new HashSet<>();
        inputs.add(node1);

        Map<String, Object> context = new HashMap<>();
        context.put("Person name", "Klaus");
        context.put("Person age", 27);
        context.put("Person phone", 1234567899);
        Map<String, Object> baseInputs = new HashMap<>();
        baseInputs.put("Person name", "Klaus");
        baseInputs.put("Person age", 27);
        baseInputs.put("Person phone", 1234567899);

        Map<String, Object> expected = Map.of("Person age", 27);
        Map<String, Object> result = DMNRuntimeImpl.getFilteredInputs(inputs, context, baseInputs);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testGetFilteredInputsWithEmptyBaseInput() {
        InputDataNode node1 = mock(InputDataNode.class);
        when(node1.getName()).thenReturn("Person age");
        Set<InputDataNode> inputs = new HashSet<>();
        inputs.add(node1);

        Map<String, Object> context = new HashMap<>();
        context.put("Person name", "Klaus");
        context.put("Person age", 27);
        context.put("Person phone", 1234567899);
        Map<String, Object> baseInputs = new HashMap<>();

        Map<String, Object> result = DMNRuntimeImpl.getFilteredInputs(inputs, context, baseInputs);
        assertThat(result).isEqualTo(context);
    }

    @Test
    void testRetrieveContext() {
        List<List<String>> importChainAliases = List.of(List.of("Model B", "modelA"));

        Map<String, Object> context = new HashMap<>();
        context.put("Person name", "Klaus");
        Map<String, Object> filteredInputs = new HashMap<>();
        filteredInputs.put("Person name", "Klaus");

        Map<String, Object> expectedContext = new HashMap<>();
        Map<String, Object> modelA = new HashMap<>();
        modelA.put("Person name", "Klaus");
        expectedContext.put("Person name", "Klaus");
        Map<String, Object> modelB = new HashMap<>();
        modelB.put("modelA", modelA);
        expectedContext.put("Model B", modelB);

        Map<String, Object> updatedContext = DMNRuntimeImpl.retrieveContext(importChainAliases, context, filteredInputs);
        assertThat(updatedContext).isEqualTo(expectedContext);
    }
}