package org.drools.xml.processes;

import java.util.Map;

import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.Split;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.XmlDumper;
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

	public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
		Split splitNode = (Split) node;
		writeNode("split", splitNode, xmlDump, includeMeta);
        int type = splitNode.getType();
        if (type != 0) {
            xmlDump.append("type=\"" + type + "\" ");
        }
        if (splitNode.getConstraints().isEmpty()) {
            endNode(xmlDump);
        } else {
            xmlDump.append(">" + EOL);
            xmlDump.append("      <constraints>" + EOL);
            for (Map.Entry<Split.ConnectionRef, Constraint> entry: splitNode.getConstraints().entrySet()) {
                Split.ConnectionRef connection = entry.getKey();
                Constraint constraint = entry.getValue();
                xmlDump.append("        <constraint "
                        + "toNodeId=\"" + connection.getNodeId() + "\" "
                        + "toType=\"" + connection.getToType() + "\" ");
                String name = constraint.getName();
                if (name != null && !"".equals(name)) {
                    xmlDump.append("name=\"" + constraint.getName() + "\" ");
                }
                int priority = constraint.getPriority();
                if (priority != 0) {
                    xmlDump.append("priority=\"" + constraint.getPriority() + "\" ");
                }
                xmlDump.append("type=\"" + constraint.getType() + "\" ");
                String dialect = constraint.getDialect();
                if (dialect != null && !"".equals(dialect)) {
                    xmlDump.append("dialect=\"" + dialect + "\" ");
                }
                String constraintString = constraint.getConstraint();
                if (constraintString != null) {
                    xmlDump.append(">" + XmlDumper.replaceIllegalChars(constraintString) + "</constraint>" + EOL);
                } else {
                    xmlDump.append("/>" + EOL);
                }
            }
            xmlDump.append("      </constraints>" + EOL);
            endNode("split", xmlDump);
        }
	}

}
