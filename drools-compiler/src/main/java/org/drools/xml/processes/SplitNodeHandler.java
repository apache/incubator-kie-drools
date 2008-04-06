package org.drools.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.Split;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.xml.sax.SAXException;

public class SplitNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new Split();
    }

    public void handleNode(final Node node, final Configuration config, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, config, uri, localName, parser);
        Split splitNode = (Split) node;
        String type = config.getAttribute("type");
        if (type != null) {
            splitNode.setType(new Integer(type));
        }
    }

    public Class generateNodeFor() {
        return Split.class;
    }

}
