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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class KiePMMLRegressionTable {

    protected Map<String, Function<Double, Double>> numericFunctionMap = new HashMap<>();
    protected Map<String, Function<Object, Double>> categoricalFunctionMap = new HashMap<>();
    protected Map<String, Function<Map<String, Object>, Double>> predictorTermsFunctionMap = new HashMap<>();
    protected double intercept;
    protected String targetField;

    public Object evaluateRegression(Map<String, Object> input) {
        final AtomicReference<Double> result = new AtomicReference<>(intercept);
        final Map<String, Double> resultMap = input.entrySet().stream()
                .filter(entry -> numericFunctionMap.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> numericFunctionMap.get(e.getKey())
                .apply(((Number) e.getValue()).doubleValue())));
        resultMap.putAll(input.entrySet().stream().filter(entry -> categoricalFunctionMap.containsKey(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, e -> categoricalFunctionMap.get(e.getKey()).apply(e.getValue()))));
        resultMap.putAll(predictorTermsFunctionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().apply(input))));
        resultMap.values().forEach(value -> result.accumulateAndGet(value, Double::sum));
        updateResult(result);
        return result.get();
    }

    public Map<String, Object> getOutputFieldsMap() {
        return Collections.unmodifiableMap(new HashMap<>());
    }

    public abstract Object getTargetCategory();

    protected void updateResult(final AtomicReference<Double> toUpdate) {
        // NONE
    }
}
