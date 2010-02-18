package org.drools.compiler.xml.processes;

import java.util.ArrayList;
import java.util.HashSet;

import org.drools.process.core.Process;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FunctionImportHandler extends BaseAbstractHandler
    implements
    Handler {
    public FunctionImportHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Process.class );

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
        
        WorkflowProcessImpl  process = ( WorkflowProcessImpl ) parser.getParent();        
        
        final String name = attrs.getValue( "name" );        
        emptyAttributeCheck( localName, "name", name, parser );       
        
        java.util.List<String> list = process.getFunctionImports();
        if ( list == null ) {
            list = new ArrayList<String>();
            process.setFunctionImports( list );
        }
        list.add( name );
        
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
