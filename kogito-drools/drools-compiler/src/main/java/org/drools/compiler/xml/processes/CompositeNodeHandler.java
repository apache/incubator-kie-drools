package org.drools.compiler.xml.processes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.compiler.xml.XmlRuleFlowProcessDumper;
import org.drools.compiler.xml.XmlWorkflowProcessDumper;
import org.drools.definition.process.Connection;
import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.CompositeContextNode;
import org.drools.workflow.core.node.CompositeNode;

public class CompositeNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        CompositeContextNode result = new CompositeContextNode();
        VariableScope variableScope = new VariableScope();
        result.addContext(variableScope);
        result.setDefaultContext(variableScope);
        return result;
    }

    public Class<?> generateNodeFor() {
        return CompositeNode.class;
    }
    
    public boolean allowNesting() {
    	return true;
    }
    
    protected String getNodeName() {
    	return "composite";
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
    	super.writeNode(getNodeName(), node, xmlDump, includeMeta);
        CompositeNode compositeNode = (CompositeNode) node;
        writeAttributes(compositeNode, xmlDump, includeMeta);
        xmlDump.append(">" + EOL);
    	for (String eventType: compositeNode.getActionTypes()) {
        	writeActions(eventType, compositeNode.getActions(eventType), xmlDump);
        }
        writeTimers(compositeNode.getTimers(), xmlDump);
        if (compositeNode instanceof CompositeContextNode) {
        	VariableScope variableScope = (VariableScope)
				((CompositeContextNode) compositeNode).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        	if (variableScope != null) {
        		List<Variable> variables = variableScope.getVariables();
        		XmlWorkflowProcessDumper.visitVariables(variables, xmlDump);
        	}
        	ExceptionScope exceptionScope = (ExceptionScope)
				((CompositeContextNode) compositeNode).getDefaultContext(ExceptionScope.EXCEPTION_SCOPE);
	    	if (exceptionScope != null) {
	    		XmlWorkflowProcessDumper.visitExceptionHandlers(
    				exceptionScope.getExceptionHandlers(), xmlDump);
	    	}
        }
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
    
    protected void writeAttributes(CompositeNode compositeNode, StringBuilder xmlDump, boolean includeMeta) {
    }
    
    protected List<Node> getSubNodes(CompositeNode compositeNode) {
    	List<Node> subNodes =
    		new ArrayList<Node>();
        for (org.drools.definition.process.Node subNode: compositeNode.getNodes()) {
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
        for (org.drools.definition.process.Node subNode: compositeNode.getNodes()) {
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
