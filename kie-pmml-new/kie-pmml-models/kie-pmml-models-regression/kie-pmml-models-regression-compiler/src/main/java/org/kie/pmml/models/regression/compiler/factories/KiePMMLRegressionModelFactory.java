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
package org.kie.pmml.models.regression.compiler.factories;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.regression.RegressionModel;
import org.kie.pmml.commons.exceptions.KieEnumException;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.api.model.enums.MODEL_TYPE;
import org.kie.pmml.models.regression.api.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetField;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableFactory.getRegressionTables;

public class KiePMMLRegressionModelFactory {

    private static final Logger log = LoggerFactory.getLogger(KiePMMLRegressionModelFactory.class.getName());

    private KiePMMLRegressionModelFactory() {
    }

    public static KiePMMLRegressionModel getKiePMMLRegressionModel(DataDictionary dataDictionary, RegressionModel model) throws KiePMMLException {
        log.info("getKiePMMLModel {}", model);
        String name = model.getModelName();
        Optional<String> targetFieldName = getTargetField(model);
        final Optional<DataField> targetDataField = dataDictionary.getDataFields().stream()
                .filter(field -> Objects.equals(targetFieldName.orElse(null), field.getName().getValue()))
                .findFirst();
        List<KiePMMLOutputField> outputFields = null;
        if (model.getOutput() != null) {
            outputFields = model.getOutput().getOutputFields().stream().map(throwingFunctionWrapper(KiePMMLRegressionModelFactory::getKiePMMLOutputField)).collect(Collectors.toList());
        }
        final OP_TYPE opType = targetDataField
                .map(throwingFunctionWrapper(field -> OP_TYPE.byName(field.getOpType().value()))).orElseThrow(() -> new KiePMMLException("Failed to find OpType for TargetField"));

        return KiePMMLRegressionModel.builder(name, MINING_FUNCTION.byName(model.getMiningFunction().value()), getRegressionTables(model.getRegressionTables()), opType)
                .withAlgorithmName(model.getAlgorithmName())
                .withModelType(model.getModelType() != null ? MODEL_TYPE.byName(model.getModelType().value()) : null)
                .withRegressionNormalizationMethod(REGRESSION_NORMALIZATION_METHOD.byName(model.getNormalizationMethod().value()))
                .withScorable(model.isScorable())
                .withTargetField(targetFieldName.orElse(null))
                .withOutputFields(outputFields)
                .build();
    }

    private static KiePMMLOutputField getKiePMMLOutputField(OutputField outputField) throws KieEnumException {
        return KiePMMLOutputField.builder(outputField.getName().getValue())
                .withResultFeature(RESULT_FEATURE.byName(outputField.getResultFeature().value()))
                .withTargetField(outputField.getTargetField().getValue())
                .withValue(outputField.getValue())
                .build();
    }
}
