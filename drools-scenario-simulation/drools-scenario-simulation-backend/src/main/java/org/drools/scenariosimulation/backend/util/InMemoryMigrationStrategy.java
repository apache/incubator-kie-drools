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

import static org.drools.scenariosimulation.backend.util.ScenarioSimulationXMLPersistence.getColumnWidth;

public class InMemoryMigrationStrategy implements MigrationStrategy {

    private static final String DMO_SESSION_NODE = "dmoSession";
    private static final String EXPRESSION_IDENTIFIER_NODE = "expressionIdentifier";
    private static final String FACT_IDENTIFIER = "factIdentifier";
    private static final String FACT_MAPPING_NODE = "FactMapping";
    private static final String FACT_MAPPINGS_NODE = "factMappings";
    private static final String SIMULATION_NODE = "simulation";
    private static final String SIMULATION_DESCRIPTOR_NODE = "simulationDescriptor";

    @Override
    public ThrowingConsumer<Document> from1_0to1_1() {
        return document -> {
            DOMParserUtil.replaceNodeText(document, EXPRESSION_IDENTIFIER_NODE, "type", "EXPECTED", "EXPECT");
            updateVersion(document, "1.1");
        };
    }

    @Override
    public ThrowingConsumer<Document> from1_1to1_2() {
        return document -> {
            Map<Node, List<Node>> dmoSessionNodesMap = DOMParserUtil.getNestedChildrenNodesMap(document, SIMULATION_NODE, SIMULATION_DESCRIPTOR_NODE, DMO_SESSION_NODE);
            Map<Node, List<Node>> dmnFilePathNodesMap = DOMParserUtil.getNestedChildrenNodesMap(document, SIMULATION_NODE, SIMULATION_DESCRIPTOR_NODE, "dmnFilePath");
            Map<Node, List<Node>> typeNodesMap = DOMParserUtil.getNestedChildrenNodesMap(document, SIMULATION_NODE, SIMULATION_DESCRIPTOR_NODE, "type");
            List<Node> dmoSessionNodes = dmoSessionNodesMap.values().iterator().next();
            List<Node> dmnFilePathNodes = dmnFilePathNodesMap.values().iterator().next();
            List<Node> typeNodes = typeNodesMap.values().iterator().next();
            if (!dmoSessionNodes.isEmpty() || (!dmnFilePathNodes.isEmpty() && !typeNodes.isEmpty())) {
                //
            } else {
                DOMParserUtil.createNestedNodes(document, SIMULATION_NODE, SIMULATION_DESCRIPTOR_NODE, DMO_SESSION_NODE, null);
                DOMParserUtil.createNestedNodes(document, SIMULATION_NODE, SIMULATION_DESCRIPTOR_NODE, "type", "RULE");
            }
            updateVersion(document, "1.2");
        };
    }

    @Override
    public ThrowingConsumer<Document> from1_2to1_3() {
        return document -> {
            List<Node> factMappingsNodes = DOMParserUtil.getNestedChildrenNodesList(document, SIMULATION_NODE, SIMULATION_DESCRIPTOR_NODE, FACT_MAPPINGS_NODE);
            Node factMappingsNode = factMappingsNodes.get(0);
            final List<Node> factIdentifierNodeList = DOMParserUtil.getNestedChildrenNodesList(factMappingsNode, FACT_MAPPING_NODE, FACT_IDENTIFIER);
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
            List<Node> typeNodes = DOMParserUtil.getNestedChildrenNodesList(document, SIMULATION_NODE, SIMULATION_DESCRIPTOR_NODE, "type");
            if (!typeNodes.isEmpty()) {
                Node typeNode = typeNodes.get(0);
                Node simulationDescriptorNode = typeNode.getParentNode();
                String defaultContent = "default";
                switch (typeNode.getTextContent()) {
                    case "RULE":
                        if (DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "kieSession").isEmpty()) {
                            DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "kieSession", defaultContent, null);
                        }
                        if (DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "kieBase").isEmpty()) {
                            DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "kieBase", defaultContent, null);
                        }
                        if (DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, "ruleFlowGroup").isEmpty()) {
                            DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, "ruleFlowGroup", defaultContent, null);
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
            Node simulationDescriptorNode = DOMParserUtil.getNestedChildrenNodesList(document, "ScenarioSimulationModel", SIMULATION_NODE, SIMULATION_DESCRIPTOR_NODE).get(0);
            List<Node> dmoSessionNodesList = DOMParserUtil.getChildrenNodesList(simulationDescriptorNode, DMO_SESSION_NODE);
            if (dmoSessionNodesList.isEmpty()) {
                DOMParserUtil.createNodeAtPosition(simulationDescriptorNode, DMO_SESSION_NODE, null, null);
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
            DOMParserUtil.cleanupNodes(document, "Scenario", SIMULATION_DESCRIPTOR_NODE);
            List<Node> simulationFactMappingNodeList = DOMParserUtil.getNestedChildrenNodesList(document, SIMULATION_DESCRIPTOR_NODE, FACT_MAPPINGS_NODE, FACT_MAPPING_NODE);
            for (Node simulationFactMapping : simulationFactMappingNodeList) {
                replaceReference(simulationFactMappingNodeList, simulationFactMapping, FACT_IDENTIFIER);
            }
            List<Node> scenarioFactMappingValueNodeList = DOMParserUtil.getNestedChildrenNodesList(document, "Scenario", "factMappingValues", "FactMappingValue");
            scenarioFactMappingValueNodeList.forEach(scenarioFactMappingValue -> {
                replaceReference(simulationFactMappingNodeList, scenarioFactMappingValue, FACT_IDENTIFIER);
                replaceReference(simulationFactMappingNodeList, scenarioFactMappingValue, EXPRESSION_IDENTIFIER_NODE);
            });
            updateVersion(document, "1.6");
        };
    }

    @Override
    public ThrowingConsumer<Document> from1_6to1_7() {
        return document -> {
            final List<Node> factMappingNodeList = DOMParserUtil.getNestedChildrenNodesList(document, SIMULATION_DESCRIPTOR_NODE, FACT_MAPPINGS_NODE, FACT_MAPPING_NODE);
            factMappingNodeList.forEach(factMappingNode -> {
                List<Node> expressionIdentifierNamesNodes = DOMParserUtil.getNestedChildrenNodesList(factMappingNode, EXPRESSION_IDENTIFIER_NODE, "name");
                String expressionIdentifierName = expressionIdentifierNamesNodes.get(0).getTextContent();
                DOMParserUtil.createNodeAtPosition(factMappingNode, "columnWidth", Double.toString(getColumnWidth(expressionIdentifierName)), null);
            });
            updateVersion(document, "1.7");
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
                    referredIndex = referenceAttribute.substring(referenceAttribute.indexOf('[') + 1, referenceAttribute.indexOf(']'));
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
