package org.drools.xml.processes;

import java.util.Map;

import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.StateNode;
import org.drools.workflow.core.node.StateNode.ConnectionRef;
import org.drools.xml.XmlDumper;

public class StateNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new StateNode();
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return StateNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		StateNode stateNode = (StateNode) node;
		writeNode("state", stateNode, xmlDump, includeMeta);
        xmlDump.append(">\n");
        if (!stateNode.getConstraints().isEmpty()) {
	        xmlDump.append("      <constraints>" + EOL);
	        for (Map.Entry<ConnectionRef, Constraint> entry: stateNode.getConstraints().entrySet()) {
	            ConnectionRef connection = entry.getKey();
	            Constraint constraint = entry.getValue();
	            xmlDump.append("        <constraint "
	                + "toNodeId=\"" + connection.getNodeId() + "\" "
	                + "toType=\"" + connection.getToType() + "\" ");
	            String name = constraint.getName();
	            if (name != null && !"".equals(name)) {
	                xmlDump.append("name=\"" + XmlDumper.replaceIllegalChars(constraint.getName()) + "\" ");
	            }
	            int priority = constraint.getPriority();
	            if (priority != 0) {
	                xmlDump.append("priority=\"" + constraint.getPriority() + "\" ");
	            }
	            xmlDump.append("type=\"rule\" dialect=\"mvel\" ");
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
