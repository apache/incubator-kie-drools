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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.InputDataNodeImpl;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.InputData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class DMNRuntimeUtilsTest {

    @Test
    void populateResultContextWithTopmostParentsValues() {
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

        DMNContext context = new DMNContextImpl();
        Map<String, Object> parentAliasMap = new HashMap<>();
        parentAliasMap.put("inputA", "valueA");
        context.set("parentAlias", parentAliasMap);

        DMNModelImpl.ModelImportTuple tuple = new DMNModelImpl.ModelImportTuple("parentModel", parent);
        Optional<Set<DMNModelImpl.ModelImportTuple>> optionalTopmostModels = Optional.of(Set.of(tuple));
        when(DMNRuntimeUtils.getTopmostModel(model)).thenReturn(optionalTopmostModels);

        Map<String, Collection<List<String>>> mockImportChainAliases = new HashMap<>();
        mockImportChainAliases.put("ns2", List.of(List.of("parentAlias")));
        when(model.getImportChainAliases()).thenReturn(mockImportChainAliases);

        DMNRuntimeUtils.populateResultContextWithTopmostParentsValues(context, model);

        DMNContext expectedContext = new DMNContextImpl();
        expectedContext.set("parentAlias", Map.of("inputA", "valueA"));

        assertThat(context)
                .usingRecursiveComparison()
                .isEqualTo(expectedContext);
    }

    @Test
    void populateInputsFromTopmostModels() {
        DMNModelImpl importingModel = mock(DMNModelImpl.class);
        DMNContext context = new DMNContextImpl();
        context.set("Person Name", "Klaus");

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

        DMNRuntimeUtils.populateInputsFromTopmostModel(context, importingModel, topmostModels);

        assertThat(context.get("Person Name")).isEqualTo("Klaus");
    }

    @Test
    void processTopmostModelTuple() {
        DMNModelImpl model = mock(DMNModelImpl.class);
        DMNModelImpl.ModelImportTuple topmostModelTuple = mock(DMNModelImpl.ModelImportTuple.class);
        DMNModelImpl topmostModel = mock(DMNModelImpl.class);

        DMNContext context = new DMNContextImpl();
        context.set("Person Name", "Klaus");

        InputData topmostInput = mock(InputData.class);
        InputDataNodeImpl node = new InputDataNodeImpl(topmostInput);

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

        DMNRuntimeUtils.processTopmostModelTuple(context, topmostModelTuple, model);

        assertThat(context.get("Person Name")).isEqualTo("Klaus");
    }

    @Test
    void updateContextMap() {
        DMNModelImpl topmostModel = mock(DMNModelImpl.class);
        String namespace = "namespace";
        String topmostModelImportName = "parentModel";
        String importingModelName = "Child A";
        when(topmostModel.getNamespace()).thenReturn(namespace);
        when(topmostModel.getName()).thenReturn(topmostModelImportName);

        String inputName = "Person Name";
        String storedValue = "Klaus";

        Map<String, Collection<List<String>>> importChainAliases = new HashMap<>();
        // Order is from bottom - i.e. executed model - to topmost - i.e. imported
        List<String> chain = Arrays.asList(importingModelName, topmostModelImportName);
        importChainAliases.put(namespace, Collections.singletonList(chain));

        DMNContextImpl context = new DMNContextImpl();

        DMNModelImpl.ModelImportTuple topmostModelTuple =
                new DMNModelImpl.ModelImportTuple(topmostModelImportName, topmostModel);

        DMNRuntimeUtils.updateContextMap(context, importChainAliases, topmostModelTuple, inputName, storedValue);

        @SuppressWarnings("unchecked")
        Map<String, Object> childA = (Map<String, Object>) context.get(importingModelName);
        @SuppressWarnings("unchecked")
        Map<String, Object> parentMap = (Map<String, Object>) childA.get(topmostModelImportName);
        assertThat(parentMap).containsEntry(inputName, storedValue);
    }

    @Test
    void populateContextWithInheritedData() {
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
        when(importChainAliases.get(topMostNamespace)).thenReturn(Collections.singletonList(chainedModels));

        DMNRuntimeUtils.populateContextWithInheritedData(toPopulate, toStore, importName, topMostNamespace,
                                                         importChainAliases);
        assertThat(toPopulate).usingRecursiveComparison().isEqualTo(context);
    }

    @Test
    void addNewMapToContext() {
        DMNContext toPopulate = new DMNContextImpl();
        String importName = "parentModel";
        Map<String, Object> toStore = Map.of("Person Name", "Klaus");
        String chainedModel = "chainedModel";
        DMNRuntimeUtils.addNewMapToContext(toPopulate, importName, toStore, chainedModel);
        assertThat(toPopulate.get(chainedModel)).isNotNull().isInstanceOf(Map.class);
        Map<String, Object> retrieved = (Map<String, Object>) toPopulate.get(chainedModel);
        assertThat(retrieved).containsEntry(importName, toStore);
    }

    @Test
    void addValueInsideMap() {
        Map<String, Object> map = new HashMap<>();
        DMNRuntimeUtils.addValueInsideMap(map, "Person Name", "Klaus");
        assertThat(map).containsEntry("Person Name", "Klaus");

        map = Collections.unmodifiableMap(new HashMap<>());
        DMNRuntimeUtils.addValueInsideMap(map, "Person Name", "Klaus");
        assertThat(map).isEmpty();
    }

    @Test
    void getTopmostModel() {
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

        Optional<Set<DMNModelImpl.ModelImportTuple>> topmostModel = DMNRuntimeUtils.getTopmostModel(importingModel);

        assertThat(topmostModel).isPresent();
        topmostModel.ifPresent(set ->
                                       assertThat(set)
                                               .extracting(DMNModelImpl.ModelImportTuple::getImportName)
                                               .containsOnly("parentModel")
        );
    }

    @Test
    void coerceSingleItemCollectionToValueWithCollectionValue() {
        String nestedValue = "value";
        Object value = Collections.singletonList("value");
        DMNType type = mock(DMNType.class);
        when(type.isCollection()).thenReturn(false);
        Object retrieved = DMNRuntimeUtils.coerceSingleItemCollectionToValue(value, type);
        assertThat(retrieved).isEqualTo(nestedValue);

        when(type.isCollection()).thenReturn(true);
        retrieved = DMNRuntimeUtils.coerceSingleItemCollectionToValue(value, type);
        assertThat(retrieved).isEqualTo(value);

        value = Arrays.asList("value", "value2");
        when(type.isCollection()).thenReturn(false);
        retrieved = DMNRuntimeUtils.coerceSingleItemCollectionToValue(value, type);
        assertThat(retrieved).isEqualTo(value);

        when(type.isCollection()).thenReturn(true);
        retrieved = DMNRuntimeUtils.coerceSingleItemCollectionToValue(value, type);
        assertThat(retrieved).isEqualTo(value);
    }

    @Test
    void coerceSingleItemCollectionToValueWithSingleValue() {
        Object value = "value";
        DMNType type = mock(DMNType.class);
        when(type.isCollection()).thenReturn(false);
        Object retrieved = DMNRuntimeUtils.coerceSingleItemCollectionToValue(value, type);
        assertThat(retrieved).isEqualTo(value);

        when(type.isCollection()).thenReturn(true);
        retrieved = DMNRuntimeUtils.coerceSingleItemCollectionToValue(value, type);
        assertThat(retrieved).isEqualTo(Collections.singletonList(value));
    }

    @Test
    void getObjectString() {
        StringBuilder builder = new StringBuilder();
        IntStream.range(0, 10).forEach(i -> builder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        String retrieved = DMNRuntimeUtils.getObjectString(builder.toString());
        assertThat(retrieved).contains("[string clipped after 50 chars, total length is 260]");

        OverflowingObject overflowingObject = new OverflowingObject("OVERFLOWING OBJECT");
        retrieved = DMNRuntimeUtils.getObjectString(overflowingObject);
        assertThat(retrieved).isEqualTo("(_undefined_)");
    }

    @Test
    void getDependencyIdentifierSameNamespace() {
        String namespace = "test-namespace";
        String callerId = "callerId";
        String callerName = "callerName";
        DMNNode callerNode = mock(DMNNode.class);
        when(callerNode.getName()).thenReturn(callerName);
        when(callerNode.getId()).thenReturn(callerId);
        when(callerNode.getModelNamespace()).thenReturn(namespace);

        String calledId = "calledId";
        String calledName = "calledName";
        DMNNode calledNode = mock(DMNNode.class);
        when(calledNode.getName()).thenReturn(calledName);
        when(calledNode.getId()).thenReturn(calledId);
        when(calledNode.getModelNamespace()).thenReturn(namespace);
        assertThat(DMNRuntimeUtils.getDependencyIdentifier(callerNode, calledNode)).isEqualTo(calledName);
    }

    @Test
    void getDependencyIdentifierDifferentNamespace() {
        String callerId = "callerId";
        String callerName = "callerName";
        String callerNamespace = "callerNamespace";
        String alias = "alias";
        Optional<String> importAlias = Optional.of(alias);
        String calledId = "calledId";
        String calledName = "calledName";
        String calledNamespace = "calledNamespace";
        String calledModelName = "calledModelName";

        DMNNode callerNode = mock(DMNNode.class);
        when(callerNode.getName()).thenReturn(callerName);
        when(callerNode.getId()).thenReturn(callerId);
        when(callerNode.getModelNamespace()).thenReturn(callerNamespace);
        when(callerNode.getModelImportAliasFor(calledNamespace, calledModelName)).thenReturn(importAlias);

        DMNNode calledNode = mock(DMNNode.class);
        when(calledNode.getName()).thenReturn(calledName);
        when(calledNode.getId()).thenReturn(calledId);
        when(calledNode.getModelNamespace()).thenReturn(calledNamespace);
        when(calledNode.getModelName()).thenReturn(calledModelName);

        String expected = alias + "." + calledName;
        assertThat(DMNRuntimeUtils.getDependencyIdentifier(callerNode, calledNode)).isEqualTo(expected);
    }

    @Test
    void getDependencyIdentifierDifferentNamespaceWithoutAlias() {
        String callerId = "callerId";
        String callerName = "callerName";
        String callerNamespace = "callerNamespace";
        String calledId = "calledId";
        String calledName = "calledName";
        String calledNamespace = "calledNamespace";
        String calledModelName = "calledModelName";

        DMNNode callerNode = mock(DMNNode.class);
        when(callerNode.getName()).thenReturn(callerName);
        when(callerNode.getId()).thenReturn(callerId);
        when(callerNode.getModelNamespace()).thenReturn(callerNamespace);
        when(callerNode.getModelImportAliasFor(calledNamespace, calledModelName)).thenReturn(Optional.empty());

        DMNNode calledNode = mock(DMNNode.class);
        when(calledNode.getName()).thenReturn(calledName);
        when(calledNode.getId()).thenReturn(calledId);
        when(calledNode.getModelNamespace()).thenReturn(calledNamespace);
        when(calledNode.getModelName()).thenReturn(calledModelName);

        String expected = "{" + calledNamespace + "}" + "." + calledName;
        assertThat(DMNRuntimeUtils.getDependencyIdentifier(callerNode, calledNode)).isEqualTo(expected);
    }

    @Test
    void getPrefixedIdentifierWithAlias() {
        String callerId = "callerId";
        String callerName = "callerName";
        String callerNamespace = "callerNamespace";
        String alias = "alias";
        Optional<String> importAlias = Optional.of(alias);
        String calledId = "calledId";
        String calledName = "calledName";
        String calledNamespace = "calledNamespace";
        String calledModelName = "calledModelName";

        DMNNode callerNode = mock(DMNNode.class);
        when(callerNode.getName()).thenReturn(callerName);
        when(callerNode.getId()).thenReturn(callerId);
        when(callerNode.getModelNamespace()).thenReturn(callerNamespace);
        when(callerNode.getModelImportAliasFor(calledNamespace, calledModelName)).thenReturn(importAlias);

        DMNNode calledNode = mock(DMNNode.class);
        when(calledNode.getName()).thenReturn(calledName);
        when(calledNode.getId()).thenReturn(calledId);
        when(calledNode.getModelNamespace()).thenReturn(calledNamespace);
        when(calledNode.getModelName()).thenReturn(calledModelName);

        String expected = alias + "." + calledName;
        assertThat(DMNRuntimeUtils.getPrefixedIdentifier(callerNode, calledNode)).isEqualTo(expected);
    }

    @Test
    void getPrefixedIdentifierWithoutAlias() {
        String callerId = "callerId";
        String callerName = "callerName";
        String callerNamespace = "callerNamespace";
        String calledId = "calledId";
        String calledName = "calledName";
        String calledNamespace = "calledNamespace";
        String calledModelName = "calledModelName";

        DMNNode callerNode = mock(DMNNode.class);
        when(callerNode.getName()).thenReturn(callerName);
        when(callerNode.getId()).thenReturn(callerId);
        when(callerNode.getModelNamespace()).thenReturn(callerNamespace);
        when(callerNode.getModelImportAliasFor(calledNamespace, calledModelName)).thenReturn(Optional.empty());

        DMNNode calledNode = mock(DMNNode.class);
        when(calledNode.getName()).thenReturn(calledName);
        when(calledNode.getId()).thenReturn(calledId);
        when(calledNode.getModelNamespace()).thenReturn(calledNamespace);
        when(calledNode.getModelName()).thenReturn(calledModelName);

        String expected = "{" + calledNamespace + "}" + "." + calledName;
        assertThat(DMNRuntimeUtils.getPrefixedIdentifier(callerNode, calledNode)).isEqualTo(expected);
    }

    @Test
    void getIdentifier() {
        String id = "id";
        String name = "name";
        DMNNode dmnNode = mock(DMNNode.class);
        when(dmnNode.getName()).thenReturn(null);
        when(dmnNode.getId()).thenReturn(id);
        assertThat(DMNRuntimeUtils.getIdentifier(dmnNode)).isEqualTo(id);
        when(dmnNode.getName()).thenReturn(name);
        assertThat(DMNRuntimeUtils.getIdentifier(dmnNode)).isEqualTo(name);
    }

    private static class OverflowingObject {

        String string;

        public OverflowingObject(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return this.toString();
        }
    }
}