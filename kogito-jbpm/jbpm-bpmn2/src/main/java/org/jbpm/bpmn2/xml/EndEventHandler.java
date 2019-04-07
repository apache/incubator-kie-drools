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
import java.util.List;
import java.util.Map;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.bpmn2.core.Signal;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.FaultNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EndEventHandler extends AbstractNodeHandler {

    protected Node createNode(Attributes attrs) {
        EndNode node = new EndNode();
        node.setTerminate(false);
        return node;
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EndNode.class;
    }

    public Object end(final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Node node = (Node) parser.getCurrent();
        // determine type of event definition, so the correct type of node
        // can be generated
        super.handleNode(node, element, uri, localName, parser);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("terminateEventDefinition".equals(nodeName)) {
                // reuse already created EndNode
                handleTerminateNode(node, element, uri, localName, parser);
                break;
            } else if ("signalEventDefinition".equals(nodeName)) {
                handleSignalNode(node, element, uri, localName, parser);
            } else if ("messageEventDefinition".equals(nodeName)) {
                handleMessageNode(node, element, uri, localName, parser);
            } else if ("errorEventDefinition".equals(nodeName)) {
                // create new faultNode
                FaultNode faultNode = new FaultNode();
                faultNode.setId(node.getId());
                faultNode.setName(node.getName());
                faultNode.setTerminateParent(true);
                faultNode.setMetaData("UniqueId", node.getMetaData().get("UniqueId"));
                node = faultNode;
                super.handleNode(node, element, uri, localName, parser);
                handleErrorNode(node, element, uri, localName, parser);
                break;
            } else if ("escalationEventDefinition".equals(nodeName)) {
                // create new faultNode
                FaultNode faultNode = new FaultNode();
                faultNode.setId(node.getId());
                faultNode.setName(node.getName());
                faultNode.setMetaData("UniqueId", node.getMetaData().get("UniqueId"));
                node = faultNode;
                super.handleNode(node, element, uri, localName, parser);
                handleEscalationNode(node, element, uri, localName, parser);
                break;
            } else if ("compensateEventDefinition".equals(nodeName)) {
                // reuse already created ActionNode
                handleThrowCompensationEventNode(node, element, uri, localName, parser);
                break;
            }
            xmlNode = xmlNode.getNextSibling();
        }
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        ((ProcessBuildData) parser.getData()).addNode(node);
        return node;
    }

    public void handleTerminateNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        ((EndNode) node).setTerminate(true);

        EndNode endNode = (EndNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("terminateEventDefinition".equals(nodeName)) {

                String scope = ((Element) xmlNode).getAttribute("scope");
                if ("process".equalsIgnoreCase(scope)) {
                    endNode.setScope(EndNode.PROCESS_SCOPE);
                } else {
                    endNode.setScope(EndNode.CONTAINER_SCOPE);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    public void handleSignalNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        EndNode endNode = (EndNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInput".equals(nodeName)) {
                String id = ((Element) xmlNode).getAttribute("id");
                String inputName = ((Element) xmlNode).getAttribute("name");
                dataInputs.put(id, inputName);
            } else if ("dataInputAssociation".equals(nodeName)) {
                readEndDataInputAssociation(xmlNode, endNode);
            } else if ("signalEventDefinition".equals(nodeName)) {
                String signalName = ((Element) xmlNode).getAttribute("signalRef");
                String variable = (String) endNode.getMetaData("MappingVariable");

                signalName = checkSignalAndConvertToRealSignalNam(parser, signalName);

                endNode.setMetaData("EventType", "signal");
                endNode.setMetaData("Ref", signalName);
                endNode.setMetaData("Variable", variable);

                // check if signal should be send async
                if (dataInputs.containsValue("async")) {
                    signalName = "ASYNC-" + signalName;
                }

                String signalExpression = getSignalExpression(endNode, signalName, variable);

                List<DroolsAction> actions = new ArrayList<DroolsAction>();
                actions.add(new DroolsConsequenceAction("mvel",signalExpression));
                endNode.setActions(EndNode.EVENT_NODE_ENTER, actions);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    @SuppressWarnings("unchecked")
    public void handleMessageNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        EndNode endNode = (EndNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readEndDataInputAssociation(xmlNode, endNode);
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
                String variable = (String) endNode.getMetaData("MappingVariable");
                endNode.setMetaData("MessageType", message.getType());
                List<DroolsAction> actions = new ArrayList<DroolsAction>();

                actions.add(new DroolsConsequenceAction("java",
                    "org.drools.core.process.instance.impl.WorkItemImpl workItem = new org.drools.core.process.instance.impl.WorkItemImpl();" + EOL +
                    "workItem.setName(\"Send Task\");" + EOL +
                    "workItem.setNodeInstanceId(kcontext.getNodeInstance().getId());" + EOL +
                    "workItem.setProcessInstanceId(kcontext.getProcessInstance().getId());" + EOL +
                    "workItem.setNodeId(kcontext.getNodeInstance().getNodeId());" + EOL +
                    "workItem.setParameter(\"MessageType\", \"" + message.getType() + "\");" + EOL +
                    (variable == null ? "" : "workItem.setParameter(\"Message\", " + variable + ");" + EOL) +
					"workItem.setDeploymentId((String) kcontext.getKnowledgeRuntime().getEnvironment().get(\"deploymentId\"));" + EOL +
                    "((org.drools.core.process.instance.WorkItemManager) kcontext.getKnowledgeRuntime().getWorkItemManager()).internalExecuteWorkItem(workItem);"));
                endNode.setActions(EndNode.EVENT_NODE_ENTER, actions);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    protected void readEndDataInputAssociation(org.w3c.dom.Node xmlNode, EndNode endNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        if ("sourceRef".equals(subNode.getNodeName())) {
            String eventVariable = subNode.getTextContent();
            if (eventVariable != null && eventVariable.trim().length() > 0) {
                if (dataInputs.containsKey(eventVariable)) {
                    eventVariable = dataInputs.get(eventVariable);
                }
                
                endNode.setMetaData("MappingVariable", eventVariable);
            }
        } else {
            // targetRef
            // assignment
            subNode = subNode.getNextSibling();
            if (subNode != null) {
                org.w3c.dom.Node subSubNode = subNode.getFirstChild();
                NodeList nl = subSubNode.getChildNodes();
                if (nl.getLength() > 1) {
                    endNode.setMetaData("MappingVariable", subSubNode.getTextContent());
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
                endNode.setMetaData("MappingVariable", "\"" + result + "\"");
            }
        }
    }

    @SuppressWarnings("unchecked")
	public void handleErrorNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        FaultNode faultNode = (FaultNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readFaultDataInputAssociation(xmlNode, faultNode);
            } else if ("errorEventDefinition".equals(nodeName)) {
                String errorRef = ((Element) xmlNode).getAttribute("errorRef");
                if (errorRef != null && errorRef.trim().length() > 0) {
                    List<Error> errors = (List<Error>) ((ProcessBuildData) parser.getData()).getMetaData("Errors");
		            if (errors == null) {
		                throw new IllegalArgumentException("No errors found");
		            }
		            Error error = null;
		            for( Error listError: errors ) {
		                if( errorRef.equals(listError.getId()) ) {
		                    error = listError;
		                    break;
		                }
		            }
		            if (error == null) {
		                throw new IllegalArgumentException("Could not find error " + errorRef);
		            }
		            faultNode.setFaultName(error.getErrorCode());
	                faultNode.setTerminateParent(true);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    @SuppressWarnings("unchecked")
	public void handleEscalationNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        FaultNode faultNode = (FaultNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readFaultDataInputAssociation(xmlNode, faultNode);
            } else if ("escalationEventDefinition".equals(nodeName)) {
                String escalationRef = ((Element) xmlNode).getAttribute("escalationRef");
                if (escalationRef != null && escalationRef.trim().length() > 0) {
                    Map<String, Escalation> escalations = (Map<String, Escalation>)
		                ((ProcessBuildData) parser.getData()).getMetaData(ProcessHandler.ESCALATIONS);
		            if (escalations == null) {
		                throw new IllegalArgumentException("No escalations found");
		            }
		            Escalation escalation = escalations.get(escalationRef);
		            if (escalation == null) {
		                throw new IllegalArgumentException("Could not find escalation " + escalationRef);
		            }
		            faultNode.setFaultName(escalation.getEscalationCode());
                } else {
                    // BPMN2 spec, p. 83: end event's with <escalationEventDefintions>
                    // are _required_ to reference a specific escalation(-code).
                    throw new IllegalArgumentException("End events throwing an escalation must throw *specific* escalations (and not general ones).");
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    protected void readFaultDataInputAssociation(org.w3c.dom.Node xmlNode, FaultNode faultNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String faultVariable = subNode.getTextContent();
        faultNode.setFaultVariable(faultVariable);
    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}
