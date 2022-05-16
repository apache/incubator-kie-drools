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

package org.kie.pmml.api.enums.builtinfunctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.models.Interval;
import org.kie.pmml.api.models.MiningField;

import static org.assertj.core.api.Assertions.assertThat;

public class BooleanFunctionsTest {

    public final static List<BooleanFunctions> supportedBooleanFunctions;
    public final static List<BooleanFunctions> unsupportedBooleanFunctions;
    private final static MiningField EMPTY_MINING_FIELD = new MiningField("TEST",
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null);

    static {
        supportedBooleanFunctions = new ArrayList<>();
        supportedBooleanFunctions.add(BooleanFunctions.EQUAL);
        supportedBooleanFunctions.add(BooleanFunctions.NOT_EQUAL);
        supportedBooleanFunctions.add(BooleanFunctions.LESS_THAN);
        supportedBooleanFunctions.add(BooleanFunctions.LESS_OR_EQUAL);
        supportedBooleanFunctions.add(BooleanFunctions.GREATER_THAN);
        supportedBooleanFunctions.add(BooleanFunctions.GREATER_OR_EQUAL);
        supportedBooleanFunctions.add(BooleanFunctions.AND);
        supportedBooleanFunctions.add(BooleanFunctions.OR);
        supportedBooleanFunctions.add(BooleanFunctions.NOT);
        supportedBooleanFunctions.add(BooleanFunctions.IS_MISSING);
        supportedBooleanFunctions.add(BooleanFunctions.IS_NOT_MISSING);
        supportedBooleanFunctions.add(BooleanFunctions.IS_VALID);
        supportedBooleanFunctions.add(BooleanFunctions.IS_NOT_VALID);
        supportedBooleanFunctions.add(BooleanFunctions.IS_IN);
        supportedBooleanFunctions.add(BooleanFunctions.IS_NOT_IN);
        supportedBooleanFunctions.add(BooleanFunctions.IF);

        unsupportedBooleanFunctions = new ArrayList<>();
    }

    @Test
    public void getIsMissingValueCorrectInput() {
        Object[] input1 = {null};
        Object retrieved = BooleanFunctions.IS_MISSING.getValue(input1, EMPTY_MINING_FIELD);
        assertThat((boolean) retrieved).isTrue();
        Object[] input2 = {35};
        retrieved = BooleanFunctions.IS_MISSING.getValue(input2, EMPTY_MINING_FIELD);
        assertThat((boolean) retrieved).isFalse();
        for (INVALID_VALUE_TREATMENT_METHOD invalidValueTreatmentMethod : INVALID_VALUE_TREATMENT_METHOD.values()) {
            MiningField referredByFieldRef = getReferredByFieldRef(
                    invalidValueTreatmentMethod,
                    null,
                    Arrays.asList(new Interval(20, 29),
                                  new Interval(41, 50)));
            boolean expected = INVALID_VALUE_TREATMENT_METHOD.AS_MISSING.equals(invalidValueTreatmentMethod);
            assertThat(BooleanFunctions.IS_MISSING.getValue(input2, referredByFieldRef)).isEqualTo(expected);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsMissingValueWrongSizeInput() {
        final Object[] input = {34, 34, 34};
        BooleanFunctions.IS_MISSING.getValue(input, EMPTY_MINING_FIELD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsMissingValueNoMiningField() {
        final Object[] input = {34};
        BooleanFunctions.IS_MISSING.getValue(input, null);
    }

    @Test
    public void getIsNotMissingValueCorrectInput() {
        Object[] input1 = {35};
        Object retrieved = BooleanFunctions.IS_NOT_MISSING.getValue(input1, EMPTY_MINING_FIELD);
        assertThat((boolean) retrieved).isTrue();
        Object[] input2 = {null};
        retrieved = BooleanFunctions.IS_NOT_MISSING.getValue(input2, EMPTY_MINING_FIELD);
        assertThat((boolean) retrieved).isFalse();
        for (INVALID_VALUE_TREATMENT_METHOD invalidValueTreatmentMethod : INVALID_VALUE_TREATMENT_METHOD.values()) {
            MiningField referredByFieldRef = getReferredByFieldRef(
                    invalidValueTreatmentMethod,
                    null,
                    Arrays.asList(new Interval(20, 29), new Interval(41, 50)));
            boolean expected = !INVALID_VALUE_TREATMENT_METHOD.AS_MISSING.equals(invalidValueTreatmentMethod);
            assertThat(BooleanFunctions.IS_NOT_MISSING.getValue(input1, referredByFieldRef)).isEqualTo(expected);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsNotMissingValueWrongSizeInput() {
        final Object[] input = {34, 34, 34};
        BooleanFunctions.IS_NOT_MISSING.getValue(input, EMPTY_MINING_FIELD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsNotMissingValueNoMiningField() {
        final Object[] input = {34};
        BooleanFunctions.IS_NOT_MISSING.getValue(input, null);
    }

    @Test
    public void getIsValidValueCorrectInput() {
        Object[] input1 = {35};
        MiningField referredByFieldRef = getReferredByFieldRef(null,
                                                               null,
                                                               Arrays.asList(new Interval(20, 29), new Interval(30,
                                                                                                                40),
                                                                             new Interval(41, 50)));
        Object retrieved = BooleanFunctions.IS_VALID.getValue(input1, referredByFieldRef);
        assertThat((boolean) retrieved).isTrue();
        referredByFieldRef = getReferredByFieldRef(
                null,
                null,
                Arrays.asList(new Interval(20, 29), new Interval(41, 50)));
        retrieved = BooleanFunctions.IS_VALID.getValue(input1, referredByFieldRef);
        assertThat((boolean) retrieved).isFalse();

        referredByFieldRef = getReferredByFieldRef(null,
                                                   Arrays.asList("123", "35"),
                                                   Arrays.asList(new Interval(20, 29), new Interval(41, 50)));
        retrieved = BooleanFunctions.IS_VALID.getValue(input1, referredByFieldRef);
        assertThat((boolean) retrieved).isTrue();

        referredByFieldRef = getReferredByFieldRef(null,
                                                   Arrays.asList("123", "36"),
                                                   Arrays.asList(new Interval(20, 29), new Interval(41, 50)));
        retrieved = BooleanFunctions.IS_VALID.getValue(input1, referredByFieldRef);
        assertThat((boolean) retrieved).isFalse();

        Object[] input2 = {"VALUE"};
        referredByFieldRef = getReferredByFieldRef(null,
                                                   Arrays.asList("123", "VALUE"),
                                                   Collections.emptyList());
        retrieved = BooleanFunctions.IS_VALID.getValue(input2, referredByFieldRef);
        assertThat((boolean) retrieved).isTrue();

        referredByFieldRef = getReferredByFieldRef(null,
                                                   Arrays.asList("123", "VELUE"),
                                                   Collections.emptyList());
        retrieved = BooleanFunctions.IS_VALID.getValue(input2, referredByFieldRef);
        assertThat((boolean) retrieved).isFalse();
        Object[] input3 = {null};
        retrieved = BooleanFunctions.IS_VALID.getValue(input3, referredByFieldRef);
        assertThat((boolean) retrieved).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsValidValueWrongSizeInput() {
        final Object[] input = {34, 34};
        MiningField referredByFieldRef = getReferredByFieldRef(null,
                                                               null,
                                                               Arrays.asList(new Interval(20, 29), new Interval(30,
                                                                                                                40),
                                                                             new Interval(41, 50)));
        BooleanFunctions.IS_VALID.getValue(input, referredByFieldRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsValidValueWrongTypeInput() {
        final Object[] input = {"34"};
        MiningField referredByFieldRef = getReferredByFieldRef(null,
                                                               null,
                                                               Arrays.asList(new Interval(20, 29), new Interval(30,
                                                                                                                40),
                                                                             new Interval(41, 50)));
        BooleanFunctions.IS_VALID.getValue(input, referredByFieldRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsValidValueWrongNoMiningField() {
        final Object[] input = {34};
        BooleanFunctions.IS_VALID.getValue(input, null);
    }

    @Test
    public void getIsNotValidValueCorrectInput() {
        Object[] input1 = {35};
        MiningField referredByFieldRef = getReferredByFieldRef(null,
                                                               null,
                                                               Arrays.asList(new Interval(20, 29), new Interval(30,
                                                                                                                40),
                                                                             new Interval(41, 50)));
        Object retrieved = BooleanFunctions.IS_NOT_VALID.getValue(input1, referredByFieldRef);
        assertThat((boolean) retrieved).isFalse();
        referredByFieldRef = getReferredByFieldRef(null,
                                                   null,
                                                   Arrays.asList(new Interval(20, 29), new Interval(41, 50)));
        retrieved = BooleanFunctions.IS_NOT_VALID.getValue(input1, referredByFieldRef);
        assertThat((boolean) retrieved).isTrue();

        referredByFieldRef = getReferredByFieldRef(null,
                                                   Arrays.asList("123", "35"),
                                                   Arrays.asList(new Interval(20, 29), new Interval(41, 50)));
        retrieved = BooleanFunctions.IS_NOT_VALID.getValue(input1, referredByFieldRef);
        assertThat((boolean) retrieved).isFalse();

        referredByFieldRef = getReferredByFieldRef(INVALID_VALUE_TREATMENT_METHOD.AS_MISSING,
                                                   Arrays.asList("123", "36"),
                                                   Arrays.asList(new Interval(20, 29), new Interval(41, 50)));
        retrieved = BooleanFunctions.IS_NOT_VALID.getValue(input1, referredByFieldRef);
        assertThat((boolean) retrieved).isFalse();

        referredByFieldRef = getReferredByFieldRef(INVALID_VALUE_TREATMENT_METHOD.AS_IS,
                                                   Arrays.asList("123", "36"),
                                                   Arrays.asList(new Interval(20, 29), new Interval(41, 50)));
        retrieved = BooleanFunctions.IS_NOT_VALID.getValue(input1, referredByFieldRef);
        assertThat((boolean) retrieved).isTrue();

        referredByFieldRef = getReferredByFieldRef(null,
                                                   Arrays.asList("123", "36"),
                                                   Arrays.asList(new Interval(20, 29), new Interval(41, 50)));
        retrieved = BooleanFunctions.IS_NOT_VALID.getValue(input1, referredByFieldRef);
        assertThat((boolean) retrieved).isTrue();

        Object[] input2 = {"VALUE"};
        referredByFieldRef = getReferredByFieldRef(null,
                                                   Arrays.asList("123", "VALUE"),
                                                   Collections.emptyList());
        retrieved = BooleanFunctions.IS_NOT_VALID.getValue(input2, referredByFieldRef);
        assertThat((boolean) retrieved).isFalse();

        referredByFieldRef = getReferredByFieldRef(null,
                                                   Arrays.asList("123", "VELUE"),
                                                   Collections.emptyList());
        retrieved = BooleanFunctions.IS_NOT_VALID.getValue(input2, referredByFieldRef);
        assertThat((boolean) retrieved).isTrue();
        Object[] input3 = {null};
        retrieved = BooleanFunctions.IS_NOT_VALID.getValue(input3, referredByFieldRef);
        assertThat((boolean) retrieved).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsNotValidValueWrongSizeInput() {
        final Object[] input = {34, 34};
        MiningField referredByFieldRef = getReferredByFieldRef(null,
                                                               null,
                                                               Arrays.asList(new Interval(20, 29), new Interval(30,
                                                                                                                40),
                                                                             new Interval(41, 50)));
        BooleanFunctions.IS_NOT_VALID.getValue(input, referredByFieldRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsNotValidValueWrongTypeInput() {
        final Object[] input = {"34"};
        MiningField referredByFieldRef = getReferredByFieldRef(null,
                                                               null,
                                                               Arrays.asList(new Interval(20, 29), new Interval(30,
                                                                                                                40),
                                                                             new Interval(41, 50)));
        BooleanFunctions.IS_NOT_VALID.getValue(input, referredByFieldRef);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsNotValidValueWrongNoMiningField() {
        final Object[] input = {34};
        BooleanFunctions.IS_NOT_VALID.getValue(input, null);
    }

    @Test
    public void getEqualValueCorrectInput() {
        Object[] input1 = {35, 12};
        Object retrieved = BooleanFunctions.EQUAL.getValue(input1, null);
        assertThat((boolean) retrieved).isFalse();
        Object[] input2 = {35, 35};
        retrieved = BooleanFunctions.EQUAL.getValue(input2, null);
        assertThat((boolean) retrieved).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEqualValueWrongSizeInput() {
        final Object[] input = {34, 34, 34};
        BooleanFunctions.EQUAL.getValue(input, null);
    }

    @Test
    public void getNotEqualValueCorrectInput() {
        Object[] input1 = {35, 12};
        Object retrieved = BooleanFunctions.NOT_EQUAL.getValue(input1, null);
        assertThat((boolean) retrieved).isTrue();
        Object[] input2 = {35, 35};
        retrieved = BooleanFunctions.NOT_EQUAL.getValue(input2, null);
        assertThat((boolean) retrieved).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNotEqualValueWrongSizeInput() {
        final Object[] input = {34, 34, 34};
        BooleanFunctions.NOT_EQUAL.getValue(input, null);
    }

    @Test
    public void getLessThanValueCorrectInput() {
        Object[] input1 = {35, 37};
        Object retrieved = BooleanFunctions.LESS_THAN.getValue(input1, null);
        assertThat((boolean) retrieved).isTrue();
        Object[] input2 = {35, 35};
        retrieved = BooleanFunctions.LESS_THAN.getValue(input2, null);
        assertThat((boolean) retrieved).isFalse();
        Object[] input3 = {35, 12};
        retrieved = BooleanFunctions.LESS_THAN.getValue(input3, null);
        assertThat((boolean) retrieved).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLessThanValueWrongTypeInput() {
        final Object[] input = {34, "A"};
        BooleanFunctions.LESS_THAN.getValue(input, null);
    }

    @Test
    public void getLessOrEqualValueCorrectInput() {
        Object[] input1 = {35, 37};
        Object retrieved = BooleanFunctions.LESS_OR_EQUAL.getValue(input1, null);
        assertThat((boolean) retrieved).isTrue();
        Object[] input2 = {35, 35};
        retrieved = BooleanFunctions.LESS_OR_EQUAL.getValue(input2, null);
        assertThat((boolean) retrieved).isTrue();
        Object[] input3 = {35, 12};
        retrieved = BooleanFunctions.LESS_OR_EQUAL.getValue(input3, null);
        assertThat((boolean) retrieved).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLessOrEqualValueWrongTypeInput() {
        final Object[] input = {34, "A"};
        BooleanFunctions.LESS_OR_EQUAL.getValue(input, null);
    }

    @Test
    public void getGreaterThanValueCorrectInput() {
        Object[] input1 = {35, 37};
        Object retrieved = BooleanFunctions.GREATER_THAN.getValue(input1, null);
        assertThat((boolean) retrieved).isFalse();
        Object[] input2 = {35, 35};
        retrieved = BooleanFunctions.GREATER_THAN.getValue(input2, null);
        assertThat((boolean) retrieved).isFalse();
        Object[] input3 = {35, 12};
        retrieved = BooleanFunctions.GREATER_THAN.getValue(input3, null);
        assertThat((boolean) retrieved).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGreaterThanValueWrongTypeInput() {
        final Object[] input = {34, "A"};
        BooleanFunctions.GREATER_THAN.getValue(input, null);
    }

    @Test
    public void getGreaterOrEqualValueCorrectInput() {
        Object[] input1 = {35, 37};
        Object retrieved = BooleanFunctions.GREATER_OR_EQUAL.getValue(input1, null);
        assertThat((boolean) retrieved).isFalse();
        Object[] input2 = {35, 35};
        retrieved = BooleanFunctions.GREATER_OR_EQUAL.getValue(input2, null);
        assertThat((boolean) retrieved).isTrue();
        Object[] input3 = {35, 12};
        retrieved = BooleanFunctions.GREATER_OR_EQUAL.getValue(input3, null);
        assertThat((boolean) retrieved).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGreaterOrEqualValueWrongTypeInput() {
        final Object[] input = {34, "A"};
        BooleanFunctions.GREATER_OR_EQUAL.getValue(input, null);
    }

    @Test
    public void getAndValueCorrectInput() {
        Object[] input1 = {true, Boolean.valueOf("false")};
        Object retrieved = BooleanFunctions.AND.getValue(input1, null);
        assertThat((boolean) retrieved).isFalse();
        Object[] input2 = {true, Boolean.valueOf("true")};
        retrieved = BooleanFunctions.AND.getValue(input2, null);
        assertThat((boolean) retrieved).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAndValueWrongTypeInput() {
        final Object[] input = {true, "false"};
        BooleanFunctions.AND.getValue(input, null);
    }

    @Test
    public void getOrValueCorrectInput() {
        Object[] input1 = {true, Boolean.valueOf("false")};
        Object retrieved = BooleanFunctions.OR.getValue(input1, null);
        assertThat((boolean) retrieved).isTrue();
        Object[] input2 = {false, Boolean.valueOf("true")};
        retrieved = BooleanFunctions.OR.getValue(input2, null);
        assertThat((boolean) retrieved).isTrue();
        Object[] input3 = {false, Boolean.valueOf("false")};
        retrieved = BooleanFunctions.OR.getValue(input3, null);
        assertThat((boolean) retrieved).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOrValueWrongTypeInput() {
        final Object[] input = {true, "false"};
        BooleanFunctions.OR.getValue(input, null);
    }

    @Test
    public void getNotValueCorrectInput() {
        Object[] input1 = {true};
        Object retrieved = BooleanFunctions.NOT.getValue(input1, null);
        assertThat((boolean) retrieved).isFalse();
        Object[] input2 = {Boolean.valueOf("false")};
        retrieved = BooleanFunctions.NOT.getValue(input2, null);
        assertThat((boolean) retrieved).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNotValueWrongTypeInput() {
        final Object[] input = {"false"};
        BooleanFunctions.NOT.getValue(input, null);
    }

    @Test
    public void getIsInValueCorrectInput() {
        Object[] input1 = {35, 12, 35, 435, "A"};
        Object retrieved = BooleanFunctions.IS_IN.getValue(input1, null);
        assertThat((boolean) retrieved).isTrue();
        Object[] input2 = {35, 36, "35"};
        retrieved = BooleanFunctions.IS_IN.getValue(input2, null);
        assertThat((boolean) retrieved).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsInEqualValueWrongSizeInput() {
        final Object[] input = {34};
        BooleanFunctions.IS_IN.getValue(input, null);
    }

    @Test
    public void getIsNotInValueCorrectInput() {
        Object[] input1 = {35, 36, "35"};
        Object retrieved = BooleanFunctions.IS_NOT_IN.getValue(input1, null);
        assertThat((boolean) retrieved).isTrue();
        Object[] input2 = {35, 12, 35, 435, "A"};
        retrieved = BooleanFunctions.IS_NOT_IN.getValue(input2, null);
        assertThat((boolean) retrieved).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsNotInEqualValueWrongSizeInput() {
        final Object[] input = {34};
        BooleanFunctions.IS_NOT_IN.getValue(input, null);
    }

    @Test
    public void getIfFValueCorrectInput() {
        Object[] input1 = {true, 36, "35"};
        Object retrieved = BooleanFunctions.IF.getValue(input1, null);
        assertThat(retrieved).isEqualTo(36);
        Object[] input2 = {false, 12, 35};
        retrieved = BooleanFunctions.IF.getValue(input2, null);
        assertThat(retrieved).isEqualTo(35);
        Object[] input3 = {false, 12};
        retrieved = BooleanFunctions.IF.getValue(input3, null);
        assertThat(retrieved).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIfFEqualValueWrongSizeInput() {
        final Object[] input = {34};
        BooleanFunctions.IF.getValue(input, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIfFEqualValueWrongTypeInput() {
        final Object[] input = {"true", 36, "35"};
        BooleanFunctions.IF.getValue(input, null);
    }

    private MiningField getReferredByFieldRef(INVALID_VALUE_TREATMENT_METHOD invalidValueTreatmentMethod,
                                              List<String> allowedValues, List<Interval> intervals) {
        return new MiningField("TEST",
                               null,
                               null,
                               null,
                               null,
                               invalidValueTreatmentMethod,
                               null,
                               null,
                               allowedValues,
                               intervals);
    }
}