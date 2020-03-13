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
package org.kie.pmml.models.regression.compiler.executor;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.OpType;
import org.dmg.pmml.regression.RegressionModel;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.commons.model.tuples.KiePMMLNameOpType;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionModelFactory;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.commons.utils.ModelUtils.getOpType;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFields;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Regression</b>
 */
public class RegressionModelImplementationProvider implements ModelImplementationProvider<RegressionModel, KiePMMLRegressionModel> {

    private static final Logger logger = LoggerFactory.getLogger(RegressionModelImplementationProvider.class.getName());
    private static final String INVALID_NORMALIZATION_METHOD = "Invalid Normalization Method ";

    @Override
    public PMML_MODEL getPMMLModelType() {
        logger.debug("getPMMLModelType");
        return PMML_MODEL.REGRESSION_MODEL;
    }

    @Override
    public KiePMMLRegressionModel getKiePMMLModel(DataDictionary dataDictionary, RegressionModel model, Object kBuilder) {
        logger.debug("getKiePMMLModel {} {}", dataDictionary, model);
        validate(dataDictionary, model);
        try {
            return KiePMMLRegressionModelFactory.getKiePMMLRegressionModel(dataDictionary, model);
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    protected void validate(DataDictionary dataDictionary, RegressionModel toValidate) {
        if (toValidate.getRegressionTables() == null || toValidate.getRegressionTables().isEmpty()) {
            throw new KiePMMLException("At least one RegressionTable required");
        }
        if (isRegression(toValidate)) {
            List<KiePMMLNameOpType> targetFields = getTargetFields(dataDictionary, toValidate);
            validateRegression(targetFields, toValidate);
        } else {
            validateClassification(dataDictionary, toValidate);
        }
    }

    private void validateRegression(List<KiePMMLNameOpType> targetFields, RegressionModel toValidate) {
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

    private void validateClassification(DataDictionary dataDictionary, RegressionModel toValidate) {
        final String categoricalTargeName = getCategoricalTargetName(dataDictionary, toValidate);
        final OP_TYPE opType = getOpType(dataDictionary, toValidate, categoricalTargeName);
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

    private void validateClassificationCategorical(DataDictionary dataDictionary, RegressionModel toValidate, String categoricalFieldName) {
        if (isBinary(dataDictionary, categoricalFieldName)) {
            validateClassificationCategoricalBinary(toValidate);
        } else {
            validateClassificationCategoricalNotBinary(toValidate);
        }
    }

    private void validateClassificationCategoricalBinary(RegressionModel toValidate) {
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

    private void validateClassificationCategoricalNotBinary(RegressionModel toValidate) {
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

    private void validateClassificationOrdinal(RegressionModel toValidate) {
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

    private void validateRegressionTargetField(List<KiePMMLNameOpType> targetFields, RegressionModel toValidate) {
        if (targetFields.size() != 1) {
            throw new KiePMMLException("Expected one target field, retrieved " + targetFields.size());
        }
        if (toValidate.getTargetField() != null && !(Objects.equals(toValidate.getTargetField().getValue(), targetFields.get(0).getName()))) {
            throw new KiePMMLException(String.format("Not-matching target fields: %s %s", toValidate.getTargetField(), targetFields.get(0).getName()));
        }
    }

    private boolean isRegression(RegressionModel toValidate) {
        return Objects.equals(MiningFunction.REGRESSION, toValidate.getMiningFunction());
    }

    private boolean isBinary(DataDictionary dataDictionary, String categoricalFieldName) {
        return dataDictionary.getDataFields().stream()
                .filter(dataField -> Objects.equals(dataField.getName().getValue(), categoricalFieldName)).mapToDouble(dataField -> dataField.getValues().size())
                .findFirst().orElse(0) == 2;
    }

    private String getCategoricalTargetName(DataDictionary dataDictionary, RegressionModel toValidate) {
        List<KiePMMLNameOpType> targetFields = getTargetFields(dataDictionary, toValidate);
        final List<String> categoricalFields = dataDictionary.getDataFields().stream()
                .filter(dataField -> OpType.CATEGORICAL.equals(dataField.getOpType()))
                .map(dataField -> dataField.getName().getValue())
                .collect(Collectors.toList());
        final List<KiePMMLNameOpType> categoricalNameTypes = targetFields.stream().filter(targetField -> categoricalFields.contains(targetField.getName())).collect(Collectors.toList());
        if (categoricalNameTypes.size() != 1) {
            throw new KiePMMLException(String.format("Expected exactly one categorical targets, found %s", categoricalNameTypes.size()));
        }
        return categoricalNameTypes.get(0).getName();
    }
}
