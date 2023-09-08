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

import java.util.Arrays;
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
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.IN_NOTIN;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLCompoundPredicateTest {

    private final String COMPOUND_PREDICATE_NAME = "COMPOUNDPREDICATENAME";
    private final String SIMPLE_SET_PREDICATE_STRING_NAME = "SIMPLESETPREDICATESTRINGNAME";
    private final String SIMPLE_SET_PREDICATE_INT_NAME = "SIMPLESETPREDICATEINTNAME";

    @Test
    void evaluateCompoundPredicateSinglePredicate() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> stringValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateString =
                getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_STRING_NAME,
                                                                                                 stringValues,
                                                                                                 arrayType,
                                                                                                 IN_NOTIN.IN);
        KiePMMLCompoundPredicate kiePMMLCompoundPredicate = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.AND,
                                                                                        Collections.singletonList(kiePMMLSimpleSetPredicateString));
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT");
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0));
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isTrue();

        arrayType = ARRAY_TYPE.INT;
        List<Object> intValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateInt = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_INT_NAME,
                                                                                              intValues,
                                                                                              arrayType,
                                                                                              IN_NOTIN.NOT_IN);
        kiePMMLCompoundPredicate = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.AND,
                                                               Collections.singletonList(kiePMMLSimpleSetPredicateInt));
        inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0));
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isFalse();
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, "234");
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluateCompoundPredicateAnd() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> stringValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateString =
                getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_STRING_NAME,
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
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isFalse();

        inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT");
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, "234");
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isFalse();

        inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0));
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, "234");
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluateCompoundPredicateOr() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> stringValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateString = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_STRING_NAME,
                                                                                                 stringValues,
                                                                                                 arrayType,
                                                                                                 IN_NOTIN.IN);
        arrayType = ARRAY_TYPE.INT;
        List<Object> intValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateInt =
                getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_INT_NAME,
                                                                                              intValues,
                                                                                              arrayType,
                                                                                              IN_NOTIN.NOT_IN);
        KiePMMLCompoundPredicate kiePMMLCompoundPredicate = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.OR,
                                                                                        Arrays.asList(kiePMMLSimpleSetPredicateString, kiePMMLSimpleSetPredicateInt));
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT");
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0));
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isFalse();

        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0));
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0));
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isTrue();

        inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT");
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, "234");
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isTrue();

        inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0));
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, "234");
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluateCompoundPredicateSurrogate() {
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        List<Object> stringValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateString = getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_STRING_NAME,
                                                                                                 stringValues,
                                                                                                 arrayType,
                                                                                                 IN_NOTIN.IN);
        arrayType = ARRAY_TYPE.INT;
        List<Object> intValues = getObjects(arrayType, 4);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicateInt =
                getKiePMMLSimpleSetPredicate(SIMPLE_SET_PREDICATE_INT_NAME,
                                                                                              intValues,
                                                                                              arrayType,
                                                                                              IN_NOTIN.NOT_IN);
        KiePMMLCompoundPredicate kiePMMLCompoundPredicate = getKiePMMLCompoundPredicate(BOOLEAN_OPERATOR.SURROGATE,
                                                                                        Arrays.asList(kiePMMLSimpleSetPredicateString, kiePMMLSimpleSetPredicateInt));
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT"); // This predicate verify the "IN" condition
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0)); // This predicate verify the "NOT_IN" condition
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isFalse();

        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0)); // This predicate verify the "IN" condition
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, intValues.get(0)); // This predicate verify the "NOT_IN" condition
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isTrue();

        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, "NOT"); // This predicate verify the "IN" condition
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, 1); // This predicate verify the "NOT_IN" condition
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isFalse();

        inputData.put(SIMPLE_SET_PREDICATE_STRING_NAME, stringValues.get(0)); // This predicate verify the "IN"
        // condition
        inputData.put(SIMPLE_SET_PREDICATE_INT_NAME, 1); // This predicate verify the "NOT_IN" condition
        assertThat(kiePMMLCompoundPredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void orOperator() {
        Boolean aBoolean = null;
        boolean aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2)).isTrue();

        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = false;
        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = true;
        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2)).isTrue();

        aBoolean = false;
        aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2)).isTrue();

        aBoolean = true;
        aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.orOperator(aBoolean, aBoolean2)).isTrue();
    }

    @Test
    void andOperator() {
        Boolean aBoolean = null;
        boolean aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2)).isTrue();

        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = false;
        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = true;
        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = false;
        aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = true;
        aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.andOperator(aBoolean, aBoolean2)).isTrue();
    }

    @Test
    void xorOperator() {
        Boolean aBoolean = null;
        boolean aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2)).isTrue();

        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = false;
        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = true;
        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2)).isTrue();

        aBoolean = false;
        aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2)).isTrue();

        aBoolean = true;
        aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.xorOperator(aBoolean, aBoolean2)).isFalse();

    }

    @Test
    void surrogateOperator() {
        Boolean aBoolean = null;
        boolean aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2)).isTrue();

        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = false;
        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = true;
        aBoolean2 = false;
        assertThat(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2)).isTrue();

        aBoolean = false;
        aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2)).isFalse();

        aBoolean = true;
        aBoolean2 = true;
        assertThat(KiePMMLCompoundPredicate.surrogateOperator(aBoolean, aBoolean2)).isTrue();
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