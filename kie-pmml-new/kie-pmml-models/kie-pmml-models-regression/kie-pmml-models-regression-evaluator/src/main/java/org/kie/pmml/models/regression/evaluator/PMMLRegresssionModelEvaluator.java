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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.api.model.enums.REGRESSION_NORMALIZATION_METHOD;

import static org.kie.pmml.commons.enums.StatusCode.OK;

public class PMMLRegresssionModelEvaluator {

    private PMMLRegresssionModelEvaluator() {
        // Avoid instantiation
    }

    public static PMML4Result evaluateRegression(KiePMMLRegressionModel regressionModel, PMMLContext context) {
        final List<KiePMMLRegressionTable> regressionTables = regressionModel.getRegressionTables();
        return evaluateRegression(regressionModel.getTargetField(), regressionModel.getRegressionNormalizationMethod(), regressionTables.get(0), context.getRequestData());
    }

    public static PMML4Result evaluateRegression(String targetFieldName, REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod, KiePMMLRegressionTable regressionTable, PMMLRequestData requestData) {
        final AtomicReference<Double> result = new AtomicReference<>(regressionTable.getIntercept().doubleValue());
        Map<String, Double> resultMap = new HashMap<>();
        requestData.getRequestParams().forEach(parameterInfo -> {
            PMMLRegresssionModelUtils.evaluateNumericPredictors(regressionTable, parameterInfo, resultMap);
            PMMLRegresssionModelUtils.evaluateCategoricalPredictors(regressionTable, parameterInfo, resultMap);
        });
        PMMLRegresssionModelUtils.evaluatePredictorTerms(regressionTable, requestData.getRequestParams(), resultMap);
        resultMap.values().forEach(value -> result.accumulateAndGet(value, Double::sum));
        if (regressionNormalizationMethod != null) {
            switch (regressionNormalizationMethod) {
                case SOFTMAX:
                case LOGIT:
                    result.updateAndGet(y -> 1.0 / (1.0 + Math.exp(-y)));
                    break;
                case EXP:
                    result.updateAndGet(Math::exp);
                    break;
                case PROBIT:
                    result.updateAndGet(y -> new NormalDistribution().cumulativeProbability(y));
                    break;
                case CLOGLOG:
                    result.updateAndGet(y -> 1.0 - Math.exp(-Math.exp(y)));
                    break;
                case CAUCHIT:
                    result.updateAndGet(y -> 0.5 + (1 / Math.PI) * Math.atan(y));
                    break;
                case NONE:
                default:
                    // no op
            }
        }
        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(targetFieldName, result.get());
        toReturn.setResultObjectName(targetFieldName);
        toReturn.setResultCode(OK.getName());
        return toReturn;
    }
}
