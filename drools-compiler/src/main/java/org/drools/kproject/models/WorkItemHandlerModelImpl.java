package org.drools.kproject.models;

import org.drools.core.util.AbstractXStreamConverter;
import org.kie.builder.QualifierModel;
import org.kie.builder.WorkItemHandlerModel;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class WorkItemHandlerModelImpl implements WorkItemHandlerModel {

    private KieSessionModelImpl kSession;
    private String type;
    private QualifierModel qualifier;

    public WorkItemHandlerModelImpl() { }

    public WorkItemHandlerModelImpl(KieSessionModelImpl kSession, String type) {
        this.kSession = kSession;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
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

    public static class WorkItemHandelerConverter extends AbstractXStreamConverter {

        public WorkItemHandelerConverter() {
            super(WorkItemHandlerModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            WorkItemHandlerModelImpl wih = (WorkItemHandlerModelImpl) value;
            writer.addAttribute("type", wih.getType());
            QualifierModelImpl qualifier = (QualifierModelImpl)wih.getQualifierModel();
            if (qualifier != null) {
                if (qualifier.isSimple()) {
                    writer.addAttribute("qualifier", qualifier.getType());
                } else {
                    writeObject(writer, context, "qualifier", qualifier);
                }
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final WorkItemHandlerModelImpl wih = new WorkItemHandlerModelImpl();
            wih.setType(reader.getAttribute("type"));
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
            return wih;
        }
    }
}
