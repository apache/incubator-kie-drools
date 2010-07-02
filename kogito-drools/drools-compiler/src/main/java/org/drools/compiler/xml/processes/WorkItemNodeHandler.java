package org.drools.compiler.xml.processes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.drools.compiler.xml.XmlWorkflowProcessDumper;
import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.DataType;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.ExtensibleXmlParser;
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
        	handleAction(workItemNode, element, eventType);
        }
    }

    protected Node createNode() {
        return new WorkItemNode();
    }

    public Class<?> generateNodeFor() {
        return WorkItemNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		WorkItemNode workItemNode = (WorkItemNode) node;
		writeNode("workItem", workItemNode, xmlDump, includeMeta);
        visitParameters(workItemNode, xmlDump);
        xmlDump.append(">" + EOL);
        if (includeMeta) {
        	writeMetaData(workItemNode, xmlDump);
        }
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
	
	protected void visitParameters(WorkItemNode workItemNode, StringBuilder xmlDump) {
	    if (!workItemNode.isWaitForCompletion()) {
            xmlDump.append("waitForCompletion=\"false\" ");
        }
	}
	
	protected void visitInMappings(Map<String, String> inMappings, StringBuilder xmlDump) {
        for (Map.Entry<String, String> inMapping: inMappings.entrySet()) {
            xmlDump.append(
                "      <mapping type=\"in\" "
                             + "from=\"" + inMapping.getValue() + "\" "
                             + "to=\"" + inMapping.getKey() + "\" />" + EOL);
        }
	}
	
	protected void visitOutMappings(Map<String, String> outMappings, StringBuilder xmlDump) {
        for (Map.Entry<String, String> outMapping: outMappings.entrySet()) {
            xmlDump.append(
                "      <mapping type=\"out\" "
                             + "from=\"" + outMapping.getKey() + "\" "
                             + "to=\"" + outMapping.getValue() + "\" />" + EOL);
        }
    }
    
    protected void visitWork(Work work, StringBuilder xmlDump, boolean includeMeta) {
        if (work != null) {
            xmlDump.append("      <work name=\"" + work.getName() + "\" >" + EOL);
            List<ParameterDefinition> parameterDefinitions =
            	new ArrayList<ParameterDefinition>(work.getParameterDefinitions());
            Collections.sort(parameterDefinitions, new Comparator<ParameterDefinition>() {
				public int compare(ParameterDefinition o1,
						ParameterDefinition o2) {
					return o1.getName().compareTo(o2.getName());
				}
            	
            });
            for (ParameterDefinition paramDefinition: parameterDefinitions) {
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
