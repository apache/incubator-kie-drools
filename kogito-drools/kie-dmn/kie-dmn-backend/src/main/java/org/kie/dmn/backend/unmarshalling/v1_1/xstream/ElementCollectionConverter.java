package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.DMNElementReference;
import org.kie.dmn.feel.model.v1_1.ElementCollection;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ElementCollectionConverter extends NamedElementConverter {

    public static final String DRG_ELEMENT = "drgElement";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        ElementCollection ec = (ElementCollection) parent;    
        
        if (DRG_ELEMENT.equals( nodeName )) {
            ec.getDrgElement().add((DMNElementReference) child);
        }
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        
        // no attributes.
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        ElementCollection ec = (ElementCollection) parent;    
        
        for (DMNElementReference e : ec.getDrgElement()) {
            writeChildrenNode(writer, context, e, DRG_ELEMENT);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public ElementCollectionConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new ElementCollection();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( ElementCollection.class );
    }

}
