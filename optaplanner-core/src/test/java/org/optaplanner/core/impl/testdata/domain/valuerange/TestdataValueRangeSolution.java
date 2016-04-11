/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.testdata.domain.valuerange;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataValueRangeSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataValueRangeSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataValueRangeSolution.class, TestdataValueRangeEntity.class);
    }

    private List<TestdataValueRangeEntity> entityList;

    private SimpleScore score;

    public TestdataValueRangeSolution() {
    }

    public TestdataValueRangeSolution(String code) {
        super(code);
    }

    @PlanningEntityCollectionProperty
    public List<TestdataValueRangeEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataValueRangeEntity> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @ValueRangeProvider(id = "intValueRange")
    public CountableValueRange<Integer> createIntValueRange() {
        return ValueRangeFactory.createIntValueRange(0, 3);
    }

    @ValueRangeProvider(id = "longValueRange")
    public CountableValueRange<Long> createLongValueRange() {
        return ValueRangeFactory.createLongValueRange(1_000L, 1_003L);
    }

    @ValueRangeProvider(id = "bigIntegerValueRange")
    public CountableValueRange<BigInteger> createBigIntegerValueRange() {
        return ValueRangeFactory.createBigIntegerValueRange(
                BigInteger.valueOf(1_000_000L), BigInteger.valueOf(1_000_003L));
    }

    @ValueRangeProvider(id = "bigDecimalValueRange")
    public CountableValueRange<BigDecimal> createBigDecimalValueRange() {
        return ValueRangeFactory.createBigDecimalValueRange(new BigDecimal("0.00"), new BigDecimal("0.03"));
    }

    @ValueRangeProvider(id = "localDateValueRange")
    public CountableValueRange<Temporal> createLocalDateValueRange() { // TODO FIXME return value
        return ValueRangeFactory.createTemporalValueRange(
                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 4), 1, ChronoUnit.DAYS);
    }

    @ValueRangeProvider(id = "localTimeValueRange")
    public CountableValueRange<Temporal> createLocaleTimeValueRange() {
        return ValueRangeFactory.createTemporalValueRange(
                LocalTime.of(10, 0), LocalTime.of(10, 3), 1, ChronoUnit.MINUTES);
    }

    @ValueRangeProvider(id = "localDateTimeValueRange")
    public CountableValueRange<Temporal> createLocaleDateTimeValueRange() {
        return ValueRangeFactory.createTemporalValueRange(
                LocalDateTime.of(2000, 1, 1, 10, 0), LocalDateTime.of(2000, 1, 1, 10, 3), 1, ChronoUnit.MINUTES);
    }

}
