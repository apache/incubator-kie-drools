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

import java.util.ArrayList;
import java.util.List;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.kie.definition.process.Connection;
import org.xml.sax.Attributes;

public class CompositeContextNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
    	throw new IllegalArgumentException("Reading in should be handled by end event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return CompositeContextNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
    	CompositeContextNode compositeNode = (CompositeContextNode) node;
    	String nodeType = "subProcess";
    	if (node.getMetaData().get("Transaction") != null) {
    		nodeType = "transaction";
    	}
		writeNode(nodeType, compositeNode, xmlDump, metaDataType);
		xmlDump.append(" >" + EOL);
        // variables
		VariableScope variableScope = (VariableScope) 
            compositeNode.getDefaultContext(VariableScope.VARIABLE_SCOPE);
		if (variableScope != null && !variableScope.getVariables().isEmpty()) {
            xmlDump.append("    <!-- variables -->" + EOL);
            for (Variable variable: variableScope.getVariables()) {
                xmlDump.append("    <property id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(variable.getName()) + "\" ");
                if (variable.getType() != null) {
                    xmlDump.append("itemSubjectRef=\"" + XmlBPMNProcessDumper.getUniqueNodeId(compositeNode) + "-" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(variable.getName()) + "Item\"" );
                }
                // TODO: value
                xmlDump.append("/>" + EOL);
            }
		}
		// nodes
		List<Node> subNodes = getSubNodes(compositeNode);
    	xmlDump.append("    <!-- nodes -->" + EOL);
        for (Node subNode: subNodes) {
    		XmlBPMNProcessDumper.INSTANCE.visitNode(subNode, xmlDump, metaDataType);
        }
        // connections
        List<Connection> connections = getSubConnections(compositeNode);
    	xmlDump.append("    <!-- connections -->" + EOL);
        for (Connection connection: connections) {
        	XmlBPMNProcessDumper.INSTANCE.visitConnection(connection, xmlDump, metaDataType);
        }
		endNode(nodeType, xmlDump);
	}
	
	protected List<Node> getSubNodes(CompositeNode compositeNode) {
    	List<Node> subNodes =
    		new ArrayList<Node>();
        for (org.kie.definition.process.Node subNode: compositeNode.getNodes()) {
        	// filter out composite start and end nodes as they can be regenerated
        	if ((!(subNode instanceof CompositeNode.CompositeNodeStart)) &&
    			(!(subNode instanceof CompositeNode.CompositeNodeEnd))) {
        		subNodes.add((Node) subNode);
        	}
        }
        return subNodes;
    }
    
    protected List<Connection> getSubConnections(CompositeNode compositeNode) {
    	List<Connection> connections = new ArrayList<Connection>();
        for (org.kie.definition.process.Node subNode: compositeNode.getNodes()) {
        	// filter out composite start and end nodes as they can be regenerated
            if (!(subNode instanceof CompositeNode.CompositeNodeEnd)) {
                for (Connection connection: subNode.getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE)) {
                    if (!(connection.getFrom() instanceof CompositeNode.CompositeNodeStart)) {
                        connections.add(connection);
                    }
                }
            }
        }
        return connections;
    }

}