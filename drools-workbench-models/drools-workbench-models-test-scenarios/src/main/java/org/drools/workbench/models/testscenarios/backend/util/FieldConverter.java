package org.drools.workbench.models.testscenarios.backend.util;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import org.drools.workbench.models.testscenarios.shared.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FieldConverter implements Converter {


    private final XStream xt;

    public FieldConverter(XStream xt) {
        this.xt = xt;
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        context.convertAnother(source, getDefaultConverter());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        reader.moveDown();
        String name = reader.getValue();
        reader.moveUp();

        reader.moveDown();

        if (reader.getNodeName().equals("collectionFieldList")) {

            CollectionFieldData collectionFieldData = createCollectionFieldData(context, name);
            reader.moveUp();

            return collectionFieldData;

        } else if (reader.getNodeName().equals("value")) {
            FieldData fieldData = new FieldData();

            fieldData.setName(name);

            fieldData.setValue(reader.getValue());
            reader.moveUp();

            // Nature is optional
            if (reader.hasMoreChildren()) {
                reader.moveDown();
                String value = reader.getValue();
                fieldData.setNature(Integer.parseInt(value));
                reader.moveUp();
            }

            // Could be a legacy CollectionFieldData, let's see
            if (reader.hasMoreChildren()) {
                reader.moveDown();
                if (reader.getNodeName().equals("collectionFieldList")) {
                    CollectionFieldData collectionFieldData = createCollectionFieldData(context, name);
                    reader.moveUp();

                    if (!collectionFieldData.getCollectionFieldList().isEmpty()) {
                        return collectionFieldData;
                    }
                }
            }
            // And since we have a big big big pile of legacy test scenarios with a marvelous design
            // we do one more check if this field data is after all a collection field data!
            if (fieldData.getValue() != null && fieldData.getValue().startsWith("=[")) {
                CollectionFieldData collectionFieldData = new CollectionFieldData();
                collectionFieldData.setName(name);

                String list = fieldData.getValue().substring(2, fieldData.getValue().length() - 1);


                if (list.contains(",")) {
                    for (String value : list.split(",")) {
                        FieldData subFieldData = new FieldData();
                        subFieldData.setName(name);
                        subFieldData.setValue(value);
                        collectionFieldData.getCollectionFieldList().add(subFieldData);
                    }
                } else {
                    FieldData subFieldData = new FieldData();
                    subFieldData.setName(name);
                    subFieldData.setValue(list);
                    collectionFieldData.getCollectionFieldList().add(subFieldData);
                }

                return collectionFieldData;
            } else {
                return fieldData;
            }

        } else if (reader.getNodeName().equals("fact")) {

            FactAssignmentField factAssignmentField = new FactAssignmentField();
            factAssignmentField.setName(name);

            factAssignmentField.setFact((Fact) context.convertAnother(factAssignmentField, Fact.class));
            reader.moveUp();

            return factAssignmentField;
        }

        throw new InvalidParameterException("Unknown Field instance.");
    }

    private CollectionFieldData createCollectionFieldData(UnmarshallingContext context, String name) {
        CollectionFieldData collectionFieldData = new CollectionFieldData();
        collectionFieldData.setName(name);

        collectionFieldData.setCollectionFieldList((ArrayList) context.convertAnother(collectionFieldData, ArrayList.class));
        return collectionFieldData;
    }

    @Override
    public boolean canConvert(Class type) {
        return Field.class.isAssignableFrom(type);
    }


    private ReflectionConverter getDefaultConverter() {
        return new ReflectionConverter(xt.getMapper(), xt.getReflectionProvider());
    }

}
