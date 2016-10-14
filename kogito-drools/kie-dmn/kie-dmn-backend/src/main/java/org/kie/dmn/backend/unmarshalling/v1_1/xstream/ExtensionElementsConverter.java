package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.DMNElement;
import org.kie.dmn.feel.model.v1_1.DMNElement.ExtensionElements;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ExtensionElementsConverter extends DMNModelInstrumentedBaseConverter {

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        // no attributes.
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Object obj = createModelObject();
        assignAttributes( reader, obj );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            // skipping nodeName
            reader.moveUp();
        }
        return obj;
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        // no attributes.
    }

    public ExtensionElementsConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new DMNElement.ExtensionElements();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( ExtensionElements.class );
    }

}
