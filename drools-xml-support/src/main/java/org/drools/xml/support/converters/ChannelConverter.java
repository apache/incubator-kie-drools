package org.drools.xml.support.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.models.ChannelModelImpl;

public class ChannelConverter extends AbstractXStreamConverter {

    public ChannelConverter() {
        super(ChannelModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        ChannelModelImpl channel = (ChannelModelImpl) value;
        writer.addAttribute("name", channel.getName());
        writer.addAttribute("type", channel.getType());
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final ChannelModelImpl channel = new ChannelModelImpl();
        channel.setName(reader.getAttribute("name"));
        channel.setType(reader.getAttribute("type"));
        return channel;
    }
}