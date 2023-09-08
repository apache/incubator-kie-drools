/**
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
package org.drools.scenariosimulation.backend.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.assertj.core.api.Assertions.assertThat;

public class DOMParserUtilTest {

    private static final String MAIN_NODE = "Main";
    private static final String MAIN_ATTRIBUTE_NAME = "mainattribute";
    private static final String ATTRIBUTE_VALUE = "default";
    private static final String TEST_NODE = "testnode";
    private static final String TEST_NODE_CONTENT = "testnodecontent";
    private static final String TEST_NODE_TOREMOVE_1 = "toremove1";
    private static final String TEST_NODE_TOREMOVE_2 = "toremove2";
    private static final String CHILD_NODE = "child";
    private static final String CHILD_ATTRIBUTE_NAME = "childattribute";
    private static final String OTHER_NODE = "othernode";
    private static final String OTHER_NODE_CONTENT_1 = "othernodecontent1";
    private static final String OTHER_NODE_CONTENT_2 = "othernodecontent2";
    private static final String NOT_EXISTING = "NOT_EXISTING";
    private static final String NESTING_NODE = "nesting";
    private static final String NESTED_NODE = "nested";

    private static final String XML = "<" + MAIN_NODE + " " + MAIN_ATTRIBUTE_NAME + " =\"" + ATTRIBUTE_VALUE + "\">" +
            "<" + TEST_NODE + ">" + TEST_NODE_CONTENT + "</" + TEST_NODE + ">" +
            "<" + CHILD_NODE + " " + CHILD_ATTRIBUTE_NAME + " =\"" + ATTRIBUTE_VALUE + "\">" +
            "<" + TEST_NODE + ">" + TEST_NODE_TOREMOVE_1 + "</" + TEST_NODE + ">" +
            "<" + OTHER_NODE + ">" + OTHER_NODE_CONTENT_1 + "</" + OTHER_NODE + ">" +
            "<" + NESTING_NODE + ">" +
            "<" + NESTED_NODE + "/>" +
            "</" + NESTING_NODE + ">" +
            "</" + CHILD_NODE + ">" +
            "<" + CHILD_NODE + " " + CHILD_ATTRIBUTE_NAME + " =\"" + ATTRIBUTE_VALUE + "\">" +
            "<" + TEST_NODE + ">" + TEST_NODE_TOREMOVE_2 + "</" + TEST_NODE + ">" +
            "<" + OTHER_NODE + ">" + OTHER_NODE_CONTENT_2 + "</" + OTHER_NODE + ">" +
            "<" + NESTING_NODE + ">" +
            "<" + NESTED_NODE + "/>" +
            "</" + NESTING_NODE + ">" +
            "</" + CHILD_NODE + ">" +
            "</" + MAIN_NODE + ">";

    @Test
    public void cleanupNodesString() throws Exception {
        String retrieved = DOMParserUtil.cleanupNodes(XML, CHILD_NODE, TEST_NODE);
        assertThat(retrieved).isNotNull();
        Map<Node, List<Node>> childrenNodes = DOMParserUtil.getChildrenNodesMap(retrieved, MAIN_NODE, TEST_NODE);
        assertThat(childrenNodes).isNotNull();
        assertThat(childrenNodes).hasSize(1);
        Node keyNode = childrenNodes.keySet().iterator().next();
        assertThat(keyNode.getNodeName()).isEqualTo(MAIN_NODE);
        List<Node> valueNodes = childrenNodes.get(keyNode);
        assertThat(valueNodes).isNotNull().hasSize(1);
        assertThat(valueNodes.get(0).getNodeName()).isEqualTo(TEST_NODE);

        childrenNodes = DOMParserUtil.getChildrenNodesMap(retrieved, CHILD_NODE, OTHER_NODE);
        assertThat(childrenNodes).hasSize(2);
        
        childrenNodes.forEach((childKeyNode, childValueNodes) -> {
            assertThat(childKeyNode).isNotNull();
            assertThat(childKeyNode.getNodeName()).isEqualTo(CHILD_NODE);
            assertThat(childValueNodes).isNotNull().hasSize(1);
            assertThat(childValueNodes.get(0).getNodeName()).isEqualTo(OTHER_NODE);
        });

        childrenNodes = DOMParserUtil.getChildrenNodesMap(retrieved, CHILD_NODE, TEST_NODE);
        childrenNodes.forEach((childKeyNode, childValueNodes) -> {
            assertThat(childKeyNode).isNotNull();
            assertThat(childKeyNode.getNodeName()).isEqualTo(CHILD_NODE);
            assertThat(childValueNodes).isNotNull().isEmpty();
        });
    }

    @Test
    public void cleanupNodesDocument() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        DOMParserUtil.cleanupNodes(document, CHILD_NODE, TEST_NODE);
        assertThat(document).isNotNull();
        Map<Node, List<Node>> childrenNodes = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, TEST_NODE);
        assertThat(childrenNodes).isNotNull();
        assertThat(childrenNodes).hasSize(1);
        Node keyNode = childrenNodes.keySet().iterator().next();
        assertThat(keyNode.getNodeName()).isEqualTo(MAIN_NODE);
        List<Node> valueNodes = childrenNodes.get(keyNode);
        assertThat(valueNodes).isNotNull().hasSize(1);
        assertThat(valueNodes.get(0).getNodeName()).isEqualTo(TEST_NODE);

        childrenNodes = DOMParserUtil.getChildrenNodesMap(document, CHILD_NODE, OTHER_NODE);
        assertThat(childrenNodes).hasSize(2);
        childrenNodes.forEach((childKeyNode, childValueNodes) -> {
            assertThat(childKeyNode).isNotNull();
            assertThat(childKeyNode.getNodeName()).isEqualTo(CHILD_NODE);
            assertThat(childValueNodes).isNotNull().hasSize(1);
            assertThat(childValueNodes.get(0).getNodeName()).isEqualTo(OTHER_NODE);
        });

        childrenNodes = DOMParserUtil.getChildrenNodesMap(document, CHILD_NODE, TEST_NODE);
        childrenNodes.forEach((childKeyNode, childValueNodes) -> {
            assertThat(childKeyNode).isNotNull();
            assertThat(childKeyNode.getNodeName()).isEqualTo(CHILD_NODE);
            assertThat(childValueNodes).isNotNull().isEmpty();
        });
    }

    @Test
    public void replaceNodeText() throws Exception {
        final String replacement = "replacement";
        Document document = DOMParserUtil.getDocument(XML);
        DOMParserUtil.replaceNodeText(document, MAIN_NODE, TEST_NODE, TEST_NODE_CONTENT, replacement);
        final Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, TEST_NODE);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        List<Node> testNodes = retrieved.values().iterator().next();
        assertThat(testNodes).isNotNull();
        assertThat(testNodes).hasSize(1);
        assertThat(testNodes.get(0).getTextContent()).isEqualTo(replacement);
    }

    @Test
    public void replaceNodeName() throws Exception {
        final String replacement = "replacement";
        Document document = DOMParserUtil.getDocument(XML);
        
        DOMParserUtil.replaceNodeName(document, MAIN_NODE, TEST_NODE, replacement);
        
        final Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, replacement);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        
        List<Node> testNodes = retrieved.values().iterator().next();
        assertThat(testNodes).isNotNull();
        assertThat(testNodes).hasSize(1);
        assertThat(testNodes.get(0).getNodeName()).isEqualTo("replacement");
    }

    @Test
    public void getAttributeValuesByNode() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);

        Map<Node, String> retrieved = DOMParserUtil.getAttributeValues(document, MAIN_NODE, MAIN_ATTRIBUTE_NAME);
        
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        assertThat(retrieved.values().toArray()[0]).isEqualTo(ATTRIBUTE_VALUE);
        
        retrieved = DOMParserUtil.getAttributeValues(document, MAIN_NODE, NOT_EXISTING);
        
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();
        
        retrieved = DOMParserUtil.getAttributeValues(document, CHILD_NODE, CHILD_ATTRIBUTE_NAME);
        
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(2);
        retrieved.values().forEach(attributeValue -> assertThat(attributeValue).isEqualTo(ATTRIBUTE_VALUE));
        
        retrieved = DOMParserUtil.getAttributeValues(document, CHILD_NODE, NOT_EXISTING);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();
    }

    @Test
    public void getAllAttributeValues() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, String> retrieved = DOMParserUtil.getAttributeValues(document, MAIN_ATTRIBUTE_NAME);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        assertThat(retrieved.values().toArray()[0]).isEqualTo(ATTRIBUTE_VALUE);
        retrieved = DOMParserUtil.getAttributeValues(document, CHILD_ATTRIBUTE_NAME);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(2);
        retrieved.values().forEach(attributeValue -> assertThat(attributeValue).isEqualTo(ATTRIBUTE_VALUE));
    }

    @Test
    public void setAttributeValue() throws Exception {
        final String newValue = "NEW_VALUE";
        Document document = DOMParserUtil.getDocument(XML);
        DOMParserUtil.setAttributeValue(document, MAIN_NODE, MAIN_ATTRIBUTE_NAME, newValue);
        Map<Node, String> retrieved = DOMParserUtil.getAttributeValues(document, MAIN_NODE, MAIN_ATTRIBUTE_NAME);
        assertThat(newValue).isEqualTo(retrieved.values().toArray()[0]);
        DOMParserUtil.setAttributeValue(document, MAIN_NODE, NOT_EXISTING, newValue);
        retrieved = DOMParserUtil.getAttributeValues(document, MAIN_NODE, NOT_EXISTING);
        assertThat(retrieved).isEmpty();
        DOMParserUtil.setAttributeValue(document, CHILD_NODE, CHILD_ATTRIBUTE_NAME, newValue);
        retrieved = DOMParserUtil.getAttributeValues(document, CHILD_NODE, CHILD_ATTRIBUTE_NAME);
        assertThat(retrieved).hasSize(2);
        retrieved.values().forEach(attributeValue -> assertThat(attributeValue).isEqualTo(newValue));
    }

    @Test
    public void createNodes() throws Exception {
        final String newNodeName = "NEW_NODE_NAME";
        final String newNodeValue = "NEW_NODE_VALUE";
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, Node> retrieved = DOMParserUtil.createNodes(document, MAIN_NODE, newNodeName, newNodeValue);
        assertThat(retrieved).hasSize(1);
        Node created = (Node) retrieved.values().toArray()[0];
        assertThat(created).isNotNull();
        assertThat(created.getNodeName()).isEqualTo(newNodeName);
        assertThat(created.getTextContent()).isEqualTo(newNodeValue);
        retrieved = DOMParserUtil.createNodes(document, MAIN_NODE, newNodeName, null);
        assertThat(retrieved).hasSize(1);
        created = (Node) retrieved.values().toArray()[0];
        assertThat(created).isNotNull();
        assertThat(created.getNodeName()).isEqualTo(newNodeName);
        assertThat(created.getTextContent()).isEmpty();

        retrieved = DOMParserUtil.createNodes(document, CHILD_NODE, newNodeName, newNodeValue);
        assertThat(retrieved).hasSize(2);
        retrieved.forEach((key, createdNode) -> {
            assertThat(createdNode).isNotNull();
            assertThat(createdNode.getNodeName()).isEqualTo(newNodeName);
            assertThat(createdNode.getTextContent()).isEqualTo(newNodeValue);
        });
        retrieved = DOMParserUtil.createNodes(document, CHILD_NODE, newNodeName, null);
        assertThat(retrieved).hasSize(2);
        retrieved.forEach((key, createdNode) -> {
            assertThat(createdNode).isNotNull();
            assertThat(createdNode.getNodeName()).isEqualTo(newNodeName);
            assertThat(createdNode.getTextContent()).isEmpty();
        });
    }

    @Test
    public void createNestedNodes() throws Exception {
        final String newNodeName = "NEW_NODE_NAME";
        final String newNodeValue = "NEW_NODE_VALUE";
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, Node> retrieved = DOMParserUtil.createNestedNodes(document, MAIN_NODE, TEST_NODE, newNodeName, newNodeValue);
        assertThat(retrieved).hasSize(1);
        Node created = (Node) retrieved.values().toArray()[0];
        assertThat(created).isNotNull();
        assertThat(created.getNodeName()).isEqualTo(newNodeName);
        assertThat(created.getTextContent()).isEqualTo(newNodeValue);
        retrieved = DOMParserUtil.createNestedNodes(document, MAIN_NODE, TEST_NODE, newNodeName, null);
        assertThat(retrieved).hasSize(1);
        created = (Node) retrieved.values().toArray()[0];
        assertThat(created).isNotNull();
        assertThat(created.getNodeName()).isEqualTo(newNodeName);
        assertThat(created.getTextContent()).isEmpty();

        retrieved = DOMParserUtil.createNestedNodes(document, MAIN_NODE, CHILD_NODE, newNodeName, newNodeValue);
        assertThat(retrieved).hasSize(2);
        retrieved.forEach((key, createdNode) -> {
            assertThat(createdNode).isNotNull();
            assertThat(createdNode.getNodeName()).isEqualTo(newNodeName);
            assertThat(createdNode.getTextContent()).isEqualTo(newNodeValue);
        });
        retrieved = DOMParserUtil.createNestedNodes(document, MAIN_NODE, CHILD_NODE, newNodeName, null);
        assertThat(retrieved).hasSize(2);
        retrieved.forEach((key, createdNode) -> {
            assertThat(createdNode).isNotNull();
            assertThat(createdNode.getNodeName()).isEqualTo(newNodeName);
            assertThat(createdNode.getTextContent()).isEmpty();
        });
    }

    @Test
    public void createNodeAtPosition() throws Exception {
        String newNodeName = "NEW_NODE_NAME_0";
        String newNodeValue = "NEW_NODE_VALUE_=";
        Document document = DOMParserUtil.getDocument(XML);
        
        Map<Node, List<Node>> testNodesMap = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, TEST_NODE);
        assertThat(testNodesMap).hasSize(1);
        
        Node mainNode = testNodesMap.keySet().iterator().next();
        Node retrieved = DOMParserUtil.createNodeAtPosition(mainNode, newNodeName, newNodeValue, null);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getNodeName()).isEqualTo(newNodeName);
        assertThat(retrieved.getTextContent()).isEqualTo(newNodeValue);
        assertThat(mainNode.getChildNodes().item(mainNode.getChildNodes().getLength() - 1)).isEqualTo(retrieved);
        
        newNodeName = "NEW_NODE_NAME_1";
        newNodeValue = "NEW_NODE_VALUE_1";
        retrieved = DOMParserUtil.createNodeAtPosition(mainNode, newNodeName, newNodeValue, 0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getNodeName()).isEqualTo(newNodeName);
        assertThat(retrieved.getTextContent()).isEqualTo(newNodeValue);
        assertThat(mainNode.getChildNodes().item(0)).isEqualTo(retrieved);
        
        newNodeName = "NEW_NODE_NAME_2";
        newNodeValue = "NEW_NODE_VALUE_2";
        retrieved = DOMParserUtil.createNodeAtPosition(mainNode, newNodeName, newNodeValue, 2);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getNodeName()).isEqualTo(newNodeName);
        assertThat(retrieved.getTextContent()).isEqualTo(newNodeValue);
        assertThat(mainNode.getChildNodes().item(2)).isEqualTo(retrieved);
    }

    @Test
    public void createNodeAndAppend() throws Exception {
        String newNodeName0 = "NEW_NODE_NAME_0";
        String newNodeValue0 = "NEW_NODE_VALUE_=";
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, List<Node>> testNodesMap = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, TEST_NODE);
        assertThat(testNodesMap).hasSize(1);
        
        Node mainNode = testNodesMap.keySet().iterator().next();
        int startingChildNodes = mainNode.getChildNodes().getLength();
        
        Node retrieved = DOMParserUtil.createNodeAndAppend(mainNode, newNodeName0, newNodeValue0);
        
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getNodeName()).isEqualTo(newNodeName0);
        assertThat(retrieved.getTextContent()).isEqualTo(newNodeValue0);
        assertThat(mainNode.getChildNodes().item(mainNode.getChildNodes().getLength() - 1)).isEqualTo(retrieved);
        assertThat(mainNode.getChildNodes().getLength()).isEqualTo(startingChildNodes + 1);
        String newNodeName1 = "NEW_NODE_NAME_1";
        String newNodeValue1 = "NEW_NODE_VALUE_1";
        
        retrieved = DOMParserUtil.createNodeAndAppend(mainNode, newNodeName1, newNodeValue1);
        
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getNodeName()).isEqualTo(newNodeName1);
        assertThat(retrieved.getTextContent()).isEqualTo(newNodeValue1);
        assertThat(mainNode.getChildNodes().item(mainNode.getChildNodes().getLength() - 1)).isEqualTo(retrieved);
        assertThat(mainNode.getChildNodes().getLength()).isEqualTo(startingChildNodes + 2);
        String newNodeName2 = "NEW_NODE_NAME_2";
        String newNodeValue2 = "NEW_NODE_VALUE_2";

        retrieved = DOMParserUtil.createNodeAndAppend(mainNode, newNodeName2, newNodeValue2);
        
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getNodeName()).isEqualTo(newNodeName2);
        assertThat(retrieved.getTextContent()).isEqualTo(newNodeValue2);
        assertThat(mainNode.getChildNodes().item(mainNode.getChildNodes().getLength() - 1)).isEqualTo(retrieved);
        assertThat(mainNode.getChildNodes().getLength()).isEqualTo(startingChildNodes + 3);
        assertThat(mainNode.getChildNodes().item(startingChildNodes).getNodeName()).isEqualTo(newNodeName0);
        assertThat(mainNode.getChildNodes().item(startingChildNodes).getTextContent()).isEqualTo(newNodeValue0);
        assertThat(mainNode.getChildNodes().item(startingChildNodes + 1).getNodeName()).isEqualTo(newNodeName1);
        assertThat(mainNode.getChildNodes().item(startingChildNodes + 1).getTextContent()).isEqualTo(newNodeValue1);
        assertThat(mainNode.getChildNodes().item(startingChildNodes + 2).getNodeName()).isEqualTo(newNodeName2);
        assertThat(mainNode.getChildNodes().item(startingChildNodes + 2).getTextContent()).isEqualTo(newNodeValue2);
    }

    @Test
    public void getChildrenNodesFromDocument() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, TEST_NODE);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        Node keyNode = retrieved.keySet().iterator().next();
        assertThat(keyNode).isNotNull();
        assertThat(keyNode.getNodeName()).isEqualTo(MAIN_NODE);
        List<Node> valueNodes = retrieved.get(keyNode);
        assertThat(valueNodes).isNotNull().hasSize(1);
        assertThat(valueNodes.get(0).getNodeName()).isEqualTo(TEST_NODE);
        
        retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, NOT_EXISTING);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        assertThat(retrieved.values().iterator().next()).isEmpty();
        
        retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, CHILD_NODE);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        keyNode = retrieved.keySet().iterator().next();
        assertThat(keyNode).isNotNull();
        assertThat(keyNode.getNodeName()).isEqualTo(MAIN_NODE);
        valueNodes = retrieved.get(keyNode);
        assertThat(valueNodes).isNotNull().hasSize(2);
        assertThat(valueNodes).extracting(x -> x.getNodeName()).containsExactly(CHILD_NODE, CHILD_NODE);
        
        List<String> nodeToTest = List.of(TEST_NODE, OTHER_NODE);
        for (String childNodeName : nodeToTest) {
            retrieved = DOMParserUtil.getChildrenNodesMap(XML, CHILD_NODE, childNodeName);
            assertThat(retrieved).isNotNull();
            assertThat(retrieved).hasSize(2);
            retrieved.forEach((childKeyNode, childValueNodes) -> {
                assertThat(childKeyNode).isNotNull();
                assertThat(childKeyNode.getNodeName()).isEqualTo(CHILD_NODE);
                assertThat(childValueNodes).isNotNull().hasSize(1);
                assertThat(childValueNodes.get(0).getNodeName()).isEqualTo(childNodeName);
            });
        }
    }

    @Test
    public void getChildrenNodesFromNode() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, CHILD_NODE);
        
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        
        Node mainNode = (Node) retrieved.keySet().toArray()[0];
        
        assertThat(mainNode.getNodeName()).isEqualTo(MAIN_NODE);
        
        List<Node> nodes = retrieved.get(mainNode);
        nodes.forEach(childNode -> assertThat(childNode.getNodeName()).isEqualTo(CHILD_NODE));
        retrieved = DOMParserUtil.getChildrenNodesMap(nodes.get(0), NESTING_NODE, NESTED_NODE);

        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        
        Node childNode = (Node) retrieved.keySet().toArray()[0];
        
        assertThat(childNode.getNodeName()).isEqualTo(NESTING_NODE);
        
        nodes = retrieved.get(childNode);
        assertThat(nodes).isNotNull();
        assertThat(nodes).hasSize(1);
        assertThat(nodes.get(0).getNodeName()).isEqualTo(NESTED_NODE);
    }

    @Test
    public void getNestedChildrenNodesMap() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, List<Node>> retrieved = DOMParserUtil.getNestedChildrenNodesMap(document, MAIN_NODE, CHILD_NODE, TEST_NODE);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(2);
        retrieved.forEach((childNode, testNodes) -> {
            assertThat(childNode.getNodeName()).isEqualTo(CHILD_NODE);
            assertThat(testNodes).hasSize(1);
            assertThat(testNodes.get(0).getNodeName()).isEqualTo(TEST_NODE);
        });
        retrieved = DOMParserUtil.getNestedChildrenNodesMap(document, CHILD_NODE, NESTING_NODE, NESTED_NODE);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(2);
        retrieved.forEach((nestingNode, nestedNodes) -> {
        	assertThat(nestingNode.getNodeName()).isEqualTo(NESTING_NODE);
        	assertThat(nestedNodes).hasSize(1);
        	assertThat(nestedNodes.get(0).getNodeName()).isEqualTo(NESTED_NODE);
        });
    }

    @Test
    public void getNestedChildrenNodesList() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        List<Node> retrieved = DOMParserUtil.getNestedChildrenNodesList(document, MAIN_NODE, CHILD_NODE, TEST_NODE);
        
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(2);

        retrieved.forEach(testNode -> assertThat(testNode.getNodeName()).isEqualTo(TEST_NODE));
        retrieved = DOMParserUtil.getNestedChildrenNodesList(document, CHILD_NODE, NESTING_NODE, NESTED_NODE);
        
        assertThat(retrieved).isNotNull().hasSize(2);
        retrieved.forEach(nestedNode -> assertThat(nestedNode.getNodeName()).isEqualTo(NESTED_NODE));
    }

    @Test
    public void getDocument() throws Exception {
        Document retrieved = DOMParserUtil.getDocument(XML);
        
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void getString() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document document = builder.newDocument();
        document.appendChild(document.createElement("CREATED"));
        String retrieved = DOMParserUtil.getString(document);
        
        assertThat(retrieved).isNotNull().contains("CREATED");
    }

    @Test
    public void asStream() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        final NodeList mainNodeList = document.getElementsByTagName("Main");
        commonCheckNodeStream(mainNodeList);
        final NodeList childNodesList = mainNodeList.item(0).getChildNodes();
        commonCheckNodeStream(childNodesList);
        final NodeList innerNodesList = childNodesList.item(0).getChildNodes();
        commonCheckNodeStream(innerNodesList);
    }

    private void commonCheckNodeStream(NodeList src) {
        assertThat(DOMParserUtil.asStream(src).count()).isEqualTo(src.getLength());
        AtomicInteger counter = new AtomicInteger();
        final Stream<Node> nodeStream = DOMParserUtil.asStream(src);
        nodeStream.forEach(node -> assertThat(node).isEqualTo(src.item(counter.getAndIncrement())));
    }
}
