package org.drools.xml.support.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.models.ListenerModelImpl;

public class ListenerConverter extends AbstractXStreamConverter {

    public ListenerConverter() {
        super(ListenerModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ListenerModelImpl listener = (ListenerModelImpl) value;
        writer.addAttribute("type", listener.getType());
            /* TODO make qualifiers working properly before readd them to the xml
            QualifierModelImpl qualifier = (QualifierModelImpl)listener.getQualifierModel();
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
        final ListenerModelImpl listener = new ListenerModelImpl();
        listener.setType(reader.getAttribute("type"));
            /* TODO make qualifiers working properly before readd them to the xml
            String qualifierType = reader.getAttribute("qualifier");
            if (qualifierType != null) {
                listener.newQualifierModel(qualifierType);
            }

            readNodes( reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader,
                                   String name,
                                   String value) {
                    if ( "qualifier".equals( name ) ) {
                        QualifierModelImpl qualifier = readObject(reader, context, QualifierModelImpl.class);
                        listener.setQualifierModel(qualifier);
                    }
                }
            } );
            */
        return listener;
    }
}