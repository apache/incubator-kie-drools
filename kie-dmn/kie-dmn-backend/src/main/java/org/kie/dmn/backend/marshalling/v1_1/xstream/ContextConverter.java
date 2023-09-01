package org.kie.dmn.backend.marshalling.v1_1.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.Context;
import org.kie.dmn.model.api.ContextEntry;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.TContext;

public class ContextConverter extends ExpressionConverter {
    public static final String CONTEXT_ENTRY = "contextEntry";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Context c = (Context) parent;
        
        if (CONTEXT_ENTRY.equals(nodeName)) {
            c.getContextEntry().add((ContextEntry) child);
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
        Context c = (Context) parent;
        
        for (ContextEntry ce : c.getContextEntry()) {
            writeChildrenNode(writer, context, ce, CONTEXT_ENTRY);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public ContextConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TContext();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TContext.class);
    }

}
