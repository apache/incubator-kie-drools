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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
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
        final NodeList nodeList = document.getElementsByTagName(containerNodeName);
        if (nodeList != null) {
            // iterate all the "container" nodes
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                // iterate all nodes inside the container one
                final NodeList childNodes = node.getChildNodes();
                if (childNodes != null) {
                    for (int j=0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (Objects.equals(nodeName, childNode.getNodeName()) && Objects.equals(toReplace, childNode.getTextContent())) {
                            childNode.setTextContent(replacement);
                        }
                    }
                }
            }
        }
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
        Map<Node, String> toReturn = new HashMap<>();
        final NodeList nodeList = document.getElementsByTagName(containerNodeName);
        if (nodeList != null) {
            // iterate all the "container" nodes
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Node attributeNode = node.getAttributes().getNamedItem(attributeName);
                if (attributeNode != null) {
                    toReturn.put(node, attributeNode.getNodeValue());
                }
            }
        }
        return toReturn;
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
        final NodeList nodeList = document.getChildNodes();
        if (nodeList != null) {
            // iterate all the "container" nodes
            for (int i = 0; i < nodeList.getLength(); i++) {
                populateAttributeValuesMap(nodeList.item(i), attributeName, toReturn);
            }
        }
        return toReturn;
    }

    public static void setAttributeValue(Document document, String containerNodeName, String attributeName, String attributeValue) {
        final NodeList nodeList = document.getElementsByTagName(containerNodeName);
        if (nodeList != null) {
            // iterate all the "container" nodes
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                final NamedNodeMap attributes = node.getAttributes();
                if (attributes != null) {
                    Node attributeNode = attributes.getNamedItem(attributeName);
                    if (attributeNode != null) {
                        attributeNode.setNodeValue(attributeValue);
                    }
                }
            }
        }
    }

    public static Map<Node, Node> createNodes(Document document, String containerNodeName, String childNodeName, String nodeContent) {
        Map<Node, Node> toReturn = new HashMap<>();
        final NodeList nodeList = document.getElementsByTagName(containerNodeName);
        if (nodeList != null) {
            // iterate all the "container" nodes
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Node childNode = document.createElement(childNodeName);
                node.appendChild(childNode);
                if (nodeContent != null) {
                    childNode.setTextContent(nodeContent);
                }
                toReturn.put(node, childNode);
            }
        }
        return toReturn;
    }

    public static Map<Node, List<Node>> getChildrenNodes(String fullXml, String containerNodeName, String childNodeName) throws Exception {
        Document document = getDocument(fullXml);
        return getChildrenNodes(document, containerNodeName, childNodeName);
    }

    public static Map<Node, List<Node>> getChildrenNodes(Document document, String containerNodeName, String childNodeName) {
        Map<Node, List<Node>> toReturn = new HashMap<>();
        final NodeList nodeList = document.getElementsByTagName(containerNodeName);
        if (nodeList != null) {
            // iterate all the "container" nodes
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                // iterate all nodes inside the container one
                final NodeList childNodes = node.getChildNodes();
                if (childNodes != null) {
                    List<Node> value = new ArrayList<>();
                    toReturn.put(node, value);
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (Objects.equals(childNode.getNodeName(), childNodeName)) {
                            value.add(childNode);
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    public static Map<Node, List<Node>> getChildrenNodes(Node node, String containerNodeName, String childNodeName) {
        Map<Node, List<Node>> toReturn = new HashMap<>();
        final NodeList nodeList = node.getChildNodes();
        if (nodeList != null) {
            // iterate all the "container" nodes
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node containerNode = nodeList.item(i);
                if (Objects.equals(containerNode.getNodeName(), containerNodeName)) {
                    // iterate all nodes inside the container one
                    final NodeList childNodes = containerNode.getChildNodes();
                    if (childNodes != null) {
                        if (childNodes.getLength() > 0) {
                            List<Node> value = new ArrayList<>();
                            toReturn.put(containerNode, value);
                            for (int j = 0; j < childNodes.getLength(); j++) {
                                Node childNode = childNodes.item(j);
                                if (Objects.equals(childNode.getNodeName(), childNodeName)) {
                                    value.add(childNode);
                                }
                            }
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    public static List<Node> getChildrenNodes(Node node, String childNodeName) {
        List<Node> toReturn = new ArrayList<>();
        final NodeList childNodes = node.getChildNodes();
        if (childNodes != null) {
            // iterate all the "container" nodes
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (Objects.equals(childNode.getNodeName(), childNodeName)) {
                    toReturn.add(childNode);
                }
            }
        }
        return toReturn;
    }

    public static Document getDocument(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        try (InputStream inputStream = new ByteArrayInputStream(xml.getBytes())) {
            return dBuilder.parse(inputStream);
        }
    }

    public static String getString(Document toRead) throws Exception {
        DOMSource domSource = new DOMSource(toRead);
        TransformerFactory factory = TransformerFactory.newInstance();
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
    private static void populateAttributeValuesMap(Node node, String attributeName, Map<Node, String> toPopulate) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node attributeNode = attributes.getNamedItem(attributeName);
            if (attributeNode != null) {
                toPopulate.put(node, attributeNode.getNodeValue());
            }
        }
        final NodeList childNodes = node.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                populateAttributeValuesMap(childNodes.item(i), attributeName, toPopulate);
            }
        }
    }
}
