package org.drools.xml.processes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.CompositeNode;
import org.drools.xml.XmlRuleFlowProcessDumper;

public class CompositeNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new CompositeNode();
    }

    public Class generateNodeFor() {
        return CompositeNode.class;
    }
    
    protected String getNodeName() {
    	return "composite";
    }

    public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
    	super.writeNode(getNodeName(), node, xmlDump, includeMeta);
        CompositeNode compositeNode = (CompositeNode) node;
        writeAttributes(compositeNode, xmlDump, includeMeta);
        xmlDump.append(">" + EOL);
        List<Node> subNodes = getSubNodes(compositeNode);
        xmlDump.append("      <nodes>" + EOL);
        for (Node subNode: subNodes) {
    		XmlRuleFlowProcessDumper.INSTANCE.visitNode(subNode, xmlDump, includeMeta);
        }
        xmlDump.append("      </nodes>" + EOL);
        List<Connection> connections = getSubConnections(compositeNode);
        xmlDump.append("      <connections>" + EOL);
        for (Connection connection: connections) {
        	XmlRuleFlowProcessDumper.INSTANCE.visitConnection(connection, xmlDump, includeMeta);
        }
        xmlDump.append("      </connections>" + EOL);
        Map<String, CompositeNode.NodeAndType> inPorts = getInPorts(compositeNode);
        xmlDump.append("      <in-ports>" + EOL);
        for (Map.Entry<String, CompositeNode.NodeAndType> entry: inPorts.entrySet()) {
            xmlDump.append("        <in-port type=\"" + entry.getKey() + "\" nodeId=\"" + entry.getValue().getNodeId() + "\" nodeInType=\"" + entry.getValue().getType() + "\" />" + EOL);
        }
        xmlDump.append("      </in-ports>" + EOL);
        Map<String, CompositeNode.NodeAndType> outPorts = getOutPorts(compositeNode);
        xmlDump.append("      <out-ports>" + EOL);
        for (Map.Entry<String, CompositeNode.NodeAndType> entry: outPorts.entrySet()) {
            xmlDump.append("        <out-port type=\"" + entry.getKey() + "\" nodeId=\"" + entry.getValue().getNodeId() + "\" nodeOutType=\"" + entry.getValue().getType() + "\" />" + EOL);
        }
        xmlDump.append("      </out-ports>" + EOL);
        endNode(getNodeName(), xmlDump);
    }
    
    protected void writeAttributes(CompositeNode compositeNode, StringBuffer xmlDump, boolean includeMeta) {
    }
    
    protected List<Node> getSubNodes(CompositeNode compositeNode) {
    	List<Node> subNodes = new ArrayList<Node>();
        for (Node subNode: compositeNode.getNodes()) {
        	// filter out composite start and end nodes as they can be regenerated
        	if ((!(subNode instanceof CompositeNode.CompositeNodeStart)) &&
    			(!(subNode instanceof CompositeNode.CompositeNodeEnd))) {
        		subNodes.add(subNode);
        	}
        }
        return subNodes;
    }
    
    protected List<Connection> getSubConnections(CompositeNode compositeNode) {
    	List<Connection> connections = new ArrayList<Connection>();
        for (Node subNode: compositeNode.getNodes()) {
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
    
    protected Map<String, CompositeNode.NodeAndType> getInPorts(CompositeNode compositeNode) {
    	return compositeNode.getLinkedIncomingNodes();
    }
    
    protected Map<String, CompositeNode.NodeAndType> getOutPorts(CompositeNode compositeNode) {
    	return compositeNode.getLinkedOutgoingNodes();
    }

}
