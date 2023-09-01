package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.v1_3.TInformationItem;

public class InformationItemConverter
        extends NamedElementConverter {
    private static final String TYPE_REF = "typeRef";

    public InformationItemConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals(TInformationItem.class);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement( parent, nodeName, child );
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        InformationItem ii = (InformationItem) parent;

        String typeRef = reader.getAttribute( TYPE_REF );
        ii.setTypeRef( MarshallingUtils.parseQNameString( typeRef ) );
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TInformationItem();
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        InformationItem ii = (InformationItem) parent;
        
        if (ii.getTypeRef() != null) {
            writer.addAttribute(TYPE_REF, MarshallingUtils.formatQName(ii.getTypeRef(), ii));
        }
    }

}
