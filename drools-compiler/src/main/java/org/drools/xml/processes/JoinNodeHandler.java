package org.drools.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.Join;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class JoinNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new Join();
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        Join joinNode = (Join) node;
        String type = element.getAttribute("type");
        if (type != null && type.length() != 0 ) {
            joinNode.setType(new Integer(type));
        }
    }

    public Class generateNodeFor() {
        return Join.class;
    }

	public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
		Join joinNode = (Join) node;
		writeNode("join", joinNode, xmlDump, includeMeta);
        int type = joinNode.getType();
        if (type != 0) {
            xmlDump.append("type=\"" + type + "\" ");
        }
        endNode(xmlDump);
	}

}
