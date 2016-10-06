package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.BusinessContextElement;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class BusinessContextElementConverter extends NamedElementConverter {
    public static final String URI = "URI";

    public BusinessContextElementConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        BusinessContextElement bce = (BusinessContextElement) parent;
        
        String uri = reader.getAttribute(URI);
        
        bce.setURI(uri);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        BusinessContextElement bce = (BusinessContextElement) parent;
        
        if (bce.getURI() != null) writer.addAttribute(URI, bce.getURI());
    }
}
