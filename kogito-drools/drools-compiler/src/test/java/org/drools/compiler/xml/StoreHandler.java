package org.drools.compiler.xml;

import java.util.HashSet;

import org.drools.compiler.xml.ProcessBuildData;
import org.drools.definition.process.Process;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.StartNode;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StoreHandler extends BaseAbstractHandler
    implements
    Handler {
    public StoreHandler() {
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
                        final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        xmlPackageReader.startElementBuilder( localName,
                                                  attrs );
        
        WorkflowProcessImpl  process = ( WorkflowProcessImpl ) xmlPackageReader.getParent();
        
        ActionNode actionNode = new ActionNode();
        
        final String name = attrs.getValue( "name" );        
        emptyAttributeCheck( localName, "name", name, xmlPackageReader );        
        actionNode.setName( name );
        
        final String id = attrs.getValue( "id" );        
        emptyAttributeCheck( localName, "id", name, xmlPackageReader );        
        actionNode.setId( new Long(id) );
        
        process.addNode( actionNode );
        ((ProcessBuildData)xmlPackageReader.getData()).addNode( actionNode );
        
        return actionNode;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        final Element element = xmlPackageReader.endElementBuilder();
        WorkflowProcessImpl  process = ( WorkflowProcessImpl ) xmlPackageReader.getParent();

        ActionNode actionNode = ( ActionNode ) xmlPackageReader.getCurrent();
        
        String text = ((org.w3c.dom.Text)element.getChildNodes().item( 0 )).getWholeText();
        
        DroolsConsequenceAction actionText = new DroolsConsequenceAction( "mvel", "list.add(\"" + text + "\")" );
        
        actionNode.setAction( actionText );
        
        return actionNode;
    }

    public Class generateNodeFor() {
        return ActionNode.class;
    }    

}
