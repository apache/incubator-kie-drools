package org.drools.compiler.xml.processes;

import java.util.HashSet;

import org.drools.process.core.ValueObject;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.workflow.core.Node;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MetaDataHandler extends BaseAbstractHandler
    implements
    Handler {
    public MetaDataHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Node.class );

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
        Node node = (Node) parser.getParent();
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        return new MetaDataWrapper(node, name);
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return MetaDataWrapper.class;
    }
    
    public class MetaDataWrapper implements ValueObject {
    	private Node node;
    	private String name;
    	public MetaDataWrapper(Node node, String name) {
    		this.node = node;
    		this.name = name;
    	}
		public Object getValue() {
			return node.getMetaData(name);
		}
		public void setValue(Object value) {
			node.setMetaData(name, value);
		}
		public DataType getType() {
			return new StringDataType();
		}
		public void setType(DataType type) {
		}
    }

}
