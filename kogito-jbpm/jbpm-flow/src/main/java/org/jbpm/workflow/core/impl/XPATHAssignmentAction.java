/*
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
package org.jbpm.workflow.core.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.jbpm.process.instance.impl.AssignmentAction;
import org.jbpm.process.instance.impl.AssignmentProducer;
import org.jbpm.workflow.core.node.Assignment;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

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

        XPathFactory factory = XPathFactory.newInstance();

        String from = assignment.getFrom().getExpression();
        XPath xpathFrom = factory.newXPath();
        XPathExpression exprFrom = null;

        XPath xpathTo = factory.newXPath();
        String to = assignment.getTo().getExpression();
        XPathExpression exprTo = xpathTo.compile(to);

        Object target = null;
        Object source = null;

        if (!sourcesDefinitions.isEmpty()) {
            // it means there is not expression (it is constant)
            source = sourceResolver.apply(sourcesDefinitions.get(0).getLabel());
            exprFrom = xpathFrom.compile(from);
        } else {
            source = assignment.getFrom().getExpression();
            exprFrom = xpathFrom.compile(".");
        }
        target = targetResolver.apply(targetDefinition.getLabel());

        // calculate node source. The outcome is Node type
        org.w3c.dom.Node sourceDOM = null;
        if (source instanceof org.w3c.dom.Node) {
            sourceDOM = (org.w3c.dom.Node) exprFrom.evaluate(source, XPathConstants.NODE);
        } else if (source instanceof String sourceString) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            sourceDOM = builder.parse(new InputSource(new ByteArrayInputStream(sourceString.getBytes()))).getFirstChild();
        }

        if (sourceDOM == null) {
            throw new RuntimeException("Nothing was selected by the from expression " + from + " on " + source);
        }

        // now compute the target either there is a parent node node or there is not
        if (target instanceof org.w3c.dom.Node parentNode) {
            org.w3c.dom.Node parent = parentNode.getParentNode();
            org.w3c.dom.Node targetElem = (org.w3c.dom.Node) exprTo.evaluate(parent, XPathConstants.NODE);
            if (targetElem == null) {
                throw new RuntimeException("Nothing was selected by the to expression " + to + " on " + target);
            }
            org.w3c.dom.Node n = targetElem.getOwnerDocument().importNode(sourceDOM, true);
            if (n instanceof Attr attr) {
                ((Element) targetElem).setAttributeNode(attr);
            } else {
                (targetElem).appendChild(n);
            }
            target = targetElem;
        } else if (org.w3c.dom.Node.class.getName().equals(targetDefinition.getType())) {
            target = sourceDOM;
        } else if (sourceDOM instanceof Attr) {
            target = ((Attr) sourceDOM).getValue();
        } else if (sourceDOM instanceof Text) {
            target = ((Text) sourceDOM).getWholeText();
        } else {
            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(sourceDOM), new StreamResult(writer));
            target = writer.toString();
        }

        producer.accept(targetDefinition.getLabel(), target);
    }

}
