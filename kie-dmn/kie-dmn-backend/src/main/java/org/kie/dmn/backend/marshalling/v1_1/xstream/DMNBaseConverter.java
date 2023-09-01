package org.kie.dmn.backend.marshalling.v1_1.xstream;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase;

public abstract class DMNBaseConverter
        extends AbstractCollectionConverter {

    public DMNBaseConverter(Mapper mapper) {
        super( mapper );
    }

    public void marshal(
            Object object,
            HierarchicalStreamWriter writer,
            MarshallingContext context) {
        writeAttributes(writer, object);
        writeChildren(writer, context, object);
    }
    
    protected void writeChildrenNode(HierarchicalStreamWriter writer, MarshallingContext context, Object node, String nodeAlias) {
        writer.startNode(nodeAlias);
        context.convertAnother(node);
        writer.endNode();
    }
    
    protected void writeChildrenNodeAsValue(HierarchicalStreamWriter writer, MarshallingContext context, String nodeValue, String nodeAlias) {
        writer.startNode(nodeAlias);
        writer.setValue(nodeValue);
        writer.endNode();
    }

    protected abstract void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent);

    protected abstract void writeAttributes(HierarchicalStreamWriter writer, Object parent);

    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        DMNModelInstrumentedBase obj = createModelObject();
        assignAttributes( reader, obj );
        parseElements( reader, context, obj );
        return obj;
    }

    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            Object object = readItem(
                    reader,
                    context,
                    null );
            if( object instanceof DMNModelInstrumentedBase ) {
                ((KieDMNModelInstrumentedBase) object).setParent((KieDMNModelInstrumentedBase) parent);
                ((KieDMNModelInstrumentedBase) parent).addChildren((KieDMNModelInstrumentedBase) object);
            }
            reader.moveUp();
            assignChildElement( parent, nodeName, object );
        }
    }

    protected abstract DMNModelInstrumentedBase createModelObject();

    protected abstract void assignChildElement(Object parent, String nodeName, Object child);

    protected abstract void assignAttributes(HierarchicalStreamReader reader, Object parent);

}
