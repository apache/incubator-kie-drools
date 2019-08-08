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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static com.github.javaparser.utils.Utils.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DOMParserUtilTest {

    private static final String MAIN_NODE = "Main";
    private static final String MAIN_ATTRIBUTE_NAME = "mainattribute";
    private static final String ATTRIBUTE_VALUE = "default";
    private static final String TEST_NODE = "testnode";
    private static final String TEST_NODE_CONTENT = "testnode";
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
    public void cleanupNodesString() {
        try {
            String retrieved = DOMParserUtil.cleanupNodes(XML, CHILD_NODE, TEST_NODE);
            assertNotNull(retrieved);
            Map<Node, List<Node>> childrenNodes = DOMParserUtil.getChildrenNodes(retrieved, MAIN_NODE, TEST_NODE);
            assertNotNull(childrenNodes);
            assertEquals(1, childrenNodes.size());
            Node keyNode = childrenNodes.keySet().iterator().next();
            assertEquals(MAIN_NODE, keyNode.getNodeName());
            List<Node> valueNodes = childrenNodes.get(keyNode);
            assertTrue(valueNodes != null && valueNodes.size() == 1);
            assertEquals(TEST_NODE, valueNodes.get(0).getNodeName());

            childrenNodes = DOMParserUtil.getChildrenNodes(retrieved, CHILD_NODE, OTHER_NODE);
            assertEquals(2, childrenNodes.size());
            childrenNodes.forEach((childKeyNode, childValueNodes) -> {
                assertNotNull(childKeyNode);
                assertEquals(CHILD_NODE, childKeyNode.getNodeName());
                assertTrue(childValueNodes != null && childValueNodes.size() == 1);
                assertEquals(OTHER_NODE, childValueNodes.get(0).getNodeName());
            });

            childrenNodes = DOMParserUtil.getChildrenNodes(retrieved, CHILD_NODE, TEST_NODE);
            childrenNodes.forEach((childKeyNode, childValueNodes) -> {
                assertNotNull(childKeyNode);
                assertEquals(CHILD_NODE, childKeyNode.getNodeName());
                assertTrue(childValueNodes != null && childValueNodes.isEmpty());
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void cleanupNodesDocument() {
        try {
            Document document = DOMParserUtil.getDocument(XML);
            DOMParserUtil.cleanupNodes(document, CHILD_NODE, TEST_NODE);
            assertNotNull(document);
            Map<Node, List<Node>> childrenNodes = DOMParserUtil.getChildrenNodes(document, MAIN_NODE, TEST_NODE);
            assertNotNull(childrenNodes);
            assertEquals(1, childrenNodes.size());
            Node keyNode = childrenNodes.keySet().iterator().next();
            assertEquals(MAIN_NODE, keyNode.getNodeName());
            List<Node> valueNodes = childrenNodes.get(keyNode);
            assertTrue(valueNodes != null && valueNodes.size() == 1);
            assertEquals(TEST_NODE, valueNodes.get(0).getNodeName());

            childrenNodes = DOMParserUtil.getChildrenNodes(document, CHILD_NODE, OTHER_NODE);
            assertEquals(2, childrenNodes.size());
            childrenNodes.forEach((childKeyNode, childValueNodes) -> {
                assertNotNull(childKeyNode);
                assertEquals(CHILD_NODE, childKeyNode.getNodeName());
                assertTrue(childValueNodes != null && childValueNodes.size() == 1);
                assertEquals(OTHER_NODE, childValueNodes.get(0).getNodeName());
            });

            childrenNodes = DOMParserUtil.getChildrenNodes(document, CHILD_NODE, TEST_NODE);
            childrenNodes.forEach((childKeyNode, childValueNodes) -> {
                assertNotNull(childKeyNode);
                assertEquals(CHILD_NODE, childKeyNode.getNodeName());
                assertTrue(childValueNodes != null && childValueNodes.isEmpty());
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void replaceNodeText() {
        try {
            final String replacement = "replacement";
            Document document = DOMParserUtil.getDocument(XML);
            DOMParserUtil.replaceNodeText(document, MAIN_NODE, TEST_NODE, TEST_NODE_CONTENT, replacement);
            final Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodes(document, MAIN_NODE, TEST_NODE);
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            List<Node> testNodes = retrieved.values().iterator().next();
            assertNotNull(testNodes);
            assertEquals(1, testNodes.size());
            assertEquals(replacement, testNodes.get(0).getTextContent());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getAttributeValuesByNode() {
        try {
            Document document = DOMParserUtil.getDocument(XML);
            Map<Node, String> retrieved = DOMParserUtil.getAttributeValues(document, MAIN_NODE, MAIN_ATTRIBUTE_NAME);
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            assertEquals(retrieved.values().toArray()[0], ATTRIBUTE_VALUE);
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
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getAllAttributeValues() {
        try {
            Document document = DOMParserUtil.getDocument(XML);
            Map<Node, String> retrieved = DOMParserUtil.getAttributeValues(document, MAIN_ATTRIBUTE_NAME);
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            assertEquals(ATTRIBUTE_VALUE, retrieved.values().toArray()[0]);
            retrieved = DOMParserUtil.getAttributeValues(document, CHILD_ATTRIBUTE_NAME);
            assertNotNull(retrieved);
            assertEquals(2, retrieved.size());
            retrieved.values().forEach(attributeValue -> assertEquals(ATTRIBUTE_VALUE, attributeValue));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void setAttributeValue() {
        try {
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
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void createNodes() {
        try {
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
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getChildrenNodesFromDocument() {
        try {
            Document document = DOMParserUtil.getDocument(XML);
            Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodes(document, MAIN_NODE, TEST_NODE);
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            Node keyNode = retrieved.keySet().iterator().next();
            assertNotNull(keyNode);
            assertEquals(MAIN_NODE, keyNode.getNodeName());
            List<Node> valueNodes = retrieved.get(keyNode);
            assertTrue(valueNodes != null && valueNodes.size() == 1);
            assertEquals(TEST_NODE, valueNodes.get(0).getNodeName());
            retrieved = DOMParserUtil.getChildrenNodes(document, MAIN_NODE, NOT_EXISTING);
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            assertTrue(retrieved.values().iterator().next().isEmpty());
            retrieved = DOMParserUtil.getChildrenNodes(document, MAIN_NODE, CHILD_NODE);
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
                retrieved = DOMParserUtil.getChildrenNodes(XML, CHILD_NODE, childNodeName);
                assertNotNull(retrieved);
                assertEquals(2, retrieved.size());
                retrieved.forEach((childKeyNode, childValueNodes) -> {
                    assertNotNull(childKeyNode);
                    assertEquals(CHILD_NODE, childKeyNode.getNodeName());
                    assertTrue(childValueNodes != null && childValueNodes.size() == 1);
                    assertEquals(childNodeName, childValueNodes.get(0).getNodeName());
                });
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getChildrenNodesFromNode() {
        try {
            Document document = DOMParserUtil.getDocument(XML);
            Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodes(document, MAIN_NODE, CHILD_NODE);
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            Node mainNode = (Node) retrieved.keySet().toArray()[0];
            assertEquals(MAIN_NODE, mainNode.getNodeName());
            List<Node> nodes = retrieved.get(mainNode);
            nodes.forEach(childNode -> assertEquals(CHILD_NODE, childNode.getNodeName()));
            retrieved = DOMParserUtil.getChildrenNodes(nodes.get(0), NESTING_NODE, NESTED_NODE);
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            Node childNode = (Node) retrieved.keySet().toArray()[0];
            assertEquals(NESTING_NODE, childNode.getNodeName());
            nodes = retrieved.get(childNode);
            assertNotNull(nodes);
            assertEquals(1, nodes.size());
            assertEquals(NESTED_NODE, nodes.get(0).getNodeName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getDocument() {
        try {
            Document retrieved = DOMParserUtil.getDocument(XML);
            assertNotNull(retrieved);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getString() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document document = builder.newDocument();
            document.appendChild(document.createElement("CREATED"));
            String retrieved = DOMParserUtil.getString(document);
            assertNotNull(retrieved);
            assertTrue(retrieved.contains("CREATED"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
