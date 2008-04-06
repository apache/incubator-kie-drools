package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MappingHandler extends BaseAbstractHandler
    implements
    Handler {
    public MappingHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( WorkItemNode.class );

            this.validPeers = new HashSet();         
            this.validPeers.add( null );            

            this.allowNesting = false;
        }
    }
    

    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startConfiguration(localName, attrs);
        WorkItemNode workItemNode = (WorkItemNode) parser.getParent();
        final String type = attrs.getValue("type");
        emptyAttributeCheck(localName, "type", type, parser);
        final String parameterName = attrs.getValue("parameterName");
        emptyAttributeCheck(localName, "parameterName", parameterName, parser);
        final String variableName = attrs.getValue("variableName");
        emptyAttributeCheck(localName, "variableName", variableName, parser);
        if ("in".equals(type)) {
            workItemNode.addInMapping(parameterName, variableName);
        } else if ("out".equals(type)) {
            workItemNode.addOutMapping(parameterName, variableName);
        } else {
            throw new SAXParseException(
                "Unknown mapping type " + type, parser.getLocator());
        }
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endConfiguration();
        return null;
    }

    public Class generateNodeFor() {
        return null;
    }    

}
