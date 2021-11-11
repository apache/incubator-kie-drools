package org.drools.xml.support.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.models.ChannelModelImpl;

public class ChannelConverter extends AbstractXStreamConverter {

    public ChannelConverter() {
        super(ChannelModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ChannelModelImpl channel = (ChannelModelImpl) value;
        writer.addAttribute("name", channel.getName());
        writer.addAttribute("type", channel.getType());
            /* TODO make qualifiers working properly before readd them to the xml
            QualifierModelImpl qualifier = (QualifierModelImpl)channel.getQualifierModel();
            if (qualifier != null) {
                if (qualifier.isSimple()) {
                    writer.addAttribute("qualifier", qualifier.getType());
                } else {
                    writeObject(writer, context, "qualifier", qualifier);
                }
            }
            */
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final ChannelModelImpl channel = new ChannelModelImpl();
        channel.setName(reader.getAttribute("name"));
        channel.setType(reader.getAttribute("type"));
            /* TODO make qualifiers working properly before readd them to the xml
            String qualifierType = reader.getAttribute("qualifier");
            if (qualifierType != null) {
                channel.newQualifierModel(qualifierType);
            }

            readNodes( reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader,
                                   String name,
                                   String value) {
                    if ( "qualifier".equals( name ) ) {
                        QualifierModelImpl qualifier = readObject(reader, context, QualifierModelImpl.class);
                        channel.setQualifierModel(qualifier);
                    }
                }
            } );
            */
        return channel;
    }
}