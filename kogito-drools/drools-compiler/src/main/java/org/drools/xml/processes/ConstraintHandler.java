package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ConstraintImpl;
import org.drools.workflow.core.node.Constrainable;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StateNode;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ConstraintHandler extends BaseAbstractHandler implements Handler {
    
    public ConstraintHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add(Split.class);
            this.validParents.add(StateNode.class);
            this.validParents.add(Constrainable.class);

            this.validPeers = new HashSet<Class<?>>();
            this.validPeers.add(null);

            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Object parent = parser.getParent();
        // TODO use Constraintable interface
        if (parent instanceof Split) {
	        Split splitNode = (Split) parser.getParent();
	        
	        final String toNodeIdString = element.getAttribute("toNodeId");
	        emptyAttributeCheck(localName, "toNodeId", toNodeIdString, parser);
	        int toNodeId = new Integer(toNodeIdString);
	        final String toType = element.getAttribute("toType");
	        emptyAttributeCheck(localName, "toType", toType, parser);
	        Split.ConnectionRef connectionRef = new Split.ConnectionRef(toNodeId, toType);
	        Constraint constraint = new ConstraintImpl();
	        
	        final String name = element.getAttribute("name");
	        constraint.setName(name);
	        final String priority = element.getAttribute("priority");
	        if (priority != null && priority.length() != 0) {
	            constraint.setPriority(new Integer(priority));
	        }
	        final String type = element.getAttribute("type");
	        constraint.setType(type);
	        final String dialect = element.getAttribute("dialect");
	        constraint.setDialect(dialect);
	        
	        String text = ((Text)element.getChildNodes().item( 0 )).getWholeText();
	        if (text != null) {
	            text = text.trim();
	            if ("".equals(text)) {
	                text = null;
	            }
	        }
	        constraint.setConstraint(text);
	        splitNode.internalSetConstraint(connectionRef, constraint);
        } else if (parent instanceof StateNode) {
        	StateNode stateNode = (StateNode) parser.getParent();
	        
	        final String toNodeIdString = element.getAttribute("toNodeId");
	        emptyAttributeCheck(localName, "toNodeId", toNodeIdString, parser);
	        int toNodeId = new Integer(toNodeIdString);
	        final String toType = element.getAttribute("toType");
	        emptyAttributeCheck(localName, "toType", toType, parser);
	        StateNode.ConnectionRef connectionRef = new StateNode.ConnectionRef(toNodeId, toType);
	        Constraint constraint = new ConstraintImpl();
	        
	        final String name = element.getAttribute("name");
	        constraint.setName(name);
	        final String priority = element.getAttribute("priority");
	        if (priority != null && priority.length() != 0) {
	            constraint.setPriority(new Integer(priority));
	        }
	        final String type = element.getAttribute("type");
	        constraint.setType(type);
	        final String dialect = element.getAttribute("dialect");
	        constraint.setDialect(dialect);
	        
	        String text = ((Text)element.getChildNodes().item( 0 )).getWholeText();
	        if (text != null) {
	            text = text.trim();
	            if ("".equals(text)) {
	                text = null;
	            }
	        }
	        constraint.setConstraint(text);
	        stateNode.internalSetConstraint(connectionRef, constraint);
        } else if (parent instanceof Constrainable) {
        	Constrainable constrainable = (Constrainable) parent;
             Constraint constraint = new ConstraintImpl();

	        final String name = element.getAttribute("name");
	        constraint.setName(name);
	        final String priority = element.getAttribute("priority");
	        if (priority != null && priority.length() != 0) {
	            constraint.setPriority(new Integer(priority));
	        }
	        final String type = element.getAttribute("type");
	        constraint.setType(type);
	        final String dialect = element.getAttribute("dialect");
	        constraint.setDialect(dialect);
	        String text = ((Text)element.getChildNodes().item( 0 )).getWholeText();
	        if (text != null) {
	            text = text.trim();
	            if ("".equals(text)) {
	                text = null;
	            }
	        }
            constraint.setConstraint(text);
	        constrainable.addConstraint(name, constraint);
        } else {
        	throw new SAXException("Invalid parent node " + parent);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Constraint.class;
    }    

}
