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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.core.process.core.Work;
import org.drools.core.process.core.datatype.DataType;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.drools.core.process.core.impl.WorkImpl;
import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TaskHandler extends AbstractNodeHandler {
    

    protected Node createNode(Attributes attrs) {
        return new WorkItemNode();
    }
    
	public Class<?> generateNodeFor() {
        return Node.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	WorkItemNode workItemNode = (WorkItemNode) node;
        String name = getTaskName(element);
        Work work = new WorkImpl();
        work.setName(name);
    	workItemNode.setWork(work);
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	String nodeName = xmlNode.getNodeName();
        	if ("ioSpecification".equals(nodeName)) {
        		readIoSpecification(xmlNode, dataInputs, dataOutputs);
        	} else if ("dataInputAssociation".equals(nodeName)) {
        		readDataInputAssociation(xmlNode, workItemNode, dataInputs);
        	} else if ("dataOutputAssociation".equals(nodeName)) {
        		readDataOutputAssociation(xmlNode, workItemNode, dataOutputs);
        	}
    		xmlNode = xmlNode.getNextSibling();
        }
        handleScript(workItemNode, element, "onEntry");
        handleScript(workItemNode, element, "onExit");
	}
    
    protected String getTaskName(final Element element) {
        return element.getAttribute("taskName");
    }

    protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, WorkItemNode workItemNode, Map<String, String> dataInputs) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		if ("sourceRef".equals(subNode.getNodeName())) {
    		String source = subNode.getTextContent();
    		// targetRef
    		subNode = subNode.getNextSibling();
    		String target = subNode.getTextContent();
    		subNode = subNode.getNextSibling();
    		List<Assignment> assignments = new LinkedList<Assignment>();
    		while(subNode != null){
    			org.w3c.dom.Node ssubNode = subNode.getFirstChild();
    			String from = ssubNode.getTextContent();
    			String to = ssubNode.getNextSibling().getTextContent();
    			assignments.add(new Assignment("XPath", from, to));
        		subNode = subNode.getNextSibling();
    		}
    		workItemNode.addInAssociation(new DataAssociation(
    				source,
    				dataInputs.get(target), assignments, null));
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
	    		    workItemNode.getWork().setParameter(dataInputs.get(to), subSubNode.getTextContent());
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
	    		workItemNode.getWork().setParameter(dataInputs.get(to), result);
			}
		}
    }
    
    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode, WorkItemNode workItemNode, Map<String, String> dataOutputs) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		String source = subNode.getTextContent();
		// targetRef
		subNode = subNode.getNextSibling();
		String target = subNode.getTextContent();
		subNode = subNode.getNextSibling();
		List<Assignment> assignments = new LinkedList<Assignment>();
		while(subNode != null){
			org.w3c.dom.Node ssubNode = subNode.getFirstChild();
			String from = ssubNode.getTextContent();
			String to = ssubNode.getNextSibling().getTextContent();
			assignments.add(new Assignment("XPath", from, to));
    		subNode = subNode.getNextSibling();
		}
		workItemNode.addOutAssociation(new DataAssociation(dataOutputs.get(source), target, assignments, null));
    }

    @Override
    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException(
            "Writing out should be handled by the WorkItemNodeHandler");
    }
    
    public Object end(final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		Node node = (Node) parser.getCurrent();
		// determine type of event definition, so the correct type of node
		// can be generated
    	handleNode(node, element, uri, localName, parser);
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
				// create new timerNode
				ForEachNode forEachNode = new ForEachNode();
				forEachNode.setId(node.getId());
				forEachNode.setMetaData("UniqueId", node.getMetaData().get("UniqueId"));
				node.setMetaData("UniqueId", null);
				node.setMetaData("hidden", true);
				forEachNode.addNode(node);
				forEachNode.linkIncomingConnections(NodeImpl.CONNECTION_DEFAULT_TYPE, node.getId(), NodeImpl.CONNECTION_DEFAULT_TYPE);
				forEachNode.linkOutgoingConnections(node.getId(), NodeImpl.CONNECTION_DEFAULT_TYPE, NodeImpl.CONNECTION_DEFAULT_TYPE);
				node = forEachNode;
				handleForEachNode(node, element, uri, localName, parser);
				break;
			}
			xmlNode = xmlNode.getNextSibling();
		}
		NodeContainer nodeContainer = (NodeContainer) parser.getParent();
		nodeContainer.addNode(node);
		return node;
	}

    protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, ForEachNode forEachNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String inputVariable = subNode.getTextContent();
        if (inputVariable != null && inputVariable.trim().length() > 0) {
        	forEachNode.setCollectionExpression(inputVariable);
        }
    }
    
	protected void handleForEachNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	ForEachNode forEachNode = (ForEachNode) node;
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, forEachNode);
            } else if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
            	readMultiInstanceLoopCharacteristics(xmlNode, forEachNode, parser);
            }
            xmlNode = xmlNode.getNextSibling();
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
}
