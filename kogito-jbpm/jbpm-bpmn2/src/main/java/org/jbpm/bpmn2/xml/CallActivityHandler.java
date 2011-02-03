/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2.xml;

import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.xml.XmlDumper;
import org.drools.xml.ExtensibleXmlParser;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CallActivityHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new SubProcessNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return SubProcessNode.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	SubProcessNode subProcessNode = (SubProcessNode) node;
		String processId = element.getAttribute("calledElement");
		if (processId != null) {
			subProcessNode.setProcessId(processId);
		}
		String waitForCompletion = element.getAttribute("waitForCompletion");
		if (waitForCompletion != null && "false".equals(waitForCompletion)) {
			subProcessNode.setWaitForCompletion(false);
		}
		String independent = element.getAttribute("independent");
		if (independent != null && "false".equals(independent)) {
			subProcessNode.setIndependent(false);
		}
    	Map<String, String> dataInputs = new HashMap<String, String>();
    	Map<String, String> dataOutputs = new HashMap<String, String>();
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	String nodeName = xmlNode.getNodeName();
        	if ("ioSpecification".equals(nodeName)) {
        		readIoSpecification(xmlNode, dataInputs, dataOutputs);
        	} else if ("dataInputAssociation".equals(nodeName)) {
        		readDataInputAssociation(xmlNode, subProcessNode, dataInputs);
        	} else if ("dataOutputAssociation".equals(nodeName)) {
        		readDataOutputAssociation(xmlNode, subProcessNode, dataOutputs);
        	}
    		xmlNode = xmlNode.getNextSibling();
        }
        handleScript(subProcessNode, element, "onEntry");
        handleScript(subProcessNode, element, "onExit");
	}
    
    protected void readIoSpecification(org.w3c.dom.Node xmlNode, Map<String, String> dataInputs, Map<String, String> dataOutputs) {
    	org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		while (subNode instanceof Element) {
			String subNodeName = subNode.getNodeName();
        	if ("dataInput".equals(subNodeName)) {
        		String id = ((Element) subNode).getAttribute("id");
        		String inputName = ((Element) subNode).getAttribute("name");
        		dataInputs.put(id, inputName);
        	} else if ("dataOutput".equals(subNodeName)) {
        		String id = ((Element) subNode).getAttribute("id");
        		String outputName = ((Element) subNode).getAttribute("name");
        		dataOutputs.put(id, outputName);
        	}
        	subNode = subNode.getNextSibling();
		}
    }
    
    protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, SubProcessNode subProcessNode, Map<String, String> dataInputs) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		String from = subNode.getTextContent();
		// targetRef
		subNode = subNode.getNextSibling();
		String to = subNode.getTextContent();
		subProcessNode.addInMapping(dataInputs.get(to), from);
    }
    
    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode, SubProcessNode subProcessNode, Map<String, String> dataOutputs) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		String from = subNode.getTextContent();
		// targetRef
		subNode = subNode.getNextSibling();
		String to = subNode.getTextContent();
		subProcessNode.addOutMapping(dataOutputs.get(from), to);
    }

	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		SubProcessNode subProcessNode = (SubProcessNode) node;
		writeNode("callActivity", subProcessNode, xmlDump, metaDataType);
		if (subProcessNode.getProcessId() != null) {
			xmlDump.append("calledElement=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(subProcessNode.getProcessId()) + "\" ");
		}
		if (!subProcessNode.isWaitForCompletion()) {
			xmlDump.append("tns:waitForCompletion=\"false\" ");
		}
		if (!subProcessNode.isIndependent()) {
			xmlDump.append("tns:independent=\"false\" ");
		}
		xmlDump.append(">" + EOL);
		writeScripts(subProcessNode, xmlDump);
		writeIO(subProcessNode, xmlDump);
		endNode("callActivity", xmlDump);
	}

	protected void writeIO(SubProcessNode subProcessNode, StringBuilder xmlDump) {
		xmlDump.append("      <ioSpecification>" + EOL);
		for (Map.Entry<String, String> entry: subProcessNode.getInMappings().entrySet()) {
			xmlDump.append("        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "Input\" name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "\" />" + EOL);
		}
		for (Map.Entry<String, String> entry: subProcessNode.getOutMappings().entrySet()) {
			xmlDump.append("        <dataOutput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "Output\" name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "\" />" + EOL);
		}
		xmlDump.append("        <inputSet>" + EOL);
		for (Map.Entry<String, String> entry: subProcessNode.getInMappings().entrySet()) {
			xmlDump.append("          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</dataInputRefs>" + EOL);
		}
		xmlDump.append("        </inputSet>" + EOL);
		xmlDump.append("        <outputSet>" + EOL);
		for (Map.Entry<String, String> entry: subProcessNode.getOutMappings().entrySet()) {
			xmlDump.append("          <dataOutputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Output</dataOutputRefs>" + EOL);
		}
		xmlDump.append("        </outputSet>" + EOL);
		xmlDump.append("      </ioSpecification>" + EOL);
		for (Map.Entry<String, String> entry: subProcessNode.getInMappings().entrySet()) {
			xmlDump.append("      <dataInputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>" + XmlDumper.replaceIllegalChars(entry.getValue()) + "</sourceRef>" + EOL +
				"        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</targetRef>" + EOL);
			xmlDump.append("      </dataInputAssociation>" + EOL);
		}
		for (Map.Entry<String, String> entry: subProcessNode.getOutMappings().entrySet()) {
			xmlDump.append("      <dataOutputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Output</sourceRef>" + EOL +
				"        <targetRef>" + entry.getValue() + "</targetRef>" + EOL);
			xmlDump.append("      </dataOutputAssociation>" + EOL);
		}
	}

}