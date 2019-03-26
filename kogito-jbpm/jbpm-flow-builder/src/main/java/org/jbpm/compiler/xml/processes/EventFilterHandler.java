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

import java.util.HashSet;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.process.core.event.EventFilter;
import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventTrigger;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EventFilterHandler extends BaseAbstractHandler implements Handler {
    
    public EventFilterHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add(EventNode.class);
            this.validParents.add(EventTrigger.class);

            this.validPeers = new HashSet<Class<?>>();
            this.validPeers.add(null);

            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Object parent = parser.getParent();
        final String type = element.getAttribute("type");
        emptyAttributeCheck(localName, "type", type, parser);
        if ("eventType".equals(type)) {
            final String eventType = element.getAttribute("eventType");
            emptyAttributeCheck(localName, "eventType", eventType, parser);
            EventTypeFilter eventTypeFilter = new EventTypeFilter();
            eventTypeFilter.setType(eventType);
            if (parent instanceof EventNode) {
            	((EventNode) parent).addEventFilter(eventTypeFilter);
            } else if (parent instanceof EventTrigger) {
            	((EventTrigger) parent).addEventFilter(eventTypeFilter);
            }
        } else {
        	throw new IllegalArgumentException(
    			"Unknown event filter type: " + type);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EventFilter.class;
    }    

}
