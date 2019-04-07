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

import org.drools.compiler.compiler.xml.XmlDumper;
import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.node.StateNode;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class StateNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new StateNode();
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return StateNode.class;
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        StateNode stateNode = (StateNode) node;
        for (String eventType: stateNode.getActionTypes()) {
        	handleAction(stateNode, element, eventType);
        }
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		StateNode stateNode = (StateNode) node;
		writeNode("state", stateNode, xmlDump, includeMeta);
        xmlDump.append(">\n");
        if (includeMeta) {
        	writeMetaData(stateNode, xmlDump);
        }
    	for (String eventType: stateNode.getActionTypes()) {
        	writeActions(eventType, stateNode.getActions(eventType), xmlDump);
        }
        writeTimers(stateNode.getTimers(), xmlDump);
        if (!stateNode.getConstraints().isEmpty()) {
	        xmlDump.append("      <constraints>" + EOL);
	        for (Map.Entry<ConnectionRef, Constraint> entry: stateNode.getConstraints().entrySet()) {
	            ConnectionRef connection = entry.getKey();
	            Constraint constraint = entry.getValue();
	            xmlDump.append("        <constraint "
	                + "toNodeId=\"" + connection.getNodeId() + "\" ");
	            String name = constraint.getName();
	            if (name != null && !"".equals(name)) {
	                xmlDump.append("name=\"" + XmlDumper.replaceIllegalChars(constraint.getName()) + "\" ");
	            }
	            int priority = constraint.getPriority();
	            if (priority != 0) {
	                xmlDump.append("priority=\"" + constraint.getPriority() + "\" ");
	            }
	            String constraintString = constraint.getConstraint();
	            if (constraintString != null) {
	                xmlDump.append(">" + XmlDumper.replaceIllegalChars(constraintString) + "</constraint>" + EOL);
	            } else {
	                xmlDump.append("/>" + EOL);
	            }
	        }
	        xmlDump.append("      </constraints>" + EOL);
        }
        endNode("state", xmlDump);
	}

}
