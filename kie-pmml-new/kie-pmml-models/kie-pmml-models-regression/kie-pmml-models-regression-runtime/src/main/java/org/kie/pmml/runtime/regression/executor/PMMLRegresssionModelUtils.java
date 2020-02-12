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

import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLCategoricalPredictor;

import static org.kie.pmml.commons.interfaces.FunctionalWrapperFactory.throwingConsumerWrapper;

public class PMMLRegresssionModelUtils {

    private PMMLRegresssionModelUtils() {
        // Avoid instantiation
    }

    @SuppressWarnings("rawtypes")
    public static void evaluateNumericPredictors(KiePMMLRegressionTable regressionTable, ParameterInfo parameterInfo, AtomicReference<Double> result) throws KiePMMLException {
        regressionTable.getKiePMMLNumericPredictorByName(parameterInfo.getName())
                .ifPresent(throwingConsumerWrapper(kiePMMLRegressionTablePredictor ->
                                                           result.accumulateAndGet(kiePMMLRegressionTablePredictor.evaluate(parameterInfo.getValue()), Double::sum)));
    }

    @SuppressWarnings("rawtypes")
    public static void evaluateCategoricalPredictors(KiePMMLRegressionTable regressionTable, ParameterInfo parameterInfo, AtomicReference<Double> result) throws KiePMMLException {
        regressionTable.getKiePMMLCategoricalPredictorsByName(parameterInfo.getName())
                .ifPresent(throwingConsumerWrapper(predictors -> evaluateCategoricalPredictors(predictors, parameterInfo, result)));
    }

    @SuppressWarnings("rawtypes")
    public static void evaluateCategoricalPredictors(List<KiePMMLCategoricalPredictor> predictors, ParameterInfo parameterInfo, AtomicReference<Double> result) throws KiePMMLException {
        predictors
                .forEach(throwingConsumerWrapper(kiePMMLRegressionTablePredictor ->
                                                         result.accumulateAndGet(kiePMMLRegressionTablePredictor.evaluate(parameterInfo.getValue()), Double::sum)));
    }
}
