package org.drools.compiler.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class RuleSetNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new RuleSetNode();
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        RuleSetNode ruleSetNode = (RuleSetNode) node;
        String ruleFlowGroup = element.getAttribute("ruleFlowGroup");
        if (ruleFlowGroup != null && ruleFlowGroup.length() > 0) {
        	ruleSetNode.setRuleFlowGroup(ruleFlowGroup);
        }
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return RuleSetNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		RuleSetNode ruleSetNode = (RuleSetNode) node;
		writeNode("ruleSet", ruleSetNode, xmlDump, includeMeta);
        String ruleFlowGroup = ruleSetNode.getRuleFlowGroup();
        if (ruleFlowGroup != null) {
            xmlDump.append("ruleFlowGroup=\"" + ruleFlowGroup + "\" ");
        }
        if (ruleSetNode.getTimers() != null || (includeMeta && containsMetaData(ruleSetNode))) {
            xmlDump.append(">\n");
            if (ruleSetNode.getTimers() != null) {
            	writeTimers(ruleSetNode.getTimers(), xmlDump);
            }
            if (includeMeta) {
            	writeMetaData(ruleSetNode, xmlDump);
            }
            endNode("ruleSet", xmlDump);
        } else {
            endNode(xmlDump);
        }
	}

}
