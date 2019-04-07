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

import org.drools.compiler.compiler.xml.XmlDumper;
import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.bpmn2.core.Escalation;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.bpmn2.core.Signal;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTransformerImpl;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.event.NonAcceptingEventTypeFilter;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.api.runtime.process.DataTransformer;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BoundaryEventHandler extends AbstractNodeHandler {

	private DataTransformerRegistry transformerRegistry = DataTransformerRegistry.get();

    protected Node createNode(Attributes attrs) {
        return new BoundaryEventNode();
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return BoundaryEventNode.class;
    }

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Node node = (Node) parser.getCurrent();
        String attachedTo = element.getAttribute("attachedToRef");
        Attr cancelActivityAttr = element.getAttributeNode("cancelActivity");
        boolean cancelActivity = Boolean.parseBoolean(cancelActivityAttr.getValue());

        // determine type of event definition, so the correct type of node can be generated
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
        ((ProcessBuildData) parser.getData()).addNode(node);
        return node;
    }

    @SuppressWarnings("unchecked")
	protected void handleEscalationNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser, final String attachedTo,
            final boolean cancelActivity) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        BoundaryEventNode eventNode = (BoundaryEventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        /**
         * TODO: because of how we process bpmn2/xml files, we can't tell
         *       if the cancelActivity attribute is set to false or not
         *       (because we override with the xsd settings)
         * BPMN2 spec, p. 255, Escalation row:
         * "In contrast to an Error, an Escalation by default is assumed to not abort
         * the Activity to which the boundary Event is attached."
         */
        eventNode.setMetaData("CancelActivity", cancelActivity);
        eventNode.setAttachedToNodeId(attachedTo);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataOutput".equals(nodeName)) {
                String id = ((Element) xmlNode).getAttribute("id");
                String outputName = ((Element) xmlNode).getAttribute("name");
                dataOutputs.put(id, outputName);
            } else if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, eventNode);
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
                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    String type = escalation.getEscalationCode();
                    eventFilter.setType("Escalation-" + attachedTo + "-" + type);
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                    eventNode.setMetaData("EscalationEvent", type);
                } else {
                    throw new UnsupportedOperationException("General escalation is not yet supported.");
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
        BoundaryEventNode eventNode = (BoundaryEventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setAttachedToNodeId(attachedTo);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataOutput".equals(nodeName)) {
                String id = ((Element) xmlNode).getAttribute("id");
                String outputName = ((Element) xmlNode).getAttribute("name");
                dataOutputs.put(id, outputName);
            } else if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, eventNode);
            } else if ("errorEventDefinition".equals(nodeName)) {
                String errorRef = ((Element) xmlNode).getAttribute("errorRef");
                if (errorRef != null && errorRef.trim().length() > 0) {
                	List<Error> errors = (List<Error>) ((ProcessBuildData) parser.getData()).getMetaData("Errors");
		            if (errors == null) {
		                throw new IllegalArgumentException("No errors found");
		            }
		            Error error = null;
		            for( Error listError : errors ) {
		                if( errorRef.equals(listError.getId()) ) {
		                    error = listError;
		                }
		            }
		            if (error == null) {
		                throw new IllegalArgumentException("Could not find error " + errorRef);
		            }
		            String type = error.getErrorCode();
		            boolean hasErrorCode = true;
		            if (type == null) {
		            	type = error.getId();
		            	hasErrorCode = false;
		            }
		            String structureRef = error.getStructureRef();
		            if (structureRef != null) {
		            	Map<String, ItemDefinition> itemDefs = (Map<String, ItemDefinition>)((ProcessBuildData)
		            			parser.getData()).getMetaData("ItemDefinitions");

		            	if (itemDefs.containsKey(structureRef)) {
		            		structureRef = itemDefs.get(structureRef).getStructureRef();
		            	}
		            }

                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType("Error-" + attachedTo + "-" + type);
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                    eventNode.setMetaData("ErrorEvent", type);
                    eventNode.setMetaData("HasErrorEvent", hasErrorCode);
                    eventNode.setMetaData("ErrorStructureRef", structureRef);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }

    protected void handleTimerNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser, final String attachedTo,
            final boolean cancelActivity) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        BoundaryEventNode eventNode = (BoundaryEventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setMetaData("CancelActivity", cancelActivity);
        eventNode.setAttachedToNodeId(attachedTo);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("timerEventDefinition".equals(nodeName)) {
                String timeDuration = null;
                String timeCycle = null;
                String timeDate = null;
                String language = "";
                org.w3c.dom.Node subNode = xmlNode.getFirstChild();
                while (subNode instanceof Element) {
                    String subNodeName = subNode.getNodeName();
                    if ("timeDuration".equals(subNodeName)) {
                        timeDuration = subNode.getTextContent();
                        break;
                    } else if ("timeCycle".equals(subNodeName)) {
                        timeCycle = subNode.getTextContent();
                        language = ((Element) subNode).getAttribute("language");
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
                    eventFilter.setType("Timer-" + attachedTo + "-" + timeDuration + "-" + eventNode.getId());
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                    eventNode.setMetaData("TimeDuration", timeDuration);
                } else if (timeCycle != null && timeCycle.trim().length() > 0) {
                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType("Timer-" + attachedTo + "-" + timeCycle + "-" + eventNode.getId());
                    eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
                    eventNode.setMetaData("TimeCycle", timeCycle);
                    eventNode.setMetaData("Language", language);
                } else if (timeDate != null && timeDate.trim().length() > 0) {
                    List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType("Timer-" + attachedTo + "-" + timeDate + "-" + eventNode.getId());
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
        BoundaryEventNode eventNode = (BoundaryEventNode) parser.getCurrent();

        super.handleNode(node, element, uri, localName, parser);
        NodeList childs = element.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            if (childs.item(i) instanceof Element) {
                Element el = (Element) childs.item(i);
                if ("compensateEventDefinition".equalsIgnoreCase(el.getNodeName())) {
                    String activityRef = el.getAttribute("activityRef");
                    if( activityRef != null && activityRef.length() > 0 ) {
                        logger.warn("activityRef value [" + activityRef + "] on Boundary Event '" + eventNode.getMetaData("UniqueId")
                                + "' ignored per the BPMN2 specification.");
                    }
                }
            }
        }
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setAttachedToNodeId(attachedTo);

        // 1. Find the parent (sub-)process
        NodeContainer parentContainer = (NodeContainer) parser.getParent();

        // 2. Add the event filter (never fires, purely for dumping purposes)
        EventTypeFilter eventFilter = new NonAcceptingEventTypeFilter();
        eventFilter.setType("Compensation");
        List<EventFilter> eventFilters = new ArrayList<EventFilter>();
        eventNode.setEventFilters(eventFilters);
        eventFilters.add(eventFilter);

        // 3. Add compensation scope (with key/id: attachedTo)
        ProcessHandler.addCompensationScope((RuleFlowProcess) parser.getParent(RuleFlowProcess.class), eventNode, parentContainer, attachedTo);
    }

    protected void handleSignalNode(final Node node, final Element element,
            final String uri, final String localName,
            final ExtensibleXmlParser parser, final String attachedTo,
            final boolean cancelActivity) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        BoundaryEventNode eventNode = (BoundaryEventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setMetaData("CancelActivity", cancelActivity);
        eventNode.setAttachedToNodeId(attachedTo);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataOutput".equals(nodeName)) {
                String id = ((Element) xmlNode).getAttribute("id");
                String outputName = ((Element) xmlNode).getAttribute("name");
                dataOutputs.put(id, outputName);
            } if ("dataOutputAssociation".equals(nodeName)) {
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
        BoundaryEventNode eventNode = (BoundaryEventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setMetaData("CancelActivity", cancelActivity);
        eventNode.setAttachedToNodeId(attachedTo);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataOutput".equals(nodeName)) {
                String id = ((Element) xmlNode).getAttribute("id");
                String outputName = ((Element) xmlNode).getAttribute("name");
                dataOutputs.put(id, outputName);
            } else if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, eventNode);
            } else if ("conditionalEventDefinition".equals(nodeName)) {
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
        BoundaryEventNode eventNode = (BoundaryEventNode) node;
        eventNode.setMetaData("AttachedTo", attachedTo);
        eventNode.setMetaData("CancelActivity", cancelActivity);
        eventNode.setAttachedToNodeId(attachedTo);
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
                eventFilter.setType("Message-" + message.getName());
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
        String from = subNode.getTextContent();
        // targetRef
        subNode = subNode.getNextSibling();
        String to = subNode.getTextContent();
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

        eventNode.setVariableName(to);

    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
        EventNode eventNode = (EventNode) node;
        String attachedTo = (String) eventNode.getMetaData("AttachedTo");
        if (attachedTo != null) {
            String type = ((EventTypeFilter) eventNode.getEventFilters().get(0)).getType();
            if (type.startsWith("Escalation-")) {
                type = type.substring(attachedTo.length() + 12);
                boolean cancelActivity = (Boolean) eventNode.getMetaData("CancelActivity");
                writeNode("boundaryEvent", eventNode, xmlDump, metaDataType);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                if (!cancelActivity) {
                    xmlDump.append("cancelActivity=\"false\" ");
                }
                xmlDump.append(">" + EOL);
                writeExtensionElements(node, xmlDump);
                xmlDump.append("      <escalationEventDefinition escalationRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\" />" + EOL);
                endNode("boundaryEvent", xmlDump);
            } else if (type.startsWith("Error-")) {
                type = type.substring(attachedTo.length() + 7);
                writeNode("boundaryEvent", eventNode, xmlDump, metaDataType);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                xmlDump.append(">" + EOL);
                writeExtensionElements(node, xmlDump);
                writeVariableName(eventNode, xmlDump);
                String errorId = getErrorIdForErrorCode(type, eventNode);
                xmlDump.append("      <errorEventDefinition errorRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(errorId) + "\" " );
                xmlDump.append("/>" + EOL);
                endNode("boundaryEvent", xmlDump);
            } else if (type.startsWith("Timer-")) {
                type = type.substring(attachedTo.length() + 7);
                boolean cancelActivity = (Boolean) eventNode.getMetaData("CancelActivity");
                writeNode("boundaryEvent", eventNode, xmlDump, metaDataType);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                if (!cancelActivity) {
                    xmlDump.append("cancelActivity=\"false\" ");
                }
                xmlDump.append(">" + EOL);
                writeExtensionElements(node, xmlDump);
                String duration = (String) eventNode.getMetaData("TimeDuration");
                String cycle = (String) eventNode.getMetaData("TimeCycle");
                String date = (String) eventNode.getMetaData("TimeDate");


                if (duration != null && cycle != null) {
                	String lang = (String) eventNode.getMetaData("Language");
                	String language = "";
                	if (lang != null && !lang.isEmpty()) {
                		language = "language=\""+lang + "\" ";
                	}
                	xmlDump.append(
                            "      <timerEventDefinition>" + EOL +
                            "        <timeDuration xsi:type=\"tFormalExpression\">" + XmlDumper.replaceIllegalChars(duration) + "</timeDuration>" + EOL +
                            "        <timeCycle xsi:type=\"tFormalExpression\" " +language +">" + XmlDumper.replaceIllegalChars(cycle) + "</timeCycle>" + EOL +
                            "      </timerEventDefinition>" + EOL);
                } else if (duration != null) {
                    xmlDump.append(
                            "      <timerEventDefinition>" + EOL +
                            "        <timeDuration xsi:type=\"tFormalExpression\">" + XmlDumper.replaceIllegalChars(duration) + "</timeDuration>" + EOL +
                            "      </timerEventDefinition>" + EOL);
                } else if (date != null) {
                    xmlDump.append(
                            "      <timerEventDefinition>" + EOL +
                            "        <timeDate xsi:type=\"tFormalExpression\">" + XmlDumper.replaceIllegalChars(date) + "</timeDate>" + EOL +
                            "      </timerEventDefinition>" + EOL);
                } else {
                	String lang = (String) eventNode.getMetaData("Language");
                	String language = "";
                	if (lang != null && !lang.isEmpty()) {
                		language = "language=\""+lang + "\" ";
                	}
                    xmlDump.append(
                            "      <timerEventDefinition>" + EOL +
                            "        <timeCycle xsi:type=\"tFormalExpression\" " +language +">" + XmlDumper.replaceIllegalChars(cycle) + "</timeCycle>" + EOL +
                            "      </timerEventDefinition>" + EOL);
                }
                endNode("boundaryEvent", xmlDump);
            } else if (type.equals("Compensation")) {
                writeNode("boundaryEvent", eventNode, xmlDump, metaDataType);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                xmlDump.append(">" + EOL);
                writeExtensionElements(node, xmlDump);
                xmlDump.append("      <compensateEventDefinition/>" + EOL);
                endNode("boundaryEvent", xmlDump);
            }  else if (node.getMetaData().get("SignalName") != null) {
                boolean cancelActivity = (Boolean) eventNode.getMetaData("CancelActivity");
                writeNode("boundaryEvent", eventNode, xmlDump, metaDataType);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                if (!cancelActivity) {
                    xmlDump.append("cancelActivity=\"false\" ");
                }
                xmlDump.append(">" + EOL);
                writeExtensionElements(node, xmlDump);
                xmlDump.append("      <signalEventDefinition signalRef=\"" + type + "\"/>"+ EOL);
                endNode("boundaryEvent", xmlDump);
            } else if (node.getMetaData().get("Condition") != null) {

                boolean cancelActivity = (Boolean) eventNode.getMetaData("CancelActivity");
                writeNode("boundaryEvent", eventNode, xmlDump, metaDataType);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                if (!cancelActivity) {
                    xmlDump.append("cancelActivity=\"false\" ");
                }
                xmlDump.append(">" + EOL);
                writeExtensionElements(node, xmlDump);
                xmlDump.append("      <conditionalEventDefinition>"+ EOL);
                xmlDump.append("        <condition xsi:type=\"tFormalExpression\" language=\"http://www.jboss.org/drools/rule\">" + eventNode.getMetaData("Condition") +"</condition>"+ EOL);
                xmlDump.append("      </conditionalEventDefinition>"+ EOL);
                endNode("boundaryEvent", xmlDump);
            } else if (type.startsWith("Message-")) {
                type = type.substring(8);
                writeNode("boundaryEvent", eventNode, xmlDump, metaDataType);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                xmlDump.append(">" + EOL);
                writeExtensionElements(node, xmlDump);
                xmlDump.append("      <messageEventDefinition messageRef=\"" + type + "\"/>" + EOL);
                endNode("boundaryEvent", xmlDump);
            }
        }
    }
}
