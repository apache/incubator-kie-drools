/*
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
package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.FEELBooleanFunction;
import org.kie.dmn.feel.runtime.FEELCollectionFunction;
import org.kie.dmn.feel.runtime.FEELDateFunction;
import org.kie.dmn.feel.runtime.FEELDateTimeFunction;
import org.kie.dmn.feel.runtime.FEELDurationFunction;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.FEELNumberFunction;
import org.kie.dmn.feel.runtime.FEELStringFunction;
import org.kie.dmn.feel.runtime.FEELTimeFunction;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.functions.interval.AfterFunction;
import org.kie.dmn.feel.runtime.functions.interval.BeforeFunction;
import org.kie.dmn.feel.runtime.functions.interval.CoincidesFunction;
import org.kie.dmn.feel.runtime.functions.interval.DuringFunction;
import org.kie.dmn.feel.runtime.functions.interval.FinishedByFunction;
import org.kie.dmn.feel.runtime.functions.interval.FinishesFunction;
import org.kie.dmn.feel.runtime.functions.interval.IncludesFunction;
import org.kie.dmn.feel.runtime.functions.interval.MeetsFunction;
import org.kie.dmn.feel.runtime.functions.interval.MetByFunction;
import org.kie.dmn.feel.runtime.functions.interval.OverlapsAfterFunction;
import org.kie.dmn.feel.runtime.functions.interval.OverlapsBeforeFunction;
import org.kie.dmn.feel.runtime.functions.interval.OverlapsFunction;
import org.kie.dmn.feel.runtime.functions.interval.StartedByFunction;
import org.kie.dmn.feel.runtime.functions.interval.StartsFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAllFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAnyFunction;

import static org.assertj.core.api.Assertions.assertThat;

public class BFEELValueFunctionTest {

    private static Set<FEELBooleanFunction> FEEL_BOOLEAN_FUNCTIONS;
    private static Set<FEELCollectionFunction> FEEL_COLLECTION_FUNCTIONS;
    private static Set<FEELDateFunction> FEEL_DATE_FUNCTIONS;
    private static Set<FEELDateTimeFunction> FEEL_DATE_TIME_FUNCTIONS;
    private static Set<FEELDurationFunction> FEEL_DURATION_FUNCTIONS;
    private static Set<FEELStringFunction> FEEL_STRING_FUNCTIONS;
    private static Set<FEELTimeFunction> FEEL_TIME_FUNCTIONS;
    private static Set<FEELNumberFunction> FEEL_NUMBER_FUNCTIONS;

    @BeforeAll
    public static void setup() {
        // The functions have been manually copied from BuiltInFunctions.class,
        // and sorted accordingly their returned value
        FEEL_BOOLEAN_FUNCTIONS = new HashSet<>();
        FEEL_BOOLEAN_FUNCTIONS.add(AfterFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(AllFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(AnyFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(BeforeFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(CoincidesFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(ContainsFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(DuringFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(EndsWithFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(EvenFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(FinishedByFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(FinishesFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(IncludesFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(IsFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(ListContainsFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(MatchesFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(MeetsFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(MetByFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(NNAllFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(NNAnyFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(NotFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(OddFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(OverlapsAfterFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(OverlapsBeforeFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(OverlapsFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(StartedByFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(StartsFunction.INSTANCE);
        FEEL_BOOLEAN_FUNCTIONS.add(StartsWithFunction.INSTANCE);

        FEEL_COLLECTION_FUNCTIONS = new HashSet<>();
        FEEL_COLLECTION_FUNCTIONS.add(AppendFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(ConcatenateFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(DistinctValuesFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(FlattenFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(GetEntriesFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(IndexOfFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(InsertBeforeFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(ListReplaceFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(ModeFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(RemoveFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(ReverseFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(SortFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(SplitFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(SublistFunction.INSTANCE);
        FEEL_COLLECTION_FUNCTIONS.add(UnionFunction.INSTANCE);

        FEEL_DATE_FUNCTIONS = new HashSet<>();
        FEEL_DATE_FUNCTIONS.add(DateFunction.INSTANCE);
        FEEL_DATE_FUNCTIONS.add(TodayFunction.INSTANCE);

        FEEL_DATE_TIME_FUNCTIONS = new HashSet<>();
        FEEL_DATE_TIME_FUNCTIONS.add(DateAndTimeFunction.INSTANCE);
        FEEL_DATE_TIME_FUNCTIONS.add(NowFunction.INSTANCE);

        FEEL_DURATION_FUNCTIONS = new HashSet<>();
        FEEL_DURATION_FUNCTIONS.add(DurationFunction.INSTANCE);
        FEEL_DURATION_FUNCTIONS.add(YearsAndMonthsFunction.INSTANCE);

        FEEL_NUMBER_FUNCTIONS = new HashSet<>();
        FEEL_NUMBER_FUNCTIONS.add(AbsFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(CeilingFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(CountFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(DayOfYearFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(DecimalFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(ExpFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(FloorFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(LogFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(MeanFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(MedianFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(ModuloFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(NumberFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(ProductFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(RoundDownFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(RoundHalfDownFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(RoundHalfUpFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(RoundUpFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(SqrtFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(StddevFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(StringLengthFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(SumFunction.INSTANCE);
        FEEL_NUMBER_FUNCTIONS.add(WeekOfYearFunction.INSTANCE);

        FEEL_STRING_FUNCTIONS = new HashSet<>();
        FEEL_STRING_FUNCTIONS.add(DayOfWeekFunction.INSTANCE);
        FEEL_STRING_FUNCTIONS.add(MonthOfYearFunction.INSTANCE);
        FEEL_STRING_FUNCTIONS.add(ReplaceFunction.INSTANCE);
        FEEL_STRING_FUNCTIONS.add(StringFunction.INSTANCE);
        FEEL_STRING_FUNCTIONS.add(StringJoinFunction.INSTANCE);
        FEEL_STRING_FUNCTIONS.add(StringLowerCaseFunction.INSTANCE);
        FEEL_STRING_FUNCTIONS.add(StringUpperCaseFunction.INSTANCE);
        FEEL_STRING_FUNCTIONS.add(SubstringAfterFunction.INSTANCE);
        FEEL_STRING_FUNCTIONS.add(SubstringBeforeFunction.INSTANCE);
        FEEL_STRING_FUNCTIONS.add(SubstringFunction.INSTANCE);

        FEEL_TIME_FUNCTIONS = new HashSet<>();
        FEEL_TIME_FUNCTIONS.add(TimeFunction.INSTANCE);
    }

    @Test
    void testBooleanFunctions() {
        commonTest(FEEL_BOOLEAN_FUNCTIONS, false);
    }

    @Test
    void testCollectionFunctions() {
        commonTest(FEEL_COLLECTION_FUNCTIONS, Collections.emptyList());
    }

    @Test
    void testDateFunctions() {
        commonTest(FEEL_DATE_FUNCTIONS, LocalDate.of(1970, 1, 1));
    }

    @Test
    void testDateTimeFunctions() {
        commonTest(FEEL_DATE_TIME_FUNCTIONS, LocalDateTime.of(1970, 1, 1, 0, 0, 0));
    }

    @Test
    void testDurationFunctions() {
        commonTest(FEEL_DURATION_FUNCTIONS, ComparablePeriod.parse("P0M"));
    }

    @Test
    void testNumberFunctions() {
        commonTest(FEEL_NUMBER_FUNCTIONS, BigDecimal.ZERO);
    }

    @Test
    void testRangeFunctions() {
        Object retrievedObj = RangeFunction.INSTANCE.defaultValue();
        assertThat(retrievedObj).isNotNull().isInstanceOf(Range.class);
        Range retrieved = (Range) retrievedObj;
        assertThat(retrieved.getLowBoundary()).isEqualTo(Range.RangeBoundary.OPEN);
        assertThat(retrieved.getHighBoundary()).isEqualTo(Range.RangeBoundary.OPEN);
        assertThat(retrieved.getLowEndPoint()).isEqualTo(BigDecimal.ZERO);
        assertThat(retrieved.getHighEndPoint()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void testStringFunctions() {
        commonTest(FEEL_STRING_FUNCTIONS, "");
    }

    @Test
    void testTimeFunctions() {
        commonTest(FEEL_TIME_FUNCTIONS, OffsetTime.of(0, 0, 0, 0, ZoneOffset.ofHoursMinutes(0, 0)));
    }

    private void commonTest(Set<? extends FEELFunction> functions, Object expected) {
        functions.forEach((function) ->
                                                 assertThat(function.defaultValue()).isEqualTo(expected));
    }


}