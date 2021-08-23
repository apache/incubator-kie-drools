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

import java.util.Arrays;
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
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.IN_NOTIN;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KiePMMLCompoundPredicateTest {

    private final String COMPOUND_PREDICATE_NAME = "COMPOUNDPREDICATENAME";
    private final String SIMPLE_SET_PREDICATE_STRING_NAME = "SIMPLESETPREDICATESTRINGNAME";
    private final String SIMPLE_SET_PREDICATE_INT_NAME = "SIMPLESETPREDICATEINTNAME";

    @Test
    public void evaluateCompoundPredicateSinglePredicate() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> stringValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateString = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_STRING_NAME,
                                                                                                 stringValues,
                                                                                                 arrayType,
                                                                                                 IN_NOTIN.IN);
        KiePMMLCompoundPredicate kiePMMLCompoundPredicate = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.AND, Collections.singletonList(kiePMMLSimpleSetPredicateString));
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT");
        assertFalse(kiePMMLCompoundPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0));
        assertTrue(kiePMMLCompoundPredicate.evaluate(inputData));

        arrayType = ARRAY_TYPE.INT;
        List<Object> intValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateInt = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_INT_NAME,
                                                                                              intValues,
                                                                                              arrayType,
                                                                                              IN_NOTIN.NOT_IN);
        kiePMMLCompoundPredicate = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.AND, Collections.singletonList(kiePMMLSimpleSetPredicateInt));
        inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0));
        assertFalse(kiePMMLCompoundPredicate.evaluate(inputData));
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, "234");
        assertTrue(kiePMMLCompoundPredicate.evaluate(inputData));
    }

    @Test
    public void evaluateCompoundPredicateAnd() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> stringValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateString = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_STRING_NAME,
                                                                                                 stringValues,
                                                                                                 arrayType,
                                                                                                 IN_NOTIN.IN);
        arrayType = ARRAY_TYPE.INT;
        List<Object> intValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateInt = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_INT_NAME,
                                                                                              intValues,
                                                                                              arrayType,
                                                                                              IN_NOTIN.NOT_IN);
        KiePMMLCompoundPredicate kiePMMLCompoundPredicate = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.AND,
                                                                                        Arrays.asList(kiePMMLSimpleSetPredicateString, kiePMMLSimpleSetPredicateInt));
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0));
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0));
        assertFalse(kiePMMLCompoundPredicate.evaluate(inputData));

        inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT");
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, "234");
        assertFalse(kiePMMLCompoundPredicate.evaluate(inputData));

        inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0));
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, "234");
        assertTrue(kiePMMLCompoundPredicate.evaluate(inputData));
    }

    @Test
    public void evaluateCompoundPredicateOr() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> stringValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateString = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_STRING_NAME,
                                                                                                 stringValues,
                                                                                                 arrayType,
                                                                                                 IN_NOTIN.IN);
        arrayType = ARRAY_TYPE.INT;
        List<Object> intValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateInt = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_INT_NAME,
                                                                                              intValues,
                                                                                              arrayType,
                                                                                              IN_NOTIN.NOT_IN);
        KiePMMLCompoundPredicate kiePMMLCompoundPredicate = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.OR,
                                                                                        Arrays.asList(kiePMMLSimpleSetPredicateString, kiePMMLSimpleSetPredicateInt));
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT");
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0));
        assertFalse(kiePMMLCompoundPredicate.evaluate(inputData));

        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0));
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0));
        assertTrue(kiePMMLCompoundPredicate.evaluate(inputData));

        inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT");
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, "234");
        assertTrue(kiePMMLCompoundPredicate.evaluate(inputData));

        inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0));
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, "234");
        assertTrue(kiePMMLCompoundPredicate.evaluate(inputData));
    }

    @Test
    public void evaluateCompoundPredicateSurrogate() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> stringValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateString = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_STRING_NAME,
                                                                                                 stringValues,
                                                                                                 arrayType,
                                                                                                 IN_NOTIN.IN);
        arrayType = ARRAY_TYPE.INT;
        List<Object> intValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateInt = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_INT_NAME,
                                                                                              intValues,
                                                                                              arrayType,
                                                                                              IN_NOTIN.NOT_IN);
        KiePMMLCompoundPredicate kiePMMLCompoundPredicate = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.SURROGATE,
                                                                                        Arrays.asList(kiePMMLSimpleSetPredicateString, kiePMMLSimpleSetPredicateInt));
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT"); // This predicate verify the "IN" condition
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0)); // This predicate verify the "NOT_IN" condition
        assertFalse(kiePMMLCompoundPredicate.evaluate(inputData));

        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0)); // This predicate verify the "IN" condition
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0)); // This predicate verify the "NOT_IN" condition
        assertTrue(kiePMMLCompoundPredicate.evaluate(inputData));

        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT"); // This predicate verify the "IN" condition
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, 1); // This predicate verify the "NOT_IN" condition
        assertFalse(kiePMMLCompoundPredicate.evaluate(inputData));

        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0)); // This predicate verify the "IN" condition
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, 1); // This predicate verify the "NOT_IN" condition
        assertTrue(kiePMMLCompoundPredicate.evaluate(inputData));
    }

    @Test
    public void orOperator() {
        Boolean aBoolean = null;
        Boolean aBoolean2 = true;
        assertTrue(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2));

        aBoolean2 = false;
        assertFalse(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2));

        aBoolean = false;
        aBoolean2 = false;
        assertFalse(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2));

        aBoolean = true;
        aBoolean2 = false;
        assertTrue(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2));

        aBoolean = false;
        aBoolean2 = true;
        assertTrue(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2));

        aBoolean = true;
        aBoolean2 = true;
        assertTrue(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2));
    }

    @Test
    public void andOperator() {
        Boolean aBoolean = null;
        Boolean aBoolean2 = true;
        assertTrue(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2));

        aBoolean2 = false;
        assertFalse(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2));

        aBoolean = false;
        aBoolean2 = false;
        assertFalse(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2));

        aBoolean = true;
        aBoolean2 = false;
        assertFalse(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2));

        aBoolean = false;
        aBoolean2 = true;
        assertFalse(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2));

        aBoolean = true;
        aBoolean2 = true;
        assertTrue(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2));
    }

    @Test
    public void xorOperator() {
        Boolean aBoolean = null;
        Boolean aBoolean2 = true;
        assertTrue(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2));

        aBoolean2 = false;
        assertFalse(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2));

        aBoolean = false;
        aBoolean2 = false;
        assertFalse(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2));

        aBoolean = true;
        aBoolean2 = false;
        assertTrue(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2));

        aBoolean = false;
        aBoolean2 = true;
        assertTrue(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2));

        aBoolean = true;
        aBoolean2 = true;
        assertFalse(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2));

    }

    @Test
    public void surrogateOperator() {
        Boolean aBoolean = null;
        Boolean aBoolean2 = true;
        assertTrue(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2));

        aBoolean2 = false;
        assertFalse(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2));

        aBoolean = false;
        aBoolean2 = false;
        assertFalse(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2));

        aBoolean = true;
        aBoolean2 = false;
        assertTrue(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2));

        aBoolean = false;
        aBoolean2 = true;
        assertFalse(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2));

        aBoolean = true;
        aBoolean2 = true;
        assertTrue(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2));
    }

    private KiePMMLCompoundPredicate getKiePMMLCompoundPredicate(final BOOLEAN_OPERATOR booleanOperator,
                                                                   final List<KiePMMLPredicate> kiePMMLPredicates) {

        return KiePMMLCompoundPredicate.builder(Collections.emptyList(),
                                                booleanOperator)
                .withKiePMMLPredicates(kiePMMLPredicates)
                .build();
    }

    private KiePMMLSimpleSetPredicate getKiePMMLSimpleSetPredicate(final String predicateName,
                                                                    final List<Object> values,
                                                                   final ARRAY_TYPE arrayType, final IN_NOTIN inNotIn) {
        return KiePMMLSimpleSetPredicate.builder(predicateName,
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