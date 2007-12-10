package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.ruleflow.common.core.impl.ProcessImpl;
import org.drools.ruleflow.common.core.Process;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.ruleflow.core.ActionNode;
import org.drools.ruleflow.core.EndNode;
import org.drools.ruleflow.core.StartNode;
import org.drools.ruleflow.core.impl.EndNodeImpl;
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

public class EndNodeHandler extends BaseAbstractHandler
    implements
    Handler {
    public EndNodeHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Process.class );

            this.validPeers = new HashSet();

            this.validPeers.add( ActionNode.class );
            this.validPeers.add( StartNode.class );            

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
        
        final EndNode endNode = new EndNodeImpl();
        
        final String name = attrs.getValue( "name" );        
        emptyAttributeCheck( localName, "name", name, xmlPackageReader );
        endNode.setName( name );
        
        process.addNode( endNode );        
        ((ProcessBuildData)xmlPackageReader.getData()).addNode( endNode );
        
        return endNode;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        final Configuration config = xmlPackageReader.endConfiguration();
        return xmlPackageReader.getCurrent();
    }

    public Class generateNodeFor() {
        return EndNode.class;
    }    

}
