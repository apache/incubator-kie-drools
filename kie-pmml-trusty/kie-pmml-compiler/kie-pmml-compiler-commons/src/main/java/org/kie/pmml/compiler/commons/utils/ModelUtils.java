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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.Model;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.Target;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.commons.model.tuples.KiePMMLNameOpType;

import static org.kie.pmml.commons.utils.PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed;

/**
 * Class to provide common methods to interact with <code>Model</code>
 */
public class ModelUtils {

    private ModelUtils() {
    }

    /**
     * Return an <code>Optional</code> with the name of the field whose <b>usageType</b> is <code>TARGET</code> or <code>PREDICTED</code>
     * <p>
     * While the xsd schema does not strictly enforce this, it seems that <b>by convention</b> majority of models has only one target.
     * <p>
     * (see https://github.com/jpmml/jpmml-evaluator/issues/64 discussion)
     * @param model
     * @return
     */
    public static Optional<String> getTargetFieldName(DataDictionary dataDictionary, Model model) {
        return getTargetFields(dataDictionary, model).stream().map(KiePMMLNameOpType::getName).findFirst();
    }

    /**
     * Return the <code>DATA_TYPE</code>> of the field whose <b>usageType</b> is <code>TARGET</code> or <code>PREDICTED</code>.
     * It throws exception if none of such fields are found
     * <p>
     * While the xsd schema does not strictly enforce this, it seems that <b>by convention</b> majority of models has only one target.
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
                OP_TYPE opType = target.getOpType() != null ? OP_TYPE.byName(target.getOpType().value()) : getOpType(dataDictionary, model, target.getField().getValue());
                toReturn.add(new KiePMMLNameOpType(target.getField().getValue(), opType));
            }
        } else {
            for (MiningField miningField : model.getMiningSchema().getMiningFields()) {
                if (MiningField.UsageType.TARGET.equals(miningField.getUsageType()) || MiningField.UsageType.PREDICTED.equals(miningField.getUsageType())) {
                    OP_TYPE opType = miningField.getOpType() != null ? OP_TYPE.byName(miningField.getOpType().value()) : getOpType(dataDictionary, model, miningField.getName().getValue());

                    toReturn.add(new KiePMMLNameOpType(miningField.getName().getValue(), opType));
                }
            }
        }
        return toReturn;
    }

    /**
     * Returns a <code>Map&lt;String, DATA_TYPE&gt;</code> of target fields, where the key is the name of the field, and the value is the <b>type</b> of the field
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
                    toReturn.put(miningField.getName().getValue(), getDataType(dataDictionary, miningField.getName().getValue()));
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
        return toReturn.orElseThrow(() -> new KiePMMLInternalException(String.format("Failed to find OpType for field %s", targetFieldName)));
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
        return toReturn.orElseThrow(() -> new KiePMMLInternalException(String.format("Failed to find DataType for field %s", targetFieldName)));
    }

    /**
     * Retrieve the <b>mapped</b> class name of the given <code>ParameterField</code>, <b>eventually</b> boxed (for primitive ones)
     * @param parameterField
     * @return
     */
    public static String getBoxedClassName(ParameterField parameterField) {
        return  parameterField.getDataType() == null ? Object.class.getName() : getBoxedClassName(parameterField.getDataType());
    }

    /**
     * Retrieve the <b>mapped</b> class name of the given <code>DataType</code>, <b>eventually</b> boxed (for primitive ones)
     * @param dataType
     * @return
     */
    public static String getBoxedClassName(DataType dataType) {
        Class<?> c = DATA_TYPE.byName(dataType.value()).getMappedClass();
        return getKiePMMLPrimitiveBoxed(c).map(primitiveBoxed -> primitiveBoxed.getBoxed().getName()).orElse(c.getName());
    }
}
