package org.drools.kproject.models;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.builder.ListenerModel;
import org.kie.builder.QualifierModel;

public class ListenerModelImpl implements ListenerModel {

    private KieSessionModelImpl kSession;
    private String type;
    private ListenerModel.Kind kind;
    private QualifierModel qualifier;

    public ListenerModelImpl() { }

    public ListenerModelImpl(KieSessionModelImpl kSession, String type, ListenerModel.Kind kind) {
        this.kSession = kSession;
        this.type = type;
        this.kind = kind;
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public ListenerModel.Kind getKind() {
        return kind;
    }

    void setKind(ListenerModel.Kind kind) {
        this.kind = kind;
    }

    public QualifierModel getQualifierModel() {
        return qualifier;
    }

    private void setQualifierModel(QualifierModel qualifier) {
        this.qualifier = qualifier;
    }

    public QualifierModel newQualifierModel(String type) {
        QualifierModelImpl qualifier = new QualifierModelImpl(type);
        this.qualifier = qualifier;
        return qualifier;
    }

    public KieSessionModelImpl getKSession() {
        return kSession;
    }

    public void setKSession(KieSessionModelImpl kSession) {
        this.kSession = kSession;
    }

    public static class ListenerConverter extends AbstractXStreamConverter {

        public ListenerConverter() {
            super(ListenerModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            ListenerModelImpl listener = (ListenerModelImpl) value;
            writer.addAttribute("type", listener.getType());
            QualifierModelImpl qualifier = (QualifierModelImpl)listener.getQualifierModel();
            if (qualifier != null) {
                if (qualifier.isSimple()) {
                    writer.addAttribute("qualifier", qualifier.getType());
                } else {
                    writeObject(writer, context, "qualifier", qualifier);
                }
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final ListenerModelImpl listener = new ListenerModelImpl();
            listener.setType(reader.getAttribute("type"));
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
            return listener;
        }
    }
}
