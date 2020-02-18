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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.Model;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.commons.model.tuples.KiePMMLNameOpType;

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
    public static Optional<String> getTargetField(DataDictionary dataDictionary, Model model) {
        return getTargetFields(dataDictionary, model).stream().map(KiePMMLNameOpType::getName).findFirst();
    }

    public static List<KiePMMLNameOpType> getTargetFields(DataDictionary dataDictionary, Model model) {
        if (model.getTargets() != null && model.getTargets().getTargets() != null) {
            return model.getTargets().getTargets().stream()
                    .map(target -> {
                        OP_TYPE opType = target.getOpType() != null ? OP_TYPE.byName(target.getOpType().value()) : getOpType(dataDictionary, model, target.getField().getValue());
                        return new KiePMMLNameOpType(target.getField().getValue(), opType);
                    }).collect(Collectors.toList());
        } else {
            return model.getMiningSchema().getMiningFields().stream()
                    .filter(miningField -> MiningField.UsageType.TARGET.equals(miningField.getUsageType()) || MiningField.UsageType.PREDICTED.equals(miningField.getUsageType()))
                    .map(miningField -> {
                        OP_TYPE opType = miningField.getOpType() != null ? OP_TYPE.byName(miningField.getOpType().value()) : getOpType(dataDictionary, model, miningField.getName().getValue());
                        return new KiePMMLNameOpType(miningField.getName().getValue(), opType);
                    })
                    .collect(Collectors.toList());
        }
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
}
