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
package org.kie.dmn.feel.runtime.functions;

import java.util.stream.Stream;

import org.kie.dmn.feel.runtime.FEELFunction;
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

public class BuiltInFunctions {

    protected static final FEELFunction[] FUNCTIONS = new FEELFunction[]{
            DateFunction.INSTANCE,
            TimeFunction.INSTANCE,
            DateAndTimeFunction.INSTANCE,
            DurationFunction.INSTANCE,
            YearsAndMonthsFunction.INSTANCE,
            StringFunction.INSTANCE,
            NumberFunction.INSTANCE,
            SubstringFunction.INSTANCE,
            SubstringBeforeFunction.INSTANCE,
            SubstringAfterFunction.INSTANCE,
            StringLengthFunction.INSTANCE,
            StringUpperCaseFunction.INSTANCE,
            StringLowerCaseFunction.INSTANCE,
            ContainsFunction.INSTANCE,
            StartsWithFunction.INSTANCE,
            EndsWithFunction.INSTANCE,
            MatchesFunction.INSTANCE,
            ReplaceFunction.INSTANCE,
            ListContainsFunction.INSTANCE,
            CountFunction.INSTANCE,
            MinFunction.INSTANCE,
            MaxFunction.INSTANCE,
            SumFunction.INSTANCE,
            MeanFunction.INSTANCE,
            SublistFunction.INSTANCE,
            AppendFunction.INSTANCE,
            ConcatenateFunction.INSTANCE,
            InsertBeforeFunction.INSTANCE,
            RemoveFunction.INSTANCE,
            ReverseFunction.INSTANCE,
            IndexOfFunction.INSTANCE,
            UnionFunction.INSTANCE,
            DistinctValuesFunction.INSTANCE,
            FlattenFunction.INSTANCE,
            DecimalFunction.INSTANCE,
            FloorFunction.INSTANCE,
            CeilingFunction.INSTANCE,
            DecisionTableFunction.INSTANCE,
            NotFunction.INSTANCE,
            SortFunction.INSTANCE,
            GetEntriesFunction.INSTANCE,
            GetValueFunction.INSTANCE,

            AllFunction.INSTANCE,
            AnyFunction.INSTANCE,
            AbsFunction.INSTANCE,
            ModuloFunction.INSTANCE,
            ProductFunction.INSTANCE,
            SplitFunction.INSTANCE,
            StddevFunction.INSTANCE,
            ModeFunction.INSTANCE,
            SqrtFunction.INSTANCE,
            LogFunction.INSTANCE,
            ExpFunction.INSTANCE,
            EvenFunction.INSTANCE,
            OddFunction.INSTANCE,
            MedianFunction.INSTANCE,

            DayOfWeekFunction.INSTANCE,
            DayOfYearFunction.INSTANCE,
            MonthOfYearFunction.INSTANCE,
            WeekOfYearFunction.INSTANCE,

            IsFunction.INSTANCE,

            // Interval based logic
            AfterFunction.INSTANCE,
            BeforeFunction.INSTANCE,
            CoincidesFunction.INSTANCE,
            StartsFunction.INSTANCE,
            StartedByFunction.INSTANCE,
            FinishesFunction.INSTANCE,
            FinishedByFunction.INSTANCE,
            DuringFunction.INSTANCE,
            IncludesFunction.INSTANCE,
            OverlapsFunction.INSTANCE,
            OverlapsBeforeFunction.INSTANCE,
            OverlapsAfterFunction.INSTANCE,
            MeetsFunction.INSTANCE,
            MetByFunction.INSTANCE,
            ListReplaceFunction.INSTANCE
    };

    public static FEELFunction[] getFunctions() {
        return FUNCTIONS;
    }

    @SuppressWarnings("unchecked")
    public static <T extends FEELFunction> T getFunction(Class<T> functionClazz) {
        return Stream.of(FUNCTIONS)
                .filter(f -> functionClazz.isAssignableFrom(f.getClass()))
                .map(f -> (T) f)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find function by class " + functionClazz.getCanonicalName() + "!"));
    }

    @SuppressWarnings("unchecked")
    public static <T extends FEELFunction> T getFunction(String functionName) {
        return Stream.of(FUNCTIONS)
                .filter(f -> f.getName().equals(functionName))
                .map(f -> (T) f)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find function by name " + functionName + "!"));
    }
}
