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

import org.drools.compiler.xml.XmlDumper;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.EventNode;
import org.xml.sax.Attributes;

public class EventNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by intermediate catch event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EventNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, int metaDataType) {
		EventNode eventNode = (EventNode) node;
		String attachedTo = (String) eventNode.getMetaData("AttachedTo");
		if (attachedTo == null) {
    		writeNode("intermediateCatchEvent", eventNode, xmlDump, metaDataType);
    		xmlDump.append(">" + EOL);
    		if (eventNode.getVariableName() != null) {
    			xmlDump.append("      <dataOutput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(eventNode) + "_Output\" name=\"event\" />" + EOL);
    			xmlDump.append("      <dataOutputAssociation>" + EOL);
    			xmlDump.append(
    				"      <sourceRef>" + XmlBPMNProcessDumper.getUniqueNodeId(eventNode) + "_Output</sourceRef>" + EOL +
    				"      <targetRef>" + XmlDumper.replaceIllegalChars(eventNode.getVariableName()) + "</targetRef>" + EOL);
    			xmlDump.append("      </dataOutputAssociation>" + EOL);
    			xmlDump.append("      <outputSet>" + EOL);
    			xmlDump.append("        <dataOutputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(eventNode) + "_Output</dataOutputRefs>" + EOL);
    			xmlDump.append("      </outputSet>" + EOL);
    		}
    		if (eventNode.getEventFilters().size() > 0) {
    			String type = ((EventTypeFilter) eventNode.getEventFilters().get(0)).getType();
    			if (type.startsWith("Message-")) {
    			    type = type.substring(8);
    			    xmlDump.append("      <messageEventDefinition messageRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\"/>" + EOL);
                } else {
                    xmlDump.append("      <signalEventDefinition signalRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\"/>" + EOL);
                }
    		}
    		endNode("intermediateCatchEvent", xmlDump);
		} else {
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
    		    xmlDump.append("      <escalationEventDefinition escalationRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\" />" + EOL);
    		    endNode("boundaryEvent", xmlDump);
		    } else if (type.startsWith("Error-")) {
                type = type.substring(attachedTo.length() + 7);
                writeNode("boundaryEvent", eventNode, xmlDump, metaDataType);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                xmlDump.append(">" + EOL);
                xmlDump.append("      <errorEventDefinition errorRef=\"" + XmlBPMNProcessDumper.replaceIllegalCharsAttribute(type) + "\" />" + EOL);
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
                xmlDump.append(
                    "      <timerEventDefinition>" + EOL +
                    "        <timeCycle xsi:type=\"tFormalExpression\">" + XmlDumper.replaceIllegalChars((String) eventNode.getMetaData("TimeCycle")) + "</timeCycle>" + EOL +
                    "      </timerEventDefinition>" + EOL);
                endNode("boundaryEvent", xmlDump);
            } else if (type.startsWith("Compensate-")) {
                type = type.substring(attachedTo.length() + 7);
                writeNode("boundaryEvent", eventNode, xmlDump, metaDataType);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                xmlDump.append(">" + EOL);
                xmlDump.append("      <compensateEventDefinition/>" + EOL);
                endNode("boundaryEvent", xmlDump);
            } 
		}
	}

}
