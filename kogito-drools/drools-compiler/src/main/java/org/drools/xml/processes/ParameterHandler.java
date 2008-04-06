package org.drools.xml.processes;

import java.io.Serializable;
import java.util.HashSet;

import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.BooleanDataType;
import org.drools.process.core.datatype.impl.type.FloatDataType;
import org.drools.process.core.datatype.impl.type.IntegerDataType;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.Configuration;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ParameterHandler extends BaseAbstractHandler
    implements
    Handler {
    public ParameterHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Work.class );

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
        Work work = (Work) parser.getParent();
        final String name = config.getAttribute("name");
        emptyAttributeCheck(localName, "name", name, parser);
        final String type = config.getAttribute("type");
        emptyAttributeCheck(localName, "type", type, parser);
        DataType dataType = null;
        try {
            dataType = (DataType) Class.forName(type).newInstance();
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
        String text = config.getText();
        if (text != null) {
            text.trim();
            if ("".equals(text)) {
                text = null;
            }
        }
        Object value = restoreValue(text, dataType, parser);
        ParameterDefinition parameterDefinition = new ParameterDefinitionImpl(name, dataType);
        work.addParameterDefinition(parameterDefinition);
        work.setParameter(name, value);
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
