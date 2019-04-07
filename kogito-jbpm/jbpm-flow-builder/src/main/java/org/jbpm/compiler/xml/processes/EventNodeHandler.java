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

package org.jbpm.compiler.xml.processes;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.EventNode;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class EventNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode() {
        return new EventNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EventNode.class;
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        EventNode eventNode = (EventNode) node;
        String variableName = element.getAttribute("variableName");
        if (variableName != null && variableName.length() != 0 ) {
            eventNode.setVariableName(variableName);
        }
        String scope = element.getAttribute("scope");
        if (scope != null && scope.length() != 0 ) {
            eventNode.setScope(scope);
        }
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		EventNode eventNode = (EventNode) node;
		writeNode("eventNode", eventNode, xmlDump, includeMeta);
		String variableName = eventNode.getVariableName();
        if (variableName != null && variableName.length() != 0) {
            xmlDump.append("variableName=\"" + variableName + "\" ");
        }
        String scope = eventNode.getScope();
        if (scope != null && scope.length() != 0) {
            xmlDump.append("scope=\"" + scope + "\" ");
        }
        xmlDump.append(">" + EOL);
        if (includeMeta) {
        	writeMetaData(eventNode, xmlDump);
        }
        xmlDump.append("      <eventFilters>" + EOL);
        for (EventFilter filter: eventNode.getEventFilters()) {
        	if (filter instanceof EventTypeFilter) {
        		xmlDump.append("        <eventFilter "
                    + "type=\"eventType\" "
                    + "eventType=\"" + ((EventTypeFilter) filter).getType() + "\" />" + EOL);
        	} else {
        		throw new IllegalArgumentException(
    				"Unknown filter type: " + filter);
        	}
        }
        xmlDump.append("      </eventFilters>" + EOL);
        endNode("eventNode", xmlDump);
	}

}
