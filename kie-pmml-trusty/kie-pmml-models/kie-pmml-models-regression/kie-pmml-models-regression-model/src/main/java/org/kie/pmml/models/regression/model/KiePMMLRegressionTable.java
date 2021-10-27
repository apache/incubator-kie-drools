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
package org.kie.pmml.models.regression.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.kie.pmml.api.iinterfaces.SerializableConsumer;
import org.kie.pmml.api.iinterfaces.SerializableFunction;

public abstract class KiePMMLRegressionTable implements Serializable {

    private static final long serialVersionUID = -7899446939844650691L;
    protected Map<String, SerializableFunction<Double, Double>> numericFunctionMap = new HashMap<>();
    protected Map<String, SerializableFunction<String, Double>> categoricalFunctionMap = new HashMap<>();
    protected Map<String, Object> outputFieldsMap = new HashMap<>();
    protected Map<String, SerializableFunction<Map<String, Object>, Double>> predictorTermsFunctionMap =
            new HashMap<>();
    protected SerializableConsumer<AtomicReference<Double>> resultUpdater;
    protected double intercept;
    protected String targetField;
    protected Object targetCategory;

    public Object getTargetCategory() {
        return targetCategory;
    }

    public Object evaluateRegression(Map<String, Object> input) {
        final AtomicReference<Double> result = new AtomicReference<>(intercept);
        final Map<String, Double> resultMap = new HashMap<>();
        for (Map.Entry<String, SerializableFunction<Double, Double>> entry : numericFunctionMap.entrySet()) {
            String key = entry.getKey();
            if (input.containsKey(key)) {
                resultMap.put(key, entry.getValue().apply(((Number) input.get(key)).doubleValue()));
            }
        }
        for (Map.Entry<String, SerializableFunction<String, Double>> entry : categoricalFunctionMap.entrySet()) {
            String key = entry.getKey();
            if (input.containsKey(key)) {
                resultMap.put(key, entry.getValue().apply(input.get(key).toString()));
            }
        }
        for (Map.Entry<String, SerializableFunction<Map<String, Object>, Double>> entry :
                predictorTermsFunctionMap.entrySet()) {
            resultMap.put(entry.getKey(), entry.getValue().apply(input));
        }
        resultMap.values().forEach(value -> result.accumulateAndGet(value, Double::sum));
        if (resultUpdater != null) {
            resultUpdater.accept(result);
        }
        return result.get();
    }

    public Map<String, Object> getOutputFieldsMap() {
        return outputFieldsMap;
    }

    public String getTargetField() {
        return targetField;
    }

    public Map<String, SerializableFunction<Double, Double>> getNumericFunctionMap() {
        return numericFunctionMap;
    }

    public Map<String, SerializableFunction<String, Double>> getCategoricalFunctionMap() {
        return categoricalFunctionMap;
    }

    public Map<String, SerializableFunction<Map<String, Object>, Double>> getPredictorTermsFunctionMap() {
        return predictorTermsFunctionMap;
    }

    public double getIntercept() {
        return intercept;
    }

    public LinkedHashMap<String, Double> getProbabilityResultMap() {
        return new LinkedHashMap<>();
    }

    protected double evaluateNumericWithExponent(double input, double coefficient, double exponent) {
        // Considering exponent because it is != 1
        return Math.pow(input, exponent) * coefficient;
    }

    protected double evaluateNumericWithoutExponent(double input, double coefficient) {
        // Ignoring exponent because it is 1
        return input * coefficient;
    }

    protected double evaluateCategoricalPredictor(final Object input, final Map<String, Double> valuesMap) {
        return valuesMap.getOrDefault(input.toString(), 0.0);
    }

    protected void updateSOFTMAXResult(final AtomicReference<Double> toUpdate) {
        toUpdate.updateAndGet(y -> 1.0 / (1.0 + Math.exp(-y)));
    }

    protected void updateLOGITResult(final AtomicReference<Double> toUpdate) {
        toUpdate.updateAndGet(y -> 1.0 / (1.0 + Math.exp(-y)));
    }

    protected void updateEXPResult(final AtomicReference<Double> toUpdate) {
        toUpdate.updateAndGet(Math::exp);
    }

    protected void updatePROBITResult(final AtomicReference<Double> toUpdate) {
        toUpdate.updateAndGet(y -> new NormalDistribution().cumulativeProbability(y));
    }

    protected void updateCLOGLOGResult(final AtomicReference<Double> toUpdate) {
        toUpdate.updateAndGet(y -> 1.0 - Math.exp(-Math.exp(y)));
    }

    protected void updateCAUCHITResult(final AtomicReference<Double> toUpdate) {
        toUpdate.updateAndGet(y -> 0.5 + (1 / Math.PI) * Math.atan(y));
    }

    protected void updateNONEResult(final AtomicReference<Double> toUpdate) {
        // NO OP
    }
}
