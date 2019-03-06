package org.kie.pmml.pmml_4_2.model;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


public class DataXstreamConverter implements Converter {

    @Override
    public boolean canConvert(Class type) {
        return AbstractPMMLData.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        AbstractPMMLData data = (AbstractPMMLData) source;
        writer.startNode("abstractPmmlData");
        writer.underlyingWriter().startNode("correlationId");
        writer.underlyingWriter().setValue(data.getCorrelationId());
        writer.underlyingWriter().endNode();
        writer.underlyingWriter().startNode("modelName");
        writer.underlyingWriter().setValue(data.getModelName());
        writer.underlyingWriter().endNode();
        writer.underlyingWriter().flush();
        writer.endNode();
        writer.flush();
        //        StringBuilder bldr = new StringBuilder("<abstractPmmlData>");
        //        bldr.append("<correlationId>").append(data.getCorrelationId()).append("</correlationId>");
        //        bldr.append("<modelName>").append(data.getModelName()).append("</modelName>");
        //        bldr.append("</abstractPmmlData>");
        //        writer.setValue(bldr.toString());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        AbstractPMMLData result = null;
        Map<String, String> values = new HashMap<>();
        String rootNode = reader.getNodeName();
        if (rootNode.equals("abstractPmmlData")) {
            int childCount = 0;
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                String nodeValue = reader.getValue();
                values.put(nodeName, nodeValue);
                childCount++;
            }
            while (childCount > 0) {
                reader.moveUp();
            }
        }
        String modelName = values.get("modelName");
        if (modelName != null && !modelName.trim().isEmpty()) {
            result = new AbstractPMMLData(values.get("correlationId"), modelName);
        }
        return result;
    }

}
