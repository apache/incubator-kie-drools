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

import java.util.Map;

import org.drools.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class IntermediateThrowEventHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new ActionNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Node.class;
    }

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        ActionNode node = (ActionNode) parser.getCurrent();
        // determine type of event definition, so the correct type of node
        // can be generated
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("signalEventDefinition".equals(nodeName)) {
                // reuse already created ActionNode
                handleSignalNode(node, element, uri, localName, parser);
                break;
            } else if ("messageEventDefinition".equals(nodeName)) {
                // reuse already created ActionNode
                handleMessageNode(node, element, uri, localName, parser);
                break;
            } else if ("escalationEventDefinition".equals(nodeName)) {
                // reuse already created ActionNode
                handleEscalationNode(node, element, uri, localName, parser);
                break;
            } else if ("compensateEventDefinition".equals(nodeName)) {
                // reuse already created ActionNode
                handleCompensationNode(node, element, uri, localName, parser);
                break;
            }
            xmlNode = xmlNode.getNextSibling();
        }
        // none event definition
        if (node.getAction() == null) {
            node.setAction(new DroolsConsequenceAction("mvel", ""));
            node.setMetaData("NodeType", "IntermediateThrowEvent-None");
        }
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        return node;
    }
    
    public void handleSignalNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        ActionNode actionNode = (ActionNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, actionNode);
            } else if ("signalEventDefinition".equals(nodeName)) {
                String signalName = ((Element) xmlNode).getAttribute("signalRef");
                String variable = (String) actionNode.getMetaData("MappingVariable");
                actionNode.setAction(new DroolsConsequenceAction("mvel",
                    "kcontext.getKnowledgeRuntime().signalEvent(\""
                        + signalName + "\", " + (variable == null ? "null" : variable) + ")"));
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void handleMessageNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        ActionNode actionNode = (ActionNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, actionNode);
            } else if ("messageEventDefinition".equals(nodeName)) {
                String messageRef = ((Element) xmlNode).getAttribute("messageRef");
                Map<String, Message> messages = (Map<String, Message>)
                    ((ProcessBuildData) parser.getData()).getMetaData("Messages");
                if (messages == null) {
                    throw new IllegalArgumentException("No messages found");
                }
                Message message = messages.get(messageRef);
                if (message == null) {
                    throw new IllegalArgumentException("Could not find message " + messageRef);
                }
                String variable = (String) actionNode.getMetaData("MappingVariable");
                actionNode.setMetaData("MessageType", message.getType());
                actionNode.setAction(new DroolsConsequenceAction("java",
                    "org.drools.process.instance.impl.WorkItemImpl workItem = new org.drools.process.instance.impl.WorkItemImpl();" + EOL + 
                    "workItem.setName(\"Send Task\");" + EOL + 
                    "workItem.setParameter(\"MessageType\", \"" + message.getType() + "\");" + EOL + 
                    (variable == null ? "" : "workItem.setParameter(\"Message\", " + variable + ");" + EOL) +
                    "((org.drools.process.instance.WorkItemManager) kcontext.getKnowledgeRuntime().getWorkItemManager()).internalExecuteWorkItem(workItem);"));
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    @SuppressWarnings("unchecked")
	public void handleEscalationNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        ActionNode actionNode = (ActionNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, actionNode);
            } else if ("escalationEventDefinition".equals(nodeName)) {
            	String escalationRef = ((Element) xmlNode).getAttribute("escalationRef");
                if (escalationRef != null && escalationRef.trim().length() > 0) {
                    Map<String, Escalation> escalations = (Map<String, Escalation>)
		                ((ProcessBuildData) parser.getData()).getMetaData("Escalations");
		            if (escalations == null) {
		                throw new IllegalArgumentException("No escalations found");
		            }
		            Escalation escalation = escalations.get(escalationRef);
		            if (escalation == null) {
		                throw new IllegalArgumentException("Could not find escalation " + escalationRef);
		            }
		            String faultName = escalation.getEscalationCode();
                    actionNode.setAction(new DroolsConsequenceAction("java",
                        "org.jbpm.process.instance.context.exception.ExceptionScopeInstance scopeInstance = (org.jbpm.process.instance.context.exception.ExceptionScopeInstance) ((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance()).resolveContextInstance(org.jbpm.process.core.context.exception.ExceptionScope.EXCEPTION_SCOPE, \"" + faultName + "\");" + EOL + 
                        "if (scopeInstance != null) {" + EOL + 
                        "  scopeInstance.handleException(\"" + faultName + "\", null);" + EOL + 
                        "} else {" + EOL + 
                        "    ((org.jbpm.process.instance.ProcessInstance) kcontext.getProcessInstance()).setState(org.jbpm.process.instance.ProcessInstance.STATE_ABORTED);" + EOL + 
                        "}"));
                }
            } 
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    public void handleCompensationNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        ActionNode actionNode = (ActionNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("compensateEventDefinition".equals(nodeName)) {
                String activityRef = ((Element) xmlNode).getAttribute("activityRef");
                if (activityRef != null && activityRef.trim().length() > 0) {
                	actionNode.setMetaData("Compensate", activityRef);
                	actionNode.setAction(new DroolsConsequenceAction("java", 
            			"kcontext.getProcessInstance().signalEvent(\"Compensate-" + activityRef + "\", null);"));
                }
//                boolean waitForCompletion = true;
//                String waitForCompletionString = ((Element) xmlNode).getAttribute("waitForCompletion");
//                if ("false".equals(waitForCompletionString)) {
//                    waitForCompletion = false;
//                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, ActionNode actionNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String eventVariable = subNode.getTextContent();
        if (eventVariable != null && eventVariable.trim().length() > 0) {
            actionNode.setMetaData("MappingVariable", eventVariable);
        }
    }

	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException("Writing out should be handled by action node handler");
    }

}
