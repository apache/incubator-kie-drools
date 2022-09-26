/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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

import org.jbpm.bpmn2.core.Error;
import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.instance.impl.actions.HandleMessageAction;
import org.jbpm.process.instance.impl.actions.SignalProcessInstanceAction;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.IOSpecification;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.FaultNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.jbpm.bpmn2.xml.ProcessHandler.createJavaAction;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_MESSAGE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_SIGNAL;
import static org.jbpm.ruleflow.core.Metadata.MAPPING_VARIABLE;
import static org.jbpm.ruleflow.core.Metadata.MAPPING_VARIABLE_INPUT;
import static org.jbpm.ruleflow.core.Metadata.PRODUCE_MESSAGE;
import static org.jbpm.ruleflow.core.Metadata.VARIABLE;

public class EndEventHandler extends AbstractNodeHandler {

    @Override
    protected Node createNode(Attributes attrs) {
        EndNode node = new EndNode();
        node.setTerminate(false);
        return node;
    }

    @Override
    public Class<EndNode> generateNodeFor() {
        return EndNode.class;
    }

    @Override
    protected Node handleNode(Node newNode, Element element, String uri, String localName, Parser parser) throws SAXException {
        NodeImpl node = (NodeImpl) newNode;
        // determine type of event definition, so the correct type of node
        // can be generated
        super.handleNode(node, element, uri, localName, parser);

        // all nodes are catch nodes but error and escalation
        IOSpecification ioSpecification = readThrowSpecification(parser, element);

        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("terminateEventDefinition".equals(nodeName)) {
                setThrowVariable(ioSpecification, node);
                handleTerminateNode(node, element, uri, localName, parser);
                break;
            } else if ("signalEventDefinition".equals(nodeName)) {
                setThrowVariable(ioSpecification, node);
                handleSignalNode(node, element, uri, localName, parser);
            } else if ("messageEventDefinition".equals(nodeName)) {
                setThrowVariable(ioSpecification, node);
                handleMessageNode(node, element, uri, localName, parser);
            } else if ("errorEventDefinition".equals(nodeName)) {
                FaultNode faultNode = new FaultNode();
                faultNode.setId(node.getId());
                faultNode.setName(node.getName());
                faultNode.setTerminateParent(true);
                faultNode.setMetaData("UniqueId", node.getMetaData().get("UniqueId"));
                node = faultNode;
                setThrowVariable(ioSpecification, node);
                faultNode.setFaultVariable((String) node.getMetaData().get(Metadata.VARIABLE));
                super.handleNode(node, element, uri, localName, parser);
                handleErrorNode(node, element, uri, localName, parser);
                break;
            } else if ("escalationEventDefinition".equals(nodeName)) {
                FaultNode faultNode = new FaultNode();
                faultNode.setId(node.getId());
                faultNode.setName(node.getName());
                faultNode.setMetaData("UniqueId", node.getMetaData().get("UniqueId"));
                node = faultNode;
                setThrowVariable(ioSpecification, node);
                faultNode.setFaultVariable((String) node.getMetaData().get(Metadata.VARIABLE));
                super.handleNode(node, element, uri, localName, parser);
                handleEscalationNode(node, element, uri, localName, parser);
                break;
            } else if ("compensateEventDefinition".equals(nodeName)) {
                setThrowVariable(ioSpecification, node);
                handleThrowCompensationEventNode(node, element, uri, localName, parser);
                break;
            }
            xmlNode = xmlNode.getNextSibling();
        }

        if (node.getName() == null) {
            node.setName("End");
        }
        return node;
    }

    public void handleTerminateNode(final Node node, final Element element, final String uri,
            final String localName, final Parser parser) throws SAXException {
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
            final String localName, final Parser parser) throws SAXException {
        EndNode endNode = (EndNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("signalEventDefinition".equals(nodeName)) {
                String signalName = ((Element) xmlNode).getAttribute("signalRef");
                String variable = (String) endNode.getMetaData(VARIABLE);
                String inputVariable = (String) endNode.getMetaData(MAPPING_VARIABLE_INPUT);
                signalName = checkSignalAndConvertToRealSignalNam(parser, signalName);

                endNode.setMetaData(Metadata.EVENT_TYPE, EVENT_TYPE_SIGNAL);
                endNode.setMetaData(Metadata.REF, signalName);
                endNode.setMetaData(Metadata.VARIABLE, variable);

                // check if signal should be send async
                if (endNode.getIoSpecification().containsInputLabel("async")) {
                    signalName = "ASYNC-" + signalName;
                }

                DroolsConsequenceAction action = createJavaAction(
                        new SignalProcessInstanceAction(signalName, variable, inputVariable, (String) endNode.getMetaData("customScope")));

                List<DroolsAction> actions = new ArrayList<>();
                actions.add(action);
                endNode.setActions(EndNode.EVENT_NODE_ENTER, actions);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    @SuppressWarnings("unchecked")
    public void handleMessageNode(final Node node, final Element element, final String uri,
            final String localName, final Parser parser) throws SAXException {
        EndNode endNode = (EndNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("messageEventDefinition".equals(nodeName)) {
                String messageRef = ((Element) xmlNode).getAttribute("messageRef");
                Map<String, Message> messages = (Map<String, Message>) ((ProcessBuildData) parser.getData()).getMetaData("Messages");
                if (messages == null) {
                    throw new ProcessParsingValidationException("No messages found");
                }
                Message message = messages.get(messageRef);
                if (message == null) {
                    throw new ProcessParsingValidationException("Could not find message " + messageRef);
                }
                String variable = (String) endNode.getMetaData(MAPPING_VARIABLE);
                endNode.setMetaData(Metadata.EVENT_TYPE, EVENT_TYPE_MESSAGE);
                endNode.setMetaData(Metadata.MESSAGE_TYPE, message.getType());
                endNode.setMetaData(Metadata.TRIGGER_TYPE, PRODUCE_MESSAGE);
                endNode.setMetaData(Metadata.TRIGGER_REF, message.getName());
                List<DroolsAction> actions = new ArrayList<>();

                DroolsConsequenceAction action = createJavaAction(new HandleMessageAction(message.getType(), variable));

                actions.add(action);
                endNode.setActions(EndNode.EVENT_NODE_ENTER, actions);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    @SuppressWarnings("unchecked")
    public void handleErrorNode(final Node node, final Element element, final String uri,
            final String localName, final Parser parser) throws SAXException {
        FaultNode faultNode = (FaultNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("errorEventDefinition".equals(nodeName)) {
                String errorRef = ((Element) xmlNode).getAttribute("errorRef");
                if (errorRef != null && errorRef.trim().length() > 0) {
                    List<Error> errors = (List<Error>) ((ProcessBuildData) parser.getData()).getMetaData("Errors");
                    if (errors == null) {
                        throw new ProcessParsingValidationException("No errors found");
                    }
                    Error error = null;
                    for (Error listError : errors) {
                        if (errorRef.equals(listError.getId())) {
                            error = listError;
                            break;
                        }
                    }
                    if (error == null) {
                        throw new ProcessParsingValidationException("Could not find error " + errorRef);
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
            final String localName, final Parser parser) throws SAXException {
        FaultNode faultNode = (FaultNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("escalationEventDefinition".equals(nodeName)) {
                String escalationRef = ((Element) xmlNode).getAttribute("escalationRef");
                if (escalationRef != null && escalationRef.trim().length() > 0) {
                    Map<String, Escalation> escalations = (Map<String, Escalation>) ((ProcessBuildData) parser.getData()).getMetaData(ProcessHandler.ESCALATIONS);
                    if (escalations == null) {
                        throw new ProcessParsingValidationException("No escalations found");
                    }
                    Escalation escalation = escalations.get(escalationRef);
                    if (escalation == null) {
                        throw new ProcessParsingValidationException("Could not find escalation " + escalationRef);
                    }
                    faultNode.setFaultName(escalation.getEscalationCode());
                } else {
                    // BPMN2 spec, p. 83: end event's with <escalationEventDefintions>
                    // are _required_ to reference a specific escalation(-code).
                    throw new ProcessParsingValidationException("End events throwing an escalation must throw *specific* escalations (and not general ones).");
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    @Override
    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}
