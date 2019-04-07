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
import org.jbpm.bpmn2.core.IntermediateLink;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.bpmn2.core.Signal;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTransformerImpl;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.api.runtime.process.DataTransformer;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class IntermediateCatchEventHandler extends AbstractNodeHandler {

	private DataTransformerRegistry transformerRegistry = DataTransformerRegistry.get();

    public static final String LINK_NAME = "LinkName";

    protected Node createNode(Attributes attrs) {
        return new EventNode();
    }

    @SuppressWarnings("unchecked")
    public Class generateNodeFor() {
        return EventNode.class;
    }

    public Object end(final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Node node = (Node) parser.getCurrent();
        // determine type of event definition, so the correct type of node
        // can be generated
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("signalEventDefinition".equals(nodeName)) {
                // reuse already created EventNode
                handleSignalNode(node, element, uri, localName, parser);
                break;
            } else if ("messageEventDefinition".equals(nodeName)) {
                // reuse already created EventNode
                handleMessageNode(node, element, uri, localName, parser);
                break;
            } else if ("timerEventDefinition".equals(nodeName)) {
                // create new timerNode
                TimerNode timerNode = new TimerNode();
                timerNode.setId(node.getId());
                timerNode.setName(node.getName());
                timerNode.setMetaData("UniqueId",
                        node.getMetaData().get("UniqueId"));
                node = timerNode;
                handleTimerNode(node, element, uri, localName, parser);
                break;
            } else if ("conditionalEventDefinition".equals(nodeName)) {
                // create new stateNode
                StateNode stateNode = new StateNode();
                stateNode.setId(node.getId());
                stateNode.setName(node.getName());
                stateNode.setMetaData("UniqueId",
                        node.getMetaData().get("UniqueId"));
                node = stateNode;
                handleStateNode(node, element, uri, localName, parser);
                break;
            } else if ("linkEventDefinition".equals(nodeName)) {
                CatchLinkNode linkNode = new CatchLinkNode();
                linkNode.setId(node.getId());
                node = linkNode;
                handleLinkNode(element, node, xmlNode, parser);
                break;
            }
            xmlNode = xmlNode.getNextSibling();
        }
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        ((ProcessBuildData) parser.getData()).addNode(node);
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

    @SuppressWarnings("unchecked")
	protected void handleSignalNode(final Node node, final Element element,
            final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataOutput".equals(nodeName)) {
                String id = ((Element) xmlNode).getAttribute("id");
                String outputName = ((Element) xmlNode).getAttribute("name");
                dataOutputs.put(id, outputName);
            }  else if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, eventNode);
            } else if ("signalEventDefinition".equals(nodeName)) {
                String type = ((Element) xmlNode).getAttribute("signalRef");
                if (type != null && type.trim().length() > 0) {

                    type = checkSignalAndConvertToRealSignalNam(parser, type);

                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType(type);
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
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
            if ("dataOutput".equals(nodeName)) {
                String id = ((Element) xmlNode).getAttribute("id");
                String outputName = ((Element) xmlNode).getAttribute("name");
                dataOutputs.put(id, outputName);
            } else if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, eventNode);
            } else if ("messageEventDefinition".equals(nodeName)) {
                String messageRef = ((Element) xmlNode)
                        .getAttribute("messageRef");
                Map<String, Message> messages = (Map<String, Message>) ((ProcessBuildData) parser
                        .getData()).getMetaData("Messages");
                if (messages == null) {
                    throw new IllegalArgumentException("No messages found");
                }
                Message message = messages.get(messageRef);
                if (message == null) {
                    throw new IllegalArgumentException(
                            "Could not find message " + messageRef);
                }
                eventNode.setMetaData("MessageType", message.getType());
                List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                EventTypeFilter eventFilter = new EventTypeFilter();
                eventFilter.setType("Message-" + message.getName());
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

    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode,
            EventNode eventNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String from = subNode.getTextContent();
        // targetRef
        subNode = subNode.getNextSibling();
        String to = subNode.getTextContent();
        eventNode.setVariableName(to);
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
  			transformation = new Transformation(lang, expression, dataOutputs.get(from));
  			eventNode.setMetaData("Transformation", transformation);

  			eventNode.setEventTransformer(new EventTransformerImpl(transformation));
  		}
    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        throw new IllegalArgumentException(
                "Writing out should be handled by specific handlers");
    }

}
