package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.DMNEdge;
import org.kie.dmn.model.api.dmndi.DMNLabel;

public class DMNEdgeConverter extends EdgeConverter {

    private static final String DMN_ELEMENT_REF = "dmnElementRef";
    private static final String SOURCE_ELEMENT  = "sourceElement";
    private static final String TARGET_ELEMENT  = "targetElement";
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
        String sourceElement = reader.getAttribute(SOURCE_ELEMENT);
        String targetElement = reader.getAttribute(TARGET_ELEMENT);

        if (dmnElementRef != null) {
            concrete.setDmnElementRef(MarshallingUtils.parseQNameString(dmnElementRef));
        }
        if (sourceElement != null) {
            concrete.setSourceElement(MarshallingUtils.parseQNameString(sourceElement));
        }
        if (targetElement != null) {
            concrete.setTargetElement(MarshallingUtils.parseQNameString(targetElement));
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
        if (concrete.getSourceElement() != null) {
            writer.addAttribute(SOURCE_ELEMENT, MarshallingUtils.formatQName(concrete.getSourceElement(), concrete));
        }
        if (concrete.getTargetElement() != null) {
            writer.addAttribute(TARGET_ELEMENT, MarshallingUtils.formatQName(concrete.getTargetElement(), concrete));
        }
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_3.dmndi.DMNEdge();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(org.kie.dmn.model.v1_3.dmndi.DMNEdge.class);
    }

}
