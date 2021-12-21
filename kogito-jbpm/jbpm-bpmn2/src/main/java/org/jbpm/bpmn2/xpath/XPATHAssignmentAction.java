/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2.xpath;

import java.util.List;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.jbpm.process.instance.impl.AssignmentAction;
import org.jbpm.process.instance.impl.AssignmentProducer;
import org.jbpm.workflow.core.impl.DataDefinition;
import org.jbpm.workflow.core.node.Assignment;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XPATHAssignmentAction implements AssignmentAction {

    private Assignment assignment;
    private List<DataDefinition> sourcesDefinitions;
    private DataDefinition targetDefinition;

    public XPATHAssignmentAction(Assignment assignment,
            List<DataDefinition> sources, DataDefinition target) {
        this.assignment = assignment;
        this.sourcesDefinitions = sources;
        this.targetDefinition = target;
    }

    public void execute(Function<String, Object> sourceResolver, Function<String, Object> targetResolver, AssignmentProducer producer) throws Exception {

        String from = assignment.getFrom().getExpression();
        String to = assignment.getTo().getExpression();

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpathFrom = factory.newXPath();

        XPathExpression exprFrom = xpathFrom.compile(from);

        XPath xpathTo = factory.newXPath();

        XPathExpression exprTo = xpathTo.compile(to);

        Object target = null;
        Object source = null;

        if (!sourcesDefinitions.isEmpty()) {
            source = sourceResolver.apply(sourcesDefinitions.get(0).getLabel());
        } else {
            source = assignment.getFrom().getExpression();
        }
        target = targetResolver.apply(targetDefinition.getLabel());

        Object targetElem = null;

        // now pick the leaf for this operation
        if (target != null) {
            org.w3c.dom.Node parent;
            parent = ((org.w3c.dom.Node) target).getParentNode();

            targetElem = exprTo.evaluate(parent, XPathConstants.NODE);

            if (targetElem == null) {
                throw new RuntimeException("Nothing was selected by the to expression " + to + " on " + target);
            }
        }
        NodeList nl = null;
        if (source instanceof org.w3c.dom.Node) {
            nl = (NodeList) exprFrom.evaluate(source, XPathConstants.NODESET);
        } else if (source instanceof String) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            //quirky: create a temporary element, use its nodelist
            Element temp = doc.createElementNS(null, "temp");
            temp.appendChild(doc.createTextNode((String) source));
            nl = temp.getChildNodes();
        } else if (source == null) {
            // don't throw errors yet ?
            throw new RuntimeException("Source value was null for source " + source);
        }

        if (nl == null || nl.getLength() == 0) {
            throw new RuntimeException("Nothing was selected by the from expression " + from + " on " + source);
        }
        for (int i = 0; i < nl.getLength(); i++) {

            if (!(targetElem instanceof org.w3c.dom.Node)) {
                if (nl.item(i) instanceof Attr) {
                    targetElem = ((Attr) nl.item(i)).getValue();
                } else if (nl.item(i) instanceof Text) {
                    targetElem = ((Text) nl.item(i)).getWholeText();
                } else {
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document doc = builder.newDocument();
                    targetElem = doc.importNode(nl.item(i), true);
                }
                target = targetElem;
            } else {
                org.w3c.dom.Node n = ((org.w3c.dom.Node) targetElem).getOwnerDocument().importNode(nl.item(i), true);
                if (n instanceof Attr) {
                    ((Element) targetElem).setAttributeNode((Attr) n);
                } else {
                    ((org.w3c.dom.Node) targetElem).appendChild(n);
                }
            }
        }

        producer.accept(targetDefinition.getLabel(), target);
    }

}
