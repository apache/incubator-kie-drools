/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.regression.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.iinterfaces.SerializableFunction;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;

import static org.kie.pmml.commons.Constants.EXPECTED_TWO_ENTRIES_RETRIEVED;

public final class KiePMMLClassificationTable extends AbstractKiePMMLTable {

    private static final long serialVersionUID = 458989873257189359L;
    private REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod;
    private OP_TYPE opType;
    private Map<String, KiePMMLRegressionTable> categoryTableMap = new LinkedHashMap<>(); // Insertion order matters
    private SerializableFunction<LinkedHashMap<String, Double>, LinkedHashMap<String, Double>> probabilityMapFunction; // Insertion order matters
    private boolean isBinary;

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    @Override
    public Object evaluateRegression(final Map<String, Object> input, final PMMLRuntimeContext context) {
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        for (Map.Entry<String, KiePMMLRegressionTable> entry : categoryTableMap.entrySet()) {
            resultMap.put(entry.getKey(), (Double) entry.getValue().evaluateRegression(input, context));
        }
        context.setProbabilityResultMap(probabilityMapFunction.apply(resultMap));
        final Map.Entry<String, Double> predictedEntry = Collections.max(context.getProbabilityResultMap().entrySet(),
                                                                         Map.Entry.comparingByValue());
        return predictedEntry.getKey();
    }

    /**
     * A <b>Classification</b> is considered <b>binary</b> if it is of <b>CATEGORICAL</b> type and contains
     * <b>exactly</b> two Regression tables
     *
     * @return
     */
    public boolean isBinary() {
        return isBinary;
    }

    public REGRESSION_NORMALIZATION_METHOD getRegressionNormalizationMethod() {
        return regressionNormalizationMethod;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    public Map<String, KiePMMLRegressionTable> getCategoryTableMap() {
        return categoryTableMap;
    }

    private KiePMMLClassificationTable(String name, List<KiePMMLExtension> extensions) {
        // Keeping private to implement Builder pattern
        super(name, extensions);
    }

    public static LinkedHashMap<String, Double> getProbabilityMap(final LinkedHashMap<String, Double> resultMap,
                                                                  DoubleUnaryOperator firstItemOperator,
                                                                  DoubleUnaryOperator secondItemOperator) {
        if (resultMap.size() != 2) {
            throw new KiePMMLException(String.format(EXPECTED_TWO_ENTRIES_RETRIEVED, resultMap.size()));
        }
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        String[] resultMapKeys = resultMap.keySet().toArray(new String[0]);
        double firstItem = firstItemOperator.applyAsDouble(resultMap.get(resultMapKeys[0]));
        double secondItem = secondItemOperator.applyAsDouble(firstItem);
        toReturn.put(resultMapKeys[0], firstItem);
        toReturn.put(resultMapKeys[1], secondItem);
        return toReturn;
    }

    public static LinkedHashMap<String, Double> getSOFTMAXProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        AtomicReference<Double> sumCounter = new AtomicReference<>(0.0);
        for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
            double toPut = Math.exp(entry.getValue());
            toReturn.put(entry.getKey(), toPut);
            sumCounter.accumulateAndGet(toPut, Double::sum);
        }
        double sum = sumCounter.get();
        for (Map.Entry<String, Double> entry : toReturn.entrySet()) {
            entry.setValue(entry.getValue() / sum);
        }
        return toReturn;
    }

    public static LinkedHashMap<String, Double> getSIMPLEMAXProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        AtomicReference<Double> sumCounter = new AtomicReference<>(0.0);
        for (Double toAdd : resultMap.values()) {
            sumCounter.accumulateAndGet(toAdd, Double::sum);
        }
        double sum = sumCounter.get();
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
            toReturn.put(entry.getKey(), entry.getValue() / sum);
        }
        return toReturn;
    }

    public static LinkedHashMap<String, Double> getNONEProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        String[] resultMapKeys = resultMap.keySet().toArray(new String[0]);
        AtomicReference<Double> sumCounter = new AtomicReference<>(0.0);
        for (int i = 0; i < resultMap.size(); i++) {
            String key = resultMapKeys[i];
            double value = resultMap.get(key);
            if (i < resultMapKeys.length - 1) {
                sumCounter.accumulateAndGet(value, Double::sum);
                toReturn.put(key, value);
            } else { // last element
                toReturn.put(key, 1 - sumCounter.get());
            }
        }
        return toReturn;
    }

    public static LinkedHashMap<String, Double> getNONEBinaryProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        String[] resultMapKeys = resultMap.keySet().toArray(new String[0]);
        String key = resultMapKeys[0];
        double value = java.lang.Math.max(0.0, Math.min(1.0, resultMap.get(key)));
        toReturn.put(key, value);
        toReturn.put(resultMapKeys[1], 1 - value);
        return toReturn;
    }

    public static LinkedHashMap<String, Double> getLOGITProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        DoubleUnaryOperator firstItemOperator = aDouble -> 1 / (1 + Math.exp(0 - aDouble));
        DoubleUnaryOperator secondItemOperator = aDouble -> 1 - aDouble;
        return getProbabilityMap(resultMap, firstItemOperator, secondItemOperator);
    }

    public static LinkedHashMap<String, Double> getPROBITProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        DoubleUnaryOperator firstItemOperator = aDouble -> new NormalDistribution().cumulativeProbability(aDouble);
        DoubleUnaryOperator secondItemOperator = aDouble -> 1 - aDouble;
        return getProbabilityMap(resultMap, firstItemOperator, secondItemOperator);
    }

    public static LinkedHashMap<String, Double> getCLOGLOGProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        DoubleUnaryOperator firstItemOperator = aDouble -> 1 - Math.exp(0 - Math.exp(aDouble));
        DoubleUnaryOperator secondItemOperator = aDouble -> 1 - aDouble;
        return getProbabilityMap(resultMap, firstItemOperator, secondItemOperator);
    }

    public static LinkedHashMap<String, Double> getCAUCHITProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        DoubleUnaryOperator firstItemOperator = aDouble -> 0.5 + (1 / Math.PI) * Math.atan(aDouble);
        DoubleUnaryOperator secondItemOperator = aDouble -> 1 - aDouble;
        return getProbabilityMap(resultMap, firstItemOperator, secondItemOperator);
    }

    public static class Builder extends AbstractKiePMMLTable.Builder<KiePMMLClassificationTable> {

        protected Builder(String name, List<KiePMMLExtension> extensions) {
            super("KiePMMLRegressionClassificationTable-", () -> new KiePMMLClassificationTable(name,
                                                                                                extensions));
        }

        public Builder withRegressionNormalizationMethod(REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod) {
            toBuild.regressionNormalizationMethod = regressionNormalizationMethod;
            return this;
        }

        public Builder withOpType(OP_TYPE opType) {
            toBuild.opType = opType;
            return this;
        }

        /**
         * @param categoryTableMap Keep in mind that insertion order matters, so provide an ordered map
         * @return
         */
        public Builder withCategoryTableMap(Map<String, KiePMMLRegressionTable> categoryTableMap) {
            if (categoryTableMap != null) {
                toBuild.categoryTableMap.putAll(categoryTableMap);
            }
            return this;
        }

        /**
         * @param probabilityMapFunction Keep in mind that insertion order matters, so provide an ordered map
         *                               inside the <code>SerializableFunction</code>
         * @return
         */
        public Builder withProbabilityMapFunction(SerializableFunction<LinkedHashMap<String, Double>,
                LinkedHashMap<String, Double>> probabilityMapFunction) {
            toBuild.probabilityMapFunction = probabilityMapFunction;
            return this;
        }

        public Builder withIsBinary(Boolean isBinary) {
            if (isBinary != null) {
                toBuild.isBinary = isBinary;
            }
            return this;
        }
    }
}
