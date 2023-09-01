package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.dmndi.Style;
import org.kie.dmn.model.api.dmndi.Style.Extension;

public abstract class StyleConverter extends DMNModelInstrumentedBaseConverter {

    private static final String EXTENSION = "extension";
    private static final String ID = "id";

    public StyleConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Style style = (Style) parent;
        
        if (child instanceof Style.Extension) {
            style.setExtension((Extension) child);
        } else {
            super.assignChildElement(style, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Style style = (Style) parent;
        String id = reader.getAttribute(ID);
        if (id != null) {
            style.setId(id);
        }

    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Style style = (Style) parent;
        
        if (style.getExtension() != null) {
            writeChildrenNode(writer, context, style.getExtension(), EXTENSION);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Style style = (Style) parent;

        if (style.getId() != null) {
            writer.addAttribute(ID, style.getId());
        }

    }


}
