package org.drools.xml.processes;

import org.drools.workflow.core.Node;
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
        	handleAction(node, element, eventType);
        }
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		StateNode milestoneNode = (StateNode) node;
		writeNode("state", milestoneNode, xmlDump, includeMeta);
        endNode(xmlDump);
	}

}
