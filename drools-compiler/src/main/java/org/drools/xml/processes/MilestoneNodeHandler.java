package org.drools.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.xml.sax.SAXException;

public class MilestoneNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new MilestoneNode();
    }

    public void handleNode(final Node node, final Configuration config, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, config, uri, localName, parser);
        MilestoneNode milestone = (MilestoneNode) node;
        String text = config.getText();
        if (text != null) {
            text.trim();
            if ("".equals(text)) {
                text = null;
            }
        }
        milestone.setConstraint(text);
    }

    public Class generateNodeFor() {
        return MilestoneNode.class;
    }

}
