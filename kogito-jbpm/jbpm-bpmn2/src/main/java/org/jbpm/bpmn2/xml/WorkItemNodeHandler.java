/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2.xml;

import java.util.Map;

import org.drools.compiler.compiler.xml.XmlDumper;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.xml.sax.Attributes;

public class WorkItemNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by specific handlers");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return WorkItemNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		WorkItemNode workItemNode = (WorkItemNode) node;
		String type = workItemNode.getWork().getName();
		if ("Manual Task".equals(type)) {
		    writeNode("manualTask", workItemNode, xmlDump, metaDataType);
		    xmlDump.append(">" + EOL);
			writeExtensionElements(workItemNode, xmlDump);
	        endNode("manualTask", xmlDump);
	        return;
		} 
        if ("Service Task".equals(type)) {
            writeNode("serviceTask", workItemNode, xmlDump, metaDataType);
            String impl = "Other";
            if (workItemNode.getWork().getParameter("implementation") != null) {
                impl = (String) workItemNode.getWork().getParameter("implementation");
            }
            xmlDump.append("operationRef=\"" + 
                XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_ServiceOperation\" implementation=\""+impl+"\" >" + EOL);
    		writeExtensionElements(workItemNode, xmlDump);
    		xmlDump.append(
                "      <ioSpecification>" + EOL +
                "        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param\" name=\"Parameter\" />" + EOL +
                "        <dataOutput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result\" name=\"Result\" />" + EOL +
                "        <inputSet>" + EOL +
                "          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param</dataInputRefs>" + EOL +
                "        </inputSet>" + EOL +
                "        <outputSet>" + EOL +
                "          <dataOutputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result</dataOutputRefs>" + EOL +
                "        </outputSet>" + EOL +
                "      </ioSpecification>" + EOL);
            String inMapping = workItemNode.getInMapping("Parameter");
            if (inMapping != null) {
                xmlDump.append(
                    "      <dataInputAssociation>" + EOL +
                    "        <sourceRef>" + XmlDumper.replaceIllegalChars(inMapping) + "</sourceRef>" + EOL +
                    "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param</targetRef>" + EOL +
                    "      </dataInputAssociation>" + EOL);
            }
            String outMapping = workItemNode.getOutMapping("Result");
            if (outMapping != null) {
                xmlDump.append(
                    "      <dataOutputAssociation>" + EOL +
                    "        <sourceRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result</sourceRef>" + EOL +
                    "        <targetRef>" + XmlDumper.replaceIllegalChars(outMapping) + "</targetRef>" + EOL +
                    "      </dataOutputAssociation>" + EOL);
            }
            endNode("serviceTask", xmlDump);
            return;
        } 
        if ("Send Task".equals(type)) {
            writeNode("sendTask", workItemNode, xmlDump, metaDataType);
            xmlDump.append("messageRef=\"" + 
                XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_Message\" implementation=\"Other\" >" + EOL);
    		writeExtensionElements(workItemNode, xmlDump);
    		xmlDump.append(
                "      <ioSpecification>" + EOL +
                "        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param\" name=\"Message\" />" + EOL +
                "        <inputSet>" + EOL +
                "          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param</dataInputRefs>" + EOL +
                "        </inputSet>" + EOL +
                "        <outputSet/>" + EOL +
                "      </ioSpecification>" + EOL);
            String inMapping = workItemNode.getInMapping("Message");
            if (inMapping != null) {
                xmlDump.append(
                    "      <dataInputAssociation>" + EOL +
                    "        <sourceRef>" + XmlDumper.replaceIllegalChars(inMapping) + "</sourceRef>" + EOL +
                    "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param</targetRef>" + EOL +
                    "      </dataInputAssociation>" + EOL);
            }
            endNode("sendTask", xmlDump);
            return;
        } 
        if ("Receive Task".equals(type)) {
            writeNode("receiveTask", workItemNode, xmlDump, metaDataType);
            String messageId = (String) workItemNode.getWork().getParameter("MessageId");
            xmlDump.append("messageRef=\"" + 
                messageId + "\" implementation=\"Other\" >" + EOL);
    		writeExtensionElements(workItemNode, xmlDump);
    		xmlDump.append(
                "      <ioSpecification>" + EOL +
                "        <dataOutput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result\" name=\"Message\" />" + EOL +
                "        <inputSet/>" + EOL +
                "        <outputSet>" + EOL +
                "          <dataOutputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result</dataOutputRefs>" + EOL +
                "        </outputSet>" + EOL +
                "      </ioSpecification>" + EOL);
            String outMapping = workItemNode.getOutMapping("Message");
            if (outMapping != null) {
                xmlDump.append(
                    "      <dataOutputAssociation>" + EOL +
                    "        <sourceRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result</sourceRef>" + EOL +
                    "        <targetRef>" + XmlDumper.replaceIllegalChars(outMapping) + "</targetRef>" + EOL +
                    "      </dataOutputAssociation>" + EOL);
            }
            endNode("receiveTask", xmlDump);
            return;
        } 
		writeNode("task", workItemNode, xmlDump, metaDataType);
		Object isForCompensationObject = workItemNode.getMetaData("isForCompensation");
        if( isForCompensationObject != null && ((Boolean) isForCompensationObject) ) { 
            xmlDump.append("isForCompensation=\"true\" ");
        }	
		xmlDump.append("tns:taskName=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\" >" + EOL);
		writeExtensionElements(workItemNode, xmlDump);
		writeIO(workItemNode, xmlDump);
		endNode("task", xmlDump);
	}
	
	protected void writeIO(WorkItemNode workItemNode, StringBuilder xmlDump) {
		xmlDump.append("      <ioSpecification>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "Input\" name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "\" />" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append("        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "Input\" name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "\" />" + EOL);
			}
		}
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("        <dataOutput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "Output\" name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "\" />" + EOL);
		}
		xmlDump.append("        <inputSet>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "Input</dataInputRefs>" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append("          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</dataInputRefs>" + EOL);
			}
		}
		xmlDump.append(
			"        </inputSet>" + EOL);
		xmlDump.append("        <outputSet>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("          <dataOutputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Output</dataOutputRefs>" + EOL);
		}
		xmlDump.append(
			"        </outputSet>" + EOL);
		xmlDump.append(
			"      </ioSpecification>" + EOL);
		writeInputAssociation(workItemNode, xmlDump);
        writeOutputAssociation(workItemNode, xmlDump);
	}
	
	protected void writeInputAssociation(WorkItemNode workItemNode, StringBuilder xmlDump) {
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("      <dataInputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>" + XmlDumper.replaceIllegalChars(entry.getValue()) + "</sourceRef>" + EOL +
				"        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</targetRef>" + EOL);
			xmlDump.append("      </dataInputAssociation>" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append("      <dataInputAssociation>" + EOL);
				xmlDump.append(
					"        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</targetRef>" + EOL +
					"        <assignment>" + EOL +
					"          <from xsi:type=\"tFormalExpression\">" + XmlDumper.replaceIllegalChars(entry.getValue().toString()) + "</from>" + EOL +
					"          <to xsi:type=\"tFormalExpression\">" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</to>" + EOL +
					"        </assignment>" + EOL);
				xmlDump.append("      </dataInputAssociation>" + EOL);
			}
		}
	}
	
    protected void writeOutputAssociation(WorkItemNode workItemNode, StringBuilder xmlDump) {
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("      <dataOutputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Output</sourceRef>" + EOL +
				"        <targetRef>" + XmlDumper.replaceIllegalChars(entry.getValue()) + "</targetRef>" + EOL);
			xmlDump.append("      </dataOutputAssociation>" + EOL);
		}
	}

}
