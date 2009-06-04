package org.drools.xml.processes;

import java.util.Map;
import java.util.Set;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.NewStateNode;
import org.drools.workflow.core.node.StateNode;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.XmlDumper;
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
		StateNode stateNode = (StateNode) node;
		writeNode("state", stateNode, xmlDump, includeMeta);
        Map<String,Constraint> constraints = stateNode.getConstraints();
        if (constraints != null || stateNode.getTimers() != null || stateNode.containsActions()) {
            xmlDump.append(">\n");
            Set<String> keys = constraints.keySet();
            for(String key : keys ){
                if (constraints.get(key) != null) {
                    xmlDump.append("      <constraint type=\"rule\" dialect=\"mvel\" >"
                            + XmlDumper.replaceIllegalChars(constraints.get(key).getConstraint().trim()) + "</constraint>" + EOL);
                }
            }
            for (String eventType: stateNode.getActionTypes()) {
                writeActions(eventType, stateNode.getActions(eventType), xmlDump);
            }
            writeTimers(stateNode.getTimers(), xmlDump);
            endNode("state", xmlDump);
        } else {
            endNode(xmlDump);
        }
	}

}
