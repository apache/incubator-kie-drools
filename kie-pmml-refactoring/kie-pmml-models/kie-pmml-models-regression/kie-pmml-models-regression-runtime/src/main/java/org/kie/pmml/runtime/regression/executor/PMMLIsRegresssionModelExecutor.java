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
package org.kie.pmml.runtime.regression.executor;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.drools.core.util.StringUtils;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLCategoricalPredictor;
import org.kie.pmml.runtime.api.exceptions.KiePMMLModelException;
import org.kie.pmml.runtime.api.executor.PMMLContext;

import static org.kie.pmml.api.interfaces.FunctionalWrapperFactory.throwingConsumerWrapper;

public class PMMLIsRegresssionModelExecutor {

    public static PMML4Result evaluateRegression(KiePMMLRegressionModel regressionModel, PMMLContext context) throws KiePMMLException {
        final List<KiePMMLRegressionTable> regressionTables = regressionModel.getRegressionTables();
        if (regressionModel.getTargetField() == null || StringUtils.isEmpty(regressionModel.getTargetField().trim())) {
            throw new KiePMMLException("TargetField required, retrieved " + regressionModel.getTargetField());
        }
        if (regressionTables.size() != 1) {
            throw new KiePMMLModelException("Expected one RegressionTable, retrieved " + regressionTables.size());
        }
        return evaluateRegression(regressionModel.getTargetField(), regressionTables.get(0), context.getRequestData());
    }

    public static PMML4Result evaluateRegression(String targetFieldName, KiePMMLRegressionTable regressionTable, PMMLRequestData requestData) throws KiePMMLException {
        final AtomicReference<Double> result = new AtomicReference<>(regressionTable.getIntercept().doubleValue());
        requestData.getRequestParams().forEach(throwingConsumerWrapper(parameterInfo -> {
            evaluateNumericPredictors(regressionTable, parameterInfo, result);
            evaluateCategoricalPredictors(regressionTable, parameterInfo, result);
        }));
        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(targetFieldName, result.get());
        toReturn.setResultObjectName(targetFieldName);
        return toReturn;
    }

    @SuppressWarnings("rawtypes")
    private static void evaluateNumericPredictors(KiePMMLRegressionTable regressionTable, ParameterInfo parameterInfo, AtomicReference<Double> result) throws KiePMMLException {
        regressionTable.getKiePMMLNumericPredictorByName(parameterInfo.getName())
                .ifPresent(throwingConsumerWrapper(kiePMMLRegressionTablePredictor ->
                                                           result.accumulateAndGet(kiePMMLRegressionTablePredictor.evaluate(parameterInfo.getValue()), Double::sum)));
    }

    @SuppressWarnings("rawtypes")
    private static void evaluateCategoricalPredictors(KiePMMLRegressionTable regressionTable, ParameterInfo parameterInfo, AtomicReference<Double> result) throws KiePMMLException {
        regressionTable.getKiePMMLCategoricalPredictorsByName(parameterInfo.getName())
                .ifPresent(throwingConsumerWrapper(predictors -> evaluateCategoricalPredictors(predictors, parameterInfo, result)));
    }

    @SuppressWarnings("rawtypes")
    private static void evaluateCategoricalPredictors(List<KiePMMLCategoricalPredictor> predictors, ParameterInfo parameterInfo, AtomicReference<Double> result) throws KiePMMLException {
        predictors
                .forEach(throwingConsumerWrapper(kiePMMLRegressionTablePredictor ->
                                                         result.accumulateAndGet(kiePMMLRegressionTablePredictor.evaluate(parameterInfo.getValue()), Double::sum)));
    }
}
