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
package org.kie.pmml.commons.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.CLOSURE;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLMiningFieldTest {

    @Test
    void isAllowedValueNoLimit() {
        final KiePMMLMiningField kiePMMLMiningField = KiePMMLMiningField
                .builder("NAME", Collections.emptyList())
                .build();
        assertThat(kiePMMLMiningField.isMatching(null)).isTrue();
        assertThat(kiePMMLMiningField.isMatching("VALUE")).isTrue();
    }

    @Test
    void isAllowedValueWithAllowedValues() {
        final List<String> allowedValues = getCommonAllowedValues();
        final KiePMMLMiningField kiePMMLMiningField = KiePMMLMiningField
                .builder("NAME", Collections.emptyList())
                .withAllowedValues(allowedValues)
                .build();
        assertThat(kiePMMLMiningField.isMatching(null)).isFalse();
        assertThat(kiePMMLMiningField.isMatching("VALUE")).isFalse();
        allowedValues.forEach(allowedValue -> assertThat(kiePMMLMiningField.isMatching(allowedValue)).isTrue());
    }

    @Test
    void isAllowedValueWithIntervals() {
        final List<KiePMMLInterval> intervals = getCommonIntervals();
        final KiePMMLMiningField kiePMMLMiningField = KiePMMLMiningField
                .builder("NAME", Collections.emptyList())
                .withIntervals(intervals)
                .build();
        assertThat(kiePMMLMiningField.isMatching(null)).isFalse();
        assertThat(kiePMMLMiningField.isMatching("VALUE")).isFalse();
        intervals.forEach(interval -> {
            double delta = (interval.getRightMargin().doubleValue() - interval.getLeftMargin().doubleValue()) / 2;
            Number toVerify = interval.getLeftMargin().doubleValue() + delta;
            assertThat(kiePMMLMiningField.isMatching(toVerify)).isTrue();
        });
    }

    @Test
    void isAllowedValueWithAllowedValuesAndIntervals() {
        final List<String> allowedValues = getCommonAllowedValues();
        final List<KiePMMLInterval> intervals = getCommonIntervals();
        final KiePMMLMiningField kiePMMLMiningField = KiePMMLMiningField
                .builder("NAME", Collections.emptyList())
                .withAllowedValues(allowedValues)
                .withIntervals(intervals)
                .build();
        assertThat(kiePMMLMiningField.isMatching(null)).isFalse();
        assertThat(kiePMMLMiningField.isMatching("VALUE")).isFalse();
        allowedValues.forEach(allowedValue -> assertThat(kiePMMLMiningField.isMatching(allowedValue)).isTrue());
        intervals.forEach(interval -> {
            double delta = (interval.getRightMargin().doubleValue() - interval.getLeftMargin().doubleValue()) / 2;
            Number toVerify = interval.getLeftMargin().doubleValue() + delta;
            assertThat(kiePMMLMiningField.isMatching(toVerify)).isFalse();
        });
    }

    private List<String> getCommonAllowedValues() {
        return IntStream
                .range(0, 3)
                .mapToObj(i -> "ALLOWED-" + i)
                .collect(Collectors.toList());
    }

    private List<KiePMMLInterval> getCommonIntervals() {
        final Random rnd = new Random();
        return IntStream
                .range(0, 3)
                .mapToObj(i -> {
                    final int leftMargin = rnd.nextInt(10);
                    final int rightMargin = leftMargin + (rnd.nextInt(10) + 10);
                    final CLOSURE closure = CLOSURE.values()[rnd.nextInt(CLOSURE.values().length)];
                    return new KiePMMLInterval(leftMargin,
                                               rightMargin,
                                               closure);
                })
                .collect(Collectors.toList());
    }
}