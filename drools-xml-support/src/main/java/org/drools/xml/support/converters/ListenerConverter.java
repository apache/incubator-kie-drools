package org.drools.xml.support.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.models.ListenerModelImpl;

public class ListenerConverter extends AbstractXStreamConverter {

    public ListenerConverter() {
        super(ListenerModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ListenerModelImpl listener = (ListenerModelImpl) value;
        writer.addAttribute("type", listener.getType());
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final ListenerModelImpl listener = new ListenerModelImpl();
        listener.setType(reader.getAttribute("type"));
        return listener;
    }
}