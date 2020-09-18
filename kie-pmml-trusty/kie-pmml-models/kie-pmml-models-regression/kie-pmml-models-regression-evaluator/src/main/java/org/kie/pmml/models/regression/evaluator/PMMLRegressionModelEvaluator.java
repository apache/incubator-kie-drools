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
package org.kie.pmml.models.regression.evaluator;

import java.util.Map;

import org.drools.core.util.StringUtils;
import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.models.regression.model.KiePMMLRegressionClassificationTable;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;

import static org.kie.pmml.commons.enums.ResultCode.OK;
import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

public class PMMLRegressionModelEvaluator implements PMMLModelEvaluator<KiePMMLRegressionModel> {

    private static final String INVALID_NORMALIZATION_METHOD = "Invalid Normalization Method %s";
    private static final String EXPECTED_AT_LEAST_TWO_REGRESSION_TABLES_RETRIEVED = "Expected at least two " +
            "RegressionTables, retrieved %s";
    private static final String EXPECTED_TWO_REGRESSION_TABLES_RETRIEVED = "Expected two RegressionTables, retrieved " +
            "%s";
    private static final String TARGET_FIELD_REQUIRED_RETRIEVED = "TargetField required, retrieved %s";
    private static final String INVALID_TARGET_TYPE = "Invalid target type %s";

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.REGRESSION_MODEL;
    }

    @Override
    public PMML4Result evaluate(final KieBase knowledgeBase,
                                final KiePMMLRegressionModel model,
                                final PMMLContext pmmlContext) {
        validate(model);
        PMML4Result toReturn = new PMML4Result();
        String targetField = model.getTargetField();
        final Map<String, Object> requestData =
                getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        Object result = model.evaluate(knowledgeBase, requestData);
        toReturn.addResultVariable(targetField, result);
        toReturn.setResultObjectName(targetField);
        toReturn.setResultCode(OK.getName());
        model.getOutputFieldsMap().forEach(toReturn::addResultVariable);
        return toReturn;
    }

    private void validate(final KiePMMLRegressionModel toValidate) {
        if (toValidate.getRegressionTable() == null) {
            throw new KiePMMLModelException("At least one RegressionTable required");
        }
        final KiePMMLRegressionTable regressionTable = toValidate.getRegressionTable();

        if (regressionTable instanceof KiePMMLRegressionClassificationTable) {
            validateClassification((KiePMMLRegressionClassificationTable) regressionTable);
        } else {
            validateRegression(regressionTable);
        }
    }

    private void validateRegression(final KiePMMLRegressionTable toValidate) {
        if (toValidate.getTargetField() == null || StringUtils.isEmpty(toValidate.getTargetField().trim())) {
            throw new KiePMMLInternalException(String.format(TARGET_FIELD_REQUIRED_RETRIEVED,
                                                             toValidate.getTargetField()));
        }
    }

    private void validateClassification(KiePMMLRegressionClassificationTable toValidate) {
        switch (toValidate.getOpType()) {
            case CATEGORICAL:
                validateClassificationCategorical(toValidate);
                break;
            case ORDINAL:
                validateClassificationOrdinal(toValidate);
                break;
            default:
                throw new KiePMMLModelException(String.format(INVALID_TARGET_TYPE, toValidate.getOpType()));
        }
    }

    private void validateClassificationCategorical(KiePMMLRegressionClassificationTable toValidate) {
        if (toValidate.isBinary()) {
            validateClassificationCategoricalBinary(toValidate);
        } else {
            validateClassificationCategoricalNotBinary(toValidate);
        }
    }

    private void validateClassificationCategoricalBinary(KiePMMLRegressionClassificationTable toValidate) {
        switch (toValidate.getRegressionNormalizationMethod()) {
            case LOGIT:
            case PROBIT:
            case CAUCHIT:
            case CLOGLOG:
            case LOGLOG:
            case NONE:
                if (toValidate.getCategoryTableMap().size() != 2) {
                    throw new KiePMMLModelException(String.format(EXPECTED_TWO_REGRESSION_TABLES_RETRIEVED,
                                                                  toValidate.getCategoryTableMap().size()));
                }
                return;
            default:
                throw new KiePMMLModelException(String.format(INVALID_NORMALIZATION_METHOD,
                                                              toValidate.getRegressionNormalizationMethod()));
        }
    }

    private void validateClassificationCategoricalNotBinary(KiePMMLRegressionClassificationTable toValidate) {
        switch (toValidate.getRegressionNormalizationMethod()) {
            case SOFTMAX:
            case SIMPLEMAX:
            case NONE:
                if (toValidate.getCategoryTableMap().size() < 2) {
                    throw new KiePMMLModelException(String.format(EXPECTED_AT_LEAST_TWO_REGRESSION_TABLES_RETRIEVED,
                                                                  toValidate.getCategoryTableMap().size()));
                }
                return;
            default:
                throw new KiePMMLModelException(String.format(INVALID_NORMALIZATION_METHOD,
                                                              toValidate.getRegressionNormalizationMethod()));
        }
    }

    private void validateClassificationOrdinal(KiePMMLRegressionClassificationTable toValidate) {
        switch (toValidate.getRegressionNormalizationMethod()) {
            case LOGIT:
            case PROBIT:
            case CAUCHIT:
            case CLOGLOG:
            case LOGLOG:
            case NONE:
                if (toValidate.getCategoryTableMap().size() < 2) {
                    throw new KiePMMLModelException(String.format(EXPECTED_AT_LEAST_TWO_REGRESSION_TABLES_RETRIEVED,
                                                                  toValidate.getCategoryTableMap().size()));
                }
                return;
            default:
                throw new KiePMMLModelException(String.format(INVALID_NORMALIZATION_METHOD,
                                                              toValidate.getRegressionNormalizationMethod()));
        }
    }
}
