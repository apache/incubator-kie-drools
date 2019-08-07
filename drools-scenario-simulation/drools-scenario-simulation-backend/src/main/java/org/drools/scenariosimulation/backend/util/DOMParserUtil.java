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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class used to provide parsing methods
 */
public class DOMParserUtil {

    /**
     * This method remove the <b>nodeToRemoveName</b> <code>Node</code>s from all the <b>containerTagName</b> Elements found inside the given <b>fullXml</b>
     * @param fullXml
     * @param containerTagName
     * @param nodeToRemoveName
     * @return
     */
    public static String cleanupNodes(String fullXml, String containerTagName, String nodeToRemoveName) throws Exception {
        Document document = getDocument(fullXml);
        final NodeList nodeList = document.getElementsByTagName(containerTagName);
        // iterate all the "container" nodes
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            // iterate all nodes inside the container one
            final NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if (childNode.getNodeName().equals(nodeToRemoveName)) {
                    node.removeChild(childNode);
                }
            }
        }
        return getString(document);
    }

    public static Map<Node, List<Node>> getChildrenNodes(String fullXml, String containerTagName, String childNodeName) throws Exception {
        Document document = getDocument(fullXml);
        Map<Node, List<Node>> toReturn = new HashMap<>();
        final NodeList nodeList = document.getElementsByTagName(containerTagName);
        // iterate all the "container" nodes
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            // iterate all nodes inside the container one
            final NodeList childNodes = node.getChildNodes();
            if (childNodes.getLength() > 0) {
                List<Node> value = new ArrayList<>();
                toReturn.put(node, value);
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);
                    if (childNode.getNodeName().equals(childNodeName)) {
                        value.add(childNode);
                    }
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
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        return dBuilder.parse(inputStream);
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
}
