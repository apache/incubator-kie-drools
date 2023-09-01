package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.Dimension;

public class DimensionConverter extends DMNModelInstrumentedBaseConverter {


    private static final String HEIGHT = "height";
    private static final String WIDTH = "width";

    public DimensionConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Dimension abs = (Dimension) parent;

        abs.setWidth(Double.valueOf(reader.getAttribute(WIDTH)));
        abs.setHeight(Double.valueOf(reader.getAttribute(HEIGHT)));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Dimension abs = (Dimension) parent;

        writer.addAttribute(WIDTH, FormatUtils.manageDouble(abs.getWidth()));
        writer.addAttribute(HEIGHT, FormatUtils.manageDouble(abs.getHeight()));
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_2.dmndi.Dimension();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(org.kie.dmn.model.v1_2.dmndi.Dimension.class);
    }

}
