package org.drools.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.xml.XmlDumper;

public class MilestoneNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new MilestoneNode();
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return MilestoneNode.class;
    }

	public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
		MilestoneNode milestoneNode = (MilestoneNode) node;
		writeNode("milestone", milestoneNode, xmlDump, includeMeta);
        String constraint = milestoneNode.getConstraint();
        if (constraint != null || milestoneNode.getTimers() != null) {
            xmlDump.append(">\n");
            if (constraint != null) {
            	xmlDump.append("      <constraint type=\"rule\" dialect=\"mvel\" >"
            			+ XmlDumper.replaceIllegalChars(constraint.trim()) + "</constraint>" + EOL);
            }
            writeTimers(milestoneNode.getTimers(), xmlDump);
            endNode("milestone", xmlDump);
        } else {
            endNode(xmlDump);
        }
	}

}
