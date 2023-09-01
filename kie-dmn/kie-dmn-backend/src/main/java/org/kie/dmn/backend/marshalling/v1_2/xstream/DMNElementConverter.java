package org.kie.dmn.backend.marshalling.v1_2.xstream;

import org.kie.dmn.model.api.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class DMNElementConverter
        extends DMNModelInstrumentedBaseConverter {
    public static final String ID          = "id";
    public static final String LABEL       = "label";
    public static final String DESCRIPTION = "description";
    public static final String EXTENSION_ELEMENTS = "extensionElements";

    public DMNElementConverter(XStream xstream) {
        super( xstream );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        if ( DESCRIPTION.equals( nodeName ) && child instanceof String ) {
            ((DMNElement) parent).setDescription( (String) child );
        } else if(EXTENSION_ELEMENTS.equals(nodeName)
                && child instanceof DMNElement.ExtensionElements) {
            ((DMNElement)parent).setExtensionElements((DMNElement.ExtensionElements)child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        String id = reader.getAttribute( ID );
        String label = reader.getAttribute( LABEL );

        DMNElement dmne = (DMNElement) parent;

        dmne.setId( id );
        dmne.setLabel( label );
    }
    
    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DMNElement e = (DMNElement) parent;

        if (e.getDescription() !=null) { writeChildrenNodeAsValue(writer, context, e.getDescription(), DESCRIPTION); }
        if (e.getExtensionElements() != null ) { writeChildrenNode(writer, context, e.getExtensionElements(), EXTENSION_ELEMENTS); }
    }
    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        DMNElement e = (DMNElement) parent;
        
        if (e.getId() != null) writer.addAttribute( ID , e.getId() );
        if (e.getLabel() != null) writer.addAttribute( LABEL , e.getLabel() );
    }
}
