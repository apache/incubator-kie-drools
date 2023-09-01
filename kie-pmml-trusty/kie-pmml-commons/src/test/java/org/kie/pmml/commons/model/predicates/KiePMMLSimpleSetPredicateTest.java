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
package org.kie.pmml.commons.model.predicates;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.ARRAY_TYPE;
import org.kie.pmml.api.enums.IN_NOTIN;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLSimpleSetPredicateTest {

    private final String SIMPLE_SET_PREDICATE_NAME = "SIMPLESETPREDICATENAME";

    @Test
    void evaluateStringIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "NOT");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "NOT");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluateStringNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "NOT");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "NOT");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluateIntIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.INT;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "234");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "432");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluateIntNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.INT;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "234");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "432");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluateRealIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.REAL;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "23.4");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "4.32");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluateRealNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.REAL;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "23.4");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "4.32");
        assertThat(kiePMMLSimpleSetPredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluationStringIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        assertThat(kiePMMLSimpleSetPredicate.evaluation("NOT")).isFalse();
        assertThat(kiePMMLSimpleSetPredicate.evaluation(values.get(0))).isTrue();
    }

    @Test
    void evaluationStringNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        assertThat(kiePMMLSimpleSetPredicate.evaluation(values.get(0))).isFalse();
        assertThat(kiePMMLSimpleSetPredicate.evaluation("NOT")).isTrue();
    }

    @Test
    void evaluationIntIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.INT;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        assertThat(kiePMMLSimpleSetPredicate.evaluation("234")).isFalse();
        assertThat(kiePMMLSimpleSetPredicate.evaluation(values.get(0))).isTrue();
    }

    @Test
    void evaluationIntNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.INT;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        assertThat(kiePMMLSimpleSetPredicate.evaluation(values.get(0))).isFalse();
        assertThat(kiePMMLSimpleSetPredicate.evaluation("234")).isTrue();
    }

    @Test
    void evaluationRealIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.REAL;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        assertThat(kiePMMLSimpleSetPredicate.evaluation("23.4")).isFalse();
        assertThat(kiePMMLSimpleSetPredicate.evaluation(values.get(0))).isTrue();
    }

    @Test
    void evaluationRealNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.REAL;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        assertThat(kiePMMLSimpleSetPredicate.evaluation(values.get(0))).isFalse();
        assertThat(kiePMMLSimpleSetPredicate.evaluation("23.4")).isTrue();
    }

    private KiePMMLSimpleSetPredicate getKiePMMLSimpleSetPredicate(final List<Object> values,
                                                                   final ARRAY_TYPE arrayType, final IN_NOTIN inNotIn) {
        return KiePMMLSimpleSetPredicate.builder(SIMPLE_SET_PREDICATE_NAME,
                                                 Collections.emptyList(),
                                                 arrayType,
                                                 inNotIn)
                .withValues(values)
                .build();
    }

    private List<Object> getObjects(ARRAY_TYPE arrayType, int size) {
        return IntStream.range(0, size).mapToObj(index -> {
                    switch (arrayType) {
                        case INT:
                            return new Random().nextInt(40);
                        case REAL:
                            return new Random().nextDouble();
                        case STRING:
                            return UUID.randomUUID().toString();
                        default:
                            return null;
                    }
                })
                .collect(Collectors.toList());
    }
}