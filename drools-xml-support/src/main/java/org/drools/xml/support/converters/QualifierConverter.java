package org.drools.xml.support.converters;

import java.util.Map;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.models.QualifierModelImpl;

public class QualifierConverter extends AbstractXStreamConverter {

    public QualifierConverter() {
        super(QualifierModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        QualifierModelImpl qualifier = (QualifierModelImpl) value;
        writer.addAttribute("type", qualifier.getType());
        if (qualifier.getValue() != null) {
            writer.addAttribute("value", qualifier.getValue());
        } else {
            for (Map.Entry<String, String> entry : qualifier.getArguments().entrySet()) {
                writer.startNode("arg");
                writer.addAttribute("key", entry.getKey());
                writer.addAttribute("value", entry.getValue());
                writer.endNode();
            }
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        final QualifierModelImpl qualifier = new QualifierModelImpl();
        qualifier.setType(reader.getAttribute("type"));
        String value = reader.getAttribute("value");

        if (value != null) {
            qualifier.setValue(value);
        } else {
            readNodes( reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader,
                                   String name,
                                   String value) {
                    if ( "arg".equals( name ) ) {
                        qualifier.addArgument(reader.getAttribute("key"), reader.getAttribute("value"));
                    }
                }
            } );
        }

        return qualifier;
    }
}