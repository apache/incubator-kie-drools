package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.datatype.DataType;
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
            this.validParents = new HashSet();
            this.validParents.add( Variable.class );

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
        Variable variable = (Variable) parser.getParent();
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        DataType dataType = null;
        try {
            dataType = (DataType) Class.forName(name).newInstance();
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
        
        variable.setType(dataType);
        return dataType;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class generateNodeFor() {
        return DataType.class;
    }    

}
