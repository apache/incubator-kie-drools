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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DataAssociation;
import org.jbpm.workflow.core.impl.IOSpecification;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.jbpm.ruleflow.core.Metadata.CONSUME_MESSAGE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_CONDITIONAL;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_LINK;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_MESSAGE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_SIGNAL;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_TIMER;
import static org.jbpm.ruleflow.core.Metadata.LINK_NAME;
import static org.jbpm.ruleflow.core.Metadata.MESSAGE_TYPE;
import static org.jbpm.ruleflow.core.Metadata.SIGNAL_TYPE;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_TYPE;

public class IntermediateCatchEventHandler extends AbstractNodeHandler {

    protected Node createNode(Attributes attrs) {
        return new EventNode();
    }

    public Class<EventNode> generateNodeFor() {
        return EventNode.class;
    }

    @Override
    protected Node handleNode(Node newNode, Element element, String uri, String localName, ExtensibleXmlParser parser) throws SAXException {
        NodeImpl node = (NodeImpl) newNode;
        // determine type of event definition, so the correct type of node
        // can be generated
        IOSpecification ioSpecification = readCatchSpecification(parser, element);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("signalEventDefinition".equals(nodeName)) {
                // reuse already created EventNode
                setCatchVariable(ioSpecification, node);
                handleSignalNode(node, element, uri, localName, parser);
                node.setMetaData(EVENT_TYPE, EVENT_TYPE_SIGNAL);
                break;
            } else if ("messageEventDefinition".equals(nodeName)) {
                // reuse already created EventNode
                setCatchVariable(ioSpecification, node);
                handleMessageNode(node, element, uri, localName, parser);
                node.setMetaData(EVENT_TYPE, EVENT_TYPE_MESSAGE);
                break;
            } else if ("timerEventDefinition".equals(nodeName)) {
                // create new timerNode
                TimerNode timerNode = new TimerNode();
                timerNode.setId(node.getId());
                timerNode.setName(node.getName());
                timerNode.setMetaData("UniqueId",
                        node.getMetaData().get("UniqueId"));
                node = timerNode;
                setCatchVariable(ioSpecification, node);
                node.setMetaData(EVENT_TYPE, EVENT_TYPE_TIMER);
                handleTimerNode(node, element, uri, localName, parser);
                break;
            } else if ("conditionalEventDefinition".equals(nodeName)) {
                // create new stateNode
                StateNode stateNode = new StateNode();
                stateNode.setId(node.getId());
                stateNode.setName(node.getName());
                stateNode.setMetaData("UniqueId", node.getMetaData().get("UniqueId"));
                node = stateNode;
                setCatchVariable(ioSpecification, node);
                node.setMetaData(EVENT_TYPE, EVENT_TYPE_CONDITIONAL);
                handleStateNode(node, element, uri, localName, parser);
                break;
            } else if ("linkEventDefinition".equals(nodeName)) {
                CatchLinkNode linkNode = new CatchLinkNode();
                linkNode.setId(node.getId());
                node = linkNode;
                setCatchVariable(ioSpecification, node);
                node.setMetaData(EVENT_TYPE, EVENT_TYPE_LINK);
                handleLinkNode(element, node, xmlNode, parser);
                break;
            }
            xmlNode = xmlNode.getNextSibling();
        }

        return node;
    }

    protected void handleLinkNode(Element element, Node node,
            org.w3c.dom.Node xmlLinkNode, ExtensibleXmlParser parser) {
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();

        node.setName(element.getAttribute("name"));

        NamedNodeMap linkAttr = xmlLinkNode.getAttributes();
        String name = linkAttr.getNamedItem("name").getNodeValue();
        String id = element.getAttribute("id");

        node.setMetaData("UniqueId", id);
        node.setMetaData(LINK_NAME, name);

        org.w3c.dom.Node xmlNode = xmlLinkNode.getFirstChild();

        IntermediateLink aLink = new IntermediateLink();
        aLink.setName(name);
        aLink.setUniqueId(id);

        while (null != xmlNode) {
            String nodeName = xmlNode.getNodeName();
            if ("target".equals(nodeName)) {
                String target = xmlNode.getTextContent();
                node.setMetaData("target", target);
                aLink.setTarget(target);
            }
            if ("source".equals(nodeName)) {
                String source = xmlNode.getTextContent();
                node.setMetaData("source", source);
                aLink.addSource(source);
            }
            xmlNode = xmlNode.getNextSibling();
        }

        if (nodeContainer instanceof RuleFlowProcess) {
            RuleFlowProcess process = (RuleFlowProcess) nodeContainer;
            List<IntermediateLink> links = (List<IntermediateLink>) process
                    .getMetaData().get(ProcessHandler.LINKS);
            if (null == links) {
                links = new ArrayList<IntermediateLink>();
            }
            links.add(aLink);
            process.setMetaData(ProcessHandler.LINKS, links);
        } else if (nodeContainer instanceof CompositeNode) {
            CompositeNode subprocess = (CompositeNode) nodeContainer;
            List<IntermediateLink> links = (List<IntermediateLink>) subprocess
                    .getMetaData().get(ProcessHandler.LINKS);
            if (null == links) {
                links = new ArrayList<IntermediateLink>();
            }
            links.add(aLink);
            subprocess.setMetaData(ProcessHandler.LINKS, links);
        }
    }

    protected void handleSignalNode(final Node node, final Element element,
            final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("signalEventDefinition".equals(nodeName)) {
                String type = ((Element) xmlNode).getAttribute("signalRef");
                if (type != null && type.trim().length() > 0) {

                    type = checkSignalAndConvertToRealSignalNam(parser, type);

                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType(type);
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                }
                List<DataAssociation> inputs = eventNode.getIoSpecification().getDataInputAssociation();
                if (!inputs.isEmpty()) {
                    String signalType = inputs.get(0).getTarget().getType();
                    eventNode.setMetaData(SIGNAL_TYPE, signalType);
                }

            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    @SuppressWarnings("unchecked")
    protected void handleMessageNode(final Node node, final Element element,
            final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            String id = ((Element) xmlNode).getAttribute("id");
            String name = ((Element) xmlNode).getAttribute("name");
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
                    throw new MalformedNodeException(
                            id, name,
                            MessageFormat.format("Could not find message \"{0}\"", messageRef));
                }
                eventNode.setMetaData(MESSAGE_TYPE, message.getType());
                eventNode.setMetaData(TRIGGER_TYPE, CONSUME_MESSAGE);
                eventNode.setMetaData(TRIGGER_REF, message.getName());
                List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                EventTypeFilter eventFilter = new EventTypeFilter();
                eventFilter.setCorrelationManager(((RuleFlowProcess) parser.getMetaData().get("CurrentProcessDefinition")).getCorrelationManager());
                eventFilter.setType("Message-" + message.getName());
                eventFilter.setMessageRef(message.getId());
                eventFilters.add(eventFilter);
                eventNode.setEventFilters(eventFilters);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    protected void handleTimerNode(final Node node, final Element element,
            final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        TimerNode timerNode = (TimerNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("timerEventDefinition".equals(nodeName)) {
                Timer timer = new Timer();
                org.w3c.dom.Node subNode = xmlNode.getFirstChild();
                while (subNode instanceof Element) {
                    String subNodeName = subNode.getNodeName();
                    if ("timeCycle".equals(subNodeName)) {
                        String delay = subNode.getTextContent();
                        int index = delay.indexOf("###");
                        if (index != -1) {
                            String period = delay.substring(index + 3);
                            delay = delay.substring(0, index);
                            timer.setPeriod(period);
                        }
                        timer.setTimeType(Timer.TIME_CYCLE);
                        timer.setDelay(delay);
                        break;
                    } else if ("timeDuration".equals(subNodeName)) {
                        String delay = subNode.getTextContent();
                        timer.setTimeType(Timer.TIME_DURATION);
                        timer.setDelay(delay);
                        break;
                    } else if ("timeDate".equals(subNodeName)) {
                        String date = subNode.getTextContent();
                        timer.setTimeType(Timer.TIME_DATE);
                        timer.setDate(date);
                        break;
                    }
                    subNode = subNode.getNextSibling();
                }
                timerNode.setTimer(timer);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    protected void handleStateNode(final Node node, final Element element,
            final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        StateNode stateNode = (StateNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("conditionalEventDefinition".equals(nodeName)) {
                org.w3c.dom.Node subNode = xmlNode.getFirstChild();
                while (subNode != null) {
                    String subnodeName = subNode.getNodeName();
                    if ("condition".equals(subnodeName)) {
                        stateNode.setMetaData("Condition",
                                xmlNode.getTextContent());
                        break;
                    }
                    subNode = subNode.getNextSibling();
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}
