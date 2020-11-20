/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.svg;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.jupiter.api.Test;
import org.kie.kogito.svg.processor.SVGProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

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
        assertEquals("", ((Element) svgDocument.getFirstChild()).getAttribute("width"));
        assertEquals("", ((Element) svgDocument.getFirstChild()).getAttribute("height"));
        assertEquals("0 0 1748 632", svgDocument.getFirstChild().getAttributes().getNamedItem("viewBox").getNodeValue());
    }

    private void validateNodesMarkedAsActive(Document svgDocument, List<String> activeNodes, String activeNodeBorderColor) throws XPathExpressionException {
        for (String activeNode : activeNodes) {

            XPathExpression expr = xpath.compile("//*[@bpmn2nodeid='" + activeNode + "']");
            Element element = (Element) expr.evaluate(svgDocument, XPathConstants.NODE);

            if (element == null) {
                fail("Active element " + activeNode + " not found in the document");
            }
            String svgId = element.getAttribute("id");

            Element border = svgDocument.getElementById(svgId + "?shapeType=BORDER&renderType=STROKE");

            String marker = border.getAttribute("stroke");
            assertNotNull(marker);
            assertEquals(activeNodeBorderColor, marker);
            String markerWidth = border.getAttribute("stroke-width");
            assertNotNull(markerWidth);
            assertEquals("2", markerWidth);
        }
    }

    private void validateNodesMarkedAsCompleted(Document svgDocument, List<String> completedNodes, String completedNodeColor) throws XPathExpressionException {

        for (String completedNode : completedNodes) {
            XPathExpression expr = xpath.compile("//*[@bpmn2nodeid='" + completedNode + "']");
            Element element = (Element) expr.evaluate(svgDocument, XPathConstants.NODE);

            if (element == null) {
                fail("Completed element " + completedNode + " not found in the document");
            }
            String svgId = element.getAttribute("id");
            Element background = svgDocument.getElementById(svgId + "?shapeType=BACKGROUND");

            String marker = background.getAttribute("fill");
            assertNotNull(marker);
            assertEquals(completedNodeColor, marker);
        }
    }

    private Document readSVG(String svgContent) throws IOException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        factory.setValidating(false);
        Document svgDocument = factory.createDocument("http://jbpm.org", new StringReader(svgContent));

        return svgDocument;
    }
}
