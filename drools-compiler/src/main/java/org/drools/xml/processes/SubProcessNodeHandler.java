package org.drools.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.xml.sax.SAXException;

public class SubProcessNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new SubProcessNode();
    }

    public void handleNode(final Node node, final Configuration config, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, config, uri, localName, parser);
        SubProcessNode subProcessNode = (SubProcessNode) node;
        String processId = config.getAttribute("processId");
        subProcessNode.setProcessId(processId);
        String waitForCompletion = config.getAttribute("waitForCompletion");
        subProcessNode.setWaitForCompletion(!"false".equals(waitForCompletion));
    }

    public Class generateNodeFor() {
        return SubProcessNode.class;
    }

	public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
		SubProcessNode subProcessNode = (SubProcessNode) node;
		writeNode("subProcess", subProcessNode, xmlDump, includeMeta);
        String processId = subProcessNode.getProcessId();
        if (processId != null) {
            xmlDump.append("processId=\"" + processId + "\" ");
        }
        if (!subProcessNode.isWaitForCompletion()) {
            xmlDump.append("waitForCompletion=\"false\" ");
        }
        endNode(xmlDump);
	}

}
