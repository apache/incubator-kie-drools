package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ConstraintImpl;
import org.drools.workflow.core.node.Split;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ConstraintHandler extends BaseAbstractHandler implements Handler {
    
    public ConstraintHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet();
            this.validParents.add(Split.class);

            this.validPeers = new HashSet();
            this.validPeers.add(null);

            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startConfiguration(localName, attrs);
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        Configuration config = parser.endConfiguration();
        Split splitNode = (Split) parser.getParent();
        final String toNodeIdString = config.getAttribute("toNodeId");
        emptyAttributeCheck(localName, "toNodeId", toNodeIdString, parser);
        int toNodeId = new Integer(toNodeIdString);
        final String toType = config.getAttribute("toType");
        emptyAttributeCheck(localName, "toType", toType, parser);
        Split.ConnectionRef connectionRef = new Split.ConnectionRef(toNodeId, toType);
        Constraint constraint = new ConstraintImpl();
        
        final String name = config.getAttribute("name");
        constraint.setName(name);
        final String priority = config.getAttribute("priority");
        if (priority != null) {
            constraint.setPriority(new Integer(priority));
        }
        final String type = config.getAttribute("type");
        constraint.setType(type);
        final String dialect = config.getAttribute("dialect");
        constraint.setDialect(dialect);
        String text = config.getText();
        if (text != null) {
            text.trim();
            if ("".equals(text)) {
                text = null;
            }
        }
        constraint.setConstraint(text);
        splitNode.internalSetConstraint(connectionRef, constraint);
        return null;
    }

    public Class generateNodeFor() {
        return Constraint.class;
    }    

}
