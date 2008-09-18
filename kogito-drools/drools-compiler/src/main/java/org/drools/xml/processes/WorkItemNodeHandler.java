package org.drools.xml.processes;

import java.util.Map;

import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.DataType;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.XmlWorkflowProcessDumper;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class WorkItemNodeHandler extends AbstractNodeHandler {

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        WorkItemNode workItemNode = (WorkItemNode) node;
        final String waitForCompletion = element.getAttribute("waitForCompletion");
        workItemNode.setWaitForCompletion(!"false".equals(waitForCompletion));
        for (String eventType: workItemNode.getActionTypes()) {
        	handleAction(node, element, eventType);
        }
    }

    protected Node createNode() {
        return new WorkItemNode();
    }

    public Class<?> generateNodeFor() {
        return WorkItemNode.class;
    }

	public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
		WorkItemNode workItemNode = (WorkItemNode) node;
		writeNode("workItem", workItemNode, xmlDump, includeMeta);
        visitParameters(workItemNode, xmlDump);
        xmlDump.append(">" + EOL);
        Work work = workItemNode.getWork();
        visitWork(work, xmlDump, includeMeta);
        visitInMappings(workItemNode.getInMappings(), xmlDump);
        visitOutMappings(workItemNode.getOutMappings(), xmlDump);
        for (String eventType: workItemNode.getActionTypes()) {
        	writeActions(eventType, workItemNode.getActions(eventType), xmlDump);
        }
        writeTimers(workItemNode.getTimers(), xmlDump);
        endNode("workItem", xmlDump);
	}
	
	protected void visitParameters(WorkItemNode workItemNode, StringBuffer xmlDump) {
	    if (!workItemNode.isWaitForCompletion()) {
            xmlDump.append("waitForCompletion=\"false\" ");
        }
	}
	
	protected void visitInMappings(Map<String, String> inMappings, StringBuffer xmlDump) {
        for (Map.Entry<String, String> inMapping: inMappings.entrySet()) {
            xmlDump.append(
                "      <mapping type=\"in\" "
                             + "from=\"" + inMapping.getValue() + "\" "
                             + "to=\"" + inMapping.getKey() + "\" />" + EOL);
        }
	}
	
	protected void visitOutMappings(Map<String, String> outMappings, StringBuffer xmlDump) {
        for (Map.Entry<String, String> outMapping: outMappings.entrySet()) {
            xmlDump.append(
                "      <mapping type=\"out\" "
                             + "from=\"" + outMapping.getKey() + "\" "
                             + "to=\"" + outMapping.getValue() + "\" />" + EOL);
        }
    }
    
    protected void visitWork(Work work, StringBuffer xmlDump, boolean includeMeta) {
        if (work != null) {
            xmlDump.append("      <work name=\"" + work.getName() + "\" >" + EOL);
            for (ParameterDefinition paramDefinition: work.getParameterDefinitions()) {
            	DataType dataType = paramDefinition.getType();
                xmlDump.append("        <parameter name=\"" + paramDefinition.getName() + "\" >" + EOL + "  ");
                XmlWorkflowProcessDumper.visitDataType(dataType, xmlDump);
                Object value = work.getParameter(paramDefinition.getName());
                if (value != null) {
                	xmlDump.append("  ");
                	XmlWorkflowProcessDumper.visitValue(value, dataType, xmlDump);
                }
                xmlDump.append("        </parameter>" + EOL); 
            }
            xmlDump.append("      </work>" + EOL);
        }
    }
}
