package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.Color;

public class ColorConverter extends DMNModelInstrumentedBaseConverter {

    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";


    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Color style = (Color) parent;

        style.setRed(Integer.valueOf(reader.getAttribute(RED)));
        style.setGreen(Integer.valueOf(reader.getAttribute(GREEN)));
        style.setBlue(Integer.valueOf(reader.getAttribute(BLUE)));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Color style = (Color) parent;

        writer.addAttribute(RED, Integer.valueOf(style.getRed()).toString());
        writer.addAttribute(GREEN, Integer.valueOf(style.getGreen()).toString());
        writer.addAttribute(BLUE, Integer.valueOf(style.getBlue()).toString());
    }

    public ColorConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_3.dmndi.Color();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(org.kie.dmn.model.v1_3.dmndi.Color.class);
    }

}
