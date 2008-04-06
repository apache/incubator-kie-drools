package org.drools.xml.processes;

import java.io.Serializable;
import java.util.HashSet;

import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.BooleanDataType;
import org.drools.process.core.datatype.impl.type.FloatDataType;
import org.drools.process.core.datatype.impl.type.IntegerDataType;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValueHandler extends BaseAbstractHandler
    implements
    Handler {
    public ValueHandler() {
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
        parser.startConfiguration(localName, attrs);
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        Configuration config = parser.endConfiguration();
        Variable variable = (Variable) parser.getParent();
        String text = config.getText();
        if (text != null) {
            text.trim();
            if ("".equals(text)) {
                text = null;
            }
        }
        Serializable value = restoreValue(text, variable.getType(), parser);
        variable.setValue(value);
        return null;
    }
    
    private Serializable restoreValue(String text, DataType dataType, ExtensibleXmlParser parser) throws SAXException {
        if (text == null || "".equals(text)) {
            return null;
        }
        if (dataType == null) {
            throw new SAXParseException(
                "Null datatype", parser.getLocator());
        }
        if (dataType instanceof StringDataType) {
            return text;
        } else if (dataType instanceof IntegerDataType) {
            return new Integer(text);
        } else if (dataType instanceof FloatDataType) {
            return new Float(text);
        } else if (dataType instanceof BooleanDataType) {
            return new Boolean(text);
        } else {
            throw new SAXParseException(
                "Unknown datatype " + dataType, parser.getLocator());
        }
    }

    public Class generateNodeFor() {
        return null;
    }

}
