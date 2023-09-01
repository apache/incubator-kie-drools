package org.drools.xml.support.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.models.WorkItemHandlerModelImpl;

public class WorkItemHandelerConverter extends AbstractXStreamConverter {

    public WorkItemHandelerConverter() {
        super(WorkItemHandlerModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        WorkItemHandlerModelImpl wih = (WorkItemHandlerModelImpl) value;
        writer.addAttribute("name", wih.getName());
        writer.addAttribute("type", wih.getType());
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final WorkItemHandlerModelImpl wih = new WorkItemHandlerModelImpl();
        wih.setName(reader.getAttribute("name"));
        wih.setType(reader.getAttribute("type"));
        return wih;
    }
}