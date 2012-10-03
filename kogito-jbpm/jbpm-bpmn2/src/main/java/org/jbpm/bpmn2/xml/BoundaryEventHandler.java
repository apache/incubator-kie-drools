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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.StateNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BoundaryEventHandler extends AbstractNodeHandler {
    
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
        String attachedTo = element.getAttribute("attachedToRef");
        String cancelActivityString = element.getAttribute("cancelActivity");
        boolean cancelActivity = true;
        if ("false".equals(cancelActivityString)) {
            cancelActivity = false;
        }
        // determine type of event definition, so the correct type of node
        // can be generated
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("escalationEventDefinition".equals(nodeName)) {
                // reuse already created EventNode
                handleEscalationNode(node, element, uri, localName, parser, attachedTo, cancelActivity);
                break;
            } else if ("errorEventDefinition".equals(nodeName)) {
                // reuse already created EventNode
                handleErrorNode(node, element, uri, localName, parser, attachedTo, cancelActivity);
                break;
            } else if ("timerEventDefinition".equals(nodeName)) {
                // reuse already created EventNode
                handleTimerNode(node, element, uri, localName, parser, attachedTo, cancelActivity);
                break;
            } else if ("compensateEventDefinition".equals(nodeName)) {
                // reuse already created EventNode
                handleCompensationNode(node, element, uri, localName, parser, attachedTo, cancelActivity);
                break;
            } else if ("signalEventDefinition".equals(nodeName)) {
                // reuse already created EventNode
                handleSignalNode(node, element, uri, localName, parser, attachedTo, cancelActivity);
                break;
            } else if ("conditionalEventDefinition".equals(nodeName)) {
                handleConditionNode(node, element, uri, localName, parser, attachedTo, cancelActivity);
                break;
            } else if ("messageEventDefinition".equals(nodeName)) {
                handleMessageNode(node, element, uri, localName, parser, attachedTo, cancelActivity);
                break;
            }
            xmlNode = xmlNode.getNextSibling();
        }
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        return node;
    }
    
    @SuppressWarnings("unchecked")
	protected void handleEscalationNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser, final String attachedTo,
            final boolean cancelActivity) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setMetaData("CancelActivity", cancelActivity);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("escalationEventDefinition".equals(nodeName)) {
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
                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    String type = escalation.getEscalationCode();
                    eventFilter.setType("Escalation-" + attachedTo + "-" + type);
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                    eventNode.setMetaData("EscalationEvent", type);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    @SuppressWarnings("unchecked")
	protected void handleErrorNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser, final String attachedTo,
            final boolean cancelActivity) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("errorEventDefinition".equals(nodeName)) {
                String errorRef = ((Element) xmlNode).getAttribute("errorRef");
                if (errorRef != null && errorRef.trim().length() > 0) {
                	Map<String, Error> errors = (Map<String, Error>)
		                ((ProcessBuildData) parser.getData()).getMetaData("Errors");
		            if (errors == null) {
		                throw new IllegalArgumentException("No errors found");
		            }
		            Error error = errors.get(errorRef);
		            if (error == null) {
		                throw new IllegalArgumentException("Could not find error " + errorRef);
		            }
		            String type = error.getErrorCode();
                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType("Error-" + attachedTo + "-" + type);
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                    eventNode.setMetaData("ErrorEvent", type);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void handleTimerNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser, final String attachedTo,
            final boolean cancelActivity) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setMetaData("CancelActivity", cancelActivity);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("timerEventDefinition".equals(nodeName)) {
                String timeDuration = null;
                String timeCycle = null;
                String timeDate = null;
                org.w3c.dom.Node subNode = xmlNode.getFirstChild();
                while (subNode instanceof Element) {
                    String subNodeName = subNode.getNodeName();
                    if ("timeDuration".equals(subNodeName)) {
                        timeDuration = subNode.getTextContent();
                        break;
                    } else if ("timeCycle".equals(subNodeName)) {
                        timeCycle = subNode.getTextContent();
                        break;
                    } else if ("timeDate".equals(subNodeName)) {
                        timeDate = subNode.getTextContent();
                        break;
                    }
                    subNode = subNode.getNextSibling();
                }
                if (timeDuration != null && timeDuration.trim().length() > 0) {
                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType("Timer-" + attachedTo + "-" + timeDuration);
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                    eventNode.setMetaData("TimeDuration", timeDuration);
                } else if (timeCycle != null && timeCycle.trim().length() > 0) {
                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType("Timer-" + attachedTo + "-" + timeCycle);
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                    eventNode.setMetaData("TimeCycle", timeCycle);
                } else if (timeDate != null && timeDate.trim().length() > 0) {
                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType("Timer-" + attachedTo + "-" + timeDate);
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                    eventNode.setMetaData("TimeDate", timeDate);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void handleCompensationNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser, final String attachedTo,
            final boolean cancelActivity) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        NodeList childs = element.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            if (childs.item(i) instanceof Element) {
                Element el = (Element) childs.item(i);
                if ("compensateEventDefinition".equalsIgnoreCase(el.getNodeName())) {
                    String activityRef = el.getAttribute("activityRef");
                    if (activityRef != null && activityRef.length() > 0) {
                        eventNode.setMetaData("ActivityRef", activityRef);
                    }
                }
            }
        }
        eventNode.setMetaData("AttachedTo", attachedTo);
        List<EventFilter> eventFilters = new ArrayList<EventFilter>();
        EventTypeFilter eventFilter = new EventTypeFilter();
        String eventType = "Compensate-";
        eventFilter.setType(eventType);
        eventFilters.add(eventFilter);
        ((EventNode) node).setEventFilters(eventFilters);
    }
    
    protected void handleSignalNode(final Node node, final Element element,
            final String uri, final String localName,
            final ExtensibleXmlParser parser, final String attachedTo,
            final boolean cancelActivity) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setMetaData("CancelActivity", cancelActivity);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, eventNode);
            } else if ("signalEventDefinition".equals(nodeName)) {
                String type = ((Element) xmlNode).getAttribute("signalRef");
                if (type != null && type.trim().length() > 0) {
                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType(type);
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                    eventNode.setScope("external");
                    eventNode.setMetaData("SignalName", type);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void handleConditionNode(final Node node, final Element element,
            final String uri, final String localName,
            final ExtensibleXmlParser parser, final String attachedTo,
            final boolean cancelActivity) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setMetaData("CancelActivity", cancelActivity);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("conditionalEventDefinition".equals(nodeName)) {
                org.w3c.dom.Node subNode = xmlNode.getFirstChild();
                while (subNode != null) {
                    String subnodeName = subNode.getNodeName();
                    if ("condition".equals(subnodeName)) {
                        eventNode.setMetaData("Condition", xmlNode.getTextContent());
                        List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                        EventTypeFilter eventFilter = new EventTypeFilter();
                        eventFilter.setType("Condition-" + attachedTo);
                        eventFilters.add(eventFilter);
                        eventNode.setScope("external");
                        eventNode.setEventFilters(eventFilters);
                        break;
                    }
                    subNode = subNode.getNextSibling();
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void handleMessageNode(final Node node, final Element element,
            final String uri, final String localName,
            final ExtensibleXmlParser parser, final String attachedTo,
            final boolean cancelActivity) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setMetaData("CancelActivity", cancelActivity);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, eventNode);
            } else if ("messageEventDefinition".equals(nodeName)) {
                String messageRef = ((Element) xmlNode).getAttribute("messageRef");
                Map<String, Message> messages = (Map<String, Message>) ((ProcessBuildData) parser
                        .getData()).getMetaData("Messages");
                if (messages == null) {
                    throw new IllegalArgumentException("No messages found");
                }
                Message message = messages.get(messageRef);
                if (message == null) {
                    throw new IllegalArgumentException("Could not find message " + messageRef);
                }
                eventNode.setMetaData("MessageType", message.getType());
                List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                EventTypeFilter eventFilter = new EventTypeFilter();
                eventFilter.setType("Message-" + messageRef);
                eventFilters.add(eventFilter);
                eventNode.setScope("external");
                eventNode.setEventFilters(eventFilters);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode,  EventNode eventNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        // targetRef
        subNode = subNode.getNextSibling();
        String to = subNode.getTextContent();
        eventNode.setVariableName(to);
    }
	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
	    throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}
