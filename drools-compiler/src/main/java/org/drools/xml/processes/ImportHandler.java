package org.drools.xml.processes;

import java.util.ArrayList;
import java.util.HashSet;

import org.drools.process.core.Process;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ImportHandler extends BaseAbstractHandler
    implements
    Handler {
    public ImportHandler() {
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
        parser.startConfiguration( localName,
                                                  attrs );
        
        WorkflowProcessImpl  process = ( WorkflowProcessImpl ) parser.getParent();        
        
        final String name = attrs.getValue( "name" );        
        emptyAttributeCheck( localName, "name", name, parser );       
        
        java.util.List<String> list =process.getImports();
        if ( list == null ) {
            list = new ArrayList<String>();
            process.setImports( list );
        }
        list.add( name );
        
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Configuration config = parser.endConfiguration();
        return null;
    }

    public Class generateNodeFor() {
        return null;
    }    

}
