/**
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
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.v1_5.TDecision;
import org.kie.dmn.model.v1_5.TDefinitions;

import static org.assertj.core.api.Assertions.assertThat;

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

        decisionNodeList.forEach(decisionNode -> {
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

        decisionNodeList.forEach(decisionNode ->
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
        decisionNodeList.forEach(decisionNode ->
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

        decisionNodeList.forEach(decisionNode -> {
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

        decisionNodeList.forEach(decisionNode ->
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
        decisionNodeList.forEach(decisionNode ->
                                         assertThat(model.getDecisionById(String.format("%s#%s", importedNameSpace, decisionNode.getId())))
                                                 .isNotNull()
                                                 .isEqualTo(decisionNode));
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