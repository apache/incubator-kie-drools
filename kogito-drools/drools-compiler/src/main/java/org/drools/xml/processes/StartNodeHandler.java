package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.process.core.Process;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.StartNode;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StartNodeHandler extends BaseAbstractHandler
    implements
    Handler {
    public StartNodeHandler() {
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
        parser.startConfiguration( localName,
                                   attrs );

        WorkflowProcessImpl process = (WorkflowProcessImpl) parser.getParent();

        final StartNode startNode = new StartNode();

        final String name = attrs.getValue( "name" );
        emptyAttributeCheck( localName,
                             "name",
                             name,
                             parser );
        startNode.setName( name );

        process.addNode( startNode );
        ((ProcessBuildData) parser.getData()).addNode( startNode );

        return startNode;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Configuration config = parser.endConfiguration();
        return parser.getCurrent();
    }

    public Class generateNodeFor() {
        return StartNode.class;
    }

}
