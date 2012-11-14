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

import org.drools.compiler.xml.XmlDumper;
import org.kie.definition.process.Connection;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.xml.sax.Attributes;

public class ForEachNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
    	throw new IllegalArgumentException("Reading in should be handled by end event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return ForEachNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
    	ForEachNode forEachNode = (ForEachNode) node;
    	writeNode("subProcess", forEachNode, xmlDump, metaDataType);
		xmlDump.append(" >" + EOL);
		// ioSpecification and dataInputAssociation 
        xmlDump.append(
            "      <ioSpecification>" + EOL);
        String parameterName = forEachNode.getVariableName();
        if (parameterName != null) {
        	xmlDump.append(
    			"        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(forEachNode) + "_input\" name=\"MultiInstanceInput\" />" + EOL);
        }
        xmlDump.append(
    		"        <inputSet/>" + EOL +
            "        <outputSet/>" + EOL +
            "      </ioSpecification>" + EOL);
        String collectionExpression = forEachNode.getCollectionExpression();
        if (collectionExpression != null) {
            xmlDump.append(
                "      <dataInputAssociation>" + EOL +
                "        <sourceRef>" + XmlDumper.replaceIllegalChars(collectionExpression) + "</sourceRef>" + EOL +
                "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(forEachNode) + "_input</targetRef>" + EOL +
                "      </dataInputAssociation>" + EOL);
        }
        // multiInstanceLoopCharacteristics
        xmlDump.append(
    		"      <multiInstanceLoopCharacteristics>" + EOL +
            "        <loopDataInputRef>" + XmlBPMNProcessDumper.getUniqueNodeId(forEachNode) + "_input</loopDataInputRef>" + EOL);
        if (parameterName != null) {
        	xmlDump.append("        <inputDataItem id=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(parameterName) + "\" itemSubjectRef=\"" + XmlBPMNProcessDumper.getUniqueNodeId(forEachNode) + "_multiInstanceItemType\"/>" + EOL);
        }
        xmlDump.append("      </multiInstanceLoopCharacteristics>" + EOL);
		// nodes
		List<Node> subNodes = getSubNodes(forEachNode);
    	xmlDump.append("    <!-- nodes -->" + EOL);
        for (Node subNode: subNodes) {
    		XmlBPMNProcessDumper.INSTANCE.visitNode(subNode, xmlDump, metaDataType);
        }
        // connections
        List<Connection> connections = getSubConnections(forEachNode);
    	xmlDump.append("    <!-- connections -->" + EOL);
        for (Connection connection: connections) {
        	XmlBPMNProcessDumper.INSTANCE.visitConnection(connection, xmlDump, metaDataType);
        }
		endNode("subProcess", xmlDump);
	}
	
	protected List<Node> getSubNodes(ForEachNode forEachNode) {
    	List<Node> subNodes =
    		new ArrayList<Node>();
        for (org.kie.definition.process.Node subNode: forEachNode.getNodes()) {
        	// filter out composite start and end nodes as they can be regenerated
        	if ((!(subNode instanceof CompositeNode.CompositeNodeStart)) &&
    			(!(subNode instanceof CompositeNode.CompositeNodeEnd))) {
        		subNodes.add((Node) subNode);
        	}
        }
        return subNodes;
    }
    
    protected List<Connection> getSubConnections(ForEachNode forEachNode) {
    	List<Connection> connections = new ArrayList<Connection>();
        for (org.kie.definition.process.Node subNode: forEachNode.getNodes()) {
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