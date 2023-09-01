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
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.OPERATOR;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class KiePMMLSimplePredicateTest {

    private final String SIMPLE_PREDICATE_NAME = "SIMPLEPREDICATENAME";

    @Test
    void evaluateStringEqual() {
        Object value = "43";
        KiePMMLSimplePredicate kiePMMLSimplePredicate = getKiePMMLSimplePredicate(OPERATOR.EQUAL, value);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "NOT");
        assertThat(kiePMMLSimplePredicate.evaluate(inputData)).isFalse();
        inputData = new HashMap<>();
        inputData.put(SIMPLE_PREDICATE_NAME, "NOT");
        assertThat(kiePMMLSimplePredicate.evaluate(inputData)).isFalse();
        inputData = new HashMap<>();
        inputData.put(SIMPLE_PREDICATE_NAME, value);
        assertThat(kiePMMLSimplePredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluateStringNotEqual() {
        Object value = "43";
        KiePMMLSimplePredicate kiePMMLSimplePredicate = getKiePMMLSimplePredicate(OPERATOR.NOT_EQUAL, value);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("FAKE", "NOT");
        assertThat(kiePMMLSimplePredicate.evaluate(inputData)).isFalse();
        inputData = new HashMap<>();
        inputData.put(SIMPLE_PREDICATE_NAME, value);
        assertThat(kiePMMLSimplePredicate.evaluate(inputData)).isFalse();
        inputData = new HashMap<>();
        inputData.put(SIMPLE_PREDICATE_NAME, "NOT");
        assertThat(kiePMMLSimplePredicate.evaluate(inputData)).isTrue();
    }

    @Test
    void evaluateStringIsNotMissing() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Object value = "43";
            KiePMMLSimplePredicate kiePMMLSimplePredicate = getKiePMMLSimplePredicate(OPERATOR.IS_NOT_MISSING, value);
            Map<String, Object> inputData = new HashMap<>();
            inputData.put(SIMPLE_PREDICATE_NAME, value);
            kiePMMLSimplePredicate.evaluate(inputData);
        });
    }

    @Test
    void evaluateStringIsMissing() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Object value = "43";
            KiePMMLSimplePredicate kiePMMLSimplePredicate = getKiePMMLSimplePredicate(OPERATOR.IS_MISSING, value);
            Map<String, Object> inputData = new HashMap<>();
            inputData.put(SIMPLE_PREDICATE_NAME, value);
            kiePMMLSimplePredicate.evaluate(inputData);
        });
    }

    @Test
    void evaluationStringEqual() {
        Object value = "43";
        KiePMMLSimplePredicate kiePMMLSimplePredicate = getKiePMMLSimplePredicate(OPERATOR.EQUAL, value);
        assertThat(kiePMMLSimplePredicate.evaluation("NOT")).isFalse();
        assertThat(kiePMMLSimplePredicate.evaluation(value)).isTrue();
    }

    @Test
    void evaluationStringNotEqual() {
        Object value = "43";
        KiePMMLSimplePredicate kiePMMLSimplePredicate = getKiePMMLSimplePredicate(OPERATOR.NOT_EQUAL, value);
        assertThat(kiePMMLSimplePredicate.evaluation(value)).isFalse();
        assertThat(kiePMMLSimplePredicate.evaluation("NOT")).isTrue();
    }

    @Test
    void evaluationStringIsNotMissing() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Object value = "43";
            KiePMMLSimplePredicate kiePMMLSimplePredicate = getKiePMMLSimplePredicate(OPERATOR.IS_NOT_MISSING, value);
            kiePMMLSimplePredicate.evaluation(value);
        });
    }

    @Test
    void evaluationStringIsMissing() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Object value = "43";
            KiePMMLSimplePredicate kiePMMLSimplePredicate = getKiePMMLSimplePredicate(OPERATOR.IS_MISSING, value);
            kiePMMLSimplePredicate.evaluation(value);
        });
    }

    private KiePMMLSimplePredicate getKiePMMLSimplePredicate(final OPERATOR operator,
                                                             final Object value) {
        return KiePMMLSimplePredicate.builder(SIMPLE_PREDICATE_NAME,
                                              Collections.emptyList(),
                                              operator)
                .withValue(value)
                .build();
    }
}