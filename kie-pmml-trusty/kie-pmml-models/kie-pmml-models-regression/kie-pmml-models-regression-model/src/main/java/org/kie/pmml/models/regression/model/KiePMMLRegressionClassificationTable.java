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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleUnaryOperator;

import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;

import static org.kie.pmml.commons.Constants.EXPECTED_TWO_ENTRIES_RETRIEVED;

public abstract class KiePMMLRegressionClassificationTable extends KiePMMLRegressionTable {

    protected REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod;
    protected OP_TYPE opType;
    protected Map<String, KiePMMLRegressionTable> categoryTableMap = new LinkedHashMap<>(); // Insertion order matters

    @Override
    public Object evaluateRegression(Map<String, Object> input) {
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        for (Map.Entry<String, KiePMMLRegressionTable> entry : categoryTableMap.entrySet()) {
            resultMap.put(entry.getKey(), (Double) entry.getValue().evaluateRegression(input));
        }
        final LinkedHashMap<String, Double> probabilityMap = getProbabilityMap(resultMap);
        final Map.Entry<String, Double> predictedEntry = Collections.max(probabilityMap.entrySet(), Map.Entry.comparingByValue());
        probabilityMap.put(targetField, predictedEntry.getValue());
        populateOutputFieldsMapWithProbability(predictedEntry, probabilityMap);
        return predictedEntry.getKey();
    }

    /**
     * A <b>Classification</b> is considered <b>binary</b> if it is of <b>CATEGORICAL</b> type and contains <b>exactly</b> two Regression tables
     * @return
     */
    public abstract boolean isBinary();

    protected abstract LinkedHashMap<String, Double> getProbabilityMap(final LinkedHashMap<String, Double> resultMap);

    protected abstract void populateOutputFieldsMapWithProbability(final Map.Entry<String, Double> predictedEntry, final LinkedHashMap<String, Double> probabilityMap);

    protected void updateResult(final AtomicReference<Double> toUpdate) {
        // NOOP
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

    protected LinkedHashMap<String, Double> getProbabilityMap(final LinkedHashMap<String, Double> resultMap, DoubleUnaryOperator firstItemOperator, DoubleUnaryOperator secondItemOperator) {
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
}
