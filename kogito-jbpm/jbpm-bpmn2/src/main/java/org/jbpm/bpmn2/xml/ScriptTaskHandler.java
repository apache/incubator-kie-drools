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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.mvel.java.JavaDialect;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.api.runtime.process.DataTransformer;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ScriptTaskHandler extends AbstractNodeHandler {

    private static Map<String, String> SUPPORTED_SCRIPT_FORMATS = new HashMap<>();

    static {
        SUPPORTED_SCRIPT_FORMATS.put(XmlBPMNProcessDumper.JAVA_LANGUAGE, JavaDialect.ID);
        SUPPORTED_SCRIPT_FORMATS.put(XmlBPMNProcessDumper.JAVASCRIPT_LANGUAGE, "JavaScript");
        SUPPORTED_SCRIPT_FORMATS.put(XmlBPMNProcessDumper.FEEL_LANGUAGE, "FEEL");
        SUPPORTED_SCRIPT_FORMATS.put(XmlBPMNProcessDumper.FEEL_LANGUAGE_SHORT, "FEEL");
    }

    public static void registerSupportedScriptFormat(String language, String dialect) {
        SUPPORTED_SCRIPT_FORMATS.put(language, dialect);
    }

	private DataTransformerRegistry transformerRegistry = DataTransformerRegistry.get();
    
    protected Node createNode( Attributes attrs) {
        ActionNode result = new ActionNode();
        result.setAction(new DroolsConsequenceAction());
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Node.class;
    }

    protected void handleNode( final Node node, final Element element, final String uri,
                               final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
        ActionNode actionNode = (ActionNode) node;
        node.setMetaData("NodeType", "ScriptTask");
        DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
        if (action == null) {
        	action = new DroolsConsequenceAction();
        	actionNode.setAction(action);
        }
        String language = element.getAttribute("scriptFormat");
        action.setDialect(SUPPORTED_SCRIPT_FORMATS.getOrDefault(language, "mvel"));
        action.setConsequence("");
	    
        dataInputs.clear();
        dataOutputs.clear();
        
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	
        	String nodeName = xmlNode.getNodeName();
        	if (xmlNode instanceof Element && "script".equals(nodeName)) {
        		action.setConsequence(xmlNode.getTextContent());
        	} else if ("ioSpecification".equals(nodeName)) {
                readIoSpecification(xmlNode, dataInputs, dataOutputs, dataInputTypes, dataOutputTypes);
            } else if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, actionNode, dataInputs);
            } else if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, actionNode, dataOutputs);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    

        actionNode.setMetaData("DataInputs", new HashMap<String, String>(dataInputs));
        actionNode.setMetaData("DataOutputs", new HashMap<String, String>(dataOutputs));
        
        String compensation = element.getAttribute("isForCompensation");
        if( compensation != null ) {
            boolean isForCompensation = Boolean.parseBoolean(compensation);
            if( isForCompensation ) { 
                actionNode.setMetaData("isForCompensation", isForCompensation );
            }
        }
	}

	public void writeNode( Node node, StringBuilder xmlDump, int metaDataType) {
	    throw new IllegalArgumentException("Writing out should be handled by action node handler");
	}

    protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, ActionNode actionNode, Map<String, String> dataInputs) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        if ("sourceRef".equals(subNode.getNodeName())) {
            List<String> sources = new ArrayList<>();
            sources.add(subNode.getTextContent());

            subNode = subNode.getNextSibling();

            while ("sourceRef".equals(subNode.getNodeName())) {
                sources.add(subNode.getTextContent());
                subNode = subNode.getNextSibling();
            }
            // targetRef
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
            while (subNode != null) {
                String expressionLang = ((Element) subNode).getAttribute("expressionLanguage");
                if (expressionLang == null || expressionLang.trim().isEmpty()) {
                    expressionLang = "XPath";
                }
                org.w3c.dom.Node ssubNode = subNode.getFirstChild();
                String from = ssubNode.getTextContent();
                String to = ssubNode.getNextSibling().getTextContent();
                assignments.add(new Assignment(expressionLang, from, to));
                subNode = subNode.getNextSibling();
            }
            actionNode.addInAssociation(new DataAssociation(
                                                            sources,
                                                            dataInputs.get(target), assignments, transformation));
        }
    }

    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode, ActionNode actionNode, Map<String, String> dataOutputs) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        List<String> sources = new ArrayList<>();
        sources.add(subNode.getTextContent());

        subNode = subNode.getNextSibling();

        while ("sourceRef".equals(subNode.getNodeName())) {
            sources.add(subNode.getTextContent());
            subNode = subNode.getNextSibling();
        }
        // targetRef
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
        while (subNode != null) {
            String expressionLang = ((Element) subNode).getAttribute("expressionLanguage");
            if (expressionLang == null || expressionLang.trim().isEmpty()) {
                expressionLang = "XPath";
            }
            org.w3c.dom.Node ssubNode = subNode.getFirstChild();
            String from = ssubNode.getTextContent();
            String to = ssubNode.getNextSibling().getTextContent();
            assignments.add(new Assignment(expressionLang, from, to));
            subNode = subNode.getNextSibling();
        }
        actionNode.addOutAssociation(new DataAssociation(sources.stream().map(source -> dataOutputs.get(source)).collect(Collectors.toList()), target, assignments, transformation));
    }

}
