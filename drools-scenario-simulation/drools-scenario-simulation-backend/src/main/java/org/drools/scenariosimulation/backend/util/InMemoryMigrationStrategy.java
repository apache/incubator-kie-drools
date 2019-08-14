/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scenariosimulation.backend.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.drools.scenariosimulation.backend.interfaces.ThrowingConsumer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class InMemoryMigrationStrategy implements MigrationStrategy {

    @Override
    public ThrowingConsumer<Document> from1_0to1_1() {
        return document -> {
            DOMParserUtil.replaceNodeText(document, "expressionIdentifier", "type", "EXPECTED", "EXPECT");
            updateVersion(document, "1.1");
        };
    }

    @Override
    public ThrowingConsumer<Document> from1_1to1_2() {
        return document -> {
            Map<Node, List<Node>> dmoSessionNodesMap = DOMParserUtil.getNestedChildrenNodesMap(document, "simulation", "simulationDescriptor", "dmoSession");
            Map<Node, List<Node>> dmnFilePathNodesMap = DOMParserUtil.getNestedChildrenNodesMap(document, "simulation", "simulationDescriptor", "dmnFilePath");
            Map<Node, List<Node>> typeNodesMap = DOMParserUtil.getNestedChildrenNodesMap(document, "simulation", "simulationDescriptor", "type");
            List<Node> dmoSessionNodes = dmoSessionNodesMap.values().iterator().next();
            List<Node> dmnFilePathNodes = dmnFilePathNodesMap.values().iterator().next();
            List<Node> typeNodes = typeNodesMap.values().iterator().next();
            if (!dmoSessionNodes.isEmpty() || (!dmnFilePathNodes.isEmpty() && !typeNodes.isEmpty())) {
                //
            } else {
                DOMParserUtil.createNestedNodes(document, "simulation", "simulationDescriptor", "dmoSession", null);
                DOMParserUtil.createNestedNodes(document, "simulation", "simulationDescriptor", "type", "RULE");
            }
            updateVersion(document, "1.2");
        };
    }

    @Override
    public ThrowingConsumer<Document> from1_2to1_3() {
        return document -> {
            List<Node> factMappingsNodes = DOMParserUtil.getNestedChildrenNodesList(document, "simulation", "simulationDescriptor", "factMappings");
            Node factMappingsNode = factMappingsNodes.get(0);
            final List<Node> factIdentifierNodeList = DOMParserUtil.getNestedChildrenNodesList(factMappingsNode, "FactMapping", "factIdentifier");
            factIdentifierNodeList.forEach(factIdentifierNode -> {
                List<Node> factIdentifierNameList = DOMParserUtil.getChildrenNodesList(factIdentifierNode, "name");
                if (!factIdentifierNameList.isEmpty()) {
                    String factIdentifierName = factIdentifierNameList.get(0).getTextContent();
                    Node factMappingNode = factIdentifierNode.getParentNode();
                    List<Node> expressionElementsNodeList = DOMParserUtil.getChildrenNodesList(factMappingNode, "expressionElements");
                    Node expressionElementsNode;
                    if (expressionElementsNodeList.isEmpty()) {
                        expressionElementsNode = DOMParserUtil.createNodeAtPosition(factMappingNode, "expressionElements", null, 0);
                    } else {
                        expressionElementsNode = expressionElementsNodeList.get(0);
                    }
                    Node expressionElementNode = DOMParserUtil.createNodeAtPosition(expressionElementsNode, "ExpressionElement", null, 0);
                    DOMParserUtil.createNodeAtPosition(expressionElementNode, "step", factIdentifierName, 0);
                }
            });
            updateVersion(document, "1.3");
        };
    }

    @Override
    public ThrowingConsumer<Document> from1_3to1_4() {
        return document -> {
            List<Node> typeNodes = DOMParserUtil.getNestedChildrenNodesList(document, "simulation", "simulationDescriptor", "type");
            if (!typeNodes.isEmpty()) {
                Node typeNode = typeNodes.get(0);
                Node simulationDescriptorNode = typeNode.getParentNode();
                switch (typeNode.getTextContent()) {
                    case "RULE":
                        if (DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "kieSession").isEmpty()) {
                            DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "kieSession", "default", null);
                        }
                        if (DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "kieBase").isEmpty()) {
                            DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "kieBase", "default", null);
                        }
                        if (DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "ruleFlowGroup").isEmpty()) {
                            DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "ruleFlowGroup", "default", null);
                        }
                        break;
                    case "DMN":
                        if (DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "dmnNamespace").isEmpty()) {
                            DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "dmnNamespace", null, null);
                        }
                        if (DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "dmnName").isEmpty()) {
                            DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "dmnName", null, null);
                        }
                        break;
                    default:
                }
                if (DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "skipFromBuild").isEmpty()) {
                    DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "skipFromBuild", "false", null);
                }
                if (DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "fileName").isEmpty()) {
                    DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "fileName", null, null);
                }
            }
            updateVersion(document, "1.4");
        };
    }

    @Override
    public ThrowingConsumer<Document> from1_4to1_5() {
        return document -> {
            Node simulationDescriptorNode = DOMParserUtil.getNestedChildrenNodesList(document, "ScenarioSimulationModel", "simulation", "simulationDescriptor").get(0);
            List<Node> dmoSessionNodesList = DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "dmoSession");
            if (dmoSessionNodesList.isEmpty()) {
                DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "dmoSession", null, null);
            } else {
                Node dmoSessionNode = dmoSessionNodesList.get(0);
                if (Objects.equals("default", dmoSessionNode.getTextContent()) || Objects.equals("", dmoSessionNode.getTextContent())) {
                    simulationDescriptorNode.removeChild(dmoSessionNode);
                }
            }
            updateVersion(document, "1.5");
        };
    }

    @Override
    public ThrowingConsumer<Document> from1_5to1_6() {
        return document -> {
            DOMParserUtil.cleanupNodes(document, "Scenario", "simulationDescriptor");
            List<Node> simulationFactMappingNodeList = DOMParserUtil.getNestedChildrenNodesList(document, "simulationDescriptor", "factMappings", "FactMapping");
            for (Node simulationFactMapping : simulationFactMappingNodeList) {
                replaceReference(simulationFactMappingNodeList, simulationFactMapping, "factIdentifier");
            }
            List<Node> scenarioFactMappingValueNodeList = DOMParserUtil.getNestedChildrenNodesList(document, "Scenario", "factMappingValues", "FactMappingValue");
            scenarioFactMappingValueNodeList.forEach(scenarioFactMappingValue -> {
                replaceReference(simulationFactMappingNodeList, scenarioFactMappingValue, "factIdentifier");
                replaceReference(simulationFactMappingNodeList, scenarioFactMappingValue, "expressionIdentifier");
            });
            updateVersion(document, "1.6");
        };
    }

    private void replaceReference(List<Node> simulationFactMappingNodeList, Node containerNode, String referredNodeName) {
        final List<Node> referredNodesList = DOMParserUtil.getChildrenNodesList(containerNode, referredNodeName);
        if (!referredNodesList.isEmpty()) {
            Node referringNode = referredNodesList.get(0);
            String referenceAttribute = DOMParserUtil.getAttributeValue(referringNode, "reference");
            if (referenceAttribute != null) {
                String referredIndex = "1";
                if (referenceAttribute.contains("[") && referenceAttribute.contains("]")) {
                    referredIndex = referenceAttribute.substring(referenceAttribute.indexOf("[") + 1, referenceAttribute.indexOf("]"));
                }
                int index = Integer.parseInt(referredIndex) - 1;
                Node referredFactMapping = simulationFactMappingNodeList.get(index);
                Node referredNode = DOMParserUtil.getChildrenNodesList(referredFactMapping, referredNodeName).get(0);
                Node clonedNode = referredNode.cloneNode(true);
                containerNode.replaceChild(clonedNode, referringNode);
            }
        }
    }
}
