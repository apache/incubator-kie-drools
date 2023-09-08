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
package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import java.util.List;
import java.util.function.Function;

public class TwoValueLogicFunctions {
    private static NNAllFunction allFunction = NNAllFunction.INSTANCE;
    private static NNAnyFunction anyFunction = NNAnyFunction.INSTANCE;
    private static NNSumFunction sumFunction = NNSumFunction.INSTANCE;
    private static NNMeanFunction meanFunction = NNMeanFunction.INSTANCE;
    private static NNCountFunction countFunction = NNCountFunction.INSTANCE;
    private static NNMaxFunction maxFunction = NNMaxFunction.INSTANCE;
    private static NNMinFunction minFunction = NNMinFunction.INSTANCE;
    private static NNMedianFunction medianFunction = NNMedianFunction.INSTANCE;
    private static NNModeFunction modeFunction = NNModeFunction.INSTANCE;

    private static NNStddevFunction stddevFunction = NNStddevFunction.INSTANCE;

    public static Boolean all(List<Boolean> list) {
        return allFunction.invoke(list).cata(e -> Boolean.FALSE, Function.identity());
    }

    public static Boolean any(List<Boolean> list) {
        return anyFunction.invoke(list).cata(e -> Boolean.FALSE, Function.identity());
    }

    public static Number sum(List<Boolean> list) {
        return sumFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Number mean(List<Number> list) {
        return meanFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Number count(List<Number> list) {
        return countFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Object max(List<Number> list) {
        return maxFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Object min(List<Number> list) {
        return minFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Number median(List<Number> list) {
        return medianFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static List mode(List<Number> list) {
        return modeFunction.invoke(list).cata(e -> null, Function.identity());
    }

    public static Number stddev(List<Number> list) {
        return stddevFunction.invoke(list).cata(e -> null, Function.identity());
    }
}
