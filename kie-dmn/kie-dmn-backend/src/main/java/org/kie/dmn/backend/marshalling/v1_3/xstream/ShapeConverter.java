package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.dmndi.Bounds;
import org.kie.dmn.model.api.dmndi.Shape;

public abstract class ShapeConverter extends DiagramElementConverter {

    private static final String BOUNDS = "Bounds";

    public ShapeConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Shape abs = (Shape) parent;

        if (child instanceof Bounds) {
            abs.setBounds((Bounds) child);
        } else {
            super.assignChildElement(abs, nodeName, child);
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
        Shape abs = (Shape) parent;

        if (abs.getBounds() != null) {
            writeChildrenNode(writer, context, abs.getBounds(), BOUNDS);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        // no attributes.
    }


}
