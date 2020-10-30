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

import org.junit.Test;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.model.tuples.KiePMMLValueWeight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
    public void byNameExisting() {
        EXISTING_VALUES.values().forEach(s -> assertNotNull(MULTIPLE_MODEL_METHOD.byName(s)));
    }

    @Test(expected = KieEnumException.class)
    public void byNameNotExisting() {
        MULTIPLE_MODEL_METHOD.byName("NOT_EXISTING");
    }

    @Test
    public void getName() {
        EXISTING_VALUES.forEach((multipleModelMethod, s) -> assertEquals(s, multipleModelMethod.getName()));
    }

    @Test
    public void applyMAJORITY_VOTE() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = 3;
        inputData.put("ValueA", new KiePMMLNameValue("valuea", 1));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", EXPECTED));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", 1));
        Object retrieved = MAJORITY_VOTE.apply(inputData);
        assertEquals(EXPECTED, retrieved);
        inputData = new LinkedHashMap<>();
        EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        retrieved = MAJORITY_VOTE.apply(inputData);
        assertEquals(EXPECTED, retrieved);
    }

    @Test(expected = KieEnumException.class)
    public void applyWEIGHTED_MAJORITY_VOTE() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = 3;
        inputData.put("ValueA", new KiePMMLNameValue("valuea", 1));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", EXPECTED));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", 1));
        WEIGHTED_MAJORITY_VOTE.apply(inputData);
    }

    @Test
    public void applyAVERAGEKiePMMLValueWeight() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double average = (Double) expectedKiePMMLValueWeightMap.get("average");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) AVERAGE.apply(inputData);
        assertEquals(average, retrieved,0.0000000000001);
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        average = (Double) expectedKiePMMLValueWeightMap.get("average");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double) AVERAGE.apply(inputData);
        assertEquals(average, retrieved,0.0000000000001);
    }

    @Test(expected = KieEnumException.class)
    public void applyAVERAGENotKiePMMLValueWeight() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        AVERAGE.apply(inputData);
    }

    @Test
    public void applyWEIGHTED_AVERAGEKiePMMLValueWeight() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double weightedAverage = (Double) expectedKiePMMLValueWeightMap.get("weightedAverage");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) WEIGHTED_AVERAGE.apply(inputData);
        assertEquals(weightedAverage, retrieved,0.0000000000001);
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        weightedAverage = (Double) expectedKiePMMLValueWeightMap.get("weightedAverage");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double) WEIGHTED_AVERAGE.apply(inputData);
        assertEquals(weightedAverage, retrieved,0.0000000000001);
    }

    @Test(expected = KieEnumException.class)
    public void applyWEIGHTED_AVERAGENotKiePMMLValueWeight() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        WEIGHTED_AVERAGE.apply(inputData);
    }

    @Test
    public void applyMEDIANNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double median = (Double) expectedKiePMMLValueWeightMap.get("median");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) MEDIAN.apply(inputData);
        assertEquals(median, retrieved,0.0000000000001);
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        median = (Double) expectedKiePMMLValueWeightMap.get("median");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double) MEDIAN.apply(inputData);
        assertEquals(median, retrieved,0.0000000000001);
    }

    @Test(expected = KieEnumException.class)
    public void applyMEDIANNotNumbers() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        MEDIAN.apply(inputData);
    }

    @Test
    public void applyWEIGHTED_MEDIANNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double weightedMedian = (Double)  expectedKiePMMLValueWeightMap.get("weightedMedian");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) WEIGHTED_MEDIAN.apply(inputData);
        assertEquals(weightedMedian, retrieved,0.0000000000001);
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        weightedMedian = (Double) expectedKiePMMLValueWeightMap.get("weightedMedian");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double)  WEIGHTED_MEDIAN.apply(inputData);
        assertEquals(weightedMedian, retrieved,0.0000000000001);
    }

    @Test(expected = KieEnumException.class)
    public void applyWEIGHTED_MEDIANNotNumbers() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        WEIGHTED_MEDIAN.apply(inputData);
    }

    @Test
    public void applyMAXNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double max = (Double)  expectedKiePMMLValueWeightMap.get("max");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) MAX.apply(inputData);
        assertEquals(max, retrieved,0.0000000000001);
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        max = (Double) expectedKiePMMLValueWeightMap.get("max");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double)  MAX.apply(inputData);
        assertEquals(max, retrieved,0.0000000000001);
    }

    @Test(expected = KieEnumException.class)
    public void applyMAXNotNumbers() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        MAX.apply(inputData);
    }

    @Test
    public void applySUMNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double sum = (Double)  expectedKiePMMLValueWeightMap.get("sum");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) SUM.apply(inputData);
        assertEquals(sum, retrieved,0.0000000000001);
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        sum = (Double) expectedKiePMMLValueWeightMap.get("sum");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double)  SUM.apply(inputData);
        assertEquals(sum, retrieved,0.0000000000001);
    }

    @Test(expected = KieEnumException.class)
    public void applySUMNotNumbers() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        SUM.apply(inputData);
    }

    @Test
    public void applyWEIGHTED_SUMNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        double weightsSum = (Double)  expectedKiePMMLValueWeightMap.get("weightedSum");
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double retrieved = (Double) WEIGHTED_SUM.apply(inputData);
        assertEquals(weightsSum, retrieved,0.0000000000001);
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        weightsSum = (Double) expectedKiePMMLValueWeightMap.get("weightedSum");
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        retrieved = (Double)  WEIGHTED_SUM.apply(inputData);
        assertEquals(weightsSum, retrieved,0.0000000000001);
    }

    @Test(expected = KieEnumException.class)
    public void applyWEIGHTED_SUMNotNumbers() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "dvsdv"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", EXPECTED));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", EXPECTED));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        WEIGHTED_SUM.apply(inputData);
    }

    @Test
    public void applySELECT_FIRSTNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        double first = ((KiePMMLValueWeight) inputData.entrySet().iterator().next().getValue().getValue()).getValue();
        double retrieved = (Double) SELECT_FIRST.apply(inputData);
        assertEquals(first, retrieved,0.0000000000001);
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        first = ((KiePMMLValueWeight) inputData.entrySet().iterator().next().getValue().getValue()).getValue();
        retrieved = (Double)  SELECT_FIRST.apply(inputData);
        assertEquals(first, retrieved,0.0000000000001);
    }

    @Test
    public void applySELECT_FIRSTNotNumbers() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        Object EXPECTED = "EXPECTED";
        inputData.put("ValueA", new KiePMMLNameValue("valuea", EXPECTED));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", "vdsvsd"));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", "vfdsvsdeeee"));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        Object retrieved =  SELECT_FIRST.apply(inputData);
        assertEquals(EXPECTED, retrieved);
    }

    @Test
    public void applySELECT_ALLNumbers() {
        Map<String, Object> expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(true);
        LinkedHashMap<String, KiePMMLNameValue> inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        List<Double> expected = inputData.values()
                .stream()
                .map(kiePMMLNameValue -> ((KiePMMLValueWeight)kiePMMLNameValue.getValue()).getValue())
                .collect(Collectors.toList());
        List retrieved = (List) SELECT_ALL.apply(inputData);
        assertEquals(expected.size(), retrieved.size());
        for (Double expDouble : expected) {
            assertTrue(retrieved.contains(expDouble));
        }
        expectedKiePMMLValueWeightMap = getExpectedKiePMMLValueWeightMap(false);
        inputData =
                (LinkedHashMap<String, KiePMMLNameValue>) expectedKiePMMLValueWeightMap.get("inputData");
        expected = inputData.values()
                .stream()
                .map(kiePMMLNameValue -> ((KiePMMLValueWeight)kiePMMLNameValue.getValue()).getValue())
                .collect(Collectors.toList());
        retrieved = (List) SELECT_ALL.apply(inputData);
        assertEquals(expected.size(), retrieved.size());
        for (Double expDouble : expected) {
            assertTrue(retrieved.contains(expDouble));
        }
    }

    @Test
    public void applySELECT_ALLNotNumbers() {
        LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        inputData.put("ValueA", new KiePMMLNameValue("valuea", "fvdsfsdfsd"));
        inputData.put("ValueB", new KiePMMLNameValue("valueb", "vdsvsd"));
        inputData.put("ValueC", new KiePMMLNameValue("valuec", "dssd"));
        inputData.put("ValueD", new KiePMMLNameValue("valuex", "vfdsvsdeeee"));
        inputData.put("ValueE", new KiePMMLNameValue("valueb", "vsd"));
        List expected = inputData.values().stream().map(KiePMMLNameValue::getValue).collect(Collectors.toList());
        List retrieved = (List) SELECT_ALL.apply(inputData);
        assertEquals(expected.size(), retrieved.size());
        expected.forEach(expString -> assertTrue(retrieved.contains(expString)));
    }

    @Test(expected = KieEnumException.class)
    public void applyMODEL_CHAIN() {
        MODEL_CHAIN.apply(new LinkedHashMap<>());
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