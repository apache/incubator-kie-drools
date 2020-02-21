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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;

import static org.kie.pmml.commons.enums.StatusCode.OK;

public class PMMLClassificationModelEvaluator {

    private static final String UNEXPECTED_OP_TYPE = "Unexpected opType %s";
    private static final String EXPECTED_TWO_ENTRIES_RETRIEVED = "Expected two entries, retrieved %d";
    private static final String UNEXPECTED_OPERATION_TYPE = "Unexpected Operation Type %s";
    private static final String UNEXPECTED_NORMALIZATION_METHOD = "Unexpected Normalization Method %s";

    private PMMLClassificationModelEvaluator() {
        // Avoid instantiation
    }

    public static PMML4Result evaluateClassification(KiePMMLRegressionModel regressionModel, PMMLContext context) {
        return evaluateClassification(regressionModel.getTargetField(), regressionModel.getRegressionNormalizationMethod(), regressionModel.getTargetOpType(), regressionModel.getRegressionTables(), regressionModel.getOutputFields(), context.getRequestData());
    }

    protected static PMML4Result evaluateClassification(String targetFieldName, REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod, OP_TYPE opType, final List<KiePMMLRegressionTable> regressionTables, final Optional<List<KiePMMLOutputField>> outputFields, PMMLRequestData requestData) {
        final LinkedHashMap<String, Double> resultMap = regressionTables.stream()
                .collect(Collectors.toMap(kiePMMLRegressionTable -> kiePMMLRegressionTable.getTargetCategory().toString(),
                                          kiePMMLRegressionTable -> {
                                              PMML4Result retrieved = PMMLRegresssionModelEvaluator.evaluateRegression(kiePMMLRegressionTable.getTargetCategory().toString(),
                                                                                                                       REGRESSION_NORMALIZATION_METHOD.NONE,
                                                                                                                       kiePMMLRegressionTable,
                                                                                                                       requestData);
                                              return (Double) retrieved.getResultVariables().get(retrieved.getResultObjectName());
                                          },
                                          (o1, o2) -> o1,
                                          LinkedHashMap::new));
        final Map<String, Double> probabilityMap = getProbabilityMap(regressionNormalizationMethod, opType, resultMap);
        final Map.Entry<String, Double> predictedEntry = Collections.max(probabilityMap.entrySet(), Comparator.comparing(Map.Entry::getValue));

        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(targetFieldName, predictedEntry.getKey());
        toReturn.setResultObjectName(targetFieldName);
        toReturn.setResultCode(OK.getName());
        outputFields.ifPresent(outFlds -> outFlds.forEach(outputField -> {
            Object toPut = null;
            switch (outputField.getResultFeature()) {
                case PREDICTED_VALUE:
                    toPut = predictedEntry.getKey();
                    break;
                case PROBABILITY:
                    if (outputField.getValue() == null) {
                        toPut = predictedEntry.getValue();
                    } else if (probabilityMap.containsKey(outputField.getValue())) {
                        toPut = probabilityMap.get(outputField.getValue());
                    }
                    break;
                default:
                    // noop
            }
            if (toPut != null) {
                toReturn.addResultVariable(outputField.getName(), toPut);
            }
        }));
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getProbabilityMap(REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod, OP_TYPE opType, final LinkedHashMap<String, Double> resultMap) {
        switch (regressionNormalizationMethod) {
            case SOFTMAX:
                return getSOFTMAXProbabilityMap(resultMap);
            case SIMPLEMAX:
                return getSIMPLEMAXProbabilityMap(resultMap);
            case NONE:
                switch (opType) {
                    case CATEGORICAL:
                        return getNONECATEGORICALProbabilityMap(resultMap);
                    case ORDINAL:
                        return getNONEORDINALProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OP_TYPE, opType));
                }
            case LOGIT:
                switch (opType) {
                    case CATEGORICAL:
                        return getLOGITCATEGORICALProbabilityMap(resultMap);
                    case ORDINAL:
                        return getLOGITORDINALProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OP_TYPE, opType));
                }
            case PROBIT:
                switch (opType) {
                    case CATEGORICAL:
                        return getPROBITCATEGORICALProbabilityMap(resultMap);
                    case ORDINAL:
                        return getPROBITORDINALProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OP_TYPE, opType));
                }
            case CLOGLOG:
                switch (opType) {
                    case CATEGORICAL:
                        return getCLOGLOGCATEGORICALProbabilityMap(resultMap);
                    case ORDINAL:
                        return getCLOGLOGORDINALProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OP_TYPE, opType));
                }
            case CAUCHIT:
                switch (opType) {
                    case CATEGORICAL:
                        return getCAUCHITCATEGORICALProbabilityMap(resultMap);
                    case ORDINAL:
                        return getCAUCHITORDINALProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OPERATION_TYPE, opType));
                }
            default:
                throw new KiePMMLModelException(String.format(UNEXPECTED_NORMALIZATION_METHOD, regressionNormalizationMethod));
        }
    }

    protected static LinkedHashMap<String, Double> getSOFTMAXProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        Map<String, Double> tmp = resultMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                                         entry -> Math.exp(entry.getValue())));
        double sum = tmp.values().stream().mapToDouble(value -> value).sum();
        return tmp.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                entry -> entry.getValue() / sum,
                                                                (o1, o2) -> o1,
                                                                LinkedHashMap::new));
    }

    protected static LinkedHashMap<String, Double> getSIMPLEMAXProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        double sum = resultMap.values().stream().mapToDouble(value -> value).sum();
        return resultMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                      entry -> entry.getValue() / sum,
                                                                      (o1, o2) -> o1,
                                                                      LinkedHashMap::new));
    }

    protected static LinkedHashMap<String, Double> getNONECATEGORICALProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        String[] resultMapKeys = resultMap.keySet().toArray(new String[0]);
        AtomicReference<Double> sumCounter = new AtomicReference<>(0.0);
        IntStream.range(0, resultMap.size()).forEach(index -> {
            String key = resultMapKeys[index];
            double value = resultMap.get(key);
            if (index < resultMapKeys.length - 1) {
                sumCounter.accumulateAndGet(value, Double::sum);
                toReturn.put(key, value);
            } else { // last element
                toReturn.put(key, 1 - sumCounter.get());
            }
        });
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getNONEORDINALProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        String[] resultMapKeys = resultMap.keySet().toArray(new String[0]);
        AtomicReference<Double> sumCounter = new AtomicReference<>(0.0);
        IntStream.range(0, resultMap.size()).forEach(index -> {
            String key = resultMapKeys[index];
            double value = resultMap.get(key);
            if (index == 0) {
                toReturn.put(key, value);
                sumCounter.set(value);
            } else if (index < resultMapKeys.length - 1) {
                double y = value - sumCounter.get();
                toReturn.put(key, y);
                sumCounter.set(y);
            } else { // last element
                toReturn.put(key, 1 - sumCounter.get());
            }
        });
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getLOGITCATEGORICALProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        if (resultMap.size() != 2) {
            throw new KiePMMLModelException(String.format(EXPECTED_TWO_ENTRIES_RETRIEVED, resultMap.size()));
        }
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        String[] resultMapKeys = resultMap.keySet().toArray(new String[0]);
        double y = 1 / (1 + Math.exp(0 - resultMap.get(resultMapKeys[0])));
        toReturn.put(resultMapKeys[0], y);
        toReturn.put(resultMapKeys[1], 1 - y);
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getLOGITORDINALProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        String[] resultMapKeys = resultMap.keySet().toArray(new String[0]);
        AtomicReference<Double> sumCounter = new AtomicReference<>(0.0);
        IntStream.range(0, resultMap.size()).forEach(index -> {
            String key = resultMapKeys[index];
            if (index == 0) {
                double y1 = 1 / (1 + Math.exp(0 - resultMap.get(key)));
                toReturn.put(key, y1);
                sumCounter.set(y1);
            } else if (index < resultMapKeys.length - 1) {
                double y1 = (1 / (1 + Math.exp(0 - resultMap.get(key)))) - sumCounter.get();
                toReturn.put(key, y1);
                sumCounter.set(y1);
            } else { // last element
                toReturn.put(key, 1 - sumCounter.get());
            }
        });
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getPROBITCATEGORICALProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        if (resultMap.size() != 2) {
            throw new KiePMMLModelException(String.format(EXPECTED_TWO_ENTRIES_RETRIEVED, resultMap.size()));
        }
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        Map.Entry<String, Double>[] entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
        Map.Entry<String, Double> entry = entrySet[0];
        double y = new NormalDistribution().cumulativeProbability(entry.getValue());
        toReturn.put(entry.getKey(), y);
        entry = entrySet[1];
        toReturn.put(entry.getKey(), 1 - y);
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getPROBITORDINALProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        Map.Entry<String, Double>[] entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
        AtomicReference<Double> sumCounter = new AtomicReference<>(0.0);
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
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getCLOGLOGCATEGORICALProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        if (resultMap.size() != 2) {
            throw new KiePMMLModelException(String.format(EXPECTED_TWO_ENTRIES_RETRIEVED, resultMap.size()));
        }
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        Map.Entry<String, Double>[] entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
        Map.Entry<String, Double> entry = entrySet[0];
        double y = 1 - Math.exp(0 - Math.exp(entry.getValue()));
        toReturn.put(entry.getKey(), y);
        entry = entrySet[1];
        toReturn.put(entry.getKey(), 1 - y);
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getCLOGLOGORDINALProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        Map.Entry<String, Double>[] entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
        AtomicReference<Double> sumCounter = new AtomicReference<>(0.0);
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
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getCAUCHITCATEGORICALProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        if (resultMap.size() != 2) {
            throw new KiePMMLModelException(String.format(EXPECTED_TWO_ENTRIES_RETRIEVED, resultMap.size()));
        }
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        Map.Entry<String, Double>[] entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
        Map.Entry<String, Double> entry = entrySet[0];
        double y = 0.5 + (1 / Math.PI) * Math.atan(entry.getValue());
        toReturn.put(entry.getKey(), y);
        entry = entrySet[1];
        toReturn.put(entry.getKey(), 1 - y);
        return toReturn;
    }

    protected static LinkedHashMap<String, Double> getCAUCHITORDINALProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        Map.Entry<String, Double>[] entrySet = (Map.Entry<String, Double>[]) resultMap.entrySet().toArray();
        AtomicReference<Double> sumCounter = new AtomicReference<>(0.0);
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
        return toReturn;
    }
}
