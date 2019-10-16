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

package org.optaplanner.core.api.score.stream.bi;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;

/**
 * Usually created with {@link ConstraintCollectors}.
 * Used by {@link BiConstraintStream#groupBy(BiFunction, BiConstraintCollector)}, ...
 * <p>
 * Loosely based on JDK's {@link Collector}, but it returns an undo operation for each accumulation
 * to enable incremental score calculation in {@link ConstraintStream constraint streams}.
 * @param <A> the type of the first fact of the tuple in the source {@link BiConstraintStream}
 * @param <B> the type of the second fact of the tuple in the source {@link BiConstraintStream}
 * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
 * @param <Result_> the type of the fact of the tuple in the destination {@link ConstraintStream}
 * @see ConstraintCollectors
 */
public interface BiConstraintCollector<A, B, ResultContainer_, Result_> {

    /**
     * A lambda that creates the result container, one for each group key combination.
     * @return never null
     */
    Supplier<ResultContainer_> supplier();

    /**
     * A lambda that extracts data from the matched facts,
     * accumulates it in the result container
     * and returns an undo operation for that accumulation.
     * @return never null, the undo operation. This lamdba is called when the facts no longer matches.
     */
    TriFunction<ResultContainer_, A, B, Runnable> accumulator();

    /**
     * A lambda that converts the result container into the result.
     * @return never null
     */
    Function<ResultContainer_, Result_> finisher();

}
