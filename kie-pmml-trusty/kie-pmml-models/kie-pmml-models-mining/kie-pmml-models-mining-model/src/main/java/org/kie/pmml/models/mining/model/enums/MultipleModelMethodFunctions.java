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
package org.kie.pmml.models.mining.model.enums;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.model.tuples.KiePMMLValueWeight;

public class MultipleModelMethodFunctions {

    private MultipleModelMethodFunctions() {
    }

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> MOST_FREQUENT_RESULT = inputData -> {
        Map<Object, Long> groupedValues = objectList(inputData.values())
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Object, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        entry -> {
                            Object key = entry.getKey();
                            if (key instanceof KiePMMLValueWeight) {
                                return ((KiePMMLValueWeight) key).getValue();
                            } else {
                                return key;
                            }
                        },
                        Map.Entry::getValue,
                        (a, b) -> {
                            throw new AssertionError();
                        },
                        LinkedHashMap::new
                ));
        Object toReturn = groupedValues.entrySet().iterator().next().getKey();
        if (toReturn == null) {
            throw new KieEnumException("Failed to retrieve MAJORITY_VOTE");
        }
        return toReturn;
    };

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> AVERAGE_RESULT =
            inputData -> doubleStream(inputData.values(), "AVERAGE")
                    .average().orElseThrow(() -> new KieEnumException("Failed to get AVERAGE"));

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> WEIGHTED_AVERAGE_RESULT =
            inputData -> {
                AtomicReference<Double> weightedSum = new AtomicReference<>(0.0);
                AtomicReference<Double> weights = new AtomicReference<>(0.0);
                valueWeightList(inputData.values(), "WEIGHTED_AVERAGE")
                        .forEach(elem -> {
                            weightedSum.accumulateAndGet(elem.weightedValue(), Double::sum);
                            weights.accumulateAndGet(elem.getWeight(), Double::sum);
                        });
                return weightedSum.get() / weights.get();
            };

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> MEDIAN_RESULT = inputData -> {
        DoubleStream sortedValues = doubleStream(inputData.values(), "MEDIAN").sorted();
        OptionalDouble toReturn = inputData.size() % 2 == 0 ?
                sortedValues.skip(inputData.size() / 2 - (long) 1).limit(2).average() :
                sortedValues.skip(inputData.size() / 2).findFirst();
        return toReturn.orElseThrow(() -> new KieEnumException("Failed to get MEDIAN"));
    };

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> WEIGHTED_MEDIAN_RESULT =
            inputData -> {
                final List<KiePMMLValueWeight> kiePMMLValueWeights = valueWeightList(inputData.values(),
                                                                                     "WEIGHTED_MEDIAN");
                kiePMMLValueWeights
                        .sort((o1, o2) -> {
                            int toReturn = 0;
                            if (o1.getValue() > o2.getValue()) {
                                toReturn = 1;
                            } else if (o1.getValue() < o2.getValue()) {
                                toReturn = -1;
                            }
                            return toReturn;
                        });

                AtomicReference<Double> weightsSumRef = new AtomicReference<>((double) 0);
                kiePMMLValueWeights.forEach(value -> weightsSumRef.updateAndGet(v -> (v + value.getWeight())));
                double weightsSum = weightsSumRef.get();
                double weightsMedian = weightsSum / 2;
                double weightedMedianSum = 0;
                for (KiePMMLValueWeight kiePMMLValueWeight : kiePMMLValueWeights) {
                    weightedMedianSum += kiePMMLValueWeight.getWeight();
                    if (weightedMedianSum > weightsMedian) {
                        return kiePMMLValueWeight.getValue();
                    }
                }
                throw new KieEnumException("Failed to get WEIGHTED_MEDIAN");
            };

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> MAX_RESULT =
            inputData -> doubleStream(inputData.values(), "MAX").max()
                    .orElseThrow(() -> new KieEnumException("Failed to get MAX"));

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> SUM_RESULT =
            inputData -> doubleStream(inputData.values(), "SUM").sum();

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> WEIGHTED_SUM_RESULT =
            inputData -> {
                AtomicReference<Double> toReturn = new AtomicReference<>((double) 0);
                valueWeightList(inputData.values(), "WEIGHTED_SUM")
                        .forEach(value -> toReturn.updateAndGet(v -> (v + value.weightedValue())));
                return toReturn.get();
            };

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> SELECT_FIRST_RESULT = inputData -> {
        if (inputData.entrySet().iterator().hasNext()) {
            Object toReturn = inputData.entrySet().iterator().next().getValue().getValue();
            if (toReturn instanceof KiePMMLValueWeight) {
                toReturn = ((KiePMMLValueWeight) toReturn).getValue();
            }
            return toReturn;
        } else {
            throw new KieEnumException("Failed to SELECT_FIRST");
        }
    };

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> SELECT_ALL_RESULT =
            inputData -> {
                List<Object> toReturn = new ArrayList<>();
                objectList(inputData.values())
                        .forEach(value -> {
                            if (value != null) {
                                if (value instanceof KiePMMLValueWeight) {
                                    toReturn.add(((KiePMMLValueWeight) value).getValue());
                                } else {
                                    toReturn.add(value);
                                }
                            }
                        });
                return toReturn;
            };

    public static final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> SELECT_LAST_RESULT = inputData -> {
        Iterator<Map.Entry<String, KiePMMLNameValue>> iterator = inputData.entrySet().iterator();
        Object toReturn = null;
        while (iterator.hasNext()) {
            toReturn = iterator.next().getValue();
        }
        if (toReturn != null) {
            if (toReturn instanceof KiePMMLValueWeight) {
                return ((KiePMMLValueWeight) toReturn).getValue();
            } else {
                return ((KiePMMLNameValue) toReturn).getValue();
            }
        } else {
            throw new KieEnumException("Failed to MODEL_CHAIN");
        }
    };

    public static final Function<LinkedHashMap<String, List<KiePMMLNameValue>>, LinkedHashMap<String, Double>> PROBABILITY_FUNCTION = inputData -> {
        Map<String, List<KiePMMLNameValue>> mappedValues = inputData.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(KiePMMLNameValue::getName));
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        mappedValues.forEach((key, probabilityValues) -> {
            Double value = doubleStream(probabilityValues, "AVERAGE")
                    .average().orElseThrow(() -> new KieEnumException("Failed to get AVERAGE"));
            toReturn.put(key, value);
        });
        return toReturn;
    };

    /**
     * Returns a <code>List&lt;Object&gt;</code> representing the values inside the original <code>Collection&lt;
     * KiePMMLNameValue&gt;</code>
     * <p>
     * {@link KiePMMLNameValue#getValue()}
     *
     * @param toUnwrap
     * @return
     */
    private static List<Object> objectList(Collection<KiePMMLNameValue> toUnwrap) {
        final List<Object> toReturn = new ArrayList<>();
        toUnwrap.forEach(nameValue -> toReturn.add(nameValue.getValue()));
        return toReturn;
    }

    /**
     * Returns a <code>List&lt;KiePMMLValueWeight&gt;</code> representing the values inside the original
     * <code>List&lt;KiePMMLNameValue&gt;</code>
     * {@link KiePMMLNameValue#getValue()}
     *
     * @param toUnwrap
     * @param enumName
     * @return
     */
    private static List<KiePMMLValueWeight> valueWeightList(Collection<KiePMMLNameValue> toUnwrap, String enumName) {
        final List<KiePMMLValueWeight> toReturn = new ArrayList<>();
        toUnwrap.forEach(nameValue -> {
            Object elem = nameValue.getValue();
            if (!(elem instanceof KiePMMLValueWeight)) {
                throw new KieEnumException("Failed to get " + enumName + ". Expecting KiePMMLValueWeight, " +
                                                   "found " + elem.getClass().getSimpleName());
            }
            toReturn.add((KiePMMLValueWeight) elem);
        });
        return toReturn;
    }

    /**
     * Returns a <code>DoubleStream</code> representing the values inside the original <code>List&lt;
     * KiePMMLNameValue&gt;</code>
     * <p>
     * {@link KiePMMLValueWeight#getValue()}
     *
     * @param toUnwrap
     * @param enumName
     * @return
     * @throws KieEnumException
     */
    private static DoubleStream doubleStream(Collection<KiePMMLNameValue> toUnwrap, String enumName) {
        return valueWeightList(toUnwrap, enumName).stream()
                .mapToDouble(KiePMMLValueWeight::getValue);
    }
}
