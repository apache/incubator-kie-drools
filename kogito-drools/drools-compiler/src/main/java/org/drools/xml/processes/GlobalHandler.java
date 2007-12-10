package org.drools.xml.processes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.ruleflow.common.core.impl.ProcessImpl;
import org.drools.ruleflow.common.core.Process;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.ruleflow.core.ActionNode;
import org.drools.ruleflow.core.StartNode;
import org.drools.ruleflow.core.impl.ActionNodeImpl;
import org.drools.ruleflow.core.impl.DroolsConsequenceAction;
import org.drools.ruleflow.core.impl.RuleFlowProcessImpl;
import org.drools.ruleflow.core.impl.StartNodeImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
                        final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        xmlPackageReader.startConfiguration( localName,
                                                  attrs );
        
        RuleFlowProcessImpl  process = ( RuleFlowProcessImpl ) xmlPackageReader.getParent();        
        
        final String identifier = attrs.getValue( "identifier" );
        final String type = attrs.getValue( "type" );
        
        emptyAttributeCheck( localName, "identifier", identifier, xmlPackageReader );
        emptyAttributeCheck( localName, "type", type, xmlPackageReader );
        
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
                      final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        final Configuration config = xmlPackageReader.endConfiguration();
        return null;
    }

    public Class generateNodeFor() {
        return null;
    }    

}
