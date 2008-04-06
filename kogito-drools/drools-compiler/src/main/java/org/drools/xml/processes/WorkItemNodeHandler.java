package org.drools.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.xml.sax.SAXException;

public class WorkItemNodeHandler extends AbstractNodeHandler {

    public void handleNode(final Node node, final Configuration config, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, config, uri, localName, parser);
        WorkItemNode workItemNode = (WorkItemNode) node;
        final String waitForCompletion = config.getAttribute("waitForCompletion");
        workItemNode.setWaitForCompletion(!"false".equals(waitForCompletion));
    }

    protected Node createNode() {
        return new WorkItemNode();
    }

    public Class generateNodeFor() {
        return WorkItemNode.class;
    }

}
