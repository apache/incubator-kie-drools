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
import java.util.function.DoubleUnaryOperator;
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

import static org.kie.pmml.commons.Constants.EXPECTED_TWO_ENTRIES_RETRIEVED;
import static org.kie.pmml.commons.Constants.UNEXPECTED_NORMALIZATION_METHOD;
import static org.kie.pmml.commons.Constants.UNEXPECTED_OPERATION_TYPE;
import static org.kie.pmml.commons.Constants.UNEXPECTED_OP_TYPE;
import static org.kie.pmml.commons.enums.StatusCode.OK;

public class PMMLClassificationModelEvaluator {

    private PMMLClassificationModelEvaluator() {
        // Avoid instantiation
    }

    public static PMML4Result evaluateClassification(KiePMMLRegressionModel regressionModel, PMMLContext context) {
        return evaluateClassification(regressionModel.getTargetField(), regressionModel.getRegressionNormalizationMethod(), regressionModel.getTargetOpType(), regressionModel.getRegressionTables(), regressionModel.getOutputFields(), context.getRequestData());
    }

    protected static PMML4Result evaluateClassification(String targetFieldName, REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod, OP_TYPE opType, final List<KiePMMLRegressionTable> regressionTables, final Optional<List<KiePMMLOutputField>> outputFields, PMMLRequestData requestData) {
        final LinkedHashMap<String, Double> resultMap = regressionTables.stream()
                .collect(Collectors.toMap(kiePMMLRegressionTable -> kiePMMLRegressionTable.getTargetCategory().orElseGet(() ->"UNKNOWN").toString(),
                                          kiePMMLRegressionTable -> {
                                              PMML4Result retrieved = PMMLRegresssionModelEvaluator.evaluateRegression(kiePMMLRegressionTable.getTargetCategory().orElseGet(() ->"UNKNOWN").toString(),
                                                                                                                       REGRESSION_NORMALIZATION_METHOD.NONE,
                                                                                                                       OP_TYPE.ORDINAL,
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
                switch (opType) {
                    case CATEGORICAL:
                        return getSOFTMAXProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OP_TYPE, opType));
                }
            case SIMPLEMAX:
                switch (opType) {
                    case CATEGORICAL:
                        return getSIMPLEMAXProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OP_TYPE, opType));
                }
            case NONE:
                switch (opType) {
                    case CATEGORICAL:
                        return getNONEProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OP_TYPE, opType));
                }
            case LOGIT:
                switch (opType) {
                    case CATEGORICAL:
                        return getLOGITProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OP_TYPE, opType));
                }
            case PROBIT:
                switch (opType) {
                    case CATEGORICAL:
                        return getPROBITProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OP_TYPE, opType));
                }
            case CLOGLOG:
                switch (opType) {
                    case CATEGORICAL:
                        return getCLOGLOGProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OP_TYPE, opType));
                }
            case CAUCHIT:
                switch (opType) {
                    case CATEGORICAL:
                        return getCAUCHITProbabilityMap(resultMap);
                    default:
                        throw new KiePMMLModelException(String.format(UNEXPECTED_OPERATION_TYPE, opType));
                }
            default:
                throw new KiePMMLModelException(String.format(UNEXPECTED_NORMALIZATION_METHOD, regressionNormalizationMethod));
        }
    }

    protected static LinkedHashMap<String, Double> getSOFTMAXProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        LinkedHashMap<String, Double> tmp = resultMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                   entry -> Math.exp(entry.getValue()),
                                                                                                   (o1, o2) -> o1,
                                                                                                   LinkedHashMap::new));
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

    protected static LinkedHashMap<String, Double> getNONEProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
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

    protected static LinkedHashMap<String, Double> getLOGITProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        DoubleUnaryOperator firstItemOperator = aDouble -> 1 / (1 + Math.exp(0 - aDouble));
        DoubleUnaryOperator secondItemOperator = aDouble -> 1 - aDouble;
        return getProbabilityMap(resultMap, firstItemOperator, secondItemOperator);
    }

    protected static LinkedHashMap<String, Double> getPROBITProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        DoubleUnaryOperator firstItemOperator = aDouble -> new NormalDistribution().cumulativeProbability(aDouble);
        DoubleUnaryOperator secondItemOperator = aDouble -> 1 - aDouble;
        return getProbabilityMap(resultMap, firstItemOperator, secondItemOperator);
    }

    protected static LinkedHashMap<String, Double> getCLOGLOGProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        DoubleUnaryOperator firstItemOperator = aDouble -> 1 - Math.exp(0 - Math.exp(aDouble));
        DoubleUnaryOperator secondItemOperator = aDouble -> 1 - aDouble;
        return getProbabilityMap(resultMap, firstItemOperator, secondItemOperator);
    }

    protected static LinkedHashMap<String, Double> getCAUCHITProbabilityMap(final LinkedHashMap<String, Double> resultMap) {
        DoubleUnaryOperator firstItemOperator = aDouble -> 0.5 + (1 / Math.PI) * Math.atan(aDouble);
        DoubleUnaryOperator secondItemOperator = aDouble -> 1 - aDouble;
        return getProbabilityMap(resultMap, firstItemOperator, secondItemOperator);
    }

    private static LinkedHashMap<String, Double> getProbabilityMap(final LinkedHashMap<String, Double> resultMap, DoubleUnaryOperator firstItemOperator, DoubleUnaryOperator secondItemOperator) {
        if (resultMap.size() != 2) {
            throw new KiePMMLModelException(String.format(EXPECTED_TWO_ENTRIES_RETRIEVED, resultMap.size()));
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
