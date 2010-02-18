package org.drools.compiler.xml.processes;

import java.util.Map;

import org.drools.compiler.xml.XmlDumper;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionRef;
import org.drools.workflow.core.node.StateNode;
import org.drools.xml.ExtensibleXmlParser;
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
