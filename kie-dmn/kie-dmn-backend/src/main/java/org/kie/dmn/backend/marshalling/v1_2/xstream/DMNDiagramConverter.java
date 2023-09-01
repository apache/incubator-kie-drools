package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.DMNDiagram;
import org.kie.dmn.model.api.dmndi.DiagramElement;
import org.kie.dmn.model.api.dmndi.Dimension;

public class DMNDiagramConverter extends DiagramConverter {

    private static final String SIZE = "Size";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DMNDiagram style = (DMNDiagram) parent;

        if (child instanceof Dimension) {
            style.setSize((Dimension) child);
        } else if (child instanceof DiagramElement) {
            style.getDMNDiagramElement().add((DiagramElement) child);
        } else {
            super.assignChildElement(style, nodeName, child);
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
        DMNDiagram style = (DMNDiagram) parent;
        
        if (style.getSize() != null) {
            writeChildrenNode(writer, context, style.getSize(), SIZE);
        }
        for (DiagramElement de : style.getDMNDiagramElement()) {
            writeChildrenNode(writer, context, de, de.getClass().getSimpleName());
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        // no attributes.
    }

    public DMNDiagramConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_2.dmndi.DMNDiagram();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(org.kie.dmn.model.v1_2.dmndi.DMNDiagram.class);
    }

}
