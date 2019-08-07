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

public class SAXParserUtilTest {

    private static final String XML = "<Main>" +
            "<testnode>testnodecontent</testnode>" +
            "<child>" +
            "<testnode>toremove1</testnode>" +
            "<othernode>othernodecontent1</othernode>" +
            "</child>" +
            "<child>" +
            "<testnode>toremove2</testnode>" +
            "<othernode>othernodecontent2</othernode>" +
            "</child>" +
            "</Main>";

    @Test
    public void cleanupNodes() {
        try {
            String retrieved = SAXParserUtil.cleanupNodes(XML, "child", "testnode");
            assertNotNull(retrieved);
            Map<Node, List<Node>> childrenNodes = SAXParserUtil.getChildrenNodes(retrieved, "Main", "testnode");
            assertNotNull(childrenNodes);
            assertEquals(1, childrenNodes.size());
            Node keyNode = childrenNodes.keySet().iterator().next();
            assertEquals("Main", keyNode.getNodeName());
            List<Node> valueNodes = childrenNodes.get(keyNode);
            assertTrue(valueNodes != null && valueNodes.size() == 1);
            assertEquals("testnode", valueNodes.get(0).getNodeName());

            childrenNodes = SAXParserUtil.getChildrenNodes(retrieved, "child", "othernode");
            assertEquals(2, childrenNodes.size());
            childrenNodes.forEach((childKeyNode, childValueNodes) -> {
                assertNotNull(childKeyNode);
                assertEquals("child", childKeyNode.getNodeName());
                assertTrue(childValueNodes != null && childValueNodes.size() == 1);
                assertEquals("othernode", childValueNodes.get(0).getNodeName());
            });

            childrenNodes = SAXParserUtil.getChildrenNodes(retrieved, "child", "testnode");
            childrenNodes.forEach((childKeyNode, childValueNodes) -> {
                assertNotNull(childKeyNode);
                assertEquals("child", childKeyNode.getNodeName());
                assertTrue(childValueNodes != null && childValueNodes.isEmpty());
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getChildrenNodes() {
        try {
            Map<Node, List<Node>> retrieved = SAXParserUtil.getChildrenNodes(XML, "Main", "testnode");
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            Node keyNode = retrieved.keySet().iterator().next();
            assertNotNull(keyNode);
            assertEquals("Main", keyNode.getNodeName());
            List<Node> valueNodes = retrieved.get(keyNode);
            assertTrue(valueNodes != null && valueNodes.size() == 1);
            assertEquals("testnode", valueNodes.get(0).getNodeName());
            retrieved = SAXParserUtil.getChildrenNodes(XML, "Main", "child");
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            keyNode = retrieved.keySet().iterator().next();
            assertNotNull(keyNode);
            assertEquals("Main", keyNode.getNodeName());
            valueNodes = retrieved.get(keyNode);
            assertTrue(valueNodes != null && valueNodes.size() == 2);
            valueNodes.forEach(childNode -> assertEquals("child", childNode.getNodeName()));
            List<String> nodeToTest = Arrays.asList("testnode", "othernode");
            for (String childNodeName : nodeToTest) {
                retrieved = SAXParserUtil.getChildrenNodes(XML, "child", childNodeName);
                assertNotNull(retrieved);
                assertEquals(2, retrieved.size());
                retrieved.forEach((childKeyNode, childValueNodes) -> {
                    assertNotNull(childKeyNode);
                    assertEquals("child", childKeyNode.getNodeName());
                    assertTrue(childValueNodes != null && childValueNodes.size() == 1);
                    assertEquals(childNodeName, childValueNodes.get(0).getNodeName());
                });
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getDocument() {
        try {
            Document retrieved = SAXParserUtil.getDocument(XML);
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
            String retrieved = SAXParserUtil.getString(document);
            assertNotNull(retrieved);
            assertTrue(retrieved.contains("CREATED"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
