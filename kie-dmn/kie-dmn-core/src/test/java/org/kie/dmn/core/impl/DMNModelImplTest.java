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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.v1_5.TDecision;
import org.kie.dmn.model.v1_5.TDefinitions;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.impl.DMNModelImpl.populateTopmostParentsCollection;
import static org.kie.dmn.core.impl.DMNModelImpl.processImportedModel;

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
    void testGetImportNames() {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_A.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ParentModel.dmn")
        );

        DMNRuntime dmnRuntime =
                DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel importingModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_2a1d771a-a899-4fef-abd6-fc894332337A", "Child_A");
        DMNModel importedModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9", "ParentModel");

        String importName = DMNModelImpl.getImportName((DMNModelImpl) importedModel, (DMNModelImpl) importingModel);
        assertThat(importName).isEqualTo("parentModel");
    }

    @Test
    void testGetTopmostParents() {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ImportingNestedInputData.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ParentModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_A.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_B.dmn")
        );

        DMNRuntime dmnRuntime =
                DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel dmnModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_10435dcd-8774-4575-a338" +
        "-49dd554a0928", "ImportingNestedInputData");

        DMNModelImpl modelImpl = (DMNModelImpl) dmnModel;
        Optional<Set<DMNModelImpl.ModelImportTuple>> result = modelImpl.getTopmostParents();

        assertThat(result).isPresent();
        assertThat(result.orElseThrow())
                .extracting(t -> t.getModel().getName())
                .contains("ParentModel");
    }

    @Test
    void testPopulateTopmostParentsCollection() {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ImportingNestedInputData.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ParentModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_A.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_B.dmn")
        );

        DMNRuntime dmnRuntime =
                DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel dmnModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_10435dcd-8774-4575-a338" +
                "-49dd554a0928", "ImportingNestedInputData");

        DMNModelImpl importingModel = (DMNModelImpl) dmnModel;
        Set<DMNModelImpl.ModelImportTuple> toPopulate = new HashSet<>();
        List<DMNModel> importChainDirectChildModels = ((DMNModelImpl) dmnModel).getImportChainDirectChildModels();
        populateTopmostParentsCollection(toPopulate, importChainDirectChildModels, importingModel);
        assertThat(toPopulate)
                .extracting(t -> t.getModel().getName())
                .contains("ParentModel");
    }

    @Test
    void testProcessImportedModel() {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ParentModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_A.dmn")
        );

        DMNRuntime dmnRuntime =
                DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel importingModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_2a1d771a-a899-4fef-abd6-fc894332337A", "Child_A");
        DMNModel importedModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9", "ParentModel");

        Set<DMNModelImpl.ModelImportTuple> toPopulate = new HashSet<>();
        processImportedModel(toPopulate, (DMNModelImpl) importingModel, (DMNModelImpl) importedModel);
        assertThat(toPopulate)
                .extracting(t -> t.getModel().getName())
                .contains("ParentModel");
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