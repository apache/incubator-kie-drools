/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.commons.model.expressions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.AGGREGATE_FUNCTIONS;
import org.kie.pmml.api.enums.BUILTIN_FUNCTIONS;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.junit.Assert.assertEquals;
import static org.kie.pmml.api.enums.AGGREGATE_FUNCTIONS.AVERAGE;
import static org.kie.pmml.api.enums.AGGREGATE_FUNCTIONS.COUNT;
import static org.kie.pmml.api.enums.AGGREGATE_FUNCTIONS.MAX;
import static org.kie.pmml.api.enums.AGGREGATE_FUNCTIONS.MIN;
import static org.kie.pmml.api.enums.AGGREGATE_FUNCTIONS.SUM;

public class KiePMMLAggregateTest {

    private static final String FIELD_NAME = "FIELD_NAME";
    private static final String GROUP_BY_NAME = "GROUP_BY_NAME";
    private static final String GROUP_BY_VALUE = "0 1 2";
    private static Map<AGGREGATE_FUNCTIONS, Object> RESULTS;
    private static Map<String, List<Object>> GROUPED_MAP;
    private static String FIELD_VALUE;

    @BeforeClass
    public static void setup() {
        String[] groupBy = GROUP_BY_VALUE.split(" ");
        GROUPED_MAP = new HashMap<>();
        for (String group : groupBy) {
            int limit = new Random().nextInt(2) + 2;
            List<Object> toPut = IntStream.range(0, limit).mapToObj(i -> group).collect(Collectors.toList());
            GROUPED_MAP.put(group, toPut);
        }
        final List<Object> allInputData =
                GROUPED_MAP.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        FIELD_VALUE = allInputData.stream().map(o -> (String)o).collect(Collectors.joining(" "));
        Object[] inputData = GROUPED_MAP.values().stream().flatMap(Collection::stream).toArray(Object[]::new);
        Object[] numberInputData = Stream.of(inputData).map(o -> Double.valueOf((String)o)).toArray(Object[]::new);
        RESULTS = new HashMap<>();
        RESULTS.put(COUNT, inputData.length);
        RESULTS.put(SUM, BUILTIN_FUNCTIONS.SUM.getValue(numberInputData));
        RESULTS.put(AVERAGE, BUILTIN_FUNCTIONS.AVG.getValue(numberInputData));
        RESULTS.put(MIN, BUILTIN_FUNCTIONS.MIN.getValue(numberInputData));
        RESULTS.put(MAX, BUILTIN_FUNCTIONS.MAX.getValue(numberInputData));
    }

    @Test
    public void evaluate() {
        final List<KiePMMLNameValue> kiePMMLNameValues = Collections.singletonList(new KiePMMLNameValue(FIELD_NAME,
                                                                                                        FIELD_VALUE));
        ProcessingDTO processingDTO = new ProcessingDTO(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), kiePMMLNameValues);
        RESULTS.forEach((function, expected) -> {
            KiePMMLAggregate kiePMMLAggregate = KiePMMLAggregate.builder(FIELD_NAME, Collections.emptyList(), function).build();
            assertEquals(expected, kiePMMLAggregate.evaluate(processingDTO));
        });
    }

    @Test(expected = KiePMMLException.class)
    public void evaluateUnmanaged() {
        final List<KiePMMLNameValue> kiePMMLNameValues = Arrays.asList(new KiePMMLNameValue(FIELD_NAME,
                                                                                            FIELD_VALUE),
                                                                       new KiePMMLNameValue(GROUP_BY_NAME,
                                                                                            GROUP_BY_VALUE));
        ProcessingDTO processingDTO = new ProcessingDTO(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), kiePMMLNameValues);
        KiePMMLAggregate kiePMMLAggregate = KiePMMLAggregate.builder(FIELD_NAME, Collections.emptyList(), AGGREGATE_FUNCTIONS.MULTISET)
                .withGroupField(GROUP_BY_NAME)
                .build();
        kiePMMLAggregate.evaluate(processingDTO);
    }

}