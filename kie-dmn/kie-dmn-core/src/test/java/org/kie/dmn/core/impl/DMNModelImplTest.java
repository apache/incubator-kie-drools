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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.v1_5.TDecision;
import org.kie.dmn.model.v1_5.TDefinitions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.dmn.core.impl.DMNModelImpl.populateTopmostParents;
import static org.kie.dmn.core.impl.DMNModelImpl.getImportName;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DMNModelImplTest {

    private DMNModelImpl model;
    private static final String MODEL_NAMESPACE = "model_namespace";

    @BeforeEach
    public void init() {
        model = new DMNModelImpl();
        model.setDefinitions(getDefinitions(MODEL_NAMESPACE));
    }

    @Test
    void addDecisionSameModel() {
        List<DecisionNode> decisionNodeList = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> {
            DecisionNode toAdd = getDecisionNode("id_" + i, "decision_" + i, model.getDefinitions());
            model.addDecision(toAdd);
            decisionNodeList.add(toAdd);
        });

        assertThat(decisionNodeList).allSatisfy(decisionNode -> {
            assertThat(model.getDecisionByName(decisionNode.getName()))
                    .isNotNull()
                    .isEqualTo(decisionNode);
            assertThat(model.getDecisionById(decisionNode.getId()))
                    .isNotNull()
                    .isEqualTo(decisionNode);
        });
    }

    @Test
    void addDecisionWithoutIdSameModel() {
        List<DecisionNode> decisionNodeList = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> {
            DecisionNode toAdd = getDecisionNode(null, "decision_" + i, model.getDefinitions());
            model.addDecision(toAdd);
            decisionNodeList.add(toAdd);
        });

        assertThat(decisionNodeList).allSatisfy(decisionNode ->
                                         assertThat(model.getDecisionByName(decisionNode.getName()))
                                                 .isNotNull()
                                                 .isEqualTo(decisionNode));
    }

    @Test
    void addDecisionWithoutNameSameModel() {
        List<DecisionNode> decisionNodeList = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> {
            DecisionNode toAdd = getDecisionNode("id_" + i, null, model.getDefinitions());
            model.addDecision(toAdd);
            decisionNodeList.add(toAdd);
        });
        assertThat(decisionNodeList).allSatisfy(decisionNode ->
                                         assertThat(model.getDecisionById(decisionNode.getId()))
                                                 .isNotNull()
                                                 .isEqualTo(decisionNode));
    }

    @Test
    void addDecisionImportedModel() {
        List<DecisionNode> decisionNodeList = new ArrayList<>();
        String importedNameSpace = "imported_namespace";
        Definitions definitions = getDefinitions(importedNameSpace);
        model.setImportAliasForNS(importedNameSpace, definitions.getNamespace(), definitions.getName());
        IntStream.range(0, 3).forEach(i -> {
            DecisionNode toAdd = getDecisionNode("id_" + i, "decision_" + i, definitions);
            model.addDecision(toAdd);
            decisionNodeList.add(toAdd);
        });

        assertThat(decisionNodeList).allSatisfy(decisionNode -> {
            assertThat(model.getDecisionByName(String.format("%s.%s", importedNameSpace, decisionNode.getName())))
                    .isNotNull()
                    .isEqualTo(decisionNode);
            assertThat(model.getDecisionById(String.format("%s#%s", importedNameSpace, decisionNode.getId())))
                    .isNotNull()
                    .isEqualTo(decisionNode);
        });
    }

    @Test
    void addDecisionWithoutIdImportedModel() {
        List<DecisionNode> decisionNodeList = new ArrayList<>();
        String importedNameSpace = "imported_namespace";
        Definitions definitions = getDefinitions(importedNameSpace);
        model.setImportAliasForNS(importedNameSpace, definitions.getNamespace(), definitions.getName());
        IntStream.range(0, 3).forEach(i -> {
            DecisionNode toAdd = getDecisionNode(null, "decision_" + i,definitions);
            model.addDecision(toAdd);
            decisionNodeList.add(toAdd);
        });

        assertThat(decisionNodeList).allSatisfy(decisionNode ->
                                         assertThat(model.getDecisionByName(String.format("%s.%s", importedNameSpace, decisionNode.getName())))
                                                 .isNotNull()
                                                 .isEqualTo(decisionNode));
    }

    @Test
    void addDecisionWithoutNameImportedModel() {
        List<DecisionNode> decisionNodeList = new ArrayList<>();
        String importedNameSpace = "imported_namespace";
        Definitions definitions = getDefinitions(importedNameSpace);
        model.setImportAliasForNS(importedNameSpace, definitions.getNamespace(), definitions.getName());
        IntStream.range(0, 3).forEach(i -> {
            DecisionNode toAdd = getDecisionNode("id_" + i, null, definitions);
            model.addDecision(toAdd);
            decisionNodeList.add(toAdd);
        });
        assertThat(decisionNodeList).allSatisfy(decisionNode ->
                                         assertThat(model.getDecisionById(String.format("%s#%s", importedNameSpace, decisionNode.getId())))
                                                 .isNotNull()
                                                 .isEqualTo(decisionNode));
    }

    @Test
    void testPopulateTopmostParents() {
        DMNModelImpl importingModel = mock(DMNModelImpl.class);
        DMNModelImpl model = mock(DMNModelImpl.class);

        when(model.getNamespace()).thenReturn("http://www.trisotech.com/definitions/_2a1d771a-a899-4fef-abd6-fc894332337A");
        when(model.getName()).thenReturn("Child_A");
        when(importingModel.getImportAliasFor("http://www.trisotech.com/definitions/_2a1d771a-a899-4fef-abd6-fc894332337A", "Child_A"))
                .thenReturn(Optional.of("ParentModel"));

        List<DMNModel> importChainDirectChildModels = List.of(model);

        Set<DMNModelImpl.ModelImportTuple> toPopulate = populateTopmostParents(importChainDirectChildModels, importingModel);

        assertThat(toPopulate)
                .hasSize(1)
                .first()
                .satisfies(tuple -> {
                    assertThat(tuple.getImportName()).isEqualTo("ParentModel");
                });
    }

    @Test
    void testPopulateTopmostParents_emptyImportChain() {
        DMNModelImpl importingModel = mock(DMNModelImpl.class);
        List<DMNModel> importChainDirectChildModels = List.of();

        Set<DMNModelImpl.ModelImportTuple> toPopulate = populateTopmostParents(importChainDirectChildModels, importingModel);
        assertThat(toPopulate).isEmpty();
    }

    @Test
    void testPopulateTopmostParents_multipleDirectChildren() {
        DMNModelImpl importingModel = mock(DMNModelImpl.class);
        DMNModelImpl childA = mock(DMNModelImpl.class);
        DMNModelImpl childB = mock(DMNModelImpl.class);

        when(childA.getNamespace()).thenReturn("http://child.a.namespace");
        when(childA.getName()).thenReturn("ChildA");
        when(importingModel.getImportAliasFor("http://child.a.namespace", "ChildA"))
                .thenReturn(Optional.of("aliasA"));

        when(childB.getNamespace()).thenReturn("http://child.b.namespace");
        when(childB.getName()).thenReturn("ChildB");
        when(importingModel.getImportAliasFor("http://child.b.namespace", "ChildB"))
                .thenReturn(Optional.of("aliasB"));

        List<DMNModel> importChainDirectChildModels = List.of(childA, childB);

        Set<DMNModelImpl.ModelImportTuple> result = populateTopmostParents(importChainDirectChildModels, importingModel);

        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        new DMNModelImpl.ModelImportTuple("aliasA", childA),
                        new DMNModelImpl.ModelImportTuple("aliasB", childB)
                );
    }

    @Test
    void testPopulateTopmostParents_missingImportAlias() {
        DMNModelImpl importingModel = mock(DMNModelImpl.class);
        DMNModelImpl model = mock(DMNModelImpl.class);

        when(model.getNamespace()).thenReturn("http://missing.alias.namespace");
        when(model.getName()).thenReturn("MissingAlias");
        when(importingModel.getImportAliasFor("http://missing.alias.namespace", "MissingAlias"))
                .thenReturn(Optional.empty());

        List<DMNModel> importChainDirectChildModels = List.of(model);

        assertThatThrownBy(() -> populateTopmostParents(importChainDirectChildModels, importingModel))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing import alias for model");
    }

    @Test
    void testPopulateTopmostParents_withMixedImports() {
        Definitions importingDefs = mock(Definitions.class);
        when(importingDefs.getNamespace()).thenReturn("http://importing.namespace");
        when(importingDefs.getName()).thenReturn("ImportingModel");
        DMNModelImpl importingModel = new DMNModelImpl(importingDefs);

        Definitions modelWithChildrenDefs = mock(Definitions.class);
        when(modelWithChildrenDefs.getNamespace()).thenReturn("http://parent.with.children");
        when(modelWithChildrenDefs.getName()).thenReturn("ParentWithChildren");
        DMNModelImpl modelWithChildren = new DMNModelImpl(modelWithChildrenDefs);

        Definitions leafDefs = mock(Definitions.class);
        when(leafDefs.getNamespace()).thenReturn("http://leaf.namespace");
        when(leafDefs.getName()).thenReturn("Leaf");
        DMNModelImpl leafModel = new DMNModelImpl(leafDefs);

        Definitions childDefs = mock(Definitions.class);
        when(childDefs.getNamespace()).thenReturn("http://child.namespace");
        when(childDefs.getName()).thenReturn("Child");
        DMNModelImpl childModel = new DMNModelImpl(childDefs);

        modelWithChildren.setImportAliasForNS("childAlias", "http://child.namespace", "Child");
        importingModel.setImportAliasForNS("parentAlias", "http://parent.with.children", "ParentWithChildren");
        importingModel.setImportAliasForNS("leafAlias", "http://leaf.namespace", "Leaf");

        DMNModelImpl.ImportChain childChain = new DMNModelImpl.ImportChain(childModel);
        modelWithChildren.addImportChainChild(childChain, "childAlias");

        DMNModelImpl.ImportChain modelWithChildrenChain = new DMNModelImpl.ImportChain(modelWithChildren);
        DMNModelImpl.ImportChain leafModelChain = new DMNModelImpl.ImportChain(leafModel);
        importingModel.addImportChainChild(modelWithChildrenChain, "parentAlias");
        importingModel.addImportChainChild(leafModelChain, "leafAlias");

        List<DMNModel> importChainDirectChildModels = List.of(modelWithChildren, leafModel);

        Set<DMNModelImpl.ModelImportTuple> result = populateTopmostParents(importChainDirectChildModels, importingModel);

        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        new DMNModelImpl.ModelImportTuple("childAlias", childModel),
                        new DMNModelImpl.ModelImportTuple("leafAlias", leafModel)
                );
    }

    @Test
    void testProcessImportedModel() {
        Definitions importingDefs = mock(Definitions.class);
        when(importingDefs.getNamespace()).thenReturn("ns1");
        when(importingDefs.getName()).thenReturn("model1");

        Definitions importedDefs = mock(Definitions.class);
        when(importedDefs.getNamespace()).thenReturn("ns2");
        when(importedDefs.getName()).thenReturn("model2");

        Definitions childDefs = mock(Definitions.class);
        when(childDefs.getNamespace()).thenReturn("ns3");
        when(childDefs.getName()).thenReturn("model3");

        DMNModelImpl importingModel = new DMNModelImpl(importingDefs);
        DMNModelImpl importedModel = new DMNModelImpl(importedDefs);
        DMNModelImpl childModel = new DMNModelImpl(childDefs);

        importingModel.setImportAliasForNS("alias2", "ns2", "model2");
        importedModel.setImportAliasForNS("alias3", "ns3", "model3");

        DMNModelImpl.ImportChain childChain = new DMNModelImpl.ImportChain(childModel);
        importedModel.addImportChainChild(childChain, "alias3");

        Set<DMNModelImpl.ModelImportTuple> result = DMNModelImpl.processImportedModel(importingModel, importedModel);

        assertThat(result.size()).isEqualTo(1);
        DMNModelImpl.ModelImportTuple tuple = result.iterator().next();
        assertThat(tuple.getImportName()).isEqualTo("alias3");
        assertThat(childModel).isEqualTo(tuple.getModel());
    }

    @Test
    void testGetImportNames() {
        DMNModelImpl importedModel = mock(DMNModelImpl.class);
        DMNModelImpl importingModel = mock(DMNModelImpl.class);

        when(importedModel.getNamespace()).thenReturn("http://www.trisotech.com/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9");
        when(importedModel.getName()).thenReturn("ParentModel");
        when(importingModel.getImportAliasFor(importedModel.getNamespace(), importedModel.getName())).thenReturn(Optional.of("parentModel"));

        String importName = getImportName(importedModel, importingModel);
        assertThat(importName).isEqualTo("parentModel");
    }

    @Test
    void testGetTopmostParentsEmptyImportChain() {
        Definitions defs = mock(Definitions.class);
        when(defs.getNamespace()).thenReturn("ns1");
        when(defs.getName()).thenReturn("model1");
        DMNModelImpl model = new DMNModelImpl(defs);

        Optional<Set<DMNModelImpl.ModelImportTuple>> result = model.getTopmostParents();
        assertThat(result).isEmpty();
    }

    @Test
    void testGetTopmostParentsWithImportChain() {
        Definitions importingDefs = mock(Definitions.class);
        when(importingDefs.getNamespace()).thenReturn("ns1");
        when(importingDefs.getName()).thenReturn("model1");
        DMNModelImpl importingModel = new DMNModelImpl(importingDefs);

        Definitions importedDefs = mock(Definitions.class);
        when(importedDefs.getNamespace()).thenReturn("ns2");
        when(importedDefs.getName()).thenReturn("model2");
        DMNModelImpl importedModel = new DMNModelImpl(importedDefs);

        importingModel.setImportAliasForNS("alias2", "ns2", "model2");

        DMNModelImpl.ImportChain importedChain = new DMNModelImpl.ImportChain(importedModel);
        importingModel.addImportChainChild(importedChain, "alias2");

        Optional<Set<DMNModelImpl.ModelImportTuple>> result = importingModel.getTopmostParents();
        assertThat(result).isPresent();
        Set<DMNModelImpl.ModelImportTuple> parents = result.get();
        assertThat(parents.size()).isEqualTo(1);
        DMNModelImpl.ModelImportTuple tuple = parents.iterator().next();
        assertThat(tuple.getImportName()).isEqualTo("alias2");
    }

    private Definitions getDefinitions(String nameSpace) {
        Definitions toReturn = new TDefinitions();
        toReturn.setNamespace(nameSpace);
        toReturn.setName("Definitions_" + nameSpace);
        return toReturn;
    }

    private DecisionNode getDecisionNode(String id, String name, Definitions parent) {
        Decision decision = getDecision(id, name, parent);
        return new DecisionNodeImpl(decision);
    }

    private Decision getDecision(String id, String name, Definitions parent) {
        Decision toReturn = new TDecision();
        toReturn.setId(id);
        toReturn.setName(name);
        toReturn.setParent(parent);
        return toReturn;
    }
}