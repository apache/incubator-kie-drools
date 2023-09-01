package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public class DMNDecisionServiceDividerLineConverter extends EdgeConverter {

    public DMNDecisionServiceDividerLineConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_3.dmndi.DMNDecisionServiceDividerLine();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(org.kie.dmn.model.v1_3.dmndi.DMNDecisionServiceDividerLine.class);
    }

}
