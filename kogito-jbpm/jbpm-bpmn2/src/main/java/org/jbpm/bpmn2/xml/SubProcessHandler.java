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

import java.util.List;
import java.util.Map;

import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SubProcessHandler extends AbstractNodeHandler {
    
    
    
    protected Node createNode(Attributes attrs) {
    	CompositeContextNode result = new CompositeContextNode();
        VariableScope variableScope = new VariableScope();
        result.addContext(variableScope);
        result.setDefaultContext(variableScope);
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return CompositeContextNode.class;
    }

    public Object end(final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		Node node = (Node) parser.getCurrent();
		// determine type of event definition, so the correct type of node
		// can be generated
		boolean found = false;
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
				// create new timerNode
				ForEachNode forEachNode = new ForEachNode();
				forEachNode.setId(node.getId());
				forEachNode.setName(node.getName());
				for (org.kie.definition.process.Node subNode: ((CompositeContextNode) node).getNodes()) {
					forEachNode.addNode(subNode);
				}
				forEachNode.setMetaData("UniqueId", ((CompositeContextNode) node).getMetaData("UniqueId"));
				forEachNode.setMetaData(ProcessHandler.CONNECTIONS, ((CompositeContextNode) node).getMetaData(ProcessHandler.CONNECTIONS));
				VariableScope v = (VariableScope) ((CompositeContextNode) node).getDefaultContext(VariableScope.VARIABLE_SCOPE);
				((VariableScope) ((CompositeContextNode) forEachNode.internalGetNode(2)).getDefaultContext(VariableScope.VARIABLE_SCOPE)).setVariables(v.getVariables());
				node = forEachNode;
				handleForEachNode(node, element, uri, localName, parser);
				found = true;
				break;
			}
			xmlNode = xmlNode.getNextSibling();
		}
		if (!found) {
			handleCompositeContextNode(node, element, uri, localName, parser);
		}
		NodeContainer nodeContainer = (NodeContainer) parser.getParent();
		nodeContainer.addNode(node);
		return node;
	}
    
    @SuppressWarnings("unchecked")
	protected void handleCompositeContextNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	CompositeContextNode compositeNode = (CompositeContextNode) node;
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			compositeNode.getMetaData(ProcessHandler.CONNECTIONS);
    	ProcessHandler.linkConnections(compositeNode, connections);
    	
    	List<IntermediateLink> throwLinks = (List<IntermediateLink>) compositeNode
		.getMetaData(ProcessHandler.LINKS);
    	ProcessHandler.linkIntermediateLinks(compositeNode, throwLinks);	
    	
    	ProcessHandler.linkBoundaryEvents(compositeNode);
    }
    
    @SuppressWarnings("unchecked")
	protected void handleForEachNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	ForEachNode forEachNode = (ForEachNode) node;
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("ioSpecification".equals(nodeName)) {
                readIoSpecification(xmlNode, dataInputs, dataOutputs);
            } else if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, forEachNode);
            } else if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
            	readMultiInstanceLoopCharacteristics(xmlNode, forEachNode, parser);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			forEachNode.getMetaData(ProcessHandler.CONNECTIONS);
    	ProcessHandler.linkConnections(forEachNode, connections);
    	ProcessHandler.linkBoundaryEvents(forEachNode);
    }
    
    protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, ForEachNode forEachNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String inputVariable = subNode.getTextContent();
        if (inputVariable != null && inputVariable.trim().length() > 0) {
        	forEachNode.setCollectionExpression(inputVariable);
        }
    }
    
    @SuppressWarnings("unchecked")
	protected void readMultiInstanceLoopCharacteristics(org.w3c.dom.Node xmlNode, ForEachNode forEachNode, ExtensibleXmlParser parser) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        while (subNode != null) {
            String nodeName = subNode.getNodeName();
            if ("inputDataItem".equals(nodeName)) {
            	String variableName = ((Element) subNode).getAttribute("id");
            	String itemSubjectRef = ((Element) subNode).getAttribute("itemSubjectRef");
            	DataType dataType = null;
            	Map<String, ItemDefinition> itemDefinitions = (Map<String, ItemDefinition>)
	            	((ProcessBuildData) parser.getData()).getMetaData("ItemDefinitions");
		        if (itemDefinitions != null) {
		        	ItemDefinition itemDefinition = itemDefinitions.get(itemSubjectRef);
		        	if (itemDefinition != null) {
		        		dataType = new ObjectDataType(itemDefinition.getStructureRef());
		        	}
		        }
		        if (dataType == null) {
		        	dataType = new ObjectDataType("java.lang.Object");
		        }
                if (variableName != null && variableName.trim().length() > 0) {
                	forEachNode.setVariable(variableName, dataType);
                }
            } else if ("outputDataItem".equals(nodeName)) {
                String variableName = ((Element) subNode).getAttribute("id");
                String itemSubjectRef = ((Element) subNode).getAttribute("itemSubjectRef");
                DataType dataType = null;
                Map<String, ItemDefinition> itemDefinitions = (Map<String, ItemDefinition>)
                    ((ProcessBuildData) parser.getData()).getMetaData("ItemDefinitions");
                if (itemDefinitions != null) {
                    ItemDefinition itemDefinition = itemDefinitions.get(itemSubjectRef);
                    if (itemDefinition != null) {
                        dataType = new ObjectDataType(itemDefinition.getStructureRef());
                    }
                }
                if (dataType == null) {
                    dataType = new ObjectDataType("java.lang.Object");
                }
                if (variableName != null && variableName.trim().length() > 0) {
                    forEachNode.setOutputVariable(variableName, dataType);
                }
            } else if ("loopDataOutputRef".equals(nodeName)) {
                
                String outputDataRef = ((Element) subNode).getTextContent();
                
                String outputDataName = dataOutputs.get(outputDataRef);
                if (outputDataName != null && outputDataName.trim().length() > 0) {
                    forEachNode.setOutputCollectionExpression(outputDataName);
                }
                
            }
            subNode = subNode.getNextSibling();
        }
    }
    

    
    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}