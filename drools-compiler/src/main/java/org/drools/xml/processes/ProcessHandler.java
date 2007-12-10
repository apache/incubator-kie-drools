package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.ruleflow.core.impl.RuleFlowProcessImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
                        final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        xmlPackageReader.startConfiguration( localName,
                                                  attrs );
        
        final String id = attrs.getValue( "id" );
        final String name = attrs.getValue( "name" );
        final String version = attrs.getValue( "version" );
        final String type = attrs.getValue( "type" );
        final String packageName = attrs.getValue( "package-name" );
        
        emptyAttributeCheck( localName, "id", name, xmlPackageReader );
        emptyAttributeCheck( localName, "name", name, xmlPackageReader );
        //emptyAttributeCheck( localName, "version", version, xmlPackageReader );
        emptyAttributeCheck( localName, "package-name", packageName, xmlPackageReader );
        

        RuleFlowProcessImpl process = new RuleFlowProcessImpl();
        process.setId( id );
        process.setName( name );
        process.setVersion( version );
        process.setType( type );
        process.setPackageName( packageName );

        ((ProcessBuildData)xmlPackageReader.getData()).setProcess( process );
        
        return process;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        final Configuration config = xmlPackageReader.endConfiguration();        
        return xmlPackageReader.getCurrent();
    }

    public Class generateNodeFor() {
        return org.drools.ruleflow.common.core.Process.class;
    }    

}
