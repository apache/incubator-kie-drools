package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.process.core.Process;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.StartNode;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ActionNodeHandler extends BaseAbstractHandler
    implements
    Handler {
    public ActionNodeHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Process.class );

            this.validPeers = new HashSet();            
            this.validPeers.add( StartNode.class );
            this.validPeers.add( ActionNode.class );            

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
        
        ActionNode actionNode = new ActionNode();
        
        final String name = attrs.getValue( "name" );        
        emptyAttributeCheck( localName, "name", name, parser );        
        actionNode.setName( name );
        
        process.addNode( actionNode );
        ((ProcessBuildData)parser.getData()).addNode( actionNode );
        
        return actionNode;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Configuration config = parser.endConfiguration();
        WorkflowProcessImpl  process = ( WorkflowProcessImpl ) parser.getParent();

        ActionNode actionNode = ( ActionNode ) parser.getCurrent();
        
        String text = config.getText();
        if ( text == null ) {
            throw new SAXParseException( "<action-node> requires content",
                                         parser.getLocator() );
        }
        
        final String dialect = config.getAttribute( "dialect" );     
        emptyAttributeCheck( localName, "dialect", dialect, parser );
        
        DroolsConsequenceAction actionText = new DroolsConsequenceAction( dialect, text);
        
        actionNode.setAction( actionText );
        
        return actionNode;
    }

    public Class generateNodeFor() {
        return ActionNode.class;
    }    

}
