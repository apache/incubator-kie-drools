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
        String n = element.getAttribute("n");
        if (n != null && n.length() != 0 ) {
            joinNode.setN(n);
        }
    }

    public Class generateNodeFor() {
        return Join.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		Join joinNode = (Join) node;
		writeNode("join", joinNode, xmlDump, includeMeta);
        int type = joinNode.getType();
        if (type != 0) {
            xmlDump.append("type=\"" + type + "\" ");
        }
        if (type == Join.TYPE_N_OF_M) {
        	String n = joinNode.getN();
	        if (n != null && n.length() != 0) {
	            xmlDump.append("n=\"" + n + "\" ");
	        }
        }
        endNode(xmlDump);
	}

}
