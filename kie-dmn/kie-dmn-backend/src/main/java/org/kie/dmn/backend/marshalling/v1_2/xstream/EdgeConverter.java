package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.dmndi.Edge;
import org.kie.dmn.model.api.dmndi.Point;

public abstract class EdgeConverter extends DiagramElementConverter {

    private static final String WAYPOINT = "waypoint";

    public EdgeConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Edge abs = (Edge) parent;

        if (child instanceof Point) {
            abs.getWaypoint().add((Point) child);
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
        Edge abs = (Edge) parent;

        for (Point pt : abs.getWaypoint()) {
            writeChildrenNode(writer, context, pt, WAYPOINT);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        // no attributes.
    }


}
