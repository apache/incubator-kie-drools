package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.process.core.Process;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.StartNode;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

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
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startConfiguration( localName,
                                                  attrs );
        
        WorkflowProcessImpl  process = ( WorkflowProcessImpl ) parser.getParent();
        
        final EndNode endNode = new EndNode();
        
        final String name = attrs.getValue( "name" );        
        emptyAttributeCheck( localName, "name", name, parser );
        endNode.setName( name );
        
        process.addNode( endNode );        
        ((ProcessBuildData)parser.getData()).addNode( endNode );
        
        return endNode;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Configuration config = parser.endConfiguration();
        return parser.getCurrent();
    }

    public Class generateNodeFor() {
        return EndNode.class;
    }    

}
