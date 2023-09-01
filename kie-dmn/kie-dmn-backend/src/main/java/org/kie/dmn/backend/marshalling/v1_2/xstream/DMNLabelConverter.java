package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.DMNLabel;

public class DMNLabelConverter extends ShapeConverter {

    public static final String TEXT = "Text";

    public DMNLabelConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DMNLabel concrete = (DMNLabel) parent;

        if (nodeName.equals(TEXT)) {
            concrete.setText((String) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);

        // no attributes.
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DMNLabel concrete = (DMNLabel) parent;

        if (concrete.getText() != null) {
            writeChildrenNode(writer, context, concrete.getText(), TEXT);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        // no attributes.
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_2.dmndi.DMNLabel();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(org.kie.dmn.model.v1_2.dmndi.DMNLabel.class);
    }

}
