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

package org.optaplanner.core.impl.score.stream.drools.common;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class DroolsAbstractUniCollectingGroupByAccumulator<ResultContainer, InTuple, KeyTuple, OutTuple>
        implements GroupByAccumulator<InTuple, OutTuple> {

    // Containers may be identical in type and contents, yet they should still not count as the same container.
    private final Map<ResultContainer, Long> containersInUseMap = new IdentityHashMap<>(0);
    // LinkedHashMap to maintain a consistent iteration order of resulting pairs.
    private final Map<KeyTuple, ResultContainer> containersMap = new LinkedHashMap<>(0);
    // Transient as Spotbugs complains otherwise ("non-transient non-serializable instance field").
    // It doesn't make sense to serialize this anyway, as it is recreated every time.
    private final transient Set<OutTuple> resultSet = new LinkedHashSet<>(0);

    @Override
    public Runnable accumulate(InTuple input) {
        KeyTuple key = toKey(input);
        ResultContainer container = containersMap.computeIfAbsent(key, __ -> newContainer());
        Runnable undo = process(input, container);
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

    private static Long increment(Long count) {
        return count == null ? 1L : count + 1L;
    }

    private static Long decrement(Long count) {
        return count == 1L ? null : count - 1L;
    }

    @Override
    public Set<OutTuple> finish() {
        resultSet.clear();
        for (Map.Entry<KeyTuple, ResultContainer> entry : containersMap.entrySet()) {
            resultSet.add(toResult(entry.getKey(), entry.getValue()));
        }
        return resultSet;
    }

    protected abstract KeyTuple toKey(InTuple tuple);

    protected abstract ResultContainer newContainer();

    protected abstract Runnable process(InTuple tuple, ResultContainer container);

    protected abstract OutTuple toResult(KeyTuple key, ResultContainer container);

}
