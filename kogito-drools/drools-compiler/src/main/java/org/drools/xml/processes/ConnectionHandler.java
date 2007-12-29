package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.ruleflow.common.core.impl.ProcessImpl;
import org.drools.ruleflow.common.core.Process;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.ruleflow.core.ActionNode;
import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.EndNode;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.StartNode;
import org.drools.ruleflow.core.impl.ConnectionImpl;
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

public class ConnectionHandler extends BaseAbstractHandler
    implements
    Handler {
    public ConnectionHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Process.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( Connection.class );

            this.allowNesting = false;
        }
    }
    

    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startConfiguration( localName,
                                                  attrs );     
        
        String fromName = attrs.getValue( "from" );
        String toName = attrs.getValue( "to" );
        emptyAttributeCheck( localName, "from", fromName, parser );
        emptyAttributeCheck( localName, "to", toName, parser );
        
        ProcessBuildData buildData = (ProcessBuildData)parser.getData();
        Node fromNode = buildData.getNode( fromName );
        Node toNode = buildData.getNode( toName );
        
        if ( fromNode == null ) {
                throw new SAXParseException( "from Node connection name '" + fromName + "' cannot be found",
                                             parser.getLocator() );
        }
        if ( toNode == null ) {
            throw new SAXParseException( "from Node connection name '" + toName + "' cannot be found",
                                         parser.getLocator() );
    }        
        
        ConnectionImpl connection = new ConnectionImpl(fromNode, toNode, Connection.TYPE_NORMAL);
        
        return connection;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Configuration config = parser.endConfiguration();
        return parser.getCurrent();
    }

    public Class generateNodeFor() {
        return Connection.class;
    }    

}
