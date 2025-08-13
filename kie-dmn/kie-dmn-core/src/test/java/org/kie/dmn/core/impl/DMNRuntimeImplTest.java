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
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.InputDataNodeImpl;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.InputData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.impl.DMNRuntimeImpl.getTopmostModel;
import static org.kie.dmn.core.impl.DMNRuntimeImpl.populateContextWithInheritedData;
import static org.kie.dmn.core.impl.DMNRuntimeImpl.populateInputsFromTopmostModel;
import static org.kie.dmn.core.impl.DMNRuntimeImpl.populateResultContextWithTopmostParentsValues;
import static org.kie.dmn.core.impl.DMNRuntimeImpl.processTopmostModelTuple;
import static org.kie.dmn.core.impl.DMNRuntimeImpl.updateContextMap;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class DMNRuntimeImplTest {

    @Test
    void testGetTopmostModel() {
        DMNModelImpl importingModel = mock(DMNModelImpl.class);
        DMNModelImpl importedModel = mock(DMNModelImpl.class);

        when(importedModel.getNamespace())
                .thenReturn("http://www.trisotech.com/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9");
        when(importedModel.getName())
                .thenReturn("ParentModel");

        when(importingModel.getImportAliasFor(importedModel.getNamespace(), importedModel.getName()))
                .thenReturn(Optional.of("parentModel"));

        DMNModelImpl.ModelImportTuple tuple =
                new DMNModelImpl.ModelImportTuple("parentModel", importedModel);
        Optional<Set<DMNModelImpl.ModelImportTuple>> optionalTopmostModels = Optional.of(Set.of(tuple));

        when(importingModel.getTopmostParents()).thenReturn(optionalTopmostModels);

        Optional<Set<DMNModelImpl.ModelImportTuple>> topmostModel = getTopmostModel(importingModel);

        assertThat(topmostModel).isPresent();
        topmostModel.ifPresent(set ->
                assertThat(set)
                        .extracting(DMNModelImpl.ModelImportTuple::getImportName)
                        .containsOnly("parentModel")
        );
    }

    @Test
    void testPopulateResultContextWithTopmostParentsValues() {
        Definitions defs = mock(Definitions.class);
        when(defs.getNamespace()).thenReturn("ns1");
        when(defs.getName()).thenReturn("model1");
        DMNModelImpl model = spy(new DMNModelImpl(defs));

        Definitions parentDefs = mock(Definitions.class);
        when(parentDefs.getNamespace()).thenReturn("ns2");
        when(parentDefs.getName()).thenReturn("parentModel");
        DMNModelImpl parent = new DMNModelImpl(parentDefs);

        InputDataNode inputNode = mock(InputDataNode.class);
        when(inputNode.getName()).thenReturn("inputA");
        when(inputNode.getModelNamespace()).thenReturn("ns2");
        when(inputNode.getId()).thenReturn("idA");
        parent.addInput(inputNode);

        model.setImportAliasForNS("parentAlias", "ns2", "parentModel");
        Set<DMNModelImpl.ModelImportTuple> topmostParents =
                Set.of(new DMNModelImpl.ModelImportTuple("parentAlias", parent));
        doReturn(Optional.of(topmostParents)).when(model).getTopmostParents();

        DMNResultImpl result = mock(DMNResultImpl.class, CALLS_REAL_METHODS);
        DMNContext context = new DMNContextImpl();
        Map<String, Object> parentAliasMap = new HashMap<>();
        parentAliasMap.put("inputA", "valueA");
        context.set("parentAlias", parentAliasMap);
        result.setContext(context);

        DMNModelImpl.ModelImportTuple tuple = new DMNModelImpl.ModelImportTuple("parentModel", parent);
        Optional<Set<DMNModelImpl.ModelImportTuple>> optionalTopmostModels = Optional.of(Set.of(tuple));
        when(getTopmostModel(model)).thenReturn(optionalTopmostModels);

        Map<String, Collection<List<String>>> mockImportChainAliases = new HashMap<>();
        mockImportChainAliases.put("ns2", List.of(List.of("parentAlias")));
        when(model.getImportChainAliases()).thenReturn(mockImportChainAliases);

        populateResultContextWithTopmostParentsValues(result, model);

        DMNContext expectedContext = new DMNContextImpl();
        expectedContext.set("parentAlias",Map.of("inputA", "valueA"));

        assertThat(context)
                .usingRecursiveComparison()
                .isEqualTo(expectedContext);
    }

    @Test
    void testPopulateInputsFromTopmostModels() {
        DMNModelImpl importingModel = mock(DMNModelImpl.class);

        DMNResultImpl result = mock(DMNResultImpl.class, CALLS_REAL_METHODS);
        DMNContext context = new DMNContextImpl();
        context.set("Person Name", "Klaus");

        when(result.getContext()).thenReturn(context);

        InputData mockInputData = mock(InputData.class);
        when(mockInputData.getName()).thenReturn("Person Name");
        InputDataNodeImpl node = new InputDataNodeImpl(mockInputData);

        DMNModelImpl topmostModel = mock(DMNModelImpl.class);
        when(topmostModel.getInputs()).thenReturn(Set.of(node));

        String namespace = "test-namespace";
        DMNModelImpl.ModelImportTuple tupleA = mock(DMNModelImpl.ModelImportTuple.class);
        when(tupleA.getModel()).thenReturn(topmostModel);
        when(tupleA.getImportName()).thenReturn("parentModel");
        when(tupleA.getModel().getNamespace()).thenReturn(namespace);

        Map<String, Collection<List<String>>> importChainAliases = new HashMap<>();
        List<String> chain = Arrays.asList("Child A", "parentModel");
        importChainAliases.put(namespace, Collections.singletonList(chain));
        when(importingModel.getImportChainAliases()).thenReturn(importChainAliases);

        Set<DMNModelImpl.ModelImportTuple> topmostModels = Set.of(tupleA);

        populateInputsFromTopmostModel(result, importingModel, topmostModels);

        assertThat(context.get("Person Name")).isEqualTo("Klaus");
    }

    @Test
    void testPopulateContextWithInheritedData() {
        DMNModelImpl importingModel = mock(DMNModelImpl.class);
        DMNContext toPopulate = new DMNContextImpl();
        toPopulate.set("Person Name", "Klaus");

        List<String> chainedModels = List.of("Child A", "parentModel");
        Map toStore = Map.of("Person Name", "Klaus");
        String importName = "parentModel";
        String topMostNamespace = "https://www.apache.org/customnamespace/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9";

        DMNContext context = new DMNContextImpl();
        Map<String, Object> parentModel = Map.of("Person Name", "Klaus");
        Map<String, Object> childA = Map.of("parentModel", parentModel);
        context.set("Person Name", "Klaus");
        context.set("Child A", childA);

        Map<String, Collection<List<String>>> importChainAliases = mock(Map.class);
        when(importingModel.getImportChainAliases()).thenReturn(importChainAliases);
        when(importChainAliases.get(topMostNamespace)).thenReturn(Collections.singletonList(chainedModels));

        populateContextWithInheritedData(toPopulate, toStore, importName, topMostNamespace, importingModel);
        assertThat(toPopulate).usingRecursiveComparison().isEqualTo(context);
    }

    @Test
    void testProcessTopmostModelTuple() {
        DMNModelImpl model = mock(DMNModelImpl.class);
        DMNResultImpl result = mock(DMNResultImpl.class, CALLS_REAL_METHODS);
        DMNModelImpl.ModelImportTuple topmostModelTuple = mock(DMNModelImpl.ModelImportTuple.class);
        DMNModelImpl topmostModel = mock(DMNModelImpl.class);

        DMNContext context = new DMNContextImpl();
        context.set("Person Name", "Klaus");

        InputData topmostInput = mock(InputData.class);
        InputDataNodeImpl node = new InputDataNodeImpl(topmostInput);

        when(result.getContext()).thenReturn(context);
        when(topmostModelTuple.getModel()).thenReturn(topmostModel);
        when(topmostModel.getInputs()).thenReturn(Set.of(node));
        when(topmostInput.getName()).thenReturn("Person Name");
        when(topmostModelTuple.getImportName()).thenReturn("parentModel");
        
        String namespace = "test-namespace";
        when(topmostModelTuple.getModel().getNamespace()).thenReturn(namespace);
        
        Map<String, Collection<List<String>>> importChainAliases = new HashMap<>();
        List<String> chain = Arrays.asList("Child A", "parentModel");
        importChainAliases.put(namespace, Collections.singletonList(chain));
        when(model.getImportChainAliases()).thenReturn(importChainAliases);

        processTopmostModelTuple(result, topmostModelTuple, model);

        assertThat(context.get("Person Name")).isEqualTo("Klaus");
    }

    @Test
    void testUpdateContextMap() {
        DMNModelImpl model = mock(DMNModelImpl.class);
        DMNModelImpl topmostModel = mock(DMNModelImpl.class);
        String namespace = "namespace";
        when(topmostModel.getNamespace()).thenReturn(namespace);
        when(topmostModel.getName()).thenReturn("parentModel");

        String inputName = "Person Name";
        String storedValue = "Klaus";

        Map<String, Collection<List<String>>> importChainAliases = new HashMap<>();
        List<String> chain = Arrays.asList("Child A", "parentModel");
        importChainAliases.put(namespace, Collections.singletonList(chain));
        when(model.getImportChainAliases()).thenReturn(importChainAliases);

        DMNContextImpl context = new DMNContextImpl();
        DMNResultImpl result = mock(DMNResultImpl.class, CALLS_REAL_METHODS);
        when(result.getContext()).thenReturn(context);

        DMNModelImpl.ModelImportTuple topmostModelTuple =
                new DMNModelImpl.ModelImportTuple("parentModel", topmostModel);

        updateContextMap(result, model, topmostModelTuple, inputName, storedValue);

        @SuppressWarnings("unchecked")
        Map<String, Object> childA = (Map<String, Object>) context.get("Child A");
        @SuppressWarnings("unchecked")
        Map<String, Object> parentMap = (Map<String, Object>) childA.get("parentModel");
        assertThat(parentMap).containsEntry(inputName, storedValue);
    }

}