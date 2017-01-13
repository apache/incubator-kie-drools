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

import org.kie.dmn.feel.runtime.FEELFunction;

import java.util.stream.Stream;

public class BuiltInFunctions {

    protected final static FEELFunction[] FUNCTIONS = new FEELFunction[]{
            new DateFunction(),
            new TimeFunction(),
            new DateTimeFunction(),
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
            new MaxFunction(),
            new SumFunction(),
            new MeanFunction(),
            new AllFunction(),
            new AnyFunction(),
            new SublistFunction(),
            new AppendFunction(),
            new ConcatenateFunction(),
            new InsertBeforeFunction(),
            new RemoveFunction(),
            new ReverseFunction(),
            new IndexOfFunction(),
            new UnionFunction(),
            new DistinctValuesFunction(),
            new FlattenFunction(),
            new DecimalFunction(),
            new FloorFunction(),
            new CeilingFunction(),
            new DecisionTableFunction(),
            new NotFunction(),
            new SortFunction(),

            // additional functions not part of the spec version 1.1
            new NowFunction(),
            new TodayFunction()
    };

    public static FEELFunction[] getFunctions() {
        return FUNCTIONS;
    }

    public static <T extends FEELFunction> T getFunction( Class<T> functionClazz ) {
        return (T) Stream.of( FUNCTIONS ).filter( f -> functionClazz.isAssignableFrom( f.getClass() ) ).findFirst().get();
    }
}
