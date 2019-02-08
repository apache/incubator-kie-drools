/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream.common;

import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

public final class ConstraintCollectors {

    public static <A> UniConstraintCollector<A, ?, Long> count() {
        return new UniConstraintCollector<>(
                () -> new long[1],
                (resultContainer1, a) -> {
                    resultContainer1[0] ++;
                    return (() -> resultContainer1[0] --);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A> UniConstraintCollector<A, ?, Integer> sum(ToIntFunction<? super A> groupValueMapping) {
        return new UniConstraintCollector<>(
                () -> new int[1],
                (resultContainer1, a) -> {
                    int value = groupValueMapping.applyAsInt(a);
                    resultContainer1[0] += value;
                    return (() -> resultContainer1[0] -= value);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A> UniConstraintCollector<A, ?, Long> sum(ToLongFunction<? super A> groupValueMapping) {
        return new UniConstraintCollector<>(
                () -> new long[1],
                (resultContainer1, a) -> {
                    long value = groupValueMapping.applyAsLong(a);
                    resultContainer1[0] += value;
                    return (() -> resultContainer1[0] -= value);
                },
                resultContainer -> resultContainer[0]);
    }

    private ConstraintCollectors() {
    }

}
