package org.drools.xml.processes;

import java.util.HashSet;

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
        
        RuleFlowProcessImpl  process = ( RuleFlowProcessImpl ) parser.getParent();
        
        ActionNodeImpl actionNode = new ActionNodeImpl();
        
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
        RuleFlowProcessImpl  process = ( RuleFlowProcessImpl ) parser.getParent();

        ActionNodeImpl actionNode = ( ActionNodeImpl ) parser.getCurrent();
        
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
