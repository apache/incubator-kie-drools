/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.runtime.functions;

import java.util.stream.Stream;

import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.model.api.GwtIncompatible;

public class BuiltInFunctions {

    protected static final FEELFunction[] FUNCTIONS = new FEELFunction[]{
            new DateFunction(),
            new TimeFunction(),
            new DateAndTimeFunction(),
            new DurationFunction(),
            new YearsAndMonthsFunction(),
            new StringFunction(),
            new NumberFunction(),
            new SubstringFunction(),
            new SubstringBeforeFunction(),
            new SubstringAfterFunction(),
            new StringLengthFunction(),
            new StringUpperCaseFunction(),
            new StringLowerCaseFunction(),
            new ContainsFunction(),
            new StartsWithFunction(),
            new EndsWithFunction(),
            new MatchesFunction(),
            new ReplaceFunction(),
            new ListContainsFunction(),
            new CountFunction(),
            new MinFunction(),
            new SumFunction(),
            new MeanFunction(),
            new SublistFunction(),
            new AppendFunction(),
            new ConcatenateFunction(),

            AbsFunction.INSTANCE,
            ModuloFunction.INSTANCE,
            ProductFunction.INSTANCE,
            ModeFunction.INSTANCE,

            EvenFunction.INSTANCE,
            OddFunction.INSTANCE,
            MedianFunction.INSTANCE,

            DayOfWeekFunction.INSTANCE,
            DayOfYearFunction.INSTANCE,
            MonthOfYearFunction.INSTANCE,
            IsFunction.INSTANCE,
    };

    public static FEELFunction[] getFunctions() {
        return FUNCTIONS;
    }

    @GwtIncompatible
    public static <T extends FEELFunction> T getFunction(final Class<T> functionClazz) {
        return (T) Stream.of(FUNCTIONS)
                .filter(f -> functionClazz.isAssignableFrom(f.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find function by class " + functionClazz.getCanonicalName() + "!"));
    }
}
