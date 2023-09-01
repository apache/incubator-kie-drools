package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.Invocable;

public abstract class InvocableConverter extends DRGElementConverter {

    public static final String VARIABLE = "variable";

    public InvocableConverter(XStream xstream) {
        super( xstream );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Invocable bkm = (Invocable) parent;

        if (VARIABLE.equals(nodeName)) {
            bkm.setVariable((InformationItem) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );

        // no attributes.
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);

        Invocable bkm = (Invocable) parent;

        if (bkm.getVariable() != null) {
            writeChildrenNode(writer, context, bkm.getVariable(), VARIABLE);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        // no attributes.
    }
}
