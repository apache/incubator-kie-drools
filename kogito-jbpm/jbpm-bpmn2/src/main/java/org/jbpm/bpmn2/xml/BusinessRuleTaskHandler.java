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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.compiler.compiler.xml.XmlDumper;
import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.api.runtime.process.DataTransformer;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BusinessRuleTaskHandler extends AbstractNodeHandler {
	
    private static final String NAMESPACE_PROP = "namespace";
    private static final String MODEL_PROP = "model";
    private static final String DECISION_PROP = "decision";
	private DataTransformerRegistry transformerRegistry = DataTransformerRegistry.get();
    
    protected Node createNode(Attributes attrs) {
        return new RuleSetNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return RuleSetNode.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
        RuleSetNode ruleSetNode = (RuleSetNode) node;
		String ruleFlowGroup = element.getAttribute("ruleFlowGroup");
		if (ruleFlowGroup != null) {
			ruleSetNode.setRuleFlowGroup(ruleFlowGroup);
		}
		String language = element.getAttribute("implementation");
		if (language == null || language.equalsIgnoreCase("##unspecified") || language.isEmpty()) {
		    language = RuleSetNode.DRL_LANG;
		}
		ruleSetNode.setLanguage(language);
		
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("ioSpecification".equals(nodeName)) {
                readIoSpecification(xmlNode, dataInputs, dataOutputs);
            } else if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, ruleSetNode, dataInputs);
            } else if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, ruleSetNode, dataOutputs);
            }
            xmlNode = xmlNode.getNextSibling();
        }
		ruleSetNode.setNamespace((String) ruleSetNode.removeParameter(NAMESPACE_PROP));
		ruleSetNode.setModel((String) ruleSetNode.removeParameter(MODEL_PROP));
		ruleSetNode.setDecision((String) ruleSetNode.removeParameter(DECISION_PROP));
		
        handleScript(ruleSetNode, element, "onEntry");
        handleScript(ruleSetNode, element, "onExit");
	}

	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		RuleSetNode ruleSetNode = (RuleSetNode) node;
		writeNode("businessRuleTask", ruleSetNode, xmlDump, metaDataType);
		if (ruleSetNode.getRuleFlowGroup() != null) {
			xmlDump.append("g:ruleFlowGroup=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(ruleSetNode.getRuleFlowGroup()) + "\" " + EOL);
		}
		
        xmlDump.append(" implementation=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(ruleSetNode.getLanguage()) + "\" >" + EOL);        
		
		writeExtensionElements(ruleSetNode, xmlDump);
		writeIO(ruleSetNode, xmlDump);
		endNode("businessRuleTask", xmlDump);
	}
	
	protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, RuleSetNode ruleSetNode, Map<String, String> dataInputs) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        if ("sourceRef".equals(subNode.getNodeName())) {
            String source = subNode.getTextContent();
            // targetRef
            subNode = subNode.getNextSibling();
            String target = subNode.getTextContent();
            // transformation
    		Transformation transformation = null;
    		subNode = subNode.getNextSibling();
    		if (subNode != null && "transformation".equals(subNode.getNodeName())) {
    			String lang = subNode.getAttributes().getNamedItem("language").getNodeValue();
    			String expression = subNode.getTextContent();
    			
    			DataTransformer transformer = transformerRegistry.find(lang);
    			if (transformer == null) {
    				throw new IllegalArgumentException("No transformer registered for language " + lang);
    			}    			
    			transformation = new Transformation(lang, expression);    			
    			
    			subNode = subNode.getNextSibling();
    		}
    		// assignments  
            List<Assignment> assignments = new LinkedList<Assignment>();
            while(subNode != null){
                org.w3c.dom.Node ssubNode = subNode.getFirstChild();
                String from = ssubNode.getTextContent();
                String to = ssubNode.getNextSibling().getTextContent();
                assignments.add(new Assignment("XPath", from, to));
                subNode = subNode.getNextSibling();
            }
            ruleSetNode.addInAssociation(new DataAssociation(
                    source,
                    dataInputs.get(target), assignments, transformation));
        } else {
            // targetRef
            String to = subNode.getTextContent();
            // assignment
            subNode = subNode.getNextSibling();
            if (subNode != null) {
                org.w3c.dom.Node subSubNode = subNode.getFirstChild();
                NodeList nl = subSubNode.getChildNodes();
                if (nl.getLength() > 1) {
                    // not supported ?
                    ruleSetNode.setParameter(dataInputs.get(to), subSubNode.getTextContent());
                    return;
                } else if (nl.getLength() == 0) {
                    return;
                }
                Object result = null;
                Object from = nl.item(0);
                if (from instanceof Text) {
                    String text = ((Text) from).getTextContent();
                    if (text.startsWith("\"") && text.endsWith("\"")) {
                        result = text.substring(1, text.length() -1);
                    } else {
                        result = text;
                    }
                } else {
                    result = nl.item(0);
                }
                ruleSetNode.setParameter(dataInputs.get(to), result);
            }
        }
    }
    
    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode, RuleSetNode ruleSetNode, Map<String, String> dataOutputs) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String source = subNode.getTextContent();
        // targetRef
        subNode = subNode.getNextSibling();
        String target = subNode.getTextContent();
        // transformation
 		Transformation transformation = null;
 		subNode = subNode.getNextSibling();
 		if (subNode != null && "transformation".equals(subNode.getNodeName())) {
 			String lang = subNode.getAttributes().getNamedItem("language").getNodeValue();
 			String expression = subNode.getTextContent();
 			DataTransformer transformer = transformerRegistry.find(lang);
 			if (transformer == null) {
 				throw new IllegalArgumentException("No transformer registered for language " + lang);
 			}    			
 			transformation = new Transformation(lang, expression, source); 		
 			subNode = subNode.getNextSibling();
 		}
 		// assignments 
        List<Assignment> assignments = new LinkedList<Assignment>();
        while(subNode != null){
            org.w3c.dom.Node ssubNode = subNode.getFirstChild();
            String from = ssubNode.getTextContent();
            String to = ssubNode.getNextSibling().getTextContent();
            assignments.add(new Assignment("XPath", from, to));
            subNode = subNode.getNextSibling();
        }
        ruleSetNode.addOutAssociation(new DataAssociation(dataOutputs.get(source), target, assignments, transformation));
    }
    
    protected void writeIO(RuleSetNode ruleSetNode, StringBuilder xmlDump) {
        xmlDump.append("      <ioSpecification>" + EOL);
        for (Map.Entry<String, String> entry: ruleSetNode.getInMappings().entrySet()) {
            xmlDump.append("        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(ruleSetNode) + "_" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "Input\" name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "\" />" + EOL);
        }
        for (Map.Entry<String, Object> entry: ruleSetNode.getParameters().entrySet()) {
            if (!"ActorId".equals(entry.getKey()) && entry.getValue() != null) {
                xmlDump.append("        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(ruleSetNode) + "_" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "Input\" name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "\" />" + EOL);
            }
        }
        for (Map.Entry<String, String> entry: ruleSetNode.getOutMappings().entrySet()) {
            xmlDump.append("        <dataOutput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(ruleSetNode) + "_" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "Output\" name=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(entry.getKey()) + "\" />" + EOL);
        }
        xmlDump.append("        <inputSet>" + EOL);
        for (Map.Entry<String, String> entry: ruleSetNode.getInMappings().entrySet()) {
            xmlDump.append("          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(ruleSetNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</dataInputRefs>" + EOL);
        }
        for (Map.Entry<String, Object> entry: ruleSetNode.getParameters().entrySet()) {
            if (!"ActorId".equals(entry.getKey()) && entry.getValue() != null) {
                xmlDump.append("          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(ruleSetNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</dataInputRefs>" + EOL);
            }
        }
        xmlDump.append(
            "        </inputSet>" + EOL);
        xmlDump.append("        <outputSet>" + EOL);
        for (Map.Entry<String, String> entry: ruleSetNode.getOutMappings().entrySet()) {
            xmlDump.append("          <dataOutputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(ruleSetNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Output</dataOutputRefs>" + EOL);
        }
        xmlDump.append(
            "        </outputSet>" + EOL);
        xmlDump.append(
            "      </ioSpecification>" + EOL);
        for (Map.Entry<String, String> entry: ruleSetNode.getInMappings().entrySet()) {
            xmlDump.append("      <dataInputAssociation>" + EOL);
            xmlDump.append(
                "        <sourceRef>" + XmlDumper.replaceIllegalChars(entry.getValue()) + "</sourceRef>" + EOL +
                "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(ruleSetNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</targetRef>" + EOL);
            xmlDump.append("      </dataInputAssociation>" + EOL);
        }
        for (Map.Entry<String, Object> entry: ruleSetNode.getParameters().entrySet()) {
            if (!"ActorId".equals(entry.getKey()) && entry.getValue() != null) {
                xmlDump.append("      <dataInputAssociation>" + EOL);
                xmlDump.append(
                    "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(ruleSetNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</targetRef>" + EOL +
                    "        <assignment>" + EOL +
                    "          <from xsi:type=\"tFormalExpression\">" + XmlDumper.replaceIllegalChars(entry.getValue().toString()) + "</from>" + EOL +
                    "          <to xsi:type=\"tFormalExpression\">" + XmlBPMNProcessDumper.getUniqueNodeId(ruleSetNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</to>" + EOL +
                    "        </assignment>" + EOL);
                xmlDump.append("      </dataInputAssociation>" + EOL);
            }
        }
        for (Map.Entry<String, String> entry: ruleSetNode.getOutMappings().entrySet()) {
            xmlDump.append("      <dataOutputAssociation>" + EOL);
            xmlDump.append(
                "        <sourceRef>" + XmlBPMNProcessDumper.getUniqueNodeId(ruleSetNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Output</sourceRef>" + EOL +
                "        <targetRef>" + XmlDumper.replaceIllegalChars(entry.getValue()) + "</targetRef>" + EOL);
            xmlDump.append("      </dataOutputAssociation>" + EOL);
        }
    }

}
