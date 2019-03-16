package org.kie.pmml.pmml_4_2.model;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.api.pmml.PMML4OutputField;


public class PMMLOutputConverter implements Converter {

    @Override
    public boolean canConvert(Class type) {
        return PMML4OutputField.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        PMML4OutputField fld = (PMML4OutputField) source;
        setField("context", fld.getContext(), writer);
        setField("name", fld.getName(), writer);
        setField("warning", fld.getWarning(), writer);
        writer.flush();
    }

    private void setField(String fldName, String fldValue, HierarchicalStreamWriter writer) {
        writer.startNode(fldName);
        if (fldValue != null && !fldValue.trim().isEmpty()) {
            writer.setValue(fldValue);
        }
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        PMML4OutputField outputField = new PMML4OutputField();
        System.out.println(reader.getNodeName());
        return outputField;
    }

}
