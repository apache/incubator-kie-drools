/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime.functions.extended;

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
import org.kie.dmn.feel.runtime.functions.interval.OverlappedAfterByFunction;
import org.kie.dmn.feel.runtime.functions.interval.OverlappedBeforeByFunction;
import org.kie.dmn.feel.runtime.functions.interval.OverlappedByFunction;
import org.kie.dmn.feel.runtime.functions.interval.OverlapsAfterFunction;
import org.kie.dmn.feel.runtime.functions.interval.OverlapsBeforeFunction;
import org.kie.dmn.feel.runtime.functions.interval.OverlapsFunction;
import org.kie.dmn.feel.runtime.functions.interval.StartedByFunction;
import org.kie.dmn.feel.runtime.functions.interval.StartsFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAllFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAnyFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNCountFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMaxFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMeanFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMedianFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMinFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNModeFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNStddevFunction;
import org.kie.dmn.feel.runtime.functions.twovaluelogic.NNSumFunction;

/**
 * additional functions not part of the spec version 1.1
 */
public class KieExtendedDMNFunctions {

    protected static final FEELFunction[] FUNCTIONS = new FEELFunction[]{
                                                                         TimeFunction.INSTANCE,
                                                                         DateFunction.INSTANCE,
                                                                         DurationFunction.INSTANCE,

                                                                         // additional functions not part of the spec version 1.1
                                                                         new NowFunction(),
                                                                         new TodayFunction(),
                                                                         new CodeFunction(),
                                                                         new InvokeFunction(),

                                                                         // CQL based, two value logic functions
                                                                        NNAnyFunction.INSTANCE,
                                                                        NNAllFunction.INSTANCE,
                                                                        NNCountFunction.INSTANCE,
                                                                        NNMaxFunction.INSTANCE,
                                                                        NNMeanFunction.INSTANCE,
                                                                        NNMedianFunction.INSTANCE,
                                                                        NNMinFunction.INSTANCE,
                                                                        NNModeFunction.INSTANCE,
                                                                        NNStddevFunction.INSTANCE,
                                                                        NNSumFunction.INSTANCE,

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
                                                                        OverlappedByFunction.INSTANCE,
                                                                        OverlapsBeforeFunction.INSTANCE,
                                                                        OverlappedBeforeByFunction.INSTANCE,
                                                                        OverlapsAfterFunction.INSTANCE,
                                                                        OverlappedAfterByFunction.INSTANCE,
                                                                        MeetsFunction.INSTANCE,
                                                                        MetByFunction.INSTANCE

    };

    public static FEELFunction[] getFunctions() {
        return FUNCTIONS;
    }

    public static <T extends FEELFunction> T getFunction(Class<T> functionClazz) {
        return (T) Stream.of(FUNCTIONS)
                .filter(f -> functionClazz.isAssignableFrom(f.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find function by class " + functionClazz.getCanonicalName() + "!"));
    }
}
