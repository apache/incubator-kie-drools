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

import java.util.Collection;
import java.util.Map;

import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLPredictorTerm;

import static org.kie.pmml.commons.interfaces.FunctionalWrapperFactory.throwingConsumerWrapper;
import static org.kie.pmml.runtime.core.utils.Converter.getUnwrappedParametersMap;

public class PMMLRegresssionModelUtils {

    private PMMLRegresssionModelUtils() {
        // Avoid instantiation
    }

    @SuppressWarnings("rawtypes")
    public static void evaluateNumericPredictors(KiePMMLRegressionTable regressionTable, ParameterInfo parameterInfo, Map<String, Double> resultMap) throws KiePMMLException {
        regressionTable.getKiePMMLNumericPredictorByName(parameterInfo.getName())
                .ifPresent(throwingConsumerWrapper(kiePMMLRegressionTablePredictor -> resultMap.put(kiePMMLRegressionTablePredictor.getName(), kiePMMLRegressionTablePredictor.evaluate(parameterInfo.getValue()))));
    }

    @SuppressWarnings("rawtypes")
    public static void evaluateCategoricalPredictors(KiePMMLRegressionTable regressionTable, ParameterInfo parameterInfo, Map<String, Double> resultMap) {
        regressionTable.getKiePMMLCategoricalPredictorByNameAndValue(parameterInfo.getName(), parameterInfo.getValue())
                .ifPresent(throwingConsumerWrapper(kiePMMLRegressionTablePredictor -> resultMap.put(kiePMMLRegressionTablePredictor.getName(), kiePMMLRegressionTablePredictor.evaluate(parameterInfo.getValue()))));
    }

    public static void evaluatePredictorTerms(KiePMMLRegressionTable regressionTable, Collection<ParameterInfo> parameterInfos, Map<String, Double> resultMap) throws KiePMMLException {
        regressionTable.getPredictorTerms()
                .forEach(throwingConsumerWrapper(predictor -> evaluatePredictorTerm(predictor, getUnwrappedParametersMap(parameterInfos), resultMap)));
    }

    public static void evaluatePredictorTerm(KiePMMLPredictorTerm predictorTerm, Map<String, Object> parameterInfos, Map<String, Double> resultMap) throws KiePMMLException {
        resultMap.put(predictorTerm.getName(), predictorTerm.evaluate(parameterInfos));
    }
}
