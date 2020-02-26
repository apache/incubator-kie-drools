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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.model.predictors.KiePMMLCategoricalPredictor;
import org.kie.pmml.models.regression.model.predictors.KiePMMLNumericPredictor;
import org.kie.pmml.models.regression.model.predictors.KiePMMLPredictorTerm;
import org.kie.pmml.models.regression.model.predictors.KiePMMLRegressionTablePredictor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PMMLRegressionModelUtilsTest {

    @Test
    public void evaluateNumericPredictors() {
        KiePMMLRegressionTable regressionTable = getTable();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("fld1", 0.9);
        inputData.put("fld2", 0.3);
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("fld1", 4.050000000000001);
        expectedData.put("fld2", 0.6);
        Map<String, Double> resultMap = new HashMap<>();
        getParameterInfos(inputData).forEach(parameterInfo -> {
            PMMLRegressionModelUtils.evaluateNumericPredictors(regressionTable, parameterInfo, resultMap);
            assertTrue(resultMap.containsKey(parameterInfo.getName()));
            assertEquals(expectedData.get(parameterInfo.getName()), resultMap.get(parameterInfo.getName()));
        });
    }

    @Test
    public void evaluateCategoricalPredictors() {
        KiePMMLRegressionTable regressionTable = getTable();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("fld3", "x");
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("fld3", -3.0);
        Map<String, Double> resultMap = new HashMap<>();
        getParameterInfos(inputData).forEach(parameterInfo -> {
            PMMLRegressionModelUtils.evaluateCategoricalPredictors(regressionTable, parameterInfo, resultMap);
            assertTrue(resultMap.containsKey(parameterInfo.getName()));
            assertEquals(expectedData.get(parameterInfo.getName()), resultMap.get(parameterInfo.getName()));
        });
    }

    @Test
    public void evaluatePredictorTerms() {
        KiePMMLRegressionTable regressionTable = getTable();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("fld1", 0.9);
        inputData.put("fld2", 0.3);
        inputData.put("fld3", -3.0);
        Map<String, Double> expectedData = new HashMap<>();
        expectedData.put("firstPredictorTerm", 0.10800000000000001);
        expectedData.put("secondPredictorTerm", -1.62);
        Map<String, Double> resultMap = new HashMap<>();
        getParameterInfos(inputData);
        PMMLRegressionModelUtils.evaluatePredictorTerms(regressionTable, getParameterInfos(inputData), resultMap);
        expectedData.forEach((s, aDouble) -> {
            assertTrue(resultMap.containsKey(s));
            assertEquals(aDouble, resultMap.get(s));
        });
    }

    @Test
    public void evaluatePredictorTerm() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("fld1", 0.9);
        inputData.put("fld2", 0.3);
        inputData.put("fld3", -3.0);
        Map<String, Double> expectedData = new HashMap<>();
        expectedData.put("firstPredictorTerm", 0.10800000000000001);
        expectedData.put("secondPredictorTerm", -1.62);
        Map<String, Double> resultMap = new HashMap<>();
        for (KiePMMLPredictorTerm kiePMMLPredictorTerm : getPredictorTerms()) {
            PMMLRegressionModelUtils.evaluatePredictorTerm(kiePMMLPredictorTerm, inputData, resultMap);
            assertTrue(resultMap.containsKey(kiePMMLPredictorTerm.getName()));
            assertEquals(expectedData.get(kiePMMLPredictorTerm.getName()), resultMap.get(kiePMMLPredictorTerm.getName()));
        }
    }

    @SuppressWarnings("rawtypes")
    private List<ParameterInfo> getParameterInfos(Map<String, Object> inputData) {
        return inputData.entrySet().stream().map(entry -> getParameterInfo(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    @SuppressWarnings("rawtypes")
    private ParameterInfo getParameterInfo(String name, Object value) {
        ParameterInfo toReturn = new ParameterInfo();
        toReturn.setName(name);
        toReturn.setValue(value);
        return toReturn;
    }

    private KiePMMLRegressionTable getTable() {
        return KiePMMLRegressionTable.builder("TABLE", Collections.emptyList(), 0.5)
                .withTargetCategory("clerical")
                .withNumericPredictors(Arrays.stream(getNumericPredictors()).collect(Collectors.toSet()))
                .withCategoricalPredictors(Arrays.stream(getCategoricalPredictors()).collect(Collectors.toSet()))
                .withPredictorTerms(Arrays.stream(getPredictorTerms()).collect(Collectors.toSet()))
                .build();
    }

    private KiePMMLNumericPredictor[] getNumericPredictors() {
        return new KiePMMLNumericPredictor[]{
                new KiePMMLNumericPredictor("fld1", 2, 5, Collections.emptyList()),
                new KiePMMLNumericPredictor("fld2", 1, 2, Collections.emptyList())};
    }

    private KiePMMLCategoricalPredictor[] getCategoricalPredictors() {
        return new KiePMMLCategoricalPredictor[]{
                new KiePMMLCategoricalPredictor("fld3", "x", -3, Collections.emptyList()),
                new KiePMMLCategoricalPredictor("fld3", "y", 3, Collections.emptyList())};
    }

    private KiePMMLPredictorTerm[] getPredictorTerms() {
        KiePMMLNumericPredictor[] numericPredictors = getNumericPredictors();
        KiePMMLCategoricalPredictor[] categoricalPredictors = getCategoricalPredictors();
        List<KiePMMLRegressionTablePredictor> firstPredictors = Arrays.stream(numericPredictors).collect(Collectors.toList());
        KiePMMLPredictorTerm firstPredictorTerm = new KiePMMLPredictorTerm("firstPredictorTerm", firstPredictors, 0.4, Collections.emptyList());
        List<KiePMMLRegressionTablePredictor> secondPredictors = Arrays.asList(numericPredictors[0], categoricalPredictors[0]);
        KiePMMLPredictorTerm secondPredictorTerm = new KiePMMLPredictorTerm("secondPredictorTerm", secondPredictors, 0.6, Collections.emptyList());
        return new KiePMMLPredictorTerm[]{
                firstPredictorTerm,
                secondPredictorTerm};
    }
}