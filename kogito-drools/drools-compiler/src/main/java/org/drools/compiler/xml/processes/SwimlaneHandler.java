package org.drools.compiler.xml.processes;

import java.util.HashSet;

import org.drools.definition.process.Process;
import org.drools.process.core.context.swimlane.Swimlane;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SwimlaneHandler extends BaseAbstractHandler
    implements
    Handler {
    public SwimlaneHandler() {
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
        parser.startElementBuilder( localName,
                                    attrs );
        WorkflowProcessImpl process = (WorkflowProcessImpl) parser.getParent();
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        
        SwimlaneContext swimlaneContext = (SwimlaneContext) 
            process.getDefaultContext(SwimlaneContext.SWIMLANE_SCOPE);
        if (swimlaneContext != null) {
            Swimlane swimlane = new Swimlane();
            swimlane.setName(name);
            swimlaneContext.addSwimlane(swimlane);
        } else {
            throw new SAXParseException(
                "Could not find default swimlane context.", parser.getLocator());
        }
        
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return Swimlane.class;
    }    

}
