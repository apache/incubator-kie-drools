package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.process.core.TypeObject;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class TypeHandler extends BaseAbstractHandler
    implements
    Handler {
    public TypeHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add( TypeObject.class );

            this.validPeers = new HashSet<Class<?>>();         
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
        TypeObject typeable = (TypeObject) parser.getParent();
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        DataType dataType = null;
        try {
            dataType = (DataType) Class.forName(name).newInstance();
            // TODO make this pluggable so datatypes can read in other properties as well
            if (dataType instanceof ObjectDataType) {
                String className = attrs.getValue("className");
                if (className == null) {
                	className = "java.lang.Object";
                }
                ((ObjectDataType) dataType).setClassName(className);
            }
        } catch (ClassNotFoundException e) {
            throw new SAXParseException(
                "Could not find datatype " + name, parser.getLocator());
        } catch (InstantiationException e) {
            throw new SAXParseException(
                "Could not instantiate datatype " + name, parser.getLocator());
        } catch (IllegalAccessException e) {
            throw new SAXParseException(
                "Could not access datatype " + name, parser.getLocator());
        }
        
        typeable.setType(dataType);
        return dataType;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class<?> generateNodeFor() {
        return DataType.class;
    }    

}
