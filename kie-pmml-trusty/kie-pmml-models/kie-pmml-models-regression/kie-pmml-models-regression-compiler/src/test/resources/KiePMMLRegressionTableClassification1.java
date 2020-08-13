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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.pmml.models.regression.model.KiePMMLRegressionClassificationTable;

public class KiePMMLRegressionTableClassification1 extends KiePMMLRegressionClassificationTable {

    public KiePMMLRegressionTableClassification1() {
        targetField = "targetField";
        categoryTableMap.put("clerical", new KiePMMLRegressionTableRegression2());
        categoryTableMap.put("professional", new KiePMMLRegressionTableRegression1());
    }

    @Override
    public Object getTargetCategory() {
        return null;
    }

    @Override
    protected void populateOutputFieldsMap(final Map.Entry<String, Double> predictedEntry, final LinkedHashMap<String, Double> probabilityMap) {
        outputFieldsMap.put("CAT-1", probabilityMap.get("CatPred-1"));
        outputFieldsMap.put("NUM-1", probabilityMap.get("NumPred-0"));
        outputFieldsMap.put("PREV", predictedEntry.getKey());
    }

    protected LinkedHashMap<String, Double> getProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> tmp = resultMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Math.exp(entry.getValue()), (o1, o2) -> o1, LinkedHashMap::new));
        double sum = tmp.values().stream().mapToDouble(value -> value).sum();
        return tmp.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() / sum, (o1, o2) -> o1, LinkedHashMap::new));
    }
}
