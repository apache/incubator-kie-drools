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

    public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
        CompositeNode compositeNode = (CompositeNode) node;
        writeNode("composite", compositeNode, xmlDump, includeMeta);
        xmlDump.append(">" + EOL);
        xmlDump.append("      <nodes>" + EOL);
        for (Node subNode: compositeNode.getNodes()) {
        	// filter out composite start and end nodes as they can be regenerated
        	if ((!(subNode instanceof CompositeNode.CompositeNodeStart)) &&
    			(!(subNode instanceof CompositeNode.CompositeNodeEnd))) {
        		XmlRuleFlowProcessDumper.visitNode(subNode, xmlDump, includeMeta);
        	}
        }
        xmlDump.append("      </nodes>" + EOL);
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
        xmlDump.append("      <connections>" + EOL);
        for (Connection connection: connections) {
        	XmlRuleFlowProcessDumper.visitConnection(connection, xmlDump, includeMeta);
        }
        xmlDump.append("      </connections>" + EOL);
        xmlDump.append("      <in-ports>" + EOL);
        for (Map.Entry<String, CompositeNode.NodeAndType> entry: compositeNode.getLinkedIncomingNodes().entrySet()) {
            xmlDump.append("        <in-port type=\"" + entry.getKey() + "\" nodeId=\"" + entry.getValue().getNodeId() + "\" nodeInType=\"" + entry.getValue().getType() + "\" />" + EOL);
        }
        xmlDump.append("      </in-ports>" + EOL);
        xmlDump.append("      <out-ports>" + EOL);
        for (Map.Entry<String, CompositeNode.NodeAndType> entry: compositeNode.getLinkedOutgoingNodes().entrySet()) {
            xmlDump.append("        <out-port type=\"" + entry.getKey() + "\" nodeId=\"" + entry.getValue().getNodeId() + "\" nodeOutType=\"" + entry.getValue().getType() + "\" />" + EOL);
        }
        xmlDump.append("      </out-ports>" + EOL);
        endNode("composite", xmlDump);
    }
}
