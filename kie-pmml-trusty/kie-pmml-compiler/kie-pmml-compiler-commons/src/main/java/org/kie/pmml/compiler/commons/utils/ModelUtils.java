/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.compiler.commons.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Interval;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.Output;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.Target;
import org.dmg.pmml.Value;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameOpType;

import static org.kie.pmml.api.utils.PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed;

/**
 * Class to provide common methods to interact with <code>Model</code>, to convert <b>org.dmn.pmml</b> objects to
 * <b>Kie</b> ones, etc...
 */
public class ModelUtils {

    private static final String INFINITY_SYMBOL = new String(Character.toString('\u221E').getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

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
     * @param model
     * @return
     */
    public static Optional<String> getTargetFieldName(DataDictionary dataDictionary, Model model) {
        return getTargetFields(dataDictionary, model).stream().map(KiePMMLNameOpType::getName).findFirst();
    }

    /**
     * Return the <code>DATA_TYPE</code>> of the field whose <b>usageType</b> is <code>TARGET</code> or
     * <code>PREDICTED</code>.
     * It throws exception if none of such fields are found
     * <p>
     * While the xsd schema does not strictly enforce this, it seems that <b>by convention</b> majority of models has
     * only one target.
     * <p>
     * (see https://github.com/jpmml/jpmml-evaluator/issues/64 discussion)
     * @param dataDictionary
     * @param model
     * @return
     */
    public static DATA_TYPE getTargetFieldType(DataDictionary dataDictionary, Model model) {
        return getTargetFieldsTypeMap(dataDictionary, model).entrySet().iterator().next().getValue();
    }

    /**
     * Return a <code>List&lt;KiePMMLNameOpType&gt;</code> of target fields
     * @param dataDictionary
     * @param model
     * @return
     */
    public static List<KiePMMLNameOpType> getTargetFields(DataDictionary dataDictionary, Model model) {
        List<KiePMMLNameOpType> toReturn = new ArrayList<>();
        if (model.getTargets() != null && model.getTargets().getTargets() != null) {
            for (Target target : model.getTargets().getTargets()) {
                OP_TYPE opType = target.getOpType() != null ? OP_TYPE.byName(target.getOpType().value()) :
                        getOpType(dataDictionary, model, target.getField().getValue());
                toReturn.add(new KiePMMLNameOpType(target.getField().getValue(), opType));
            }
        } else {
            for (MiningField miningField : model.getMiningSchema().getMiningFields()) {
                if (MiningField.UsageType.TARGET.equals(miningField.getUsageType()) || MiningField.UsageType.PREDICTED.equals(miningField.getUsageType())) {
                    OP_TYPE opType = miningField.getOpType() != null ?
                            OP_TYPE.byName(miningField.getOpType().value()) : getOpType(dataDictionary, model,
                                                                                        miningField.getName().getValue());

                    toReturn.add(new KiePMMLNameOpType(miningField.getName().getValue(), opType));
                }
            }
        }
        return toReturn;
    }

    /**
     * Returns a <code>Map&lt;String, DATA_TYPE&gt;</code> of target fields, where the key is the name of the field,
     * and the value is the <b>type</b> of the field
     * @param dataDictionary
     * @param model
     * @return
     */
    public static Map<String, DATA_TYPE> getTargetFieldsTypeMap(DataDictionary dataDictionary, Model model) {
        Map<String, DATA_TYPE> toReturn = new LinkedHashMap<>();
        if (model.getTargets() != null && model.getTargets().getTargets() != null) {
            for (Target target : model.getTargets().getTargets()) {
                toReturn.put(target.getField().getValue(), getDataType(dataDictionary, target.getField().getValue()));
            }
        } else {
            for (MiningField miningField : model.getMiningSchema().getMiningFields()) {
                if (MiningField.UsageType.TARGET.equals(miningField.getUsageType()) || MiningField.UsageType.PREDICTED.equals(miningField.getUsageType())) {
                    toReturn.put(miningField.getName().getValue(), getDataType(dataDictionary,
                                                                               miningField.getName().getValue()));
                }
            }
        }
        return toReturn;
    }

    /**
     * <code>OP_TYPE</code> may be defined inside <code>DataField</code>, <code>MiningField</code> or both.
     * In the latter case, <code>MiningField</code> override <code>DataField</code> definition
     * @param dataDictionary
     * @param model
     * @param targetFieldName
     * @return
     */
    public static OP_TYPE getOpType(DataDictionary dataDictionary, Model model, String targetFieldName) {
        Optional<OP_TYPE> toReturn = model.getMiningSchema()
                .getMiningFields().stream()
                .filter(dataField -> Objects.equals(targetFieldName, dataField.getName().getValue()) && dataField.getOpType() != null)
                .findFirst()
                .map(dataField -> OP_TYPE.byName(dataField.getOpType().value()));
        if (!toReturn.isPresent()) {
            toReturn = dataDictionary.getDataFields().stream()
                    .filter(dataField -> Objects.equals(targetFieldName, dataField.getName().getValue()) && dataField.getOpType() != null)
                    .findFirst()
                    .map(dataField -> OP_TYPE.byName(dataField.getOpType().value()));
        }
        return toReturn.orElseThrow(() -> new KiePMMLInternalException(String.format("Failed to find OpType for field" +
                                                                                             " %s", targetFieldName)));
    }

    /**
     * <code>DATA_TYPE</code> of the given <b>field</b>
     * @param dataDictionary
     * @param targetFieldName
     * @return
     */
    public static DATA_TYPE getDataType(DataDictionary dataDictionary, String targetFieldName) {
        Optional<DATA_TYPE> toReturn = dataDictionary.getDataFields().stream()
                .filter(dataField -> Objects.equals(targetFieldName, dataField.getName().getValue()))
                .findFirst()
                .map(dataField -> DATA_TYPE.byName(dataField.getDataType().value()));
        return toReturn.orElseThrow(() -> new KiePMMLInternalException(String.format("Failed to find DataType for " +
                                                                                             "field %s",
                                                                                     targetFieldName)));
    }

    /**
     * Return a <code>List&lt;org.kie.pmml.api.models.MiningField&glt;</code> out of a <code>org.dmg.pmml
     * .MiningSchema</code> one
     * @param toConvert
     * @param dataDictionary
     * @return
     */
    public static List<org.kie.pmml.api.models.MiningField> convertToKieMiningFieldList(final MiningSchema toConvert,
                                                                                        final DataDictionary dataDictionary) {
        if (toConvert == null) {
            return Collections.emptyList();
        }
        return toConvert.getMiningFields()
                .stream()
                .map(miningField -> {
                    DataField dataField = dataDictionary.getDataFields().stream()
                            .filter(df -> df.getName().equals(miningField.getName()))
                            .findFirst()
                            .orElseThrow(() -> new KiePMMLException("Cannot find " + miningField.getName() + " in " +
                                                                            "DataDictionary"));
                    return convertToKieMiningField(miningField, dataField);
                })
                .collect(Collectors.toList());
    }

    /**
     * Return a <code>org.kie.pmml.api.models.MiningField</code> out of a <code>org.dmg.pmml.MiningField</code> and relative <code>org.dmg.pmml.DataField</code> ones
     * @param toConvert
     * @param dataField
     * @return
     */
    public static org.kie.pmml.api.models.MiningField convertToKieMiningField(final MiningField toConvert,
                                                                              final DataField dataField) {
        final String name = toConvert.getName() != null ? toConvert.getName().getValue() : null;
        final FIELD_USAGE_TYPE fieldUsageType = toConvert.getUsageType() != null ?
                FIELD_USAGE_TYPE.byName(toConvert.getUsageType().value()) : null;
        final OP_TYPE opType = toConvert.getOpType() != null ? OP_TYPE.byName(toConvert.getOpType().value()) : null;
        final DATA_TYPE dataType = dataField.getDataType() != null ?
                DATA_TYPE.byName(dataField.getDataType().value()) : null;
        final String missingValueReplacement = toConvert.getMissingValueReplacement() != null ?
                toConvert.getMissingValueReplacement().toString() : null;
        final List<String> allowedValues = convertDataFieldValues(dataField.getValues());
        final List<org.kie.pmml.api.models.Interval> intervals = convertDataFieldIntervals(dataField.getIntervals());
        return new org.kie.pmml.api.models.MiningField(name,
                                                       fieldUsageType,
                                                       opType,
                                                       dataType,
                                                       missingValueReplacement,
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
                                                                                        final DataDictionary dataDictionary) {
        if (toConvert == null) {
            return Collections.emptyList();
        }
        return toConvert.getOutputFields()
                .stream()
                .map(outputField -> {
                    DataField dataField = dataDictionary.getDataFields().stream()
                            .filter(df -> df.getName().equals(outputField.getTargetField()))
                            .findFirst()
                            .orElse(null); // expected to be null because OutputField.targetField is not mandatory
                    return convertToKieOutputField(outputField, dataField);
                })
                .collect(Collectors.toList());
    }

    /**
     * Return a <code>org.kie.pmml.api.models.OutputField</code> out of a <code>org.dmg.pmml.OutputField</code> one
     * @param toConvert
     * @param dataField - this may be <code>null</code>
     * @return
     */
    public static org.kie.pmml.api.models.OutputField convertToKieOutputField(final OutputField toConvert,
                                                                              final DataField dataField) {
        final String name = toConvert.getName() != null ? toConvert.getName().getValue() : null;
        final OP_TYPE opType = toConvert.getOpType() != null ? OP_TYPE.byName(toConvert.getOpType().value()) : null;
        final DATA_TYPE dataFieldDataType = dataField != null ? DATA_TYPE.byName(dataField.getDataType().value()) : null;
        final DATA_TYPE dataType = toConvert.getDataType() != null ?
                DATA_TYPE.byName(toConvert.getDataType().value()) : dataFieldDataType;
        final String targetField = toConvert.getTargetField() != null ? toConvert.getTargetField().getValue() : null;
        final RESULT_FEATURE resultFeature = toConvert.getResultFeature() != null ?
                RESULT_FEATURE.byName(toConvert.getResultFeature().value()) : null;
        final List<String> allowedValues = dataField != null ? convertDataFieldValues(dataField.getValues()) : null;
        return new org.kie.pmml.api.models.OutputField(name,
                                                       opType,
                                                       dataType,
                                                       targetField,
                                                       resultFeature,
                                                       allowedValues);
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

    static List<String> convertDataFieldValues(List<Value> toConvert) {
        return toConvert != null ? toConvert.stream()
                .map(value -> value.getValue().toString())
                .collect(Collectors.toList()) : null;
    }

    static List<org.kie.pmml.api.models.Interval> convertDataFieldIntervals(List<Interval> toConvert) {
        return toConvert != null ? toConvert.stream()
                .map(interval -> new org.kie.pmml.api.models.Interval(interval.getLeftMargin(), interval.getRightMargin()))
                .collect(Collectors.toList()) : null;

    }

}
