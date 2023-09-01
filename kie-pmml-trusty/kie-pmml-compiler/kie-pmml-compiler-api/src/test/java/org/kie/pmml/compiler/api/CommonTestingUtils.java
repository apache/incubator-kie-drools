package org.kie.pmml.compiler.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Field;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.OpType;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;

public class CommonTestingUtils {

    public static String getDATA_TYPEString(DataType dataType) {
        return DATA_TYPE.class.getName() + "." + DATA_TYPE.byName(dataType.value()).name();
    }

    public static String getOP_TYPEString(OpType opType) {
        return OP_TYPE.class.getName() + "." + OP_TYPE.byName(opType.value()).name();
    }

    public static List<Field<?>> getFieldsFromDataDictionary(DataDictionary dataDictionary) {
        final List<Field<?>> toReturn = new ArrayList<>();
        dataDictionary.getDataFields().stream().map(Field.class::cast).forEach(toReturn::add);
        return toReturn;
    }

    public static List<Field<?>> getFieldsFromTransformationDictionary(TransformationDictionary transformationDictionary) {
        if (transformationDictionary != null && transformationDictionary.hasDerivedFields()) {
            final List<Field<?>> toReturn = new ArrayList<>();
            transformationDictionary.getDerivedFields().stream().map(Field.class::cast).forEach(toReturn::add);
            return toReturn;
        } else {
            return Collections.emptyList();
        }
    }

    public static List<Field<?>> getFieldsFromLocalTransformations(LocalTransformations localTransformations) {
        if (localTransformations != null && localTransformations.hasDerivedFields()) {
            final List<Field<?>> toReturn = new ArrayList<>();
            localTransformations.getDerivedFields().stream().map(Field.class::cast).forEach(toReturn::add);
            return toReturn;
        } else {
            return Collections.emptyList();
        }
    }

    public static List<Field<?>> getFieldsFromDataDictionaryAndTransformationDictionaryAndLocalTransformations(DataDictionary dataDictionary,
                                                                                                               TransformationDictionary transformationDictionary,
                                                                                                               LocalTransformations localTransformations) {
        final List<Field<?>> toReturn = new ArrayList<>();
        toReturn.addAll(getFieldsFromDataDictionary(dataDictionary));
        toReturn.addAll(getFieldsFromTransformationDictionary(transformationDictionary));
        toReturn.addAll(getFieldsFromLocalTransformations(localTransformations));
        return toReturn;
    }

    public static List<Field<?>> getFieldsFromDataDictionaryAndTransformationDictionary(DataDictionary dataDictionary, TransformationDictionary transformationDictionary) {
        final List<Field<?>> toReturn = new ArrayList<>();
        toReturn.addAll(getFieldsFromDataDictionary(dataDictionary));
        toReturn.addAll(getFieldsFromTransformationDictionary(transformationDictionary));
        return toReturn;
    }


    public static List<Field<?>> getFieldsFromDataDictionaryAndDerivedFields(DataDictionary dataDictionary, List<DerivedField> derivedFields) {
        final List<Field<?>> toReturn = new ArrayList<>();
        toReturn.addAll(getFieldsFromDataDictionary(dataDictionary));
        toReturn.addAll(derivedFields);
        return toReturn;
    }

}
