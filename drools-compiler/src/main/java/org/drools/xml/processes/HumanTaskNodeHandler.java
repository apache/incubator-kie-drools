package org.drools.xml.processes;

import org.drools.process.core.Work;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.HumanTaskNode;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class HumanTaskNodeHandler extends WorkItemNodeHandler {

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        HumanTaskNode humanTaskNode = (HumanTaskNode) node;
        final String swimlane = element.getAttribute("swimlane");
        if (swimlane != null && !"".equals(swimlane)) {
            humanTaskNode.setSwimlane(swimlane);
        }
    }

    protected Node createNode() {
        return new HumanTaskNode();
    }

    public Class<?> generateNodeFor() {
        return HumanTaskNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        WorkItemNode workItemNode = (WorkItemNode) node;
        writeNode("humanTask", workItemNode, xmlDump, includeMeta);
        visitParameters(workItemNode, xmlDump);
        xmlDump.append(">" + EOL);
        Work work = workItemNode.getWork();
        visitWork(work, xmlDump, includeMeta);
        visitInMappings(workItemNode.getInMappings(), xmlDump);
        visitOutMappings(workItemNode.getOutMappings(), xmlDump);
        for (String eventType: workItemNode.getActionTypes()) {
        	writeActions(eventType, workItemNode.getActions(eventType), xmlDump);
        }
        writeTimers(workItemNode.getTimers(), xmlDump);
        endNode("humanTask", xmlDump);
    }
    
	protected void visitParameters(WorkItemNode workItemNode, StringBuilder xmlDump) {
	    super.visitParameters(workItemNode, xmlDump);
	    HumanTaskNode humanTaskNode = (HumanTaskNode) workItemNode;
	    String swimlane = humanTaskNode.getSwimlane();
	    if (swimlane != null) {
	        xmlDump.append("swimlane=\"" + swimlane + "\" ");
	    }
	}
    
}
