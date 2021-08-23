/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
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

import static com.github.javaparser.utils.Utils.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertNotNull(retrieved);
        Map<Node, List<Node>> childrenNodes = DOMParserUtil.getChildrenNodesMap(retrieved, MAIN_NODE, TEST_NODE);
        assertNotNull(childrenNodes);
        assertEquals(1, childrenNodes.size());
        Node keyNode = childrenNodes.keySet().iterator().next();
        assertEquals(MAIN_NODE, keyNode.getNodeName());
        List<Node> valueNodes = childrenNodes.get(keyNode);
        assertTrue(valueNodes != null && valueNodes.size() == 1);
        assertEquals(TEST_NODE, valueNodes.get(0).getNodeName());

        childrenNodes = DOMParserUtil.getChildrenNodesMap(retrieved, CHILD_NODE, OTHER_NODE);
        assertEquals(2, childrenNodes.size());
        childrenNodes.forEach((childKeyNode, childValueNodes) -> {
            assertNotNull(childKeyNode);
            assertEquals(CHILD_NODE, childKeyNode.getNodeName());
            assertTrue(childValueNodes != null && childValueNodes.size() == 1);
            assertEquals(OTHER_NODE, childValueNodes.get(0).getNodeName());
        });

        childrenNodes = DOMParserUtil.getChildrenNodesMap(retrieved, CHILD_NODE, TEST_NODE);
        childrenNodes.forEach((childKeyNode, childValueNodes) -> {
            assertNotNull(childKeyNode);
            assertEquals(CHILD_NODE, childKeyNode.getNodeName());
            assertTrue(childValueNodes != null && childValueNodes.isEmpty());
        });
    }

    @Test
    public void cleanupNodesDocument() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        DOMParserUtil.cleanupNodes(document, CHILD_NODE, TEST_NODE);
        assertNotNull(document);
        Map<Node, List<Node>> childrenNodes = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, TEST_NODE);
        assertNotNull(childrenNodes);
        assertEquals(1, childrenNodes.size());
        Node keyNode = childrenNodes.keySet().iterator().next();
        assertEquals(MAIN_NODE, keyNode.getNodeName());
        List<Node> valueNodes = childrenNodes.get(keyNode);
        assertTrue(valueNodes != null && valueNodes.size() == 1);
        assertEquals(TEST_NODE, valueNodes.get(0).getNodeName());

        childrenNodes = DOMParserUtil.getChildrenNodesMap(document, CHILD_NODE, OTHER_NODE);
        assertEquals(2, childrenNodes.size());
        childrenNodes.forEach((childKeyNode, childValueNodes) -> {
            assertNotNull(childKeyNode);
            assertEquals(CHILD_NODE, childKeyNode.getNodeName());
            assertTrue(childValueNodes != null && childValueNodes.size() == 1);
            assertEquals(OTHER_NODE, childValueNodes.get(0).getNodeName());
        });

        childrenNodes = DOMParserUtil.getChildrenNodesMap(document, CHILD_NODE, TEST_NODE);
        childrenNodes.forEach((childKeyNode, childValueNodes) -> {
            assertNotNull(childKeyNode);
            assertEquals(CHILD_NODE, childKeyNode.getNodeName());
            assertTrue(childValueNodes != null && childValueNodes.isEmpty());
        });
    }

    @Test
    public void replaceNodeText() throws Exception {
        final String replacement = "replacement";
        Document document = DOMParserUtil.getDocument(XML);
        DOMParserUtil.replaceNodeText(document, MAIN_NODE, TEST_NODE, TEST_NODE_CONTENT, replacement);
        final Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, TEST_NODE);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        List<Node> testNodes = retrieved.values().iterator().next();
        assertNotNull(testNodes);
        assertEquals(1, testNodes.size());
        assertEquals(replacement, testNodes.get(0).getTextContent());
    }

    @Test
    public void replaceNodeName() throws Exception {
        final String replacement = "replacement";
        Document document = DOMParserUtil.getDocument(XML);
        DOMParserUtil.replaceNodeName(document, MAIN_NODE, TEST_NODE, replacement);
        final Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, replacement);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        List<Node> testNodes = retrieved.values().iterator().next();
        assertNotNull(testNodes);
        assertEquals(1, testNodes.size());
        assertEquals("replacement", testNodes.get(0).getNodeName());
    }

    @Test
    public void getAttributeValuesByNode() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, String> retrieved = DOMParserUtil.getAttributeValues(document, MAIN_NODE, MAIN_ATTRIBUTE_NAME);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        assertEquals(ATTRIBUTE_VALUE, retrieved.values().toArray()[0]);
        retrieved = DOMParserUtil.getAttributeValues(document, MAIN_NODE, NOT_EXISTING);
        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());
        retrieved = DOMParserUtil.getAttributeValues(document, CHILD_NODE, CHILD_ATTRIBUTE_NAME);
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        retrieved.values().forEach(attributeValue -> assertEquals(ATTRIBUTE_VALUE, attributeValue));
        retrieved = DOMParserUtil.getAttributeValues(document, CHILD_NODE, NOT_EXISTING);
        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());
    }

    @Test
    public void getAllAttributeValues() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, String> retrieved = DOMParserUtil.getAttributeValues(document, MAIN_ATTRIBUTE_NAME);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        assertEquals(ATTRIBUTE_VALUE, retrieved.values().toArray()[0]);
        retrieved = DOMParserUtil.getAttributeValues(document, CHILD_ATTRIBUTE_NAME);
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        retrieved.values().forEach(attributeValue -> assertEquals(ATTRIBUTE_VALUE, attributeValue));
    }

    @Test
    public void setAttributeValue() throws Exception {
        final String newValue = "NEW_VALUE";
        Document document = DOMParserUtil.getDocument(XML);
        DOMParserUtil.setAttributeValue(document, MAIN_NODE, MAIN_ATTRIBUTE_NAME, newValue);
        Map<Node, String> retrieved = DOMParserUtil.getAttributeValues(document, MAIN_NODE, MAIN_ATTRIBUTE_NAME);
        assertEquals(retrieved.values().toArray()[0], newValue);
        DOMParserUtil.setAttributeValue(document, MAIN_NODE, NOT_EXISTING, newValue);
        retrieved = DOMParserUtil.getAttributeValues(document, MAIN_NODE, NOT_EXISTING);
        assertTrue(retrieved.isEmpty());
        DOMParserUtil.setAttributeValue(document, CHILD_NODE, CHILD_ATTRIBUTE_NAME, newValue);
        retrieved = DOMParserUtil.getAttributeValues(document, CHILD_NODE, CHILD_ATTRIBUTE_NAME);
        assertEquals(2, retrieved.size());
        retrieved.values().forEach(attributeValue -> assertEquals(newValue, attributeValue));
    }

    @Test
    public void createNodes() throws Exception {
        final String newNodeName = "NEW_NODE_NAME";
        final String newNodeValue = "NEW_NODE_VALUE";
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, Node> retrieved = DOMParserUtil.createNodes(document, MAIN_NODE, newNodeName, newNodeValue);
        assertEquals(1, retrieved.size());
        Node created = (Node) retrieved.values().toArray()[0];
        assertNotNull(created);
        assertEquals(newNodeName, created.getNodeName());
        assertEquals(newNodeValue, created.getTextContent());
        retrieved = DOMParserUtil.createNodes(document, MAIN_NODE, newNodeName, null);
        assertEquals(1, retrieved.size());
        created = (Node) retrieved.values().toArray()[0];
        assertNotNull(created);
        assertEquals(newNodeName, created.getNodeName());
        assertTrue(created.getTextContent().isEmpty());

        retrieved = DOMParserUtil.createNodes(document, CHILD_NODE, newNodeName, newNodeValue);
        assertEquals(2, retrieved.size());
        retrieved.forEach((key, createdNode) -> {
            assertNotNull(createdNode);
            assertEquals(newNodeName, createdNode.getNodeName());
            assertEquals(newNodeValue, createdNode.getTextContent());
        });
        retrieved = DOMParserUtil.createNodes(document, CHILD_NODE, newNodeName, null);
        assertEquals(2, retrieved.size());
        retrieved.forEach((key, createdNode) -> {
            assertNotNull(createdNode);
            assertEquals(newNodeName, createdNode.getNodeName());
            assertTrue(createdNode.getTextContent().isEmpty());
        });
    }

    @Test
    public void createNestedNodes() throws Exception {
        final String newNodeName = "NEW_NODE_NAME";
        final String newNodeValue = "NEW_NODE_VALUE";
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, Node> retrieved = DOMParserUtil.createNestedNodes(document, MAIN_NODE, TEST_NODE, newNodeName, newNodeValue);
        assertEquals(1, retrieved.size());
        Node created = (Node) retrieved.values().toArray()[0];
        assertNotNull(created);
        assertEquals(newNodeName, created.getNodeName());
        assertEquals(newNodeValue, created.getTextContent());
        retrieved = DOMParserUtil.createNestedNodes(document, MAIN_NODE, TEST_NODE, newNodeName, null);
        assertEquals(1, retrieved.size());
        created = (Node) retrieved.values().toArray()[0];
        assertNotNull(created);
        assertEquals(newNodeName, created.getNodeName());
        assertTrue(created.getTextContent().isEmpty());

        retrieved = DOMParserUtil.createNestedNodes(document, MAIN_NODE, CHILD_NODE, newNodeName, newNodeValue);
        assertEquals(2, retrieved.size());
        retrieved.forEach((key, createdNode) -> {
            assertNotNull(createdNode);
            assertEquals(newNodeName, createdNode.getNodeName());
            assertEquals(newNodeValue, createdNode.getTextContent());
        });
        retrieved = DOMParserUtil.createNestedNodes(document, MAIN_NODE, CHILD_NODE, newNodeName, null);
        assertEquals(2, retrieved.size());
        retrieved.forEach((key, createdNode) -> {
            assertNotNull(createdNode);
            assertEquals(newNodeName, createdNode.getNodeName());
            assertTrue(createdNode.getTextContent().isEmpty());
        });
    }

    @Test
    public void createNodeAtPosition() throws Exception {
        String newNodeName = "NEW_NODE_NAME_0";
        String newNodeValue = "NEW_NODE_VALUE_=";
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, List<Node>> testNodesMap = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, TEST_NODE);
        assertEquals(1, testNodesMap.size());
        Node mainNode = testNodesMap.keySet().iterator().next();
        Node retrieved = DOMParserUtil.createNodeAtPosition(mainNode, newNodeName, newNodeValue, null);
        assertNotNull(retrieved);
        assertEquals(newNodeName, retrieved.getNodeName());
        assertEquals(newNodeValue, retrieved.getTextContent());
        assertEquals(retrieved, mainNode.getChildNodes().item(mainNode.getChildNodes().getLength() - 1));
        newNodeName = "NEW_NODE_NAME_1";
        newNodeValue = "NEW_NODE_VALUE_1";
        retrieved = DOMParserUtil.createNodeAtPosition(mainNode, newNodeName, newNodeValue, 0);
        assertNotNull(retrieved);
        assertEquals(newNodeName, retrieved.getNodeName());
        assertEquals(newNodeValue, retrieved.getTextContent());
        assertEquals(retrieved, mainNode.getChildNodes().item(0));
        newNodeName = "NEW_NODE_NAME_2";
        newNodeValue = "NEW_NODE_VALUE_2";
        retrieved = DOMParserUtil.createNodeAtPosition(mainNode, newNodeName, newNodeValue, 2);
        assertNotNull(retrieved);
        assertEquals(newNodeName, retrieved.getNodeName());
        assertEquals(newNodeValue, retrieved.getTextContent());
        assertEquals(retrieved, mainNode.getChildNodes().item(2));
    }

    @Test
    public void createNodeAndAppend() throws Exception {
        String newNodeName0 = "NEW_NODE_NAME_0";
        String newNodeValue0 = "NEW_NODE_VALUE_=";
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, List<Node>> testNodesMap = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, TEST_NODE);
        assertEquals(1, testNodesMap.size());
        Node mainNode = testNodesMap.keySet().iterator().next();
        int startingChildNodes = mainNode.getChildNodes().getLength();
        Node retrieved = DOMParserUtil.createNodeAndAppend(mainNode, newNodeName0, newNodeValue0);
        assertNotNull(retrieved);
        assertEquals(newNodeName0, retrieved.getNodeName());
        assertEquals(newNodeValue0, retrieved.getTextContent());
        assertEquals(retrieved, mainNode.getChildNodes().item(mainNode.getChildNodes().getLength() - 1));
        assertEquals(startingChildNodes + 1, mainNode.getChildNodes().getLength());
        String newNodeName1 = "NEW_NODE_NAME_1";
        String newNodeValue1 = "NEW_NODE_VALUE_1";
        retrieved = DOMParserUtil.createNodeAndAppend(mainNode, newNodeName1, newNodeValue1);
        assertNotNull(retrieved);
        assertEquals(newNodeName1, retrieved.getNodeName());
        assertEquals(newNodeValue1, retrieved.getTextContent());
        assertEquals(retrieved, mainNode.getChildNodes().item(mainNode.getChildNodes().getLength() - 1));
        assertEquals(startingChildNodes + 2, mainNode.getChildNodes().getLength());
        String newNodeName2 = "NEW_NODE_NAME_2";
        String newNodeValue2 = "NEW_NODE_VALUE_2";
        retrieved = DOMParserUtil.createNodeAndAppend(mainNode, newNodeName2, newNodeValue2);
        assertNotNull(retrieved);
        assertEquals(newNodeName2, retrieved.getNodeName());
        assertEquals(newNodeValue2, retrieved.getTextContent());
        assertEquals(retrieved, mainNode.getChildNodes().item(mainNode.getChildNodes().getLength() - 1));
        assertEquals(startingChildNodes + 3, mainNode.getChildNodes().getLength());

        assertEquals(newNodeName0, mainNode.getChildNodes().item(startingChildNodes).getNodeName());
        assertEquals(newNodeValue0, mainNode.getChildNodes().item(startingChildNodes).getTextContent());
        assertEquals(newNodeName1, mainNode.getChildNodes().item(startingChildNodes + 1).getNodeName());
        assertEquals(newNodeValue1, mainNode.getChildNodes().item(startingChildNodes + 1).getTextContent());
        assertEquals(newNodeName2, mainNode.getChildNodes().item(startingChildNodes + 2).getNodeName());
        assertEquals(newNodeValue2, mainNode.getChildNodes().item(startingChildNodes + 2).getTextContent());
    }

    @Test
    public void getChildrenNodesFromDocument() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, TEST_NODE);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        Node keyNode = retrieved.keySet().iterator().next();
        assertNotNull(keyNode);
        assertEquals(MAIN_NODE, keyNode.getNodeName());
        List<Node> valueNodes = retrieved.get(keyNode);
        assertTrue(valueNodes != null && valueNodes.size() == 1);
        assertEquals(TEST_NODE, valueNodes.get(0).getNodeName());
        retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, NOT_EXISTING);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        assertTrue(retrieved.values().iterator().next().isEmpty());
        retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, CHILD_NODE);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        keyNode = retrieved.keySet().iterator().next();
        assertNotNull(keyNode);
        assertEquals(MAIN_NODE, keyNode.getNodeName());
        valueNodes = retrieved.get(keyNode);
        assertTrue(valueNodes != null && valueNodes.size() == 2);
        valueNodes.forEach(childNode -> assertEquals(CHILD_NODE, childNode.getNodeName()));
        List<String> nodeToTest = Arrays.asList(TEST_NODE, OTHER_NODE);
        for (String childNodeName : nodeToTest) {
            retrieved = DOMParserUtil.getChildrenNodesMap(XML, CHILD_NODE, childNodeName);
            assertNotNull(retrieved);
            assertEquals(2, retrieved.size());
            retrieved.forEach((childKeyNode, childValueNodes) -> {
                assertNotNull(childKeyNode);
                assertEquals(CHILD_NODE, childKeyNode.getNodeName());
                assertTrue(childValueNodes != null && childValueNodes.size() == 1);
                assertEquals(childNodeName, childValueNodes.get(0).getNodeName());
            });
        }
    }

    @Test
    public void getChildrenNodesFromNode() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodesMap(document, MAIN_NODE, CHILD_NODE);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        Node mainNode = (Node) retrieved.keySet().toArray()[0];
        assertEquals(MAIN_NODE, mainNode.getNodeName());
        List<Node> nodes = retrieved.get(mainNode);
        nodes.forEach(childNode -> assertEquals(CHILD_NODE, childNode.getNodeName()));
        retrieved = DOMParserUtil.getChildrenNodesMap(nodes.get(0), NESTING_NODE, NESTED_NODE);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        Node childNode = (Node) retrieved.keySet().toArray()[0];
        assertEquals(NESTING_NODE, childNode.getNodeName());
        nodes = retrieved.get(childNode);
        assertNotNull(nodes);
        assertEquals(1, nodes.size());
        assertEquals(NESTED_NODE, nodes.get(0).getNodeName());
    }

    @Test
    public void getNestedChildrenNodesMap() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        Map<Node, List<Node>> retrieved = DOMParserUtil.getNestedChildrenNodesMap(document, MAIN_NODE, CHILD_NODE, TEST_NODE);
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        retrieved.forEach((childNode, testNodes) -> {
            assertEquals(CHILD_NODE, childNode.getNodeName());
            assertEquals(1, testNodes.size());
            assertEquals(TEST_NODE, testNodes.get(0).getNodeName());
        });
        retrieved = DOMParserUtil.getNestedChildrenNodesMap(document, CHILD_NODE, NESTING_NODE, NESTED_NODE);
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        retrieved.forEach((nestingNode, nestedNodes) -> {
            assertEquals(NESTING_NODE, nestingNode.getNodeName());
            assertEquals(1, nestedNodes.size());
            assertEquals(NESTED_NODE, nestedNodes.get(0).getNodeName());
        });
    }

    @Test
    public void getNestedChildrenNodesList() throws Exception {
        Document document = DOMParserUtil.getDocument(XML);
        List<Node> retrieved = DOMParserUtil.getNestedChildrenNodesList(document, MAIN_NODE, CHILD_NODE, TEST_NODE);
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        retrieved.forEach(testNode -> assertEquals(TEST_NODE, testNode.getNodeName()));
        retrieved = DOMParserUtil.getNestedChildrenNodesList(document, CHILD_NODE, NESTING_NODE, NESTED_NODE);
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        retrieved.forEach(nestedNode -> assertEquals(NESTED_NODE, nestedNode.getNodeName()));
    }

    @Test
    public void getDocument() throws Exception {
        Document retrieved = DOMParserUtil.getDocument(XML);
        assertNotNull(retrieved);
    }

    @Test
    public void getString() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document document = builder.newDocument();
        document.appendChild(document.createElement("CREATED"));
        String retrieved = DOMParserUtil.getString(document);
        assertNotNull(retrieved);
        assertTrue(retrieved.contains("CREATED"));
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
        assertEquals(src.getLength(), DOMParserUtil.asStream(src).count());
        AtomicInteger counter = new AtomicInteger();
        final Stream<Node> nodeStream = DOMParserUtil.asStream(src);
        nodeStream.forEach(node -> assertEquals(src.item(counter.getAndIncrement()), node));
    }
}
