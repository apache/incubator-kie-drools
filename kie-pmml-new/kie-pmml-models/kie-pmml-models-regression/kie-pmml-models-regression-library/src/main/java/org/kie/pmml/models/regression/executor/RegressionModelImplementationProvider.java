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
package org.kie.pmml.models.regression.executor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.OpType;
import org.dmg.pmml.regression.RegressionModel;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.library.api.implementations.ModelImplementationProvider;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.factories.KiePMMLRegressionModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;
import static org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel.PMML_MODEL_TYPE;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Regression</b>
 */
public class RegressionModelImplementationProvider implements ModelImplementationProvider<RegressionModel, KiePMMLRegressionModel> {

    private static final Logger log = LoggerFactory.getLogger(RegressionModelImplementationProvider.class.getName());
    private static final String INVALID_NORMALIZATION_METHOD = "Invalid Normalization Method ";

    @Override
    public PMML_MODEL getPMMLModelType() {
        log.info("getPMMLModelType");
        return PMML_MODEL_TYPE;
    }

    @Override
    public KiePMMLRegressionModel getKiePMMLModel(DataDictionary dataDictionary, RegressionModel model, Object kBuilder) throws KiePMMLException {
        log.info("getKiePMMLModel {} {}", dataDictionary, model);
        validate(dataDictionary, model);
        return KiePMMLRegressionModelFactory.getKiePMMLRegressionModel(dataDictionary, model);
    }

    protected void validate(DataDictionary dataDictionary, RegressionModel toValidate) throws KiePMMLException {
        if (toValidate.getRegressionTables() == null || toValidate.getRegressionTables().isEmpty()) {
            throw new KiePMMLException("At least one RegressionTable required");
        }
        if (isRegression(toValidate)) {
            List<NameOpType> targetFields = getTargetFields(dataDictionary, toValidate);
            validateRegression(targetFields, toValidate);
        } else {
            validateClassification(dataDictionary, toValidate);
        }
    }

    private void validateRegression(List<NameOpType> targetFields, RegressionModel toValidate) throws KiePMMLException {
        validateRegressionTargetField(targetFields, toValidate);
        if (toValidate.getRegressionTables().size() != 1) {
            throw new KiePMMLException("Expected one RegressionTable, retrieved " + toValidate.getRegressionTables().size());
        }
        switch (toValidate.getNormalizationMethod()) {
            case NONE:
            case SOFTMAX:
            case LOGIT:
            case EXP:
            case PROBIT:
            case CLOGLOG:
            case LOGLOG:
            case CAUCHIT:
                return;
            default:
                throw new KiePMMLException(INVALID_NORMALIZATION_METHOD + toValidate.getNormalizationMethod());
        }
    }

    private void validateClassification(DataDictionary dataDictionary, RegressionModel toValidate) throws KiePMMLException {
        final String categoricalTargeName = getCategoricalTargetName(dataDictionary, toValidate);
        final OpType opType = getOpType(dataDictionary, categoricalTargeName);
        switch (opType) {
            case CATEGORICAL:
                validateClassificationCategorical(dataDictionary, toValidate, categoricalTargeName);
                break;
            case ORDINAL:
                validateClassificationOrdinal(toValidate);
                break;
            default:
                throw new KiePMMLException("Invalid target type " + opType);
        }
    }

    private void validateClassificationCategorical(DataDictionary dataDictionary, RegressionModel toValidate, String categoricalFieldName) throws KiePMMLException {
        if (isBinary(dataDictionary, categoricalFieldName)) {
            validateClassificationCategoricalBinary(toValidate);
        } else {
            validateClassificationCategoricalNotBinary(toValidate);
        }
    }

    private void validateClassificationCategoricalBinary(RegressionModel toValidate) throws KiePMMLException {
        switch (toValidate.getNormalizationMethod()) {
            case LOGIT:
            case PROBIT:
            case CAUCHIT:
            case CLOGLOG:
            case LOGLOG:
            case NONE:
                if (toValidate.getRegressionTables().size() != 2) {
                    throw new KiePMMLException("Expected two RegressionTables, retrieved " + toValidate.getRegressionTables().size());
                }
                return;
            default:
                throw new KiePMMLException(INVALID_NORMALIZATION_METHOD + toValidate.getNormalizationMethod());
        }
    }

    private void validateClassificationCategoricalNotBinary(RegressionModel toValidate) throws KiePMMLException {
        switch (toValidate.getNormalizationMethod()) {
            case SOFTMAX:
            case SIMPLEMAX:
                if (toValidate.getRegressionTables().size() < 2) {
                    throw new KiePMMLException("Expected at least two RegressionTables, retrieved " + toValidate.getRegressionTables().size());
                }
                return;
            case NONE:
                if (toValidate.getRegressionTables().size() < 3) {
                    throw new KiePMMLException("Expected three RegressionTables, retrieved " + toValidate.getRegressionTables().size());
                }
                return;
            default:
                throw new KiePMMLException(INVALID_NORMALIZATION_METHOD + toValidate.getNormalizationMethod());
        }
    }

    private void validateClassificationOrdinal(RegressionModel toValidate) throws KiePMMLException {
        switch (toValidate.getNormalizationMethod()) {
            case LOGIT:
            case PROBIT:
            case CAUCHIT:
            case CLOGLOG:
            case LOGLOG:
            case NONE:
                if (toValidate.getRegressionTables().size() < 2) {
                    throw new KiePMMLException("Expected at least two RegressionTables, retrieved " + toValidate.getRegressionTables().size());
                }
                return;
            default:
                throw new KiePMMLException(INVALID_NORMALIZATION_METHOD + toValidate.getNormalizationMethod());
        }
    }

    private void validateRegressionTargetField(List<NameOpType> targetFields, RegressionModel toValidate) throws KiePMMLException {
        if (targetFields.size() != 1) {
            throw new KiePMMLException("Expected one target field, retrieved " + targetFields.size());
        }
        if (toValidate.getTargetField() != null && !(Objects.equals(toValidate.getTargetField().getValue(), targetFields.get(0).name))) {
            throw new KiePMMLException(String.format("Not-matching target fields: %s %s", toValidate.getTargetField(), targetFields.get(0).name));
        }
    }

    private boolean isRegression(RegressionModel toValidate) {
        return Objects.equals(MiningFunction.REGRESSION, toValidate.getMiningFunction());
    }

    private boolean isBinary(DataDictionary dataDictionary, String categoricalFieldName) {
        return dataDictionary.getDataFields().stream()
                .filter(dataField -> Objects.equals(dataField.getName().getValue(), categoricalFieldName))
                .map(DataField::getValues).count() == 2;
    }

    private String getCategoricalTargetName(DataDictionary dataDictionary, RegressionModel toValidate) throws KiePMMLException {
        List<NameOpType> targetFields = getTargetFields(dataDictionary, toValidate);
        final List<String> categoricalFields = dataDictionary.getDataFields().stream()
                .filter(dataField -> OpType.CATEGORICAL.equals(dataField.getOpType()))
                .map(dataField -> dataField.getName().getValue())
                .collect(Collectors.toList());
        final List<NameOpType> categoricalNameTypes = targetFields.stream().filter(targetField -> categoricalFields.contains(targetField.name)).collect(Collectors.toList());
        if (categoricalNameTypes.size() != 1) {
            throw new KiePMMLException(String.format("Expected exactly one categorical targets, found %s", categoricalNameTypes.size()));
        }
        return categoricalNameTypes.get(0).name;
    }

    private List<NameOpType> getTargetFields(DataDictionary dataDictionary, RegressionModel toValidate) throws KiePMMLException {
        if (toValidate.getTargets() != null && toValidate.getTargets().getTargets() != null) {
            return toValidate.getTargets().getTargets().stream()
                    .map(throwingFunctionWrapper(target -> {
                        OpType opType = target.getOpType() != null ? target.getOpType() : getOpType(dataDictionary, target.getField().getValue());
                        return new NameOpType(target.getField().getValue(), opType);
                    })).collect(Collectors.toList());
        } else {
            return toValidate.getMiningSchema().getMiningFields().stream()
                    .filter(miningField -> MiningField.UsageType.TARGET.equals(miningField.getUsageType()) || MiningField.UsageType.PREDICTED.equals(miningField.getUsageType()))
                    .map(throwingFunctionWrapper(miningField -> {
                        OpType opType = miningField.getOpType() != null ? miningField.getOpType() : getOpType(dataDictionary, miningField.getName().getValue());
                        return new NameOpType(miningField.getName().getValue(), opType);
                    }))
                    .collect(Collectors.toList());
        }
    }

    private OpType getOpType(DataDictionary dataDictionary, String targetFieldName) throws KiePMMLException {
        return dataDictionary.getDataFields().stream()
                .filter(dataField -> Objects.equals(targetFieldName, dataField.getName().getValue()))
                .findFirst()
                .map(DataField::getOpType)
                .orElseThrow(() -> new KiePMMLException(String.format("Failed to find OpType for field %s", targetFieldName)));
    }

    private class NameOpType {

        private final String name;
        private final OpType opType;

        public NameOpType(String name, OpType opType) {
            this.name = name;
            this.opType = opType;
        }
    }
}
