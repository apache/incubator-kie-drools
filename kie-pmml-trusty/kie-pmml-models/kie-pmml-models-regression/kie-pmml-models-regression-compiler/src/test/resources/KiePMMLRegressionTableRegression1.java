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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;

public class KiePMMLRegressionTableRegression1 extends KiePMMLRegressionTable {

    public KiePMMLRegressionTableRegression1() {
        intercept = 3.5;
        targetField = "targetField";
        numericFunctionMap.put("NumPred-2", this::evaluateNumericPredictor4);
        numericFunctionMap.put("NumPred-3", this::evaluateNumericPredictor1);
        numericFunctionMap.put("NumPred-0", this::evaluateNumericPredictor2);
        numericFunctionMap.put("NumPred-1", this::evaluateNumericPredictor3);
        categoricalFunctionMap.put("CatPred-2", this::evaluateCategoricalPredictor1);
        categoricalFunctionMap.put("CatPred-1", this::evaluateCategoricalPredictor2);
        categoricalFunctionMap.put("CatPred-0", this::evaluateCategoricalPredictor3);
        predictorTermsFunctionMap.put("PredTerm-2", this::evaluatePredictorTerm3);
        predictorTermsFunctionMap.put("PredTerm-0", this::evaluatePredictorTerm1);
        predictorTermsFunctionMap.put("PredTerm-1", this::evaluatePredictorTerm2);
    }

    @Override
    public Object getTargetCategory() {
        return "professional";
    }

    @Override
    protected void updateResult(final AtomicReference<Double> toUpdate) {
        toUpdate.updateAndGet(y -> 0.5 + (1 / Math.PI) * Math.atan(y));
    }

    private double evaluateNumericPredictor1(double input) {
        double coefficient = 32.55;
        // Ignoring exponent because it is 1
        return input * coefficient;
    }

    private double evaluateNumericPredictor2(double input) {
        double coefficient = 13.11;
        double exponent = 2.0;
        // Considering exponent because it is != 1
        return Math.pow(input, exponent) * coefficient;
    }

    private double evaluateNumericPredictor3(double input) {
        double coefficient = 13.11;
        double exponent = 2.0;
        // Considering exponent because it is != 1
        return Math.pow(input, exponent) * coefficient;
    }

    private double evaluateNumericPredictor4(double input) {
        double coefficient = 13.11;
        double exponent = 2.0;
        // Considering exponent because it is != 1
        return Math.pow(input, exponent) * coefficient;
    }

    private double evaluateCategoricalPredictor1(Object input) {
        if (Objects.equals(27.12, input))
            return 3.46;
        else if (Objects.equals(27.12, input))
            return 3.46;
        else
            return 0.0;
    }

    private double evaluateCategoricalPredictor2(Object input) {
        if (Objects.equals(27.12, input))
            return 3.46;
        else if (Objects.equals(27.12, input))
            return 3.46;
        else
            return 0.0;
    }

    private double evaluateCategoricalPredictor3(Object input) {
        if (Objects.equals(27.12, input))
            return 3.46;
        else if (Objects.equals(27.12, input))
            return 3.46;
        else
            return 0.0;
    }

    private double evaluatePredictorTerm1(Map<String, Object> resultMap) {
        final AtomicReference<Double> result = new AtomicReference<>(1.0);
        List<String> fieldRefs = Arrays.asList("CatPred-0", "NumPred-3");
        fieldRefs.forEach(fldRef -> {
            if (resultMap.containsKey(fldRef)) {
                result.set(result.get() * (Double) resultMap.get(fldRef));
            }
        });
        double coefficient = 32.29;
        return result.get() * coefficient;
    }

    private double evaluatePredictorTerm2(Map<String, Object> resultMap) {
        final AtomicReference<Double> result = new AtomicReference<>(1.0);
        List<String> fieldRefs = Arrays.asList("CatPred-0", "NumPred-3");
        fieldRefs.forEach(fldRef -> {
            if (resultMap.containsKey(fldRef)) {
                result.set(result.get() * (Double) resultMap.get(fldRef));
            }
        });
        double coefficient = 32.29;
        return result.get() * coefficient;
    }

    private double evaluatePredictorTerm3(Map<String, Object> resultMap) {
        final AtomicReference<Double> result = new AtomicReference<>(1.0);
        List<String> fieldRefs = Arrays.asList("CatPred-0", "NumPred-3");
        fieldRefs.forEach(fldRef -> {
            if (resultMap.containsKey(fldRef)) {
                result.set(result.get() * (Double) resultMap.get(fldRef));
            }
        });
        double coefficient = 32.29;
        return result.get() * coefficient;
    }
}
