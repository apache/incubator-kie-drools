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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.enums.OP_TYPE;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.api.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.runtime.api.exceptions.KiePMMLModelException;
import org.kie.pmml.runtime.api.executor.PMMLContext;

import static org.kie.pmml.api.enums.StatusCode.OK;
import static org.kie.pmml.api.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;

public class PMMLClassificationModelEvaluator {

    private PMMLClassificationModelEvaluator() {
        // Avoid instantiation
    }

    public static PMML4Result evaluateClassification(KiePMMLRegressionModel regressionModel, PMMLContext context) throws KiePMMLException {
        return evaluateClassification(regressionModel.getTargetField(), regressionModel.getRegressionNormalizationMethod(), regressionModel.getTargetOpType(), regressionModel.getRegressionTables(), context.getRequestData());
    }

    protected static PMML4Result evaluateClassification(String targetFieldName, REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod,  OP_TYPE opType,  final List<KiePMMLRegressionTable> regressionTables, PMMLRequestData requestData) throws KiePMMLException {
        final LinkedHashMap<String, Double> resultMap = regressionTables.stream()
                .collect(Collectors.toMap(kiePMMLRegressionTable -> kiePMMLRegressionTable.getTargetCategory().toString(),
                                          throwingFunctionWrapper(kiePMMLRegressionTable -> {
                                              PMML4Result retrieved = PMMLRegresssionModelEvaluator.evaluateRegression(kiePMMLRegressionTable.getTargetCategory().toString(),
                                                                                                                       regressionNormalizationMethod,
                                                                                                                       kiePMMLRegressionTable,
                                                                                                                       requestData);
                                              return (Double) retrieved.getResultVariables().get(retrieved.getResultObjectName());
                                          }),
                                          (o1, o2) -> o1,
                                          (Supplier<LinkedHashMap<String, Double>>) LinkedHashMap::new));
        final Map<String, Double> probabilityMap = getProbabilityMap(regressionNormalizationMethod, opType, resultMap);
        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(targetFieldName, probabilityMap);
        toReturn.setResultObjectName(targetFieldName);
        toReturn.setResultCode(OK.getName());
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getProbabilityMap(REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod, OP_TYPE opType, final LinkedHashMap<String, Double> resultMap) throws KiePMMLModelException {
        final LinkedHashMap<String, Double> toReturn;
        final double sum;
        final Map.Entry<String, Double>[] entrySet;
        final AtomicReference<Double> sumCounter;
        switch (regressionNormalizationMethod) {
            case SOFTMAX:
                Map<String, Double> tmp = resultMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                 entry -> Math.exp(entry.getValue())));
                sum = tmp.values().stream().mapToDouble(value -> value).sum();
                toReturn = tmp.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                            entry -> entry.getValue() / sum,
                                                                            (o1, o2) -> o1,
                                                                            (Supplier<LinkedHashMap<String, Double>>) LinkedHashMap::new));
                break;
            case SIMPLEMAX:
                sum = resultMap.values().stream().mapToDouble(value -> value).sum();
                toReturn = resultMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                                  entry -> entry.getValue() / sum,
                                                                                  (o1, o2) -> o1,
                                                                                  (Supplier<LinkedHashMap<String, Double>>) LinkedHashMap::new));
                break;
            case NONE:
                switch (opType) {
                    case CATEGORICAL:
                        toReturn = new LinkedHashMap<>();
                        entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
                        sumCounter = new AtomicReference<Double>(0.0);
                        IntStream.range(0, resultMap.size()).forEach(index -> {
                            Map.Entry<String, Double> entry = entrySet[index];
                            if (index < entrySet.length - 1) {
                                sumCounter.accumulateAndGet(entry.getValue(), Double::sum);
                                toReturn.put(entry.getKey(), entry.getValue());
                            } else { // last element
                                toReturn.put(entry.getKey(), 1 - sumCounter.get());
                            }
                        });
                        break;
                    case ORDINAL:
                        toReturn = new LinkedHashMap<>();
                        entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
                        sumCounter = new AtomicReference<Double>(0.0);
                        IntStream.range(0, resultMap.size()).forEach(index -> {
                            Map.Entry<String, Double> entry = entrySet[index];
                            if (index == 0) {
                                toReturn.put(entry.getKey(), entry.getValue());
                                sumCounter.set(entry.getValue());
                            } else if (index < entrySet.length - 1) {
                                double y = entry.getValue() - sumCounter.get();
                                toReturn.put(entry.getKey(), y);
                                sumCounter.set(y);
                            } else { // last element
                                toReturn.put(entry.getKey(), 1 - sumCounter.get());
                            }
                        });
                        break;
                    default:
                        throw new KiePMMLModelException("Unexpected opType " + opType);
                }
                break;
            case LOGIT:
                switch (opType) {
                    case CATEGORICAL:
                        if (resultMap.size() != 2) {
                            throw new KiePMMLModelException("Expected two entries, retrieved " + resultMap.size());
                        }
                        toReturn = new LinkedHashMap<>();
                        entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
                        Map.Entry<String, Double> entry = entrySet[0];
                        double y = 1 / (1 + Math.exp(0 - entry.getValue()));
                        toReturn.put(entry.getKey(), y);
                        entry = entrySet[1];
                        toReturn.put(entry.getKey(), 1 - y);
                        break;
                    case ORDINAL:
                        toReturn = new LinkedHashMap<>();
                        entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
                        sumCounter = new AtomicReference<Double>(0.0);
                        IntStream.range(0, resultMap.size()).forEach(index -> {
                            Map.Entry<String, Double> entry1 = entrySet[index];
                            if (index == 0) {
                                double y1 = 1 / (1 + Math.exp(0 - entry1.getValue()));
                                toReturn.put(entry1.getKey(), y1);
                                sumCounter.set(y1);
                            } else if (index < entrySet.length - 1) {
                                double y1 = (1 / (1 + Math.exp(0 - entry1.getValue()))) - sumCounter.get();
                                toReturn.put(entry1.getKey(), y1);
                                sumCounter.set(y1);
                            } else { // last element
                                toReturn.put(entry1.getKey(), 1 - sumCounter.get());
                            }
                        });
                        break;
                    default:
                        throw new KiePMMLModelException("Unexpected opType " + opType);
                }
                break;
            case PROBIT:
                switch (opType) {
                    case CATEGORICAL:
                        if (resultMap.size() != 2) {
                            throw new KiePMMLModelException("Expected two entries, retrieved " + resultMap.size());
                        }
                        toReturn = new LinkedHashMap<>();
                        entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
                        Map.Entry<String, Double> entry = entrySet[0];
                        double y = new NormalDistribution().cumulativeProbability(entry.getValue());
                        toReturn.put(entry.getKey(), y);
                        entry = entrySet[1];
                        toReturn.put(entry.getKey(), 1 - y);
                        break;
                    case ORDINAL:
                        toReturn = new LinkedHashMap<>();
                        entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
                        sumCounter = new AtomicReference<Double>(0.0);
                        IntStream.range(0, resultMap.size()).forEach(index -> {
                            Map.Entry<String, Double> entry1 = entrySet[index];
                            if (index == 0) {
                                double y1 = new NormalDistribution().cumulativeProbability(entry1.getValue());
                                toReturn.put(entry1.getKey(), y1);
                                sumCounter.set(y1);
                            } else if (index < entrySet.length - 1) {
                                double y1 = new NormalDistribution().cumulativeProbability(entry1.getValue()) - sumCounter.get();
                                toReturn.put(entry1.getKey(), y1);
                                sumCounter.set(y1);
                            } else { // last element
                                toReturn.put(entry1.getKey(), 1 - sumCounter.get());
                            }
                        });
                        break;
                    default:
                        throw new KiePMMLModelException("Unexpected opType " + opType);
                }
                break;
            case CLOGLOG:
                switch (opType) {
                    case CATEGORICAL:
                        if (resultMap.size() != 2) {
                            throw new KiePMMLModelException("Expected two entries, retrieved " + resultMap.size());
                        }
                        toReturn = new LinkedHashMap<>();
                        entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
                        Map.Entry<String, Double> entry = entrySet[0];
                        double y = 1 - Math.exp(0 - Math.exp(entry.getValue()));
                        toReturn.put(entry.getKey(), y);
                        entry = entrySet[1];
                        toReturn.put(entry.getKey(), 1 - y);
                        break;
                    case ORDINAL:
                        toReturn = new LinkedHashMap<>();
                        entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
                        sumCounter = new AtomicReference<Double>(0.0);
                        IntStream.range(0, resultMap.size()).forEach(index -> {
                            Map.Entry<String, Double> entry1 = entrySet[index];
                            if (index == 0) {
                                double y1 = 1 - Math.exp(0 - Math.exp(entry1.getValue()));
                                toReturn.put(entry1.getKey(), y1);
                                sumCounter.set(y1);
                            } else if (index < entrySet.length - 1) {
                                double y1 = (1 - Math.exp(0 - Math.exp(entry1.getValue()))) - sumCounter.get();
                                toReturn.put(entry1.getKey(), y1);
                                sumCounter.set(y1);
                            } else { // last element
                                toReturn.put(entry1.getKey(), 1 - sumCounter.get());
                            }
                        });
                        break;
                    default:
                        throw new KiePMMLModelException("Unexpected opType " + opType);
                }
                break;
            case CAUCHIT:
                switch (opType) {
                    case CATEGORICAL:
                        if (resultMap.size() != 2) {
                            throw new KiePMMLModelException("Expected two entries, retrieved " + resultMap.size());
                        }
                        toReturn = new LinkedHashMap<>();
                        entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
                        Map.Entry<String, Double> entry = entrySet[0];
                        double y = 0.5 + (1 / Math.PI) * Math.atan(entry.getValue());
                        toReturn.put(entry.getKey(), y);
                        entry = entrySet[1];
                        toReturn.put(entry.getKey(), 1 - y);
                        break;
                    case ORDINAL:
                        toReturn = new LinkedHashMap<>();
                        entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
                        sumCounter = new AtomicReference<Double>(0.0);
                        IntStream.range(0, resultMap.size()).forEach(index -> {
                            Map.Entry<String, Double> entry1 = entrySet[index];
                            if (index == 0) {
                                double y1 = 0.5 + (1 / Math.PI) * Math.atan(entry1.getValue());
                                toReturn.put(entry1.getKey(), y1);
                                sumCounter.set(y1);
                            } else if (index < entrySet.length - 1) {
                                double y1 = (0.5 + (1 / Math.PI) * Math.atan(entry1.getValue())) - sumCounter.get();
                                toReturn.put(entry1.getKey(), y1);
                                sumCounter.set(y1);
                            } else { // last element
                                toReturn.put(entry1.getKey(), 1 - sumCounter.get());
                            }
                        });
                        break;
                    default:
                        throw new KiePMMLModelException("Unexpected Operation Type " + opType);
                }
                break;
            default:
                throw new KiePMMLModelException("Unexpected Normalization Method " + regressionNormalizationMethod);
        }
        return toReturn;
    }
}
