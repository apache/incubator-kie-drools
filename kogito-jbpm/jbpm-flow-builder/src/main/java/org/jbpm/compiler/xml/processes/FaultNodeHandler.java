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

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.FaultNode;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class FaultNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new FaultNode();
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        FaultNode faultNode = (FaultNode) node;
        String faultName = element.getAttribute("faultName");
        if (faultName != null && faultName.length() != 0 ) {
            faultNode.setFaultName(faultName);
        }
        String faultVariable = element.getAttribute("faultVariable");
        if (faultVariable != null && !"".equals(faultVariable) ) {
            faultNode.setFaultVariable(faultVariable);
        }
    }

    public Class generateNodeFor() {
        return FaultNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		FaultNode faultNode = (FaultNode) node;
		writeNode("fault", faultNode, xmlDump, includeMeta);
		String faultName = faultNode.getFaultName();
		if (faultName != null && faultName.length() != 0) {
            xmlDump.append("faultName=\"" + faultName + "\" ");
        }
		String faultVariable = faultNode.getFaultVariable();
		if (faultVariable != null && faultVariable.length() != 0) {
            xmlDump.append("faultVariable=\"" + faultVariable + "\" ");
        }
        if (includeMeta && containsMetaData(faultNode)) {
        	xmlDump.append(">" + EOL);
        	writeMetaData(faultNode, xmlDump);
        	endNode("fault", xmlDump);
        } else {
            endNode(xmlDump);
        }
	}

}
