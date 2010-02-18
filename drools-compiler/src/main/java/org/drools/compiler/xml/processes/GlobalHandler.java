package org.drools.compiler.xml.processes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.definition.process.Process;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GlobalHandler extends BaseAbstractHandler
    implements
    Handler {
    public GlobalHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Process.class );

            this.validPeers = new HashSet();         
            this.validPeers.add( null );            
            //this.validPeers.add( ImportDescr.class );            

            this.allowNesting = false;
        }
    }
    

    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        
        WorkflowProcessImpl  process = ( WorkflowProcessImpl ) parser.getParent();        
        
        final String identifier = attrs.getValue( "identifier" );
        final String type = attrs.getValue( "type" );
        
        emptyAttributeCheck( localName, "identifier", identifier, parser );
        emptyAttributeCheck( localName, "type", type, parser );
        
        Map<String, String> map = process.getGlobals();
        if ( map == null ) {
            map = new HashMap<String, String>();
            process.setGlobals( map );
        }
        map.put( identifier, type );
        
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return null;
    }    

}
