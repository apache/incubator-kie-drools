package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.Context;
import org.kie.dmn.feel.model.v1_1.ContextEntry;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

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
    protected Object createModelObject() {
        return new Context();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( Context.class );
    }

}
