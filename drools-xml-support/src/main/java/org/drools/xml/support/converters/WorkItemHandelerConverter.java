package org.drools.xml.support.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.models.WorkItemHandlerModelImpl;

public class WorkItemHandelerConverter extends AbstractXStreamConverter {

    public WorkItemHandelerConverter() {
        super(WorkItemHandlerModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        WorkItemHandlerModelImpl wih = (WorkItemHandlerModelImpl) value;
        writer.addAttribute("name", wih.getName());
        writer.addAttribute("type", wih.getType());
            /* TODO make qualifiers working properly before readd them to the xml
            QualifierModelImpl qualifier = (QualifierModelImpl)wih.getQualifierModel();
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
        final WorkItemHandlerModelImpl wih = new WorkItemHandlerModelImpl();
        wih.setName(reader.getAttribute("name"));
        wih.setType(reader.getAttribute("type"));
            /* TODO make qualifiers working properly before readd them to the xml
            String qualifierType = reader.getAttribute("qualifier");
            if (qualifierType != null) {
                wih.newQualifierModel(qualifierType);
            }

            readNodes( reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader,
                                   String name,
                                   String value) {
                    if ( "qualifier".equals( name ) ) {
                        QualifierModelImpl qualifier = readObject(reader, context, QualifierModelImpl.class);
                        wih.setQualifierModel(qualifier);
                    }
                }
            } );
            */
        return wih;
    }
}