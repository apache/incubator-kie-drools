/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;

final class DroolsUniGroupByAccumulator<A, B, ResultContainer, NewB> implements Serializable {

    // Containers may be identical in type and contents, yet they should still not count as the same container.
    private final Map<ResultContainer, Long> containersInUseMap = new IdentityHashMap<>(0);
    // LinkedHashMap to maintain a consistent iteration order of resulting pairs.
    private final Map<A, ResultContainer> containersMap = new LinkedHashMap<>(0);
    private final Supplier<ResultContainer> supplier;
    private final BiFunction<ResultContainer, B, Runnable> accumulator;
    private final Function<ResultContainer, NewB> finisher;
    // Transient as Spotbugs complains otherwise ("non-transient non-serializable instance field").
    // It doesn't make sense to serialize this anyway, as it is recreated every time.
    private final transient Set<BiTuple<A, NewB>> resultSet = new LinkedHashSet<>(0);

    public DroolsUniGroupByAccumulator(UniConstraintCollector<B, ResultContainer, NewB> collector) {
        this.supplier = collector.supplier();
        this.accumulator = collector.accumulator();
        this.finisher = collector.finisher();
    }

    private static Long increment(Long count) {
        return count == null ? 1L : count + 1L;
    }

    private static Long decrement(Long count) {
        return count == 1L ? null : count - 1L;
    }

    public Runnable accumulate(A key, B value) {
        ResultContainer container = containersMap.computeIfAbsent(key, __ -> supplier.get());
        Runnable undo = accumulator.apply(container, value);
        containersInUseMap.compute(container, (__, count) -> increment(count)); // Increment use counter.
        return () -> {
            undo.run();
            // Decrement use counter. If 0, container is ignored during finishing. Removes empty groups from results.
            Long currentCount = containersInUseMap.compute(container, (__, count) -> decrement(count));
            if (currentCount == null) {
                containersMap.remove(key);
            }
        };
    }

    public Set<BiTuple<A, NewB>> finish() {
        resultSet.clear();
        for (Map.Entry<A, ResultContainer> entry: containersMap.entrySet()) {
            ResultContainer container = entry.getValue();
            resultSet.add(new BiTuple<>(entry.getKey(), finisher.apply(container)));
        }
        return resultSet;
    }
}
