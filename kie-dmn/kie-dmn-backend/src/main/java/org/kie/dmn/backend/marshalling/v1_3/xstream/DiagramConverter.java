package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.dmndi.Diagram;

public abstract class DiagramConverter extends DiagramElementConverter {

    private static final String RESOLUTION = "resolution";
    private static final String DOCUMENTATION = "documentation";
    private static final String NAME = "name";

    public DiagramConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Diagram abs = (Diagram) parent;

        String name = reader.getAttribute(NAME);
        String documentation = reader.getAttribute(DOCUMENTATION);
        String resolution = reader.getAttribute(RESOLUTION);

        if (name != null) {
            abs.setName(name);
        }
        if (documentation != null) {
            abs.setDocumentation(documentation);
        }
        if (resolution != null) {
            abs.setResolution(Double.valueOf(resolution));
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Diagram abs = (Diagram) parent;

        if (abs.getName() != null) {
            writer.addAttribute(NAME, abs.getName());
        }
        if (abs.getDocumentation() != null) {
            writer.addAttribute(DOCUMENTATION, abs.getDocumentation());
        }
        if (abs.getResolution() != null) {
            writer.addAttribute(RESOLUTION, abs.getResolution().toString());
        }
    }


}
