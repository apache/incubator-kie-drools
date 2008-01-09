package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessHandler extends BaseAbstractHandler
    implements
    Handler {
    public ProcessHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( null );

            this.validPeers = new HashSet();
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }
    

    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startConfiguration( localName,
                                                  attrs );
        
        final String id = attrs.getValue( "id" );
        final String name = attrs.getValue( "name" );
        final String version = attrs.getValue( "version" );
        final String type = attrs.getValue( "type" );
        final String packageName = attrs.getValue( "package-name" );
        
        emptyAttributeCheck( localName, "id", name, parser );
        emptyAttributeCheck( localName, "name", name, parser );
        //emptyAttributeCheck( localName, "version", version, parser );
        emptyAttributeCheck( localName, "package-name", packageName, parser );
        

        RuleFlowProcess process = new RuleFlowProcess();
        process.setId( id );
        process.setName( name );
        process.setVersion( version );
        process.setType( type );
        process.setPackageName( packageName );

        ((ProcessBuildData)parser.getData()).setProcess( process );
        
        return process;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Configuration config = parser.endConfiguration();        
        return parser.getCurrent();
    }

    public Class generateNodeFor() {
        return org.drools.process.core.Process.class;
    }    

}
