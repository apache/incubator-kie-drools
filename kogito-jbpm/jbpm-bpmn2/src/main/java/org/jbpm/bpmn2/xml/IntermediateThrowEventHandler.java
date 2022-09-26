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

import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.instance.impl.actions.HandleEscalationAction;
import org.jbpm.process.instance.impl.actions.HandleMessageAction;
import org.jbpm.process.instance.impl.actions.SignalProcessInstanceAction;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DataAssociation;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.IOSpecification;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.jbpm.bpmn2.xml.ProcessHandler.createJavaAction;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_MESSAGE;
import static org.jbpm.ruleflow.core.Metadata.MAPPING_VARIABLE;
import static org.jbpm.ruleflow.core.Metadata.MAPPING_VARIABLE_INPUT;
import static org.jbpm.ruleflow.core.Metadata.MESSAGE_TYPE;
import static org.jbpm.ruleflow.core.Metadata.PRODUCE_MESSAGE;
import static org.jbpm.ruleflow.core.Metadata.SIGNAL_TYPE;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_TYPE;

public class IntermediateThrowEventHandler extends AbstractNodeHandler {

    public static final String LINK_NAME = "linkName";
    public static final String LINK_SOURCE = "source";
    public static final String LINK_TARGET = "target";

    @Override
    protected Node createNode(Attributes attrs) {
        return new ActionNode();
    }

    @Override
    public Class<Node> generateNodeFor() {
        return Node.class;
    }

    @Override
    protected Node handleNode(Node newNode, Element element, String uri, String localName, Parser parser) throws SAXException {
        Node node = newNode;

        IOSpecification ioSpecification = readThrowSpecification(parser, element);

        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("signalEventDefinition".equals(nodeName)) {
                // reuse already created ActionNode
                setThrowVariable(ioSpecification, node);
                handleSignalNode(node, element, uri, localName, parser);
                break;
            } else if ("messageEventDefinition".equals(nodeName)) {
                // reuse already created ActionNode
                setThrowVariable(ioSpecification, node);
                handleMessageNode(node, element, uri, localName, parser);
                break;
            } else if ("escalationEventDefinition".equals(nodeName)) {
                // reuse already created ActionNode
                setThrowVariable(ioSpecification, node);
                handleEscalationNode(node, element, uri, localName, parser);
                break;
            } else if ("compensateEventDefinition".equals(nodeName)) {
                // reuse already created ActionNode
                setThrowVariable(ioSpecification, node);
                handleThrowCompensationEventNode(node, element, uri, localName, parser);
                break;
            } else if ("linkEventDefinition".equals(nodeName)) {
                ThrowLinkNode linkNode = new ThrowLinkNode();
                linkNode.setId(node.getId());
                node = linkNode;
                setThrowVariable(ioSpecification, node);
                handleLinkNode(element, node, xmlNode, parser);
            }
            xmlNode = xmlNode.getNextSibling();
        }

        if (node instanceof ActionNode) {
            ActionNode actionNode = (ActionNode) node;
            if (actionNode.getAction() == null) {
                actionNode.setAction(new DroolsConsequenceAction("java", ""));
                actionNode.setMetaData("NodeType", "IntermediateThrowEvent-None");
            }
        }

        return node;
    }

    protected void handleLinkNode(Element element, Node node,
            org.w3c.dom.Node xmlLinkNode, Parser parser) {

        node.setName(element.getAttribute("name"));

        NamedNodeMap linkAttr = xmlLinkNode.getAttributes();
        String name = linkAttr.getNamedItem("name").getNodeValue();

        String id = element.getAttribute("id");
        node.setMetaData("UniqueId", id);
        node.setMetaData(LINK_NAME, name);

        org.w3c.dom.Node xmlNode = xmlLinkNode.getFirstChild();

        NodeContainer nodeContainer = (NodeContainer) parser.getParent();

        IntermediateLink aLink = new IntermediateLink();
        aLink.setName(name);
        aLink.setUniqueId(id);
        while (null != xmlNode) {
            String nodeName = xmlNode.getNodeName();

            if (LINK_TARGET.equals(nodeName)) {
                String target = xmlNode.getTextContent();
                node.setMetaData(LINK_TARGET, target);
            }

            if (LINK_SOURCE.equals(nodeName)) {
                String source = xmlNode.getTextContent();
                ArrayList<String> sources = (ArrayList<String>) node
                        .getMetaData().get(LINK_SOURCE);

                // if there is no list, create one
                if (null == sources) {
                    sources = new ArrayList<>();
                }

                // to connect nodes.
                aLink.addSource(source);

                // to do the xml dump
                sources.add(source);
                node.setMetaData(LINK_SOURCE, sources);
            }
            xmlNode = xmlNode.getNextSibling();
        }
        aLink.configureThrow();

        if (nodeContainer instanceof RuleFlowProcess) {
            RuleFlowProcess process = (RuleFlowProcess) nodeContainer;
            List<IntermediateLink> links = (List<IntermediateLink>) process
                    .getMetaData().get(ProcessHandler.LINKS);
            if (null == links) {
                links = new ArrayList<>();
            }
            links.add(aLink);
            process.setMetaData(ProcessHandler.LINKS, links);
        } else if (nodeContainer instanceof CompositeNode) {
            CompositeNode subprocess = (CompositeNode) nodeContainer;
            List<IntermediateLink> links = (List<IntermediateLink>) subprocess
                    .getMetaData().get(ProcessHandler.LINKS);
            if (null == links) {
                links = new ArrayList<>();
            }
            links.add(aLink);
            subprocess.setMetaData(ProcessHandler.LINKS, links);
        }

    }

    public void handleSignalNode(final Node node, final Element element,
            final String uri, final String localName,
            final Parser parser) throws SAXException {
        ActionNode actionNode = (ActionNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("signalEventDefinition".equals(nodeName)) {
                String signalName = ((Element) xmlNode).getAttribute("signalRef");
                String variable = findVariable((String) actionNode.getMetaData(MAPPING_VARIABLE), parser);
                String inputVariable = findVariable((String) actionNode.getMetaData(MAPPING_VARIABLE_INPUT), parser);
                signalName = checkSignalAndConvertToRealSignalNam(parser, signalName);

                actionNode.setMetaData(EVENT_TYPE, "signal");
                actionNode.setMetaData(Metadata.REF, signalName);
                actionNode.setMetaData(Metadata.VARIABLE, variable);

                List<DataAssociation> inputs = actionNode.getIoSpecification().getDataInputAssociation();
                if (!inputs.isEmpty()) {
                    String type = inputs.get(0).getTarget().getType();
                    actionNode.setMetaData(SIGNAL_TYPE, type);
                }

                // check if signal should be send async
                if (actionNode.getIoSpecification().containsInputLabel("async")) {
                    signalName = "ASYNC-" + signalName;
                }

                DroolsConsequenceAction action = createJavaAction(
                        new SignalProcessInstanceAction(signalName,
                                variable,
                                inputVariable,
                                (String) actionNode.getMetaData("customScope")));
                actionNode.setAction(action);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    @SuppressWarnings("unchecked")
    public void handleMessageNode(final Node node, final Element element,
            final String uri, final String localName,
            final Parser parser) throws SAXException {
        ActionNode actionNode = (ActionNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("messageEventDefinition".equals(nodeName)) {
                String messageRef = ((Element) xmlNode)
                        .getAttribute("messageRef");
                Map<String, Message> messages = (Map<String, Message>) ((ProcessBuildData) parser
                        .getData()).getMetaData("Messages");
                if (messages == null) {
                    throw new ProcessParsingValidationException("No messages found");
                }
                Message message = messages.get(messageRef);
                if (message == null) {
                    throw new ProcessParsingValidationException(
                            "Could not find message " + messageRef);
                }
                String variable = (String) actionNode.getMetaData(MAPPING_VARIABLE);
                Variable v = (Variable) ((ProcessBuildData) parser.getData()).getMetaData("Variable");
                if (v != null) {
                    variable = (String) v.getMetaData(variable);
                }
                actionNode.setMetaData(EVENT_TYPE, EVENT_TYPE_MESSAGE);
                actionNode.setMetaData(MESSAGE_TYPE, message.getType());
                actionNode.setMetaData(TRIGGER_TYPE, PRODUCE_MESSAGE);
                actionNode.setMetaData(TRIGGER_REF, message.getName());

                DroolsConsequenceAction action = createJavaAction(new HandleMessageAction(message.getType(), variable));
                actionNode.setAction(action);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    @SuppressWarnings("unchecked")
    public void handleEscalationNode(final Node node, final Element element,
            final String uri, final String localName,
            final Parser parser) throws SAXException {
        ActionNode actionNode = (ActionNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("escalationEventDefinition".equals(nodeName)) {
                String escalationRef = ((Element) xmlNode)
                        .getAttribute("escalationRef");
                if (escalationRef != null && escalationRef.trim().length() > 0) {
                    Map<String, Escalation> escalations = (Map<String, Escalation>) ((ProcessBuildData) parser
                            .getData()).getMetaData(ProcessHandler.ESCALATIONS);
                    if (escalations == null) {
                        throw new ProcessParsingValidationException(
                                "No escalations found");
                    }
                    Escalation escalation = escalations.get(escalationRef);
                    if (escalation == null) {
                        throw new ProcessParsingValidationException(
                                "Could not find escalation " + escalationRef);
                    }
                    String faultName = escalation.getEscalationCode();
                    String variable = (String) actionNode.getMetaData(MAPPING_VARIABLE);

                    DroolsConsequenceAction action = createJavaAction(new HandleEscalationAction(faultName, variable));
                    actionNode.setAction(action);
                } else {
                    throw new ProcessParsingValidationException("General escalation is not yet supported");
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    @Override
    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException("Writing out should be handled by action node handler");
    }

}
