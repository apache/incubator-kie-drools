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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.model.tuples.KiePMMLValueWeight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.AVERAGE;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.MAJORITY_VOTE;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.MAX;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.MEDIAN;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.MODEL_CHAIN;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.SELECT_ALL;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.SELECT_FIRST;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.SUM;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.WEIGHTED_AVERAGE;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.WEIGHTED_MAJORITY_VOTE;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.WEIGHTED_MEDIAN;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.WEIGHTED_SUM;

public class MULTIPLE_MODEL_METHODTest {

    private final Map<MULTIPLE_MODEL_METHOD, String> EXISTING_VALUES = Stream.of(
            new AbstractMap.SimpleEntry<>(MAJORITY_VOTE, "majorityVote"),
            new AbstractMap.SimpleEntry<>(WEIGHTED_MAJORITY_VOTE, "weightedMajorityVote"),
            new AbstractMap.SimpleEntry<>(AVERAGE, "average"),
            new AbstractMap.SimpleEntry<>(WEIGHTED_AVERAGE, "weightedAverage"),
            new AbstractMap.SimpleEntry<>(MEDIAN, "median"),
            new AbstractMap.SimpleEntry<>(WEIGHTED_MEDIAN, "x-weightedMedian"),
            new AbstractMap.SimpleEntry<>(MAX, "max"),
            new AbstractMap.SimpleEntry<>(SUM, "sum"),
            new AbstractMap.SimpleEntry<>(WEIGHTED_SUM, "x-weightedSum"),
            new AbstractMap.SimpleEntry<>(SELECT_FIRST, "selectFirst"),
            new AbstractMap.SimpleEntry<>(SELECT_ALL, "selectAll"),
            new AbstractMap.SimpleEntry<>(MODEL_CHAIN, "modelChain")
    )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private final List<MULTIPLE_MODEL_METHOD> UNIMPLEMENTED = Arrays.asList(
            WEIGHTED_MAJORITY_VOTE,
            MODEL_CHAIN
    );

    @Test
    void byNameExisting() {
        EXISTING_VALUES.values().forEach(s -> assertThat(MULTIPLE_MODEL_METHOD.byName(s)).isNotNull());
    }

    @Test
    void byNameNotExisting() {
        assertThatExceptionOfType(KieEnumException.class).isThrownBy(() -> {
            MULTIPLE_MODEL_METHOD.byName("NOT_EXISTING");
        });
    }

    @Test
    void getName() {
        EXISTING_VALUES.forEach((multipleModelMethod, s) -> assertThat(multipleModelMethod.getName()).isEqualTo(s));
    }

    @Test
    void applyMAJORITY_VOTE() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = 3;
        inputData.put("ValueA", new KiePMMLNameValue("valuea", 1));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", EXPECTED));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", 1));
        Object retrieved = MAJORITY_VOTE.applyPrediction(inputData);
        assertThat(retrieved).isEqualTo(EXPECTED);
        inputData = new LinkedHashMap<>();
        EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        retrieved = MAJORITY_VOTE.applyPrediction(inputData);
        assertThat(retrieved).isEqualTo(EXPECTED);
    }

    @Test
    void applyWEIGHTED_MAJORITY_VOTE() {
        assertThatExceptionOfType(KieEnumException.class).isThrownBy(() -> {
            LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
            Object EXPECTED = 3;
            inputData.put("ValueA", new KiePMMLNameValue("valuea", 1));
            inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
            inputData.put("ValueC", new KiePMMLNameValue("valuec", EXPECTED));
            inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
            inputData.put("ValueE", new KiePMMLNameValue("valueb", 1));
            WEIGHTED_MAJORITY_VOTE.applyPrediction(inputData);
        });
    }

    @Test
    void applyAVERAGEKiePMMLValueWeight() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double average = (Double) expectedKiePMMLValueWeightMap.get("average");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) AVERAGE.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(average, Offset.offset(0.0000000000001));
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        average = (Double) expectedKiePMMLValueWeightMap.get("average");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double) AVERAGE.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(average, Offset.offset(0.0000000000001));
    }

    @Test
    void applyAVERAGENotKiePMMLValueWeight() {
        assertThatExceptionOfType(KieEnumException.class).isThrownBy(() -> {
            LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
            Object EXPECTED = "EXPECTED";
            inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
            inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
            inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
            inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
            inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
            AVERAGE.applyPrediction(inputData);
        });
    }

    @Test
    void applyWEIGHTED_AVERAGEKiePMMLValueWeight() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double weightedAverage = (Double) expectedKiePMMLValueWeightMap.get("weightedAverage");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) WEIGHTED_AVERAGE.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(weightedAverage, Offset.offset(0.0000000000001));
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        weightedAverage = (Double) expectedKiePMMLValueWeightMap.get("weightedAverage");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double) WEIGHTED_AVERAGE.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(weightedAverage, Offset.offset(0.0000000000001));
    }

    @Test
    void applyWEIGHTED_AVERAGENotKiePMMLValueWeight() {
        assertThatExceptionOfType(KieEnumException.class).isThrownBy(() -> {
            LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
            Object EXPECTED = "EXPECTED";
            inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
            inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
            inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
            inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
            inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
            WEIGHTED_AVERAGE.applyPrediction(inputData);
        });
    }

    @Test
    void applyMEDIANNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double median = (Double) expectedKiePMMLValueWeightMap.get("median");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) MEDIAN.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(median, Offset.offset(0.0000000000001));
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        median = (Double) expectedKiePMMLValueWeightMap.get("median");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double) MEDIAN.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(median, Offset.offset(0.0000000000001));
    }

    @Test
    void applyMEDIANNotNumbers() {
        assertThatExceptionOfType(KieEnumException.class).isThrownBy(() -> {
            LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
            Object EXPECTED = "EXPECTED";
            inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
            inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
            inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
            inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
            inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
            MEDIAN.applyPrediction(inputData);
        });
    }

    @Test
    void applyWEIGHTED_MEDIANNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double weightedMedian = (Double)  expectedKiePMMLValueWeightMap.get("weightedMedian");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) WEIGHTED_MEDIAN.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(weightedMedian, Offset.offset(0.0000000000001));
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        weightedMedian = (Double) expectedKiePMMLValueWeightMap.get("weightedMedian");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double)  WEIGHTED_MEDIAN.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(weightedMedian, Offset.offset(0.0000000000001));
    }

    @Test
    void applyWEIGHTED_MEDIANNotNumbers() {
        assertThatExceptionOfType(KieEnumException.class).isThrownBy(() -> {
            LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
            Object EXPECTED = "EXPECTED";
            inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
            inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
            inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
            inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
            inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
            WEIGHTED_MEDIAN.applyPrediction(inputData);
        });
    }

    @Test
    void applyMAXNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double max = (Double)  expectedKiePMMLValueWeightMap.get("max");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) MAX.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(max, Offset.offset(0.0000000000001));
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        max = (Double) expectedKiePMMLValueWeightMap.get("max");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double)  MAX.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(max, Offset.offset(0.0000000000001));
    }

    @Test
    void applyMAXNotNumbers() {
        assertThatExceptionOfType(KieEnumException.class).isThrownBy(() -> {
            LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
            Object EXPECTED = "EXPECTED";
            inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
            inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
            inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
            inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
            inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
            MAX.applyPrediction(inputData);
        });
    }

    @Test
    void applySUMNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double sum = (Double)  expectedKiePMMLValueWeightMap.get("sum");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) SUM.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(sum, Offset.offset(0.0000000000001));
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        sum = (Double) expectedKiePMMLValueWeightMap.get("sum");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double)  SUM.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(sum, Offset.offset(0.0000000000001));
    }

    @Test
    void applySUMNotNumbers() {
        assertThatExceptionOfType(KieEnumException.class).isThrownBy(() -> {
            LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
            Object EXPECTED = "EXPECTED";
            inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
            inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
            inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
            inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
            inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
            SUM.applyPrediction(inputData);
        });
    }

    @Test
    void applyWEIGHTED_SUMNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double weightsSum = (Double)  expectedKiePMMLValueWeightMap.get("weightedSum");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) WEIGHTED_SUM.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(weightsSum, Offset.offset(0.0000000000001));
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        weightsSum = (Double) expectedKiePMMLValueWeightMap.get("weightedSum");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double)  WEIGHTED_SUM.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(weightsSum, Offset.offset(0.0000000000001));
    }

    @Test
    void applyWEIGHTED_SUMNotNumbers() {
        assertThatExceptionOfType(KieEnumException.class).isThrownBy(() -> {
            LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
            Object EXPECTED = "EXPECTED";
            inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
            inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
            inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
            inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
            inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
            WEIGHTED_SUM.applyPrediction(inputData);
        });
    }

    @Test
    void applySELECT_FIRSTNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double first = ((KiePMMLValueWeight) inputData.entrySet().iterator().next().getValue().getValue()).getValue();
        double retrieved = (Double) SELECT_FIRST.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(first, Offset.offset(0.0000000000001));
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        first = ((KiePMMLValueWeight) inputData.entrySet().iterator().next().getValue().getValue()).getValue();
        retrieved = (Double)  SELECT_FIRST.applyPrediction(inputData);
        assertThat(retrieved).isCloseTo(first, Offset.offset(0.0000000000001));
    }

    @Test
    void applySELECT_FIRSTNotNumbers() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", EXPECTED));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", "vdsvsd"));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", "vfdsvsdeeee"));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        Object retrieved =  SELECT_FIRST.applyPrediction(inputData);
        assertThat(retrieved).isEqualTo(EXPECTED);
    }

    @Test
    void applySELECT_ALLNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        List<Double> expected = inputData.values()
                .stream()
                .map(kiePMMLNameValue -> ((KiePMMLValueWeight) kiePMMLNameValue.getValue()).getValue())
                .collect(Collectors.toList());
        List retrieved = (List) SELECT_ALL.applyPrediction(inputData);
        assertThat(retrieved).hasSameSizeAs(expected);
        for (Double expDouble : expected) {
            assertThat(retrieved).contains(expDouble);
        }
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        expected = inputData.values()
                .stream()
                .map(kiePMMLNameValue -> ((KiePMMLValueWeight) kiePMMLNameValue.getValue()).getValue())
                .collect(Collectors.toList());
        retrieved = (List) SELECT_ALL.applyPrediction(inputData);
        assertThat(retrieved).hasSameSizeAs(expected);
        for (Double expDouble : expected) {
            assertThat(retrieved).contains(expDouble);
        }
    }

    @Test
    void applySELECT_ALLNotNumbers() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "fvdsfsdfsd"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", "vdsvsd"));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", "vfdsvsdeeee"));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        List expected = inputData.values().stream().map(KiePMMLNameValue::getValue).collect(Collectors.toList());
        List retrieved = (List) SELECT_ALL.applyPrediction(inputData);
        assertThat(retrieved).hasSameSizeAs(expected);
        expected.forEach(expString -> assertThat(retrieved).contains(expString));
    }

    @Test
    void applyMODEL_CHAIN() {
        assertThatExceptionOfType(KieEnumException.class).isThrownBy(() -> {
            MODEL_CHAIN.applyPrediction(new LinkedHashMap<>());
        });
    }

    private Map<String, Object> getExpectedKiePMMLValueWeightMap(boolean evenNumberOfData) {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        int numberOfData = evenNumberOfData ? 8 : 9;
        final List<KiePMMLValueWeight> valueWeightList = new ArrayList<>();
        double seed = 0.35;
        AtomicReference<Double> WEIGHT_SEED = new AtomicReference<>(seed);
        IntStream.range(0, numberOfData).forEach(i -> {
            double weight;
            if (i < numberOfData - 1) {
                weight = ThreadLocalRandom.current().nextDouble(0.0, (1 - WEIGHT_SEED.get()));
                WEIGHT_SEED.accumulateAndGet(weight, Double::sum);
            } else {
                weight = 1 - WEIGHT_SEED.get() + seed;
            }
            double value = ThreadLocalRandom.current().nextDouble(3.0, 10.0);
            valueWeightList.add(new KiePMMLValueWeight(value, weight));
        });
        double sum = 0;
        double weightedSum = 0;
        double weightsSum = 0;
        int i = 0;
        for (KiePMMLValueWeight valueWeight : valueWeightList) {
            inputData.put("Value" + i, new KiePMMLNameValue("val-" + i, valueWeight));
            sum += valueWeight.getValue();
            weightedSum += valueWeight.weightedValue();
            weightsSum += valueWeight.getWeight();
            i++;
        }
        final Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("inputData", inputData);
        toReturn.put("sum", sum);
        toReturn.put("weightedSum", weightedSum);
        double average = sum / inputData.values().size();
        toReturn.put("average", average);
        double weightedAverage = weightedSum / weightsSum;
        toReturn.put("weightedAverage", weightedAverage);
        // ordering by values
        valueWeightList.sort((o1, o2) -> {
            int toReturn1 = 0;
            if (o1.getValue() > o2.getValue()) {
                toReturn1 = 1;
            } else if (o1.getValue() < o2.getValue()) {
                toReturn1 = -1;
            }
            return toReturn1;
        });
        double median = 0;
        if (evenNumberOfData) {
            median = (valueWeightList.get(3).getValue() + valueWeightList.get(4).getValue()) / 2;
        } else {
            median = valueWeightList.get(4).getValue();
        }
        toReturn.put("median", median);
        double halfWeight = weightsSum / 2;
        double weightedMedianSum = 0;
        double weightedMedian = 0;
        for (KiePMMLValueWeight tuple : valueWeightList) {
            weightedMedianSum += tuple.getWeight();
            if (weightedMedianSum >= halfWeight) {
                weightedMedian = tuple.getValue();
                break;
            }
        }
        toReturn.put("weightedMedian", weightedMedian);
        double max = valueWeightList.get(valueWeightList.size()-1).getValue();
        toReturn.put("max", max);
        return toReturn;
    }

}