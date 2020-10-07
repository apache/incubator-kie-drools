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

package org.kie.pmml.commons.model.predicates;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.kie.pmml.api.enums.ARRAY_TYPE;
import org.kie.pmml.api.enums.IN_NOTIN;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KiePMMLSimpleSetPredicateTest {

    private final String SIMPLE_SET_PREDICATE_NAME = "SIMPLESETPREDICATENAME";

    @Test
    public void evaluateStringIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "NOT");
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "NOT");
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertTrue(kiePMMLSimpleSetPredicate.evaluate(inputData));
    }

    @Test
    public void evaluateStringNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "NOT");
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "NOT");
        assertTrue(kiePMMLSimpleSetPredicate.evaluate(inputData));
    }

    @Test
    public void evaluateIntIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.INT;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "234");
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "432");
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertTrue(kiePMMLSimpleSetPredicate.evaluate(inputData));
    }

    @Test
    public void evaluateIntNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.INT;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "234");
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "432");
        assertTrue(kiePMMLSimpleSetPredicate.evaluate(inputData));
    }

    @Test
    public void evaluateRealIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.REAL;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "23.4");
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "4.32");
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertTrue(kiePMMLSimpleSetPredicate.evaluate(inputData));
    }

    @Test
    public void evaluateRealNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.REAL;
        List<Object> values = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "23.4");
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, values.get(0));
        assertFalse(kiePMMLSimpleSetPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_NAME, "4.32");
        assertTrue(kiePMMLSimpleSetPredicate.evaluate(inputData));
    }

    @Test
    public void evaluationStringIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        assertFalse(kiePMMLSimpleSetPredicate.evaluation("NOT"));
        assertTrue(kiePMMLSimpleSetPredicate.evaluation(values.get(0)));
    }

    @Test
    public void evaluationStringNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        assertFalse(kiePMMLSimpleSetPredicate.evaluation(values.get(0)));
        assertTrue(kiePMMLSimpleSetPredicate.evaluation("NOT"));
    }

    @Test
    public void evaluationIntIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.INT;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        assertFalse(kiePMMLSimpleSetPredicate.evaluation("234"));
        assertTrue(kiePMMLSimpleSetPredicate.evaluation(values.get(0)));
    }

    @Test
    public void evaluationIntNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.INT;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        assertFalse(kiePMMLSimpleSetPredicate.evaluation(values.get(0)));
        assertTrue(kiePMMLSimpleSetPredicate.evaluation("234"));
    }

    @Test
    public void evaluationRealIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.REAL;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.IN);
        assertFalse(kiePMMLSimpleSetPredicate.evaluation("23.4"));
        assertTrue(kiePMMLSimpleSetPredicate.evaluation(values.get(0)));
    }

    @Test
    public void evaluationRealNotIn() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.REAL;
        List<Object> values = getObjects(arrayType, 1);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = getKiePMMLSimpleSetPredicate(values, arrayType,
                                                                                           IN_NOTIN.NOT_IN);
        assertFalse(kiePMMLSimpleSetPredicate.evaluation(values.get(0)));
        assertTrue(kiePMMLSimpleSetPredicate.evaluation("23.4"));
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