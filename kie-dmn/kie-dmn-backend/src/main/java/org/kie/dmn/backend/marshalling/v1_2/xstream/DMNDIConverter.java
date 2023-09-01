package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.DMNDI;
import org.kie.dmn.model.api.dmndi.DMNDiagram;
import org.kie.dmn.model.api.dmndi.DMNStyle;

public class DMNDIConverter extends DMNModelInstrumentedBaseConverter {

    private static final String DMN_STYLE = "DMNStyle";
    private static final String DMN_DIAGRAM = "DMNDiagram";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DMNDI list = (DMNDI) parent;
        
        if (child instanceof DMNDiagram) {
            list.getDMNDiagram().add((DMNDiagram) child);
        } else if (child instanceof DMNStyle) {
            list.getDMNStyle().add((DMNStyle) child);
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
        DMNDI list = (DMNDI) parent;
        
        for (DMNDiagram e : list.getDMNDiagram()) {
            writeChildrenNode(writer, context, e, DMN_DIAGRAM);
        }
        for (DMNStyle e : list.getDMNStyle()) {
            writeChildrenNode(writer, context, e, DMN_STYLE);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public DMNDIConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_2.dmndi.DMNDI();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(org.kie.dmn.model.v1_2.dmndi.DMNDI.class);
    }

}
