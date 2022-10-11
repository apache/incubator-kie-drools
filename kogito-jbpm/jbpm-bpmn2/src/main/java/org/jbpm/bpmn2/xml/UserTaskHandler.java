/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2.xml;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.compiler.xml.Parser;
import org.jbpm.process.core.Work;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class UserTaskHandler extends TaskHandler {

    @Override
    protected Node createNode(Attributes attrs) {
        return new HumanTaskNode();
    }

    @Override
    public Class<HumanTaskNode> generateNodeFor() {
        return HumanTaskNode.class;
    }

    @Override
    protected Node handleNode(final Node node, final Element element, final String uri,
            final String localName, final Parser parser) throws SAXException {
        Node currentNode = super.handleNode(node, element, uri, localName, parser);
        HumanTaskNode humanTaskNode = (HumanTaskNode) node;
        Work work = humanTaskNode.getWork();
        work.setName("Human Task");

        setParameter(work, "Description", humanTaskNode.getIoSpecification().getDataInputAssociation());
        setParameter(work, "Comment", humanTaskNode.getIoSpecification().getDataInputAssociation());
        setParameter(work, "ActorId", humanTaskNode.getIoSpecification().getDataInputAssociation());
        setParameter(work, "GroupId", humanTaskNode.getIoSpecification().getDataInputAssociation());
        setParameter(work, "Priority", humanTaskNode.getIoSpecification().getDataInputAssociation());
        setParameter(work, "Skippable", humanTaskNode.getIoSpecification().getDataInputAssociation());
        setParameter(work, "Content", humanTaskNode.getIoSpecification().getDataInputAssociation());

        List<String> owners = new ArrayList<>();
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            // ioSpec and data{Input,Output}Spec handled in super.handleNode(...)
            if ("potentialOwner".equals(nodeName)) {
                String owner = readPotentialOwner(xmlNode, humanTaskNode);
                if (owner != null) {
                    owners.add(owner);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
        if (!owners.isEmpty()) {
            String owner = owners.get(0);
            for (int i = 1; i < owners.size(); i++) {
                owner += "," + owners.get(i);
            }
            humanTaskNode.getWork().setParameter("ActorId", owner);
        }

        return currentNode;
    }

    @Override
    public Object end(String uri, String localName, Parser parser) throws SAXException {
        return super.end(uri, localName, parser);
    }

    protected String readPotentialOwner(org.w3c.dom.Node xmlNode, HumanTaskNode humanTaskNode) {
        org.w3c.dom.Node node = xmlNode.getFirstChild();
        if (node != null) {
            node = node.getFirstChild();
            if (node != null) {
                node = node.getFirstChild();
                if (node != null) {
                    return node.getTextContent();
                }
            }
        }
        return null;
    }

    @Override
    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        HumanTaskNode humanTaskNode = (HumanTaskNode) node;
        writeNode("userTask", humanTaskNode, xmlDump, metaDataType);
        xmlDump.append(">" + EOL);
        writeExtensionElements(humanTaskNode, xmlDump);
        writeIO(humanTaskNode.getIoSpecification(), xmlDump);
        writeMultiInstance(humanTaskNode.getMultiInstanceSpecification(), xmlDump);
        String ownerString = (String) humanTaskNode.getWork().getParameter("ActorId");
        if (ownerString != null) {
            String[] owners = ownerString.split(",");
            for (String owner : owners) {
                xmlDump.append(
                        "      <potentialOwner>" + EOL +
                                "        <resourceAssignmentExpression>" + EOL +
                                "          <formalExpression>" + owner + "</formalExpression>" + EOL +
                                "        </resourceAssignmentExpression>" + EOL +
                                "      </potentialOwner>" + EOL);
            }
        }
        endNode("userTask", xmlDump);
    }

}
