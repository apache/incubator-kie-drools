package org.drools.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.XmlDumper;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class MilestoneNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new MilestoneNode();
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        MilestoneNode milestone = (MilestoneNode) node;
        String text = ((Text)element.getChildNodes().item( 0 )).getWholeText();
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

	public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
		MilestoneNode milestoneNode = (MilestoneNode) node;
		writeNode("milestone", milestoneNode, xmlDump, includeMeta);
        String constraint = milestoneNode.getConstraint();
        if (constraint != null) {
            xmlDump.append(">" + XmlDumper.replaceIllegalChars(constraint.trim()) + "</milestone>" + EOL);
        } else {
            endNode(xmlDump);
        }
	}

}
