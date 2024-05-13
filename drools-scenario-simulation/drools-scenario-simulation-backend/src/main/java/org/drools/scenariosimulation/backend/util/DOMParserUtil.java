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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class used to provide parsing methods
 */
public class DOMParserUtil {

    private DOMParserUtil() {
    }

    /**
     * This method remove the <b>nodeToRemoveName</b> <code>Node</code>s from all the <b>containerNodeName</b> Elements found inside the given <b>fullXml</b>
     * @param fullXml
     * @param containerNodeName
     * @param nodeToRemoveName
     * @return
     */
    public static String cleanupNodes(String fullXml, String containerNodeName, String nodeToRemoveName) throws Exception {
        Document document = getDocument(fullXml);
        cleanupNodes(document, containerNodeName, nodeToRemoveName);
        return getString(document);
    }

    /**
     * This method remove the <b>nodeToRemoveName</b> <code>Node</code>s from all the <b>containerNodeName</b> Elements found inside the given <b>fullXml</b>
     * @param document
     * @param containerNodeName
     * @param nodeToRemoveName
     * @return
     */
    public static void cleanupNodes(Document document, String containerNodeName, String nodeToRemoveName) {
        final NodeList nodeList = document.getElementsByTagName(containerNodeName);
        if (nodeList != null) {
            // iterate all the "container" nodes
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                // iterate all nodes inside the container one
                final NodeList childNodes = node.getChildNodes();
                if (childNodes != null) {
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (Objects.equals(childNode.getNodeName(), nodeToRemoveName)) {
                            node.removeChild(childNode);
                        }
                    }
                }
            }
        }
    }

    /**
     * Replace the text content of the given <b>nodeName</b> inside the given <code>containerNodeName</code>.
     * The replace happen only if the given <b>nodeName</b> text is equals to <b>toReplace</b>
     * @param document
     * @param containerNodeName
     * @param nodeName
     * @param toReplace
     * @param replacement
     */
    public static void replaceNodeText(Document document, String containerNodeName, String nodeName, String toReplace, String replacement) {
        asStream(document.getElementsByTagName(containerNodeName))
                .forEach(containerNode -> asStream(containerNode.getChildNodes())
                        .filter(childNode -> Objects.equals(nodeName, childNode.getNodeName()) && Objects.equals(toReplace, childNode.getTextContent()))
                        .forEach(childNode -> childNode.setTextContent(replacement)));
    }

    /**
     * Replace <b>all childNodeNameToReplace</b> nodes in <b>all containerNodeName</b>s presents in document with <b>childNodeNameReplacement</b>
     * @param document
     * @param containerNodeName
     * @param childNodeNameToReplace
     * @param childNodeNameReplacement
     * @return
     */
    public static String replaceNodeName(Document document, String containerNodeName, String childNodeNameToReplace, String childNodeNameReplacement) throws TransformerException {
        final NodeList containerNodes = document.getElementsByTagName(containerNodeName);
        if (containerNodes != null) {
            for (int i = 0; i < containerNodes.getLength(); i++) {
                Node containerNode = containerNodes.item(i);
                final NodeList childNodes = containerNode.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);
                    if (Objects.equals(childNode.getNodeName(), childNodeNameToReplace)) {
                        document.renameNode(childNode, null, childNodeNameReplacement);
                    }
                }
            }
        }
        return getString(document);
    }

    public static String getAttributeValue(Node containerNode, String attributeName) {
        return (containerNode.getAttributes() != null && containerNode.getAttributes().getNamedItem(attributeName) != null) ? containerNode.getAttributes().getNamedItem(attributeName).getNodeValue() : null;
    }

    /**
     * Return a <code>Map</code> where the <code>key</code> is the <code>Node</code> with the given <b>containerNodeName</b>
     * and the <b>value</b>   is the <b>node value</b> of the attribute with the given <b>attributeName</b>
     * It returns an <b>empty</b> map if such attribute does not exists
     * @param document
     * @param containerNodeName
     * @param attributeName
     * @return
     */
    public static Map<Node, String> getAttributeValues(Document document, String containerNodeName, String attributeName) {
        return asStream(document.getElementsByTagName(containerNodeName))
                .filter(containerNode -> containerNode.getAttributes() != null && containerNode.getAttributes().getNamedItem(attributeName) != null)
                .collect(Collectors.toMap(
                        containerNode -> containerNode,
                        containerNode -> containerNode.getAttributes().getNamedItem(attributeName).getNodeValue()
                ));
    }

    /**
     * Return a <code>Map</code> where the <code>key</code> is any <code>Node</code> inside the given <b>document</b>
     * and the <b>value</b>   is the <b>node value</b> of the attribute with the given <b>attributeName</b>
     * <p>
     * It returns an <b>empty</b> map no <code>Node</code> contains such attribute
     * @param document
     * @param attributeName
     * @return
     */
    public static Map<Node, String> getAttributeValues(Document document, String attributeName) {
        Map<Node, String> toReturn = new HashMap<>();
        asStream(document.getChildNodes())
                .forEach(childNode -> populateAttributeValuesMap(childNode, attributeName, toReturn));
        return toReturn;
    }

    public static void setAttributeValue(Document document, String containerNodeName, String attributeName, String attributeValue) {
        asStream(document.getElementsByTagName(containerNodeName))
                .map(Node::getAttributes)
                .map(attributes -> attributes.getNamedItem(attributeName))
                .filter(Objects::nonNull)
                .forEach(attributeNode -> attributeNode.setNodeValue(attributeValue));
    }

    /**
     * Create <b>childNodeName</b> nodes in <b>all containerNodeName</b> presents in the document
     * @param document
     * @param containerNodeName
     * @param childNodeName
     * @param nodeContent
     * @return
     */
    public static Map<Node, Node> createNodes(Document document, String containerNodeName, String childNodeName, String nodeContent) {
        return asStream(document.getElementsByTagName(containerNodeName))
                .collect(Collectors.toMap(
                        containerNode -> containerNode,
                        containerNode -> {
                            Node childNode = document.createElement(childNodeName);
                            containerNode.appendChild(childNode);
                            if (nodeContent != null) {
                                childNode.setTextContent(nodeContent);
                            }
                            return childNode;
                        }
                ));
    }

    /**
     * Create <b>childNodeName</b> nodes in <b>all containerNodeName</b>s presents in <b>all mainContainerNode</b>s of the document
     * @param document
     * @param containerNodeName
     * @param childNodeName
     * @param nodeContent
     * @return
     */
    public static Map<Node, Node> createNestedNodes(Document document, String mainContainerNodeName, String containerNodeName, String childNodeName, String nodeContent) {
        Map<Node, Node> toReturn = new HashMap<>();
        asStream(document.getElementsByTagName(mainContainerNodeName))
                .map(Node::getChildNodes)
                .forEach(containerNodeList ->
                                 asStream(containerNodeList)
                                         .filter(containerNode -> Objects.equals(containerNodeName, containerNode.getNodeName()))
                                         .forEach(containerNode -> {
                                             Node childNode = document.createElement(childNodeName);
                                             containerNode.appendChild(childNode);
                                             if (nodeContent != null) {
                                                 childNode.setTextContent(nodeContent);
                                             }
                                             toReturn.put(containerNode, childNode);
                                         }));
        return toReturn;
    }

    /**
     * Create a <b>nodeToCreateName</b> <code>Node</code> and appends it inside <b>containerNode</b>.
     * @param containerNode
     * @param nodeToCreateName
     * @param nodeContent
     * @return
     */
    public static Node createNodeAndAppend(Node containerNode, String nodeToCreateName, String nodeContent) {
        return createNodeAtPosition(containerNode, nodeToCreateName, nodeContent, null);
    }

    /**
     * Create a <b>nodeToCreateName</b> <code>Node</code> inside <b>containerNode</b>.
     * If <b>nodeContent</b> is not null, add it as text content.
     * If <b>position</b> is not null, put the created node at position 0
     * @param containerNode
     * @param nodeToCreateName
     * @param nodeContent
     * @param position
     * @return
     */
    public static Node createNodeAtPosition(Node containerNode, String nodeToCreateName, String nodeContent, Integer position) {
        Node toReturn = containerNode.getOwnerDocument().createElement(nodeToCreateName);
        if (nodeContent != null) {
            toReturn.setTextContent(nodeContent);
        }
        if (containerNode.hasChildNodes() && position != null && position < containerNode.getChildNodes().getLength()) {
            Node positionNode = containerNode.getChildNodes().item(position);
            containerNode.insertBefore(toReturn, positionNode);
        } else {
            containerNode.appendChild(toReturn);
        }
        return toReturn;
    }

    /**
     * Get <b>all childNodeName</b> nodes in <b>all containerNodeName</b>s presents in fullXml
     * @param fullXml
     * @param containerNodeName
     * @param childNodeName
     * @return
     */
    public static Map<Node, List<Node>> getChildrenNodesMap(String fullXml, String containerNodeName, String childNodeName) throws Exception {
        Document document = getDocument(fullXml);
        return getChildrenNodesMap(document, containerNodeName, childNodeName);
    }

    /**
     * Get <b>all childNodeName</b> nodes in <b>all containerNodeName</b>s presents in document
     * @param document
     * @param containerNodeName
     * @param childNodeName
     * @return
     */
    public static Map<Node, List<Node>> getChildrenNodesMap(Document document, String containerNodeName, String childNodeName) {
        return asStream(document.getElementsByTagName(containerNodeName))
                .collect(Collectors.toMap(
                        containerNode -> containerNode,
                        containerNode -> asStream(containerNode.getChildNodes())
                                .filter(childNode -> Objects.equals(childNode.getNodeName(), childNodeName))
                                .collect(Collectors.toList())
                ));
    }

    public static Map<Node, List<Node>> getChildrenNodesMap(Node node, String containerNodeName, String childNodeName) {
        return asStream(node.getChildNodes())
                .filter(containerNode -> Objects.equals(containerNode.getNodeName(), containerNodeName))
                .collect(Collectors.toMap(
                        containerNode -> containerNode,
                        containerNode -> asStream(containerNode.getChildNodes())
                                .filter(childNode -> Objects.equals(childNode.getNodeName(), childNodeName))
                                .collect(Collectors.toList())
                ));
    }

    public static List<Node> getChildrenNodesList(Node node, String childNodeName) {
        return asStream(node.getChildNodes()).filter(childNode -> Objects.equals(childNode.getNodeName(), childNodeName)).collect(Collectors.toList());
    }

    /**
     * Get <b>all childNodeName</b> nodes in <b>all containerNodeName</b>s presents in <b>all mainContainerNodeName</b>s of the document
     * @param document
     * @param mainContainerNodeName
     * @param containerNodeName
     * @param childNodeName
     * @return
     */
    public static Map<Node, List<Node>> getNestedChildrenNodesMap(Document document, String mainContainerNodeName, String containerNodeName, String childNodeName) {
        Map<Node, List<Node>> toReturn = new HashMap<>();
        asStream(document.getElementsByTagName(mainContainerNodeName))
                .map(mainContainerNode -> getChildrenNodesMap(mainContainerNode, containerNodeName, childNodeName))
                .forEach(toReturn::putAll);
        return toReturn;
    }

    public static List<Node> getNestedChildrenNodesList(Document document, String grandParentNodeName, String parentNodeName, String childNodeName) {
        return asStream(document.getElementsByTagName(childNodeName))
                .filter(childNode -> {
                    Node parentNode = childNode.getParentNode();
                    Node grandParentNode = parentNode.getParentNode();
                    return Objects.equals(parentNodeName, parentNode.getNodeName()) && Objects.equals(grandParentNodeName, grandParentNode.getNodeName());
                }).collect(Collectors.toList());
    }

    public static List<Node> getNestedChildrenNodesList(Node node, String containerName, String childNodeName) {
        return asStream(node.getOwnerDocument().getElementsByTagName(childNodeName))
                .filter(childNode -> Objects.equals(containerName, childNode.getParentNode().getNodeName()) && Objects.equals(node, childNode.getParentNode().getParentNode()))
                .collect(Collectors.toList());
    }

    public static Document getDocument(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setIgnoringComments(true);
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        try (InputStream inputStream = new ByteArrayInputStream(xml.getBytes())) {
            return dBuilder.parse(inputStream);
        }
    }

    public static String getString(Document toRead) throws TransformerException {
        DOMSource domSource = new DOMSource(toRead);
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        return sw.toString();
    }

    /**
     * Recursively populate the given <code>Map</code>
     * @param node
     * @param attributeName
     * @param toPopulate
     */
    protected static void populateAttributeValuesMap(Node node, String attributeName, Map<Node, String> toPopulate) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node attributeNode = attributes.getNamedItem(attributeName);
            if (attributeNode != null) {
                toPopulate.put(node, attributeNode.getNodeValue());
            }
        }
        asStream(node.getChildNodes()).forEach(childNode -> populateAttributeValuesMap(childNode, attributeName, toPopulate));
    }

    /**
     * Return a <code>Stream</code> out of the given <code>NodeList</code>.
     * It <b>nodeList</b> is <code>null</code>, returns an empty stream
     * @param nodeList
     * @return
     */
    protected static Stream<Node> asStream(NodeList nodeList) {
        if (nodeList == null) {
            return new ArrayList<Node>().stream();
        } else {
            AtomicInteger n = new AtomicInteger(0);
            return Stream.generate(() -> nodeList.item(n.getAndIncrement())).limit(nodeList.getLength());
        }
    }
}
