package org.kie.dmn.backend.marshalling.v1_4.xstream;

import org.kie.dmn.model.api.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class NamedElementConverter
        extends DMNElementConverter {
    private static final String NAME = "name";

    public NamedElementConverter(XStream xstream) {
        super( xstream );
    }

    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement( parent, nodeName, child );
    }

    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        String name = reader.getAttribute( NAME );
        ((NamedElement) parent).setName( name );
    }
    
    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        
        // no children.
    }
    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        NamedElement ne = (NamedElement) parent;
        
        writer.addAttribute( NAME , ne.getName() );
    }
}
