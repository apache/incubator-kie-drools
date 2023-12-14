/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.compiler.api.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.dmg.pmml.Array;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Field;
import org.dmg.pmml.Interval;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.Output;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.Row;
import org.dmg.pmml.Target;
import org.dmg.pmml.TargetValue;
import org.dmg.pmml.Targets;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.Value;
import org.jpmml.model.cells.InputCell;
import org.jpmml.model.cells.OutputCell;
import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.MISSING_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.commons.model.tuples.KiePMMLNameOpType;
import org.w3c.dom.Element;

import static org.kie.pmml.api.utils.PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed;

/**
 * Class to provide common methods to interact with <code>Model</code>, to convert <b>org.dmn.pmml</b> objects to
 * <b>Kie</b> ones, etc...
 */
public class ModelUtils {

    private ModelUtils() {
    }

    /**
     * Return an <code>Optional</code> with the name of the field whose <b>usageType</b> is <code>TARGET</code> or
     * <code>PREDICTED</code>
     * <p>
     * While the xsd schema does not strictly enforce this, it seems that <b>by convention</b> majority of models has
     * only one target.
     * <p>
     * (see https://github.com/jpmml/jpmml-evaluator/issues/64 discussion)
     * @param fields
     * @param model
     * @return
     */
    public static Optional<String> getTargetFieldName(final List<Field<?>> fields, Model model) {
        return getTargetFields(fields, model).stream().map(KiePMMLNameOpType::getName).findFirst();
    }

    /**
     * Return the <code>DATA_TYPE</code> of the field whose <b>usageType</b> is <code>TARGET</code> or
     * <code>PREDICTED</code>.
     * It throws exception if none of such fields are found
     * <p>
     * While the xsd schema does not strictly enforce this, it seems that <b>by convention</b> majority of models has
     * only one target.
     * <p>
     * (see https://github.com/jpmml/jpmml-evaluator/issues/64 discussion)
     * @param fields
     * @param model
     * @return
     */
    public static DATA_TYPE getTargetFieldType(final List<Field<?>> fields, final Model model) {
        return getTargetFieldsTypeMap(fields, model).entrySet().iterator().next().getValue();
    }

    /**
     * Return a <code>List&lt;KiePMMLNameOpType&gt;</code> of target fields
     * Please note that only <b>predicted/target</b>
     * <code>MiningField</code> are considered.
     * @param fields
     * @param model
     * @return
     */
    public static List<KiePMMLNameOpType> getTargetFields(final List<Field<?>> fields, final Model model) {
        List<KiePMMLNameOpType> toReturn = new ArrayList<>();
        if (model.getMiningSchema() != null && model.getMiningSchema().getMiningFields() != null) {
            for (MiningField miningField : model.getMiningSchema().getMiningFields()) {
                if (MiningField.UsageType.TARGET.equals(miningField.getUsageType()) || MiningField.UsageType.PREDICTED.equals(miningField.getUsageType())) {
                    OP_TYPE opType = getOpType(fields, model,miningField.getName());
                    toReturn.add(new KiePMMLNameOpType(miningField.getName(), opType));
                }
            }
        }
        return toReturn;
    }

    /**
     * Returns a <code>Map&lt;String, DATA_TYPE&gt;</code> of target fields, where the key is the name of the field,
     * and the value is the <b>type</b> of the field
     * Please note that only <b>predicted/target</b>
     * <code>MiningField</code> are considered.
     * @param fields
     * @param model
     * @return
     */
    public static Map<String, DATA_TYPE> getTargetFieldsTypeMap(final List<Field<?>> fields, final Model model) {
        Map<String, DATA_TYPE> toReturn = new LinkedHashMap<>();
        if (model.getMiningSchema() != null && model.getMiningSchema().getMiningFields() != null) {
            for (MiningField miningField : model.getMiningSchema().getMiningFields()) {
                if (MiningField.UsageType.TARGET.equals(miningField.getUsageType()) || MiningField.UsageType.PREDICTED.equals(miningField.getUsageType())) {
                    toReturn.put(miningField.getName(), getDATA_TYPE(fields,miningField.getName()));
                }
            }
        }
        return toReturn;
    }

    /**
     * <code>OP_TYPE</code> may be defined inside <code>DataField</code>, <code>MiningField</code> or both.
     * In the latter case, <code>MiningField</code> override <code>DataField</code> definition
     * @param fields
     * @param model
     * @param targetFieldName
     * @return
     */
    public static OP_TYPE getOpType(final List<Field<?>> fields, final Model model, final String targetFieldName) {
        return Stream.of(getOpTypeFromTargets(model.getTargets(), targetFieldName),
                         getOpTypeFromMiningFields(model.getMiningSchema(), targetFieldName),
                         getOpTypeFromFields(fields, targetFieldName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new KiePMMLInternalException(String.format("Failed to find OpType for field" +
                                                                                      " %s", targetFieldName)));
    }

    /**
     * Return <code>Optional&lt;OP_TYPE&gt;</code> of field with given <b>fieldName</b> from <code>DataDictionary</code>
     * @param fields
     * @param fieldName
     * @return
     */
    public static Optional<OP_TYPE> getOpTypeFromFields(final List<Field<?>> fields,
                                                        final String fieldName) {
        return fields == null ? Optional.empty() :
                fields.stream()
                        .filter(dataField -> Objects.equals(fieldName,dataField.getName()) && dataField.getOpType() != null)
                        .map(dataField -> OP_TYPE.byName(dataField.getOpType().value()))
                        .findFirst();
    }

    /**
     * Return <code>Optional&lt;OP_TYPE&gt;</code> of field with given <b>fieldName</b> from <code>MiningSchema</code>
     * @param miningSchema
     * @param fieldName
     * @return
     */
    public static Optional<OP_TYPE> getOpTypeFromMiningFields(MiningSchema miningSchema, String fieldName) {
        if (miningSchema != null && miningSchema.getMiningFields() != null) {
            return miningSchema.getMiningFields().stream()
                    .filter(miningField -> Objects.equals(fieldName,miningField.getName()) && miningField.getOpType() != null)
                    .findFirst()
                    .map(dataField -> OP_TYPE.byName(dataField.getOpType().value()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Return <code>Optional&lt;OP_TYPE&gt;</code> of field with given <b>fieldName</b> from <code>Targets</code>
     * @param targets
     * @param fieldName
     * @return
     */
    public static Optional<OP_TYPE> getOpTypeFromTargets(Targets targets, String fieldName) {
        if (targets != null && targets.getTargets() != null) {
            return targets.getTargets().stream()
                    .filter(target -> Objects.equals(fieldName,target.getField()) && target.getOpType() != null)
                    .findFirst()
                    .map(dataField -> OP_TYPE.byName(dataField.getOpType().value()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * <code>DataType</code> of the given <b>field</b>, first looked upon <b>derivedFields</b> and then in
     * <b>dataDictionary</b>
     * @param fields
     * @param fieldName
     * @return
     */
    public static DataType getDataType(final List<Field<?>> fields,
                                       final String fieldName) {
        return fields.stream()
                .filter(fld -> Objects.equals(fieldName,fld.getName()))
                .map(Field::getDataType)
                .findFirst()
                .orElseThrow(() -> new KiePMMLInternalException(String.format("Failed to find DataType for " +
                                                                                      "field %s",
                                                                              fieldName)));
    }

    /**
     * <code>DATA_TYPE</code> of the given <b>field</b>
     * @param fields
     * @param fieldName
     * @return
     */
    public static DATA_TYPE getDATA_TYPE(final List<Field<?>> fields, String fieldName) {
        Optional<DATA_TYPE> toReturn = fields.stream()
                .filter(fld -> Objects.equals(fieldName,fld.getName()))
                .findFirst()
                .map(dataField -> DATA_TYPE.byName(dataField.getDataType().value()));
        return toReturn.orElseThrow(() -> new KiePMMLInternalException(String.format("Failed to find DATA_TYPE for " +
                                                                                             "field %s",
                                                                                     fieldName)));
    }

    /**
     * Return <code>List&lt;DerivedField&gt;</code>s from the given <code>TransformationDictionary</code> and
     * <code>LocalTransformations</code>
     * @param transformationDictionary
     * @param localTransformations
     * @return
     */
    public static List<DerivedField> getDerivedFields(final TransformationDictionary transformationDictionary,
                                                      final LocalTransformations localTransformations) {
        final List<DerivedField> toReturn = new ArrayList<>();
        if (transformationDictionary != null && transformationDictionary.getDerivedFields() != null) {
            toReturn.addAll(transformationDictionary.getDerivedFields());
        }
        if (localTransformations != null && localTransformations.getDerivedFields() != null) {
            toReturn.addAll(localTransformations.getDerivedFields());
        }
        return toReturn;
    }

    public static List<Object> getObjectsFromArray(Array source) {
        Array.Type type = source.getType();
        List<Object> toReturn = new ArrayList<>();
        String stringValue = (String) source.getValue();
        String[] valuesArray = stringValue.split(" ");
        for (String s : valuesArray) {
            switch (type) {
                case INT:
                    toReturn.add(Integer.valueOf(s));
                    break;
                case STRING:
                    toReturn.add(s);
                    break;
                case REAL:
                    toReturn.add(Double.valueOf(s));
                    break;
                default:
                    throw new KiePMMLException("Unknown Array " + type);
            }
        }
        return toReturn;
    }

    /**
     * Return a <code>List&lt;org.kie.pmml.api.models.MiningField&glt;</code> out of a <code>org.dmg.pmml
     * .MiningSchema</code> one
     * @param toConvert
     * @param fields
     * @return
     */
    public static List<org.kie.pmml.api.models.MiningField> convertToKieMiningFieldList(final MiningSchema toConvert,
                                                                                        final List<Field<?>> fields) {
        if (toConvert == null || !toConvert.hasMiningFields()) {
            return Collections.emptyList();
        }
        return toConvert.getMiningFields()
                .stream()
                .map(miningField -> {
                    Field<?> field = fields.stream()
                            .filter(fld -> fld.getName().equals(miningField.getName()))
                            .findFirst()
                            .orElseThrow(() -> new KiePMMLException("Cannot find " + miningField.getName() + " in " +
                                                                            "DataDictionary"));
                    return convertToKieMiningField(miningField, field);
                })
                .collect(Collectors.toList());
    }

    /**
     * Return a <code>org.kie.pmml.api.models.MiningField</code> out of a <code>org.dmg.pmml.MiningField</code> and
     * relative <code>org.dmg.pmml.DataField</code> ones
     * @param toConvert
     * @param field
     * @return
     */
    public static org.kie.pmml.api.models.MiningField convertToKieMiningField(final MiningField toConvert,
                                                                              final Field<?> field) {
        final String name = toConvert.getName() != null ?toConvert.getName() : null;
        final FIELD_USAGE_TYPE fieldUsageType = toConvert.getUsageType() != null ?
                FIELD_USAGE_TYPE.byName(toConvert.getUsageType().value()) : null;
        final OP_TYPE opType = toConvert.getOpType() != null ? OP_TYPE.byName(toConvert.getOpType().value()) : null;
        final DATA_TYPE dataType = field.getDataType() != null ?
                DATA_TYPE.byName(field.getDataType().value()) : null;
        final MISSING_VALUE_TREATMENT_METHOD missingValueTreatmentMethod =
                toConvert.getMissingValueTreatment() != null ?
                        MISSING_VALUE_TREATMENT_METHOD.byName(toConvert.getMissingValueTreatment().value()) : null;
        final INVALID_VALUE_TREATMENT_METHOD invalidValueTreatmentMethod =
                toConvert.getInvalidValueTreatment() != null ?
                        INVALID_VALUE_TREATMENT_METHOD.byName(toConvert.getInvalidValueTreatment().value()) : null;
        final String missingValueReplacement = toConvert.getMissingValueReplacement() != null ?
                toConvert.getMissingValueReplacement().toString() : null;
        final String invalidValueReplacement = toConvert.getInvalidValueReplacement() != null ?
                toConvert.getInvalidValueReplacement().toString() : null;
        final List<String> allowedValues = field instanceof DataField ?
                convertDataFieldValues(((DataField) field).getValues()) : Collections.emptyList();
        final List<org.kie.pmml.api.models.Interval> intervals = field instanceof DataField ?
                convertDataFieldIntervals(((DataField) field).getIntervals()) : Collections.emptyList();

        return new org.kie.pmml.api.models.MiningField(name,
                                                       fieldUsageType,
                                                       opType,
                                                       dataType,
                                                       missingValueTreatmentMethod,
                                                       invalidValueTreatmentMethod,
                                                       missingValueReplacement,
                                                       invalidValueReplacement,
                                                       allowedValues,
                                                       intervals);
    }


    /**
     * Return a <code>List&lt;org.kie.pmml.api.models.OutputField&gt;</code> out of a <code>org.dmg.pmml
     * .Output</code> one
     * @param toConvert
     * @return
     */
    public static List<org.kie.pmml.api.models.OutputField> convertToKieOutputFieldList(final Output toConvert,
                                                                                        final List<Field<?>> fields) {
        if (toConvert == null) {
            return Collections.emptyList();
        }
        return convertToKieOutputFieldList(toConvert.getOutputFields(), fields);
    }

    /**
     * Return a <code>List&lt;org.kie.pmml.api.models.OutputField&gt;</code> out of a <code>List&lt;org.dmg.pmml.OutputField&gt;</code> one
     * @param toConvert
     * @return
     */
    public static List<org.kie.pmml.api.models.OutputField> convertToKieOutputFieldList(final List<OutputField> toConvert,
                                                                                        final List<Field<?>> fields) {
        if (toConvert == null) {
            return Collections.emptyList();
        }
        return toConvert.stream()
                .map(outputField -> {
                    Field<?> field = fields.stream()
                            .filter(fld -> fld.getName().equals(outputField.getTargetField()))
                            .findFirst()
                            .orElse(null); // expected to be null because OutputField.targetField is not mandatory
                    return convertToKieOutputField(outputField, field);
                })
                .collect(Collectors.toList());
    }

    /**
     * Return a <code>org.kie.pmml.api.models.OutputField</code> out of a <code>org.dmg.pmml.OutputField</code> one
     * @param toConvert
     * @param field - this may be <code>null</code>
     * @return
     */
    public static org.kie.pmml.api.models.OutputField convertToKieOutputField(final OutputField toConvert,
                                                                              final Field<?> field) {
        final String name = toConvert.getName() != null ?toConvert.getName() : null;
        final OP_TYPE opType = toConvert.getOpType() != null ? OP_TYPE.byName(toConvert.getOpType().value()) : null;
        final DATA_TYPE dataFieldDataType = field != null ? DATA_TYPE.byName(field.getDataType().value()) :
                null;
        final DATA_TYPE dataType = toConvert.getDataType() != null ?
                DATA_TYPE.byName(toConvert.getDataType().value()) : dataFieldDataType;
        final String targetField = toConvert.getTargetField() != null ?toConvert.getTargetField() : null;
        final RESULT_FEATURE resultFeature = toConvert.getResultFeature() != null ?
                RESULT_FEATURE.byName(toConvert.getResultFeature().value()) : null;
        final List<String> allowedValues = field instanceof DataField ?
                convertDataFieldValues(((DataField) field).getValues()) : null;
        return new org.kie.pmml.api.models.OutputField(name,
                                                       opType,
                                                       dataType,
                                                       targetField,
                                                       resultFeature,
                                                       allowedValues);
    }

    /**
     * Return a <code>List&lt;org.kie.pmml.api.models.TargetField&gt;</code> out of a <code>org.dmg.pmml
     * .Targets</code>
     * @param toConvert
     * @return
     */
    public static List<TargetField> convertToKieTargetFieldList(Targets toConvert) {
        if (toConvert == null) {
            return Collections.emptyList();
        }
        return toConvert.getTargets()
                .stream()
                .map(ModelUtils::convertToKieTargetField)
                .collect(Collectors.toList());
    }

    /**
     * Return a <code>org.kie.pmml.api.models.TargetField</code> out of a <code>org.dmg.pmml.Target</code>
     * @param toConvert
     * @return
     */
    public static TargetField convertToKieTargetField(final Target toConvert) {
        final List<org.kie.pmml.api.models.TargetValue> targetValues = convertToKieTargetValueList(toConvert.getTargetValues());
        final OP_TYPE opType = toConvert.getOpType() != null ? OP_TYPE.byName(toConvert.getOpType().value()) : null;
        final CAST_INTEGER castInteger = toConvert.getCastInteger() != null ?
                CAST_INTEGER.byName(toConvert.getCastInteger().value()) : null;
        final Double min = toConvert.getMin() != null ? toConvert.getMin().doubleValue() : null;
        final Double max = toConvert.getMax() != null ? toConvert.getMax().doubleValue() : null;
        final Double rescaleConstant = toConvert.getRescaleConstant() != null ?
                toConvert.getRescaleConstant().doubleValue() : null;
        final Double rescaleFactor = toConvert.getRescaleFactor() != null ? toConvert.getRescaleFactor().doubleValue() : null;
        return new TargetField(targetValues,
                               opType,toConvert.getField(),
                               castInteger,
                               min,
                               max,
                               rescaleConstant,
                               rescaleFactor);
    }

    /**
     * Return a <code>List&lt;org.kie.pmml.api.models.TargetValue&gt;</code> out of a
     * <code>List&lt;org.dmg.pmml.TargetValue&gt;</code>
     * @param toConvert
     * @return
     */
    public static List<org.kie.pmml.api.models.TargetValue> convertToKieTargetValueList(List<TargetValue> toConvert) {
        if (toConvert == null) {
            return Collections.emptyList();
        }
        return toConvert
                .stream()
                .map(ModelUtils::convertToKieTargetValue)
                .collect(Collectors.toList());
    }

    /**
     * Return a <code>org.kie.pmml.api.models.TargetValue</code> out of a <code>org.dmg.pmml
     * .TargetValue</code>
     * @param toConvert
     * @return
     */
    public static org.kie.pmml.api.models.TargetValue convertToKieTargetValue(final TargetValue toConvert) {
        final String value = toConvert.getValue() != null ? toConvert.getValue().toString() : null;
        final String displayValue = toConvert.getDisplayValue() != null ? toConvert.getDisplayValue() : null;
        return new org.kie.pmml.api.models.TargetValue(value, displayValue,
                                                       toConvert.getPriorProbability().doubleValue(),
                                                       toConvert.getDefaultValue().doubleValue());
    }

    /**
     * Retrieve the <b>mapped</b> class name of the given <code>ParameterField</code>, <b>eventually</b> boxed (for
     * primitive ones)
     * It returns <b>Object</b> <code>ParameterField.getDataType()</code> is null
     * @param parameterField
     * @return
     */
    public static String getBoxedClassName(ParameterField parameterField) {
        return parameterField.getDataType() == null ? Object.class.getName() :
                getBoxedClassName(parameterField.getDataType());
    }

    /**
     * Retrieve the <b>mapped</b> class name of the given <code>DataType</code>, <b>eventually</b> boxed (for
     * primitive ones).
     * It returns <b>Object</b> if null
     * @param dataType
     * @return
     */
    public static String getBoxedClassName(DataType dataType) {
        Class<?> c = dataType == null ? Object.class : DATA_TYPE.byName(dataType.value()).getMappedClass();
        return getKiePMMLPrimitiveBoxed(c).map(primitiveBoxed -> primitiveBoxed.getBoxed().getName()).orElse(c.getName());
    }

    public static List<Field<?>> getFieldsFromDataDictionaryAndTransformationDictionary(final DataDictionary dataDictionary,
                                                                                        final TransformationDictionary transformationDictionary) {
        final List<Field<?>> toReturn = new ArrayList<>();
        if (dataDictionary != null && dataDictionary.hasDataFields()) {
            dataDictionary.getDataFields().stream().map(Field.class::cast)
                    .forEach(toReturn::add);
        }
        if (transformationDictionary != null && transformationDictionary.hasDerivedFields()) {
            transformationDictionary.getDerivedFields().stream().map(Field.class::cast)
                    .forEach(toReturn::add);
        }
        return toReturn;
    }

    public static List<Field<?>> getFieldsFromDataDictionaryTransformationDictionaryAndModel(final DataDictionary dataDictionary,
                                                                                             final TransformationDictionary transformationDictionary,
                                                                                             final Model model) {
        final List<Field<?>> toReturn = getFieldsFromDataDictionaryAndTransformationDictionary(dataDictionary, transformationDictionary);
        LocalTransformations localTransformations = model.getLocalTransformations();
        if (localTransformations != null && localTransformations.hasDerivedFields()) {
            localTransformations.getDerivedFields().stream().map(Field.class::cast)
                    .forEach(toReturn::add);
        }
        Output output = model.getOutput();
        if (output != null && output.hasOutputFields()) {
            output.getOutputFields().stream().map(Field.class::cast)
                    .forEach(toReturn::add);
        }
        return toReturn;
    }

    public static List<String> convertDataFieldValues(List<Value> toConvert) {
        return toConvert != null ? toConvert.stream()
                .map(value -> value.getValue().toString())
                .collect(Collectors.toList()) : null;
    }

    public static List<org.kie.pmml.api.models.Interval> convertDataFieldIntervals(List<Interval> toConvert) {
        return toConvert != null ? toConvert.stream()
                .map(interval -> new org.kie.pmml.api.models.Interval(interval.getLeftMargin(),
                                                                      interval.getRightMargin()))
                .collect(Collectors.toList()) : null;
    }

    public static Map<String, Object> getRowDataMap(Row source) {
        Map<String, Object> toReturn = new HashMap<>();
        List<Element> elements = source.getContent().stream()
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .collect(Collectors.toList());
        if (!elements.isEmpty()) {
            elements.forEach(el -> populateWithElement(toReturn, el));
        } else {
            InputCell inputCell = source.getContent().stream()
                    .filter(InputCell.class::isInstance)
                    .map(InputCell.class::cast)
                    .findFirst()
                    .orElse(null);
            OutputCell outputCell = source.getContent().stream()
                    .filter(OutputCell.class::isInstance)
                    .map(OutputCell.class::cast)
                    .findFirst()
                    .orElse(null);
            populateWithCells(toReturn, inputCell, outputCell);
        }
        return toReturn;
    }

    public static String getPrefixedName(QName qName) {
        return String.format("%s:%s", qName.getPrefix(), qName.getLocalPart());
    }

    static void populateWithElement(Map<String, Object> toPopulate, Element source) {
        toPopulate.put(source.getTagName(), source.getFirstChild().getTextContent());
    }

    static void populateWithCells(Map<String, Object> toPopulate, InputCell inputCell, OutputCell outputCell) {
        if (inputCell != null) {
            toPopulate.put(getPrefixedName(inputCell.getName()), inputCell.getValue());
        }
        if (outputCell != null) {
            toPopulate.put(getPrefixedName(outputCell.getName()), outputCell.getValue());
        }
    }
}
