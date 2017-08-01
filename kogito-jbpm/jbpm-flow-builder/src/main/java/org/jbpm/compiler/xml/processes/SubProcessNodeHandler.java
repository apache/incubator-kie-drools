/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.compiler.xml.processes;

import java.util.Map;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class SubProcessNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new SubProcessNode();
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        SubProcessNode subProcessNode = (SubProcessNode) node;
        String processId = element.getAttribute("processId");
        if (processId != null && processId.length() > 0) {
        	subProcessNode.setProcessId(processId);
        }
        String waitForCompletion = element.getAttribute("waitForCompletion");
        subProcessNode.setWaitForCompletion(!"false".equals(waitForCompletion));
        String independent = element.getAttribute("independent");
        subProcessNode.setIndependent(!"false".equals(independent));
        for (String eventType: subProcessNode.getActionTypes()) {
        	handleAction(subProcessNode, element, eventType);
        }
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return SubProcessNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		SubProcessNode subProcessNode = (SubProcessNode) node;
		writeNode("subProcess", subProcessNode, xmlDump, includeMeta);
        String processId = subProcessNode.getProcessId();
        if (processId != null) {
            xmlDump.append("processId=\"" + processId + "\" ");
        }
        if (!subProcessNode.isWaitForCompletion()) {
            xmlDump.append("waitForCompletion=\"false\" ");
        }
        if (!subProcessNode.isIndependent()) {
            xmlDump.append("independent=\"false\" ");
        }
        xmlDump.append(">" + EOL);
        if (includeMeta) {
        	writeMetaData(subProcessNode, xmlDump);
        }
        Map<String, String> inMappings = subProcessNode.getInMappings();
        for (Map.Entry<String, String> inMapping: inMappings.entrySet()) {
            xmlDump.append(
                "      <mapping type=\"in\" "
                             + "from=\"" + inMapping.getValue() + "\" "
                             + "to=\"" + inMapping.getKey() + "\" />" + EOL);
        }
        Map<String, String> outMappings = subProcessNode.getOutMappings();
        for (Map.Entry<String, String> outMapping: outMappings.entrySet()) {
            xmlDump.append(
                "      <mapping type=\"out\" "
                             + "from=\"" + outMapping.getKey() + "\" "
                             + "to=\"" + outMapping.getValue() + "\" />" + EOL);
        }
        for (String eventType: subProcessNode.getActionTypes()) {
        	writeActions(eventType, subProcessNode.getActions(eventType), xmlDump);
        }
        writeTimers(subProcessNode.getTimers(), xmlDump);
        endNode("subProcess", xmlDump);
	}

}
