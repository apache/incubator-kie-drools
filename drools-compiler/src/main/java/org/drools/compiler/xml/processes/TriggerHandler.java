package org.drools.compiler.xml.processes;

import java.util.HashSet;

import org.drools.workflow.core.node.ConstraintTrigger;
import org.drools.workflow.core.node.EventTrigger;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.Trigger;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TriggerHandler extends BaseAbstractHandler implements Handler {
	
    public TriggerHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add( StartNode.class );

            this.validPeers = new HashSet<Class<?>>();         
            this.validPeers.add( null );
            this.validPeers.add( Trigger.class );

            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName, attrs );
        StartNode startNode = (StartNode) parser.getParent();
        String type = attrs.getValue("type");
        emptyAttributeCheck( localName, "type", type, parser );
        
        Trigger trigger = null;
        if ("constraint".equals(type)) {
        	trigger = new ConstraintTrigger();
        } else if ("event".equals(type)) {
        	trigger = new EventTrigger();
        } else {
        	throw new SAXException("Unknown trigger type " + type);
        }
        startNode.addTrigger(trigger);
        return trigger;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return parser.getCurrent();
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Trigger.class;
    }    

}
