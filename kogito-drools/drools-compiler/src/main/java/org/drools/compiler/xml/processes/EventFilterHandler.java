package org.drools.compiler.xml.processes;

import java.util.HashSet;

import org.drools.process.core.event.EventFilter;
import org.drools.process.core.event.EventTypeFilter;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.core.node.EventTrigger;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
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
