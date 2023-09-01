package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.DMNEdge;
import org.kie.dmn.model.api.dmndi.DMNLabel;

public class DMNEdgeConverter extends EdgeConverter {

    private static final String DMN_ELEMENT_REF = "dmnElementRef";
    private static final String DMN_LABEL = "DMNLabel";

    public DMNEdgeConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DMNEdge concrete = (DMNEdge) parent;

        if (child instanceof DMNLabel) {
            concrete.setDMNLabel((DMNLabel) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        DMNEdge concrete = (DMNEdge) parent;

        String dmnElementRef = reader.getAttribute(DMN_ELEMENT_REF);

        if (dmnElementRef != null) {
            concrete.setDmnElementRef(MarshallingUtils.parseQNameString(dmnElementRef));
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DMNEdge concrete = (DMNEdge) parent;

        if (concrete.getDMNLabel() != null) {
            writeChildrenNode(writer, context, concrete.getDMNLabel(), DMN_LABEL);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        DMNEdge concrete = (DMNEdge) parent;
        if (concrete.getDmnElementRef() != null) {
            writer.addAttribute(DMN_ELEMENT_REF, MarshallingUtils.formatQName(concrete.getDmnElementRef(), concrete));
        }
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_2.dmndi.DMNEdge();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(org.kie.dmn.model.v1_2.dmndi.DMNEdge.class);
    }

}
