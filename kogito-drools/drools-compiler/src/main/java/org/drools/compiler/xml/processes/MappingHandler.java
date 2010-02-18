package org.drools.compiler.xml.processes;

import java.util.HashSet;

import org.drools.process.core.context.variable.Mappable;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MappingHandler extends BaseAbstractHandler
    implements
    Handler {
    public MappingHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Mappable.class );

            this.validPeers = new HashSet();         
            this.validPeers.add( null );            

            this.allowNesting = false;
        }
    }
    

    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        Mappable mappable = (Mappable) parser.getParent();
        final String type = attrs.getValue("type");
        emptyAttributeCheck(localName, "type", type, parser);
        final String fromName = attrs.getValue("from");
        emptyAttributeCheck(localName, "from", fromName, parser);
        final String toName = attrs.getValue("to");
        emptyAttributeCheck(localName, "to", toName, parser);
        if ("in".equals(type)) {
            mappable.addInMapping(toName, fromName);
        } else if ("out".equals(type)) {
            mappable.addOutMapping(fromName, toName);
        } else {
            throw new SAXParseException(
                "Unknown mapping type " + type, parser.getLocator());
        }
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return null;
    }    

}
