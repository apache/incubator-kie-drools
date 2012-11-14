/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2.xml;

import java.util.List;

import org.kie.definition.process.Connection;
import org.drools.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.DynamicNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AdHocSubProcessHandler extends CompositeContextNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        DynamicNode result = new DynamicNode();
        VariableScope variableScope = new VariableScope();
        result.addContext(variableScope);
        result.setDefaultContext(variableScope);
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return DynamicNode.class;
    }
    
    @SuppressWarnings("unchecked")
	protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	DynamicNode dynamicNode = (DynamicNode) node;
    	String cancelRemainingInstances = element.getAttribute("cancelRemainingInstances");
    	if ("false".equals(cancelRemainingInstances)) {
    		dynamicNode.setCancelRemainingInstances(false);
    	}
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	String nodeName = xmlNode.getNodeName();
        	if ("completionCondition".equals(nodeName)) {
        		String expression = xmlNode.getTextContent();
        		if ("getActivityInstanceAttribute(\"numberOfActiveInstances\") == 0".equals(expression)) {
        			dynamicNode.setAutoComplete(true);
        		}
        	}
        	xmlNode = xmlNode.getNextSibling();
        }
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			dynamicNode.getMetaData(ProcessHandler.CONNECTIONS);
    	ProcessHandler.linkConnections(dynamicNode, connections);
    	ProcessHandler.linkBoundaryEvents(dynamicNode);
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        DynamicNode dynamicNode = (DynamicNode) node;
		writeNode("adHocSubProcess", dynamicNode, xmlDump, metaDataType);
		if (!dynamicNode.isCancelRemainingInstances()) {
			xmlDump.append(" cancelRemainingInstances=\"false\"");
		}
		xmlDump.append(" ordering=\"Parallel\" >" + EOL);
		// nodes
		List<Node> subNodes = getSubNodes(dynamicNode);
    	xmlDump.append("    <!-- nodes -->" + EOL);
        for (Node subNode: subNodes) {
    		XmlBPMNProcessDumper.INSTANCE.visitNode(subNode, xmlDump, metaDataType);
        }
        // connections
        List<Connection> connections = getSubConnections(dynamicNode);
    	xmlDump.append("    <!-- connections -->" + EOL);
        for (Connection connection: connections) {
        	XmlBPMNProcessDumper.INSTANCE.visitConnection(connection, xmlDump, metaDataType);
        }
        if (dynamicNode.isAutoComplete()) {
        	xmlDump.append("    <completionCondition xsi:type=\"tFormalExpression\">getActivityInstanceAttribute(\"numberOfActiveInstances\") == 0</completionCondition>" + EOL);
        }
		endNode("adHocSubProcess", xmlDump);
	}

}