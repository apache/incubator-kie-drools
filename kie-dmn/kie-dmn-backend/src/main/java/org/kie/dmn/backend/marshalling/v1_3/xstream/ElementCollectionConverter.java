package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.ElementCollection;
import org.kie.dmn.model.v1_3.TElementCollection;

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
    protected DMNModelInstrumentedBase createModelObject() {
        return new TElementCollection();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TElementCollection.class);
    }

}
