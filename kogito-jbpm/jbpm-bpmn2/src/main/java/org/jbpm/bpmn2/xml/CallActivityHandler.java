/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CallActivityHandler extends AbstractNodeHandler {

    protected Node createNode(Attributes attrs) {
        return new SubProcessNode();
    }

    public Class<SubProcessNode> generateNodeFor() {
        return SubProcessNode.class;
    }

    protected Node handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        SubProcessNode subProcessNode = (SubProcessNode) node;
        String processId = element.getAttribute("calledElement");
        if (processId != null && processId.length() > 0) {
            subProcessNode.setProcessId(processId);
        } else {
            String processName = element.getAttribute("calledElementByName");
            subProcessNode.setProcessName(processName);
        }
        String waitForCompletion = element.getAttribute("waitForCompletion");
        if (waitForCompletion != null && "false".equals(waitForCompletion)) {
            subProcessNode.setWaitForCompletion(false);
        }
        String independent = element.getAttribute("independent");
        if (independent != null && "false".equals(independent)) {
            subProcessNode.setIndependent(false);
        }

        subProcessNode.setMetaData("DataInputs", new HashMap<String, String>());
        subProcessNode.setMetaData("DataOutputs", new HashMap<String, String>());

        handleScript(subProcessNode, element, "onEntry");
        handleScript(subProcessNode, element, "onExit");

        Node currentNode = subProcessNode;
        subProcessNode.setIoSpecification(readIOEspecification(parser, element));
        subProcessNode.setMultiInstanceSpecification(readMultiInstanceSpecification(parser, element, subProcessNode.getIoSpecification()));
        if (subProcessNode.getMultiInstanceSpecification().hasMultiInstanceInput()) {
            currentNode = decorateMultiInstanceSpecificationActivity(subProcessNode, subProcessNode.getMultiInstanceSpecification());
        }
        return currentNode;

    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        SubProcessNode subProcessNode = (SubProcessNode) node;
        writeNode("callActivity", subProcessNode, xmlDump, metaDataType);
        if (subProcessNode.getProcessId() != null) {
            xmlDump.append("calledElement=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(subProcessNode.getProcessId()) + "\" ");
        }
        if (!subProcessNode.isWaitForCompletion()) {
            xmlDump.append("tns:waitForCompletion=\"false\" ");
        }
        if (!subProcessNode.isIndependent()) {
            xmlDump.append("tns:independent=\"false\" ");
        }
        xmlDump.append(">" + EOL);
        writeExtensionElements(subProcessNode, xmlDump);
        writeIO(subProcessNode.getIoSpecification(), xmlDump);
        writeMultiInstance(subProcessNode.getMultiInstanceSpecification(), xmlDump);
        endNode("callActivity", xmlDump);
    }

}
