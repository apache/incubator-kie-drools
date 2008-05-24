package org.drools.xml.processes;

import java.util.Map;

import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.Work;
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

	public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
		WorkItemNode workItemNode = (WorkItemNode) node;
		writeNode("workItem", workItemNode, xmlDump, includeMeta);
        if (!workItemNode.isWaitForCompletion()) {
            xmlDump.append("waitForCompletion=\"false\" ");
        }
        xmlDump.append(">" + EOL);
        Work work = workItemNode.getWork();
        if (work != null) {
            visitWork(work, xmlDump, includeMeta);
        }
        Map<String, String> inMappings = workItemNode.getInMappings();
        for (Map.Entry<String, String> inMapping: inMappings.entrySet()) {
            xmlDump.append(
                "      <mapping type=\"in\" "
                             + "from=\"" + inMapping.getValue() + "\" "
                             + "to=\"" + inMapping.getKey() + "\" />" + EOL);
        }
        Map<String, String> outMappings = workItemNode.getOutMappings();
        for (Map.Entry<String, String> outMapping: outMappings.entrySet()) {
            xmlDump.append(
                "      <mapping type=\"out\" "
                             + "from=\"" + outMapping.getKey() + "\" "
                             + "to=\"" + outMapping.getValue() + "\" />" + EOL);
        }
        endNode("workItem", xmlDump);
    }
    
    private void visitWork(Work work, StringBuffer xmlDump, boolean includeMeta) {
        xmlDump.append("      <work name=\"" + work.getName() + "\" >" + EOL);
        for (ParameterDefinition paramDefinition: work.getParameterDefinitions()) {
            if (paramDefinition == null) {
                throw new IllegalArgumentException(
                    "Could not find parameter definition " + paramDefinition.getName()
                        + " for work " + work.getName());
            }
            xmlDump.append("        <parameter name=\"" + paramDefinition.getName() + "\" " + 
                                              "type=\"" + paramDefinition.getType().getClass().getName() + "\" ");
            Object value = work.getParameter(paramDefinition.getName());
            if (value == null) {
                xmlDump.append("/>" + EOL);
            } else {
                xmlDump.append(">" + value + "</parameter>" + EOL);
            }
        }
        xmlDump.append("      </work>" + EOL);
    }
}
