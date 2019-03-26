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

import java.util.List;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.SequenceFlow;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.definition.process.Connection;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SubProcessHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
    	CompositeContextNode subProcessNode = new CompositeContextNode();    	
        String eventSubprocessAttribute = attrs.getValue("triggeredByEvent");
        if (eventSubprocessAttribute != null && Boolean.parseBoolean(eventSubprocessAttribute)) {            
            subProcessNode = new EventSubProcessNode();
    	}
        VariableScope variableScope = new VariableScope();
        subProcessNode.addContext(variableScope);
        subProcessNode.setDefaultContext(variableScope);
        
        String compensation = attrs.getValue("isForCompensation");
        if( compensation != null ) {
            boolean isForCompensation = Boolean.parseBoolean(compensation);
            if( isForCompensation ) { 
                subProcessNode.setMetaData("isForCompensation", isForCompensation );
            }
        }        
        subProcessNode.setAutoComplete(true);
        return subProcessNode;
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return CompositeContextNode.class;
    }

    public Object end(final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		Node node = (Node) parser.getCurrent();
		
		// determine type of event definition, so the correct type of node can be generated
		boolean found = false;		
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			 if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
			    Boolean isAsync = Boolean.parseBoolean((String)node.getMetaData().get("customAsync"));
				// create new timerNode
				ForEachNode forEachNode = new ForEachNode();
				forEachNode.setId(node.getId());
				forEachNode.setName(node.getName());
				
				forEachNode.setAutoComplete(((CompositeContextNode) node).isAutoComplete());
				
				for (org.kie.api.definition.process.Node subNode: ((CompositeContextNode) node).getNodes()) {
			
					forEachNode.addNode(subNode);
				}
				forEachNode.setMetaData("UniqueId", ((CompositeContextNode) node).getMetaData("UniqueId"));				
				forEachNode.setMetaData(ProcessHandler.CONNECTIONS, ((CompositeContextNode) node).getMetaData(ProcessHandler.CONNECTIONS));
				VariableScope v = (VariableScope) ((CompositeContextNode) node).getDefaultContext(VariableScope.VARIABLE_SCOPE);
				((VariableScope) ((CompositeContextNode) forEachNode.internalGetNode(2)).getDefaultContext(VariableScope.VARIABLE_SCOPE)).setVariables(v.getVariables());
				node = forEachNode;
				handleForEachNode(node, element, uri, localName, parser, isAsync);
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
        ((ProcessBuildData) parser.getData()).addNode(node);

		return node;
	}
    
    @SuppressWarnings("unchecked")
	protected void handleCompositeContextNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	CompositeContextNode compositeNode = (CompositeContextNode) node;
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			compositeNode.getMetaData(ProcessHandler.CONNECTIONS);
    	
    	handleScript(compositeNode, element, "onEntry");
        handleScript(compositeNode, element, "onExit");
    	
    	List<IntermediateLink> throwLinks = (List<IntermediateLink>) compositeNode.getMetaData(ProcessHandler.LINKS);
    	ProcessHandler.linkIntermediateLinks(compositeNode, throwLinks);	
    	
    	ProcessHandler.linkConnections(compositeNode, connections);
    	ProcessHandler.linkBoundaryEvents(compositeNode);
    	
        // This must be done *after* linkConnections(process, connections)
        //  because it adds hidden connections for compensations
        List<Association> associations = (List<Association>) compositeNode.getMetaData(ProcessHandler.ASSOCIATIONS);
        ProcessHandler.linkAssociations((Definitions) compositeNode.getMetaData("Definitions"), compositeNode, associations);
        
        // TODO: do we fully support interruping ESP's? 
        /** 
        for( org.kie.api.definition.process.Node subNode : compositeNode.getNodes() ) { 
            if( subNode instanceof StartNode ) { 
                if( ! ((StartNode) subNode).isInterrupting() ) { 
                    throw new IllegalArgumentException("Non-interrupting event subprocesses are not yet fully supported." );
                }
            }
        }
        */
        
    }
    
    @SuppressWarnings("unchecked")
	protected void handleForEachNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser, boolean isAsync) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	ForEachNode forEachNode = (ForEachNode) node;
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("ioSpecification".equals(nodeName)) {
                readIoSpecification(xmlNode, dataInputs, dataOutputs);
            } else if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, inputAssociation);
            } else if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, outputAssociation);
            } else if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
            	readMultiInstanceLoopCharacteristics(xmlNode, forEachNode, parser);
            }
            xmlNode = xmlNode.getNextSibling();
        }
        handleScript(forEachNode, element, "onEntry");
        handleScript(forEachNode, element, "onExit");
        
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			forEachNode.getMetaData(ProcessHandler.CONNECTIONS);
    	ProcessHandler.linkConnections(forEachNode, connections);
    	ProcessHandler.linkBoundaryEvents(forEachNode);
    
    	
        // This must be done *after* linkConnections(process, connections)
        //  because it adds hidden connections for compensations
        List<Association> associations = (List<Association>) forEachNode.getMetaData(ProcessHandler.ASSOCIATIONS);
        ProcessHandler.linkAssociations((Definitions) forEachNode.getMetaData("Definitions"), forEachNode, associations);
        applyAsync(node, isAsync);
    }  
    
    protected void applyAsync(Node node, boolean isAsync) {
        for (org.kie.api.definition.process.Node subNode: ((CompositeContextNode) node).getNodes()) {
            if (isAsync) {
                List<Connection> incoming = subNode.getIncomingConnections(NodeImpl.CONNECTION_DEFAULT_TYPE);
                if (incoming != null) {
                    for (Connection con : incoming) {
                        if (con.getFrom() instanceof StartNode) {
                            ((Node)subNode).setMetaData("customAsync", Boolean.toString(isAsync));
                            return;
                        }
                    }
                }
                
            }            
        }
    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}
