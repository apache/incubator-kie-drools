/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.svg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.svg.processor.SVGProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;

public class SvgTransformationTest {

    private XPath xpath = XPathFactory.newInstance().newXPath();

    public static InputStream readTestFileContent() {
        return SvgTransformationTest.class.getResourceAsStream("/META-INF/processSVG/travels.svg");
    }

    @Test
    public void transformTest() throws Exception {
        List<String> completed = new ArrayList<String>();
        completed.add("_1A708F87-11C0-42A0-A464-0B7E259C426F");
        List<String> active = new ArrayList<String>();
        active.add("_24FBB8D6-EF2D-4DCC-846D-D8C5E21849D2");
        String svg = SVGImageProcessor.transform(readTestFileContent(), completed, active);

        // verify transformation
        Document svgDocument = readSVG(svg);
        validateNodesMarkedAsActive(svgDocument, active, SVGProcessor.ACTIVE_BORDER_COLOR);
        validateNodesMarkedAsCompleted(svgDocument, completed, SVGProcessor.COMPLETED_COLOR);
    }

    @Test
    public void testCompletedAndActive() throws Exception {
        List<String> completed = new ArrayList<String>();
        completed.add("_1A708F87-11C0-42A0-A464-0B7E259C426F");
        completed.add("_24FBB8D6-EF2D-4DCC-846D-D8C5E21849D2");
        List<String> active = new ArrayList<String>();
        active.add("_24FBB8D6-EF2D-4DCC-846D-D8C5E21849D2");
        String svg = SVGImageProcessor.transform(readTestFileContent(), completed, active);

        // verify transformation
        Document svgDocument = readSVG(svg);
        validateNodesMarkedAsActive(svgDocument, active, SVGProcessor.ACTIVE_BORDER_COLOR);
        // remove it as it should be not considered completed and was already asserted as active
        completed.remove("_24FBB8D6-EF2D-4DCC-846D-D8C5E21849D2");
        validateNodesMarkedAsCompleted(svgDocument, completed, SVGProcessor.COMPLETED_COLOR);
    }

    @Test
    public void testCustomColor() throws Exception {
        String completedNodeColor = "#888888";
        String completedNodeBorderColor = "#888887";
        String activeNodeBorderColor = "#888886";
        List<String> completed = new ArrayList<String>();
        completed.add("_1A708F87-11C0-42A0-A464-0B7E259C426F");
        List<String> active = new ArrayList<String>();
        active.add("_24FBB8D6-EF2D-4DCC-846D-D8C5E21849D2");
        String svg = SVGImageProcessor.transform(readTestFileContent(),
                completed, active, null, completedNodeColor,
                completedNodeBorderColor, activeNodeBorderColor);

        // verify transformation
        Document svgDocument = readSVG(svg);
        validateNodesMarkedAsActive(svgDocument, active, activeNodeBorderColor);
        validateNodesMarkedAsCompleted(svgDocument, completed, completedNodeColor);
    }

    // helper methods for verifying svg transformation

    @Test
    public void testViewBoxAttributeAddition() throws Exception {
        List<String> completed = new ArrayList<String>();
        completed.add("_1A708F87-11C0-42A0-A464-0B7E259C426F");
        List<String> active = new ArrayList<String>();
        active.add("_24FBB8D6-EF2D-4DCC-846D-D8C5E21849D2");
        Map<String, String> links = new HashMap<>();
        links.put("_1A708F87-11C0-42A0-A464-0B7E259C426F", "http://localhost/svg/processes/1");

        String svg = SVGImageProcessor.transform(readTestFileContent(),
                completed, active, links, "#888888",
                "#888887", "#888886");

        Document svgDocument = readSVG(svg);
        assertThat(((Element) svgDocument.getFirstChild()).getAttribute("width")).isEmpty();
        assertThat(((Element) svgDocument.getFirstChild()).getAttribute("height")).isEmpty();
        assertThat(svgDocument.getFirstChild().getAttributes().getNamedItem("viewBox").getNodeValue()).isEqualTo("0 0 1748 632");
    }

    private void validateNodesMarkedAsActive(Document svgDocument, List<String> activeNodes, String activeNodeBorderColor) throws XPathExpressionException {
        for (String activeNode : activeNodes) {

            XPathExpression expr = xpath.compile("//*[@bpmn2nodeid='" + activeNode + "']");
            Element element = (Element) expr.evaluate(svgDocument, XPathConstants.NODE);

            if (element == null) {
                Assertions.fail("", "Active element " + activeNode + " not found in the document");
            }
            String svgId = element.getAttribute("id") + "?shapeType=BORDER&renderType=STROKE";

            XPathExpression expr2 = xpath.compile("//*[@id='" + svgId + "']");
            Element border = (Element) expr2.evaluate(svgDocument, XPathConstants.NODE);

            String marker = border.getAttribute("stroke");
            assertThat(marker).isNotNull()
                    .isEqualTo(activeNodeBorderColor);
            String markerWidth = border.getAttribute("stroke-width");
            assertThat(markerWidth).isNotNull()
                    .isEqualTo("2");
        }
    }

    private void validateNodesMarkedAsCompleted(Document svgDocument, List<String> completedNodes, String completedNodeColor) throws XPathExpressionException {

        for (String completedNode : completedNodes) {
            XPathExpression expr = xpath.compile("//*[@bpmn2nodeid='" + completedNode + "']");
            Element element = (Element) expr.evaluate(svgDocument, XPathConstants.NODE);

            if (element == null) {
                Assertions.fail("", "Completed element " + completedNode + " not found in the document");
            }
            String svgId = element.getAttribute("id") + "?shapeType=BACKGROUND";

            XPathExpression expr2 = xpath.compile("//*[@id='" + svgId + "']");
            Element background = (Element) expr2.evaluate(svgDocument, XPathConstants.NODE);

            String marker = background.getAttribute("fill");
            assertThat(marker).isNotNull()
                    .isEqualTo(completedNodeColor);
        }
    }

    private Document readSVG(String svgContent) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document svgDocument = builder.parse(new ByteArrayInputStream(svgContent.getBytes()));

        return svgDocument;
    }
}
