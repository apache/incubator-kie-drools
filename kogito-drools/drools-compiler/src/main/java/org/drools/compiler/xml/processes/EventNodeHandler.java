package org.drools.compiler.xml.processes;

import org.drools.process.core.event.EventFilter;
import org.drools.process.core.event.EventTypeFilter;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.EventNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class EventNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode() {
        return new EventNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EventNode.class;
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        String variableName = element.getAttribute("variableName");
        if (variableName != null && variableName.length() != 0 ) {
            eventNode.setVariableName(variableName);
        }
        String scope = element.getAttribute("scope");
        if (scope != null && scope.length() != 0 ) {
            eventNode.setScope(scope);
        }
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		EventNode eventNode = (EventNode) node;
		writeNode("eventNode", eventNode, xmlDump, includeMeta);
		String variableName = eventNode.getVariableName();
        if (variableName != null && variableName.length() != 0) {
            xmlDump.append("variableName=\"" + variableName + "\" ");
        }
        String scope = eventNode.getScope();
        if (scope != null && scope.length() != 0) {
            xmlDump.append("scope=\"" + scope + "\" ");
        }
        xmlDump.append(">" + EOL);
        xmlDump.append("      <eventFilters>" + EOL);
        for (EventFilter filter: eventNode.getEventFilters()) {
        	if (filter instanceof EventTypeFilter) {
        		xmlDump.append("        <eventFilter "
                    + "type=\"eventType\" "
                    + "eventType=\"" + ((EventTypeFilter) filter).getType() + "\" />" + EOL);
        	} else {
        		throw new IllegalArgumentException(
    				"Unknown filter type: " + filter);
        	}
        }
        xmlDump.append("      </eventFilters>" + EOL);
        endNode("eventNode", xmlDump);
	}

}
