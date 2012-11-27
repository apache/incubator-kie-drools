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
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ConstraintTrigger;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.EventTrigger;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.Trigger;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StartEventHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new StartNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return StartNode.class;
    }

    @SuppressWarnings("unchecked")
    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        StartNode startNode = (StartNode) node;
        startNode.setInterupting(Boolean.parseBoolean(element.getAttribute("isInterrupting")));
        
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataOutputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, startNode);
            } else if ("conditionalEventDefinition".equals(nodeName)) {
                String constraint = null;
                org.w3c.dom.Node subNode = xmlNode.getFirstChild();
                while (subNode != null) {
                    String subnodeName = subNode.getNodeName();
                    if ("condition".equals(subnodeName)) {
                        constraint = xmlNode.getTextContent();
                        break;
                    }
                    subNode = subNode.getNextSibling();
                }
                ConstraintTrigger trigger = new ConstraintTrigger();
                trigger.setConstraint(constraint);
                startNode.addTrigger(trigger);
                break;
            } else if ("signalEventDefinition".equals(nodeName)) {
                String type = ((Element) xmlNode).getAttribute("signalRef");
                if (type != null && type.trim().length() > 0) {
                    EventTrigger trigger = new EventTrigger();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType(type);
                    trigger.addEventFilter(eventFilter);
                    String mapping = (String) startNode.getMetaData("TriggerMapping");
                    if (mapping != null) {
                        trigger.addInMapping(mapping, "event");
                    }
                    startNode.addTrigger(trigger);
                }
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
                startNode.setMetaData("MessageType", message.getType());
                EventTrigger trigger = new EventTrigger();
                EventTypeFilter eventFilter = new EventTypeFilter();
                eventFilter.setType("Message-" + messageRef);
                trigger.addEventFilter(eventFilter);
                String mapping = (String) startNode.getMetaData("TriggerMapping");
                if (mapping != null) {
                    trigger.addInMapping(mapping, "event");
                }
                startNode.addTrigger(trigger);
            } else if ("timerEventDefinition".equals(nodeName)) {
            	org.w3c.dom.Node subNode = xmlNode.getFirstChild();
                while (subNode instanceof Element) {
                    String subNodeName = subNode.getNodeName();
                    Timer timer = null;   
                    
                    if (parser.getParent() instanceof EventSubProcessNode) {
                        // handle timer on start events like normal (non rule) timers for event sub process
                        timer = new Timer();
                        
                        EventTrigger trigger = new EventTrigger();
                        EventTypeFilter eventFilter = new EventTypeFilter();
                        eventFilter.setType("Timer-" + ((EventSubProcessNode) parser.getParent()).getId());
                        trigger.addEventFilter(eventFilter);
                        String mapping = (String) startNode.getMetaData("TriggerMapping");
                        if (mapping != null) {
                            trigger.addInMapping(mapping, "event");
                        }
                        startNode.addTrigger(trigger);
                    }
                    if ("timeCycle".equals(subNodeName)) {
                        String period = "";
                        String repeatLimit = "-1";
                        String delay = subNode.getTextContent();
                        
                        String language = ((Element) subNode).getAttribute("language");
                        if (language == null || language.trim().length() == 0) {
                        	language = "int";
                        }
                        if ("int".equalsIgnoreCase(language)) {
                            long[] repeatTimer = DateTimeUtils.parseRepeatableDateTime(delay);
                            if (repeatTimer.length == 1) {
                                startNode.setMetaData("TimerDef", delay);
                                delay = Long.toString(repeatTimer[0]);
                                period = Long.toString(repeatTimer[0]);
                            } else {
                                startNode.setMetaData("TimerDef", delay);
                                repeatLimit = Long.toString(repeatTimer[0]+1);
                                delay = Long.toString(repeatTimer[1]);
                                period = Long.toString(repeatTimer[2]);
                                
                            }
                        }
                        if (delay != null && delay.trim().length() > 0) {
                            startNode.setMetaData("TimerType", Timer.TIME_CYCLE);
                            if (timer != null) {
                                timer.setDelay(delay);
                                timer.setPeriod(period);
                                timer.setTimeType(Integer.valueOf(Timer.TIME_CYCLE));
                                ((EventSubProcessNode) parser.getParent()).addTimer(timer, new DroolsConsequenceAction("java", ""));
                            } else {
    	                        ConstraintTrigger trigger = new ConstraintTrigger();
    	                        trigger.setConstraint("");
    	                        if ("int".equals(language)) {
    	                            if (repeatLimit.equals("-1")) {
    	                                trigger.setHeader("timer (int:" + delay + " " + period + ")");
    	                            } else {
    	                                trigger.setHeader("timer (int:" + delay + " " + period + " repeat-limit=" + repeatLimit + ")");
    	                            }
    	                        } else {
    	                        	trigger.setHeader("timer (" + language + ":" + delay + ")");
    	                        }
    	                        startNode.addTrigger(trigger);
                            }
	                        break;
                        }
                    } else if ("timeDuration".equals(subNodeName)) {
                        String period = Long.toString(DateTimeUtils.parseDuration(subNode.getTextContent()));
                        
                        if (period != null && period.trim().length() > 0) {
                            startNode.setMetaData("TimerDef", period);
                            startNode.setMetaData("TimerType", Timer.TIME_DURATION);
                            if (timer != null) {
                                timer.setDelay(period);
                                timer.setTimeType(Integer.valueOf(Timer.TIME_DURATION));
                                ((EventSubProcessNode) parser.getParent()).addTimer(timer, new DroolsConsequenceAction("java", ""));
                            } else {
                                ConstraintTrigger trigger = new ConstraintTrigger();
                                trigger.setConstraint("");
                                trigger.setHeader("timer (int:" + period + " 0)");
                                
                                startNode.addTrigger(trigger);
                            }
                            break;
                        }
                    } else if ("timeDate".equals(subNodeName)) {
                        String period = Long.toString(DateTimeUtils.parseDateAsDuration(subNode.getTextContent()));
                 
                        if (period != null && period.trim().length() > 0) {
                            startNode.setMetaData("TimerDef", period);
                            startNode.setMetaData("TimerType", Timer.TIME_DATE);
                            if (timer != null) {
                                timer.setDate(period);
                                timer.setTimeType(Integer.valueOf(Timer.TIME_DATE));
                                ((EventSubProcessNode) parser.getParent()).addTimer(timer, new DroolsConsequenceAction("java", ""));
                            } else {
                                ConstraintTrigger trigger = new ConstraintTrigger();
                                trigger.setConstraint("");
                                trigger.setHeader("timer (int:" + period + " 0)");
                                
                                startNode.addTrigger(trigger);
                            }
                            break;
                        }
                    }
                    subNode = subNode.getNextSibling();
                    
               }
                // following event definitions are only for event sub process and will be validated to not be included in top process definitions
            } else if ("errorEventDefinition".equals(nodeName)) {
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
                    EventTrigger trigger = new EventTrigger();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType("Error-" + error.getErrorCode());
                    trigger.addEventFilter(eventFilter);
                    startNode.addTrigger(trigger);
                }
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
                
                    EventTrigger trigger = new EventTrigger();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType("Escalation-" + escalation.getEscalationCode());
                    trigger.addEventFilter(eventFilter);
                    startNode.addTrigger(trigger);
                }
            } if ("compensateEventDefinition".equals(nodeName)) {
                String activityRef = ((Element) xmlNode).getAttribute("activityRef");
                if (activityRef != null && activityRef.trim().length() > 0) {                    
                    EventTrigger trigger = new EventTrigger();
                    EventTypeFilter eventFilter = new EventTypeFilter();
                    eventFilter.setType("Compensate-" + activityRef);
                    trigger.addEventFilter(eventFilter);
                    startNode.addTrigger(trigger);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode, StartNode startNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        if ("sourceRef".equals(subNode.getNodeName())) {
            subNode = subNode.getNextSibling();
        }
        // targetRef
        String to = subNode.getTextContent();
        startNode.setMetaData("TriggerMapping", to);
    }

    public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		StartNode startNode = (StartNode) node;
		writeNode("startEvent", startNode, xmlDump, metaDataType);
		if (startNode.isInterupting()) {
		    xmlDump.append(" isInterrupting=\"true\" ");
		}
		List<Trigger> triggers = startNode.getTriggers();
		if (triggers != null) {
		    xmlDump.append(">" + EOL);
		    if (triggers.size() > 1) {
		        throw new IllegalArgumentException("Multiple start triggers not supported");
		    }
		    Trigger trigger = triggers.get(0);
		    if (trigger instanceof ConstraintTrigger) {
		    	ConstraintTrigger constraintTrigger = (ConstraintTrigger) trigger;
		    	if (constraintTrigger.getHeader() == null) {
			        xmlDump.append("      <conditionalEventDefinition>" + EOL);
	                xmlDump.append("        <condition xsi:type=\"tFormalExpression\" language=\"" + XmlBPMNProcessDumper.RULE_LANGUAGE + "\">" + constraintTrigger.getConstraint() + "</condition>" + EOL);
	                xmlDump.append("      </conditionalEventDefinition>" + EOL);
		    	} else {
		    	    Integer timerType = (Integer) startNode.getMetaData("TimerType");
		    	    if (timerType == Timer.TIME_DURATION) {
		    	        xmlDump.append("      <timerEventDefinition>" + EOL);
	                    xmlDump.append("        <timeDuration xsi:type=\"tFormalExpression\" >" + startNode.getMetaData("TimerDef") + "</timeDuration>" + EOL);
	                    xmlDump.append("      </timerEventDefinition>" + EOL);
		    	    } else if (timerType == Timer.TIME_DATE) {
		    	        xmlDump.append("      <timerEventDefinition>" + EOL);
	                    xmlDump.append("        <timeDate xsi:type=\"tFormalExpression\" >" + startNode.getMetaData("TimerDef") + "</timeDate>" + EOL);
	                    xmlDump.append("      </timerEventDefinition>" + EOL);    
                    } else {
    		    		String header = constraintTrigger.getHeader();
    		    		header = header.substring(7, header.length() - 1);
    		    		int index = header.indexOf(":");
    		    		String language = header.substring(0, index);
    		    		header = header.substring(index + 1);
    		    		String cycle = null;
    		    		if (startNode.getMetaData("TimerDef") != null){
    		    		    cycle = (String) startNode.getMetaData("TimerDef");
    		    		}else if ("int".equals(language)) {
    		    			int lenght = (header.length() - 1)/2;
    			    		cycle = header.substring(0, lenght);
    		    		} else {
    		    			cycle = header;
    		    		}
    			        xmlDump.append("      <timerEventDefinition>" + EOL);
    	                xmlDump.append("        <timeCycle xsi:type=\"tFormalExpression\" language=\"" + language + "\">" + cycle + "</timeCycle>" + EOL);
    	                xmlDump.append("      </timerEventDefinition>" + EOL);
                    }
		    	}
		    } else if (trigger instanceof EventTrigger) {
		        EventTrigger eventTrigger = (EventTrigger) trigger;
		        if (!trigger.getInMappings().isEmpty()) {
		            String mapping = eventTrigger.getInMappings().keySet().iterator().next();
		            xmlDump.append(
	                    "      <dataOutput id=\"_" + startNode.getId() + "_Output\" />" + EOL +
                        "      <dataOutputAssociation>" + EOL +
                        "        <sourceRef>_" + startNode.getId() + "_Output</sourceRef>" + EOL +
                        "        <targetRef>" + mapping + "</targetRef>" + EOL +
                        "      </dataOutputAssociation>" + EOL +
                        "      <outputSet>" + EOL +
                        "        <dataOutputRefs>_" + startNode.getId() + "_Output</dataOutputRefs>" + EOL +
                        "      </outputSet>" + EOL);
		        }
		        String type = ((EventTypeFilter) eventTrigger.getEventFilters().get(0)).getType();
		        if (type.startsWith("Message-")) {
                    type = type.substring(8);
                    xmlDump.append("      <messageEventDefinition messageRef=\"" + type + "\"/>" + EOL);
                } else if (type.startsWith("Error-")) {
                    type = type.substring(6);
                    xmlDump.append("      <errorEventDefinition errorRef=\"" + type + "\"/>" + EOL);
                } else if (type.startsWith("Escalation-")) {
                    type = type.substring(11);
                    xmlDump.append("      <escalationEventDefinition escalationRef=\"" + type + "\"/>" + EOL);
                } if (type.startsWith("Compensate-")) {
                    type = type.substring(11);
                    xmlDump.append("      <compensateEventDefinition activityRef=\"" + type + "\"/>" + EOL);
                } else {
                    xmlDump.append("      <signalEventDefinition signalRef=\"" + type + "\" />" + EOL);
                }
            } else {
		        throw new IllegalArgumentException("Unsupported trigger type " + trigger);
		    }
		    endNode("startEvent", xmlDump);
		} else {
		    endNode(xmlDump);
		}
	}

}
