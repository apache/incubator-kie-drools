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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class DroolsAbstractUniCollectingGroupByCollectorProcessor<ResultContainer, InTuple, KeyTuple, OutTuple>
        extends DroolsAbstractGroupByCollectorProcessor<InTuple, KeyTuple, OutTuple> {

    private final Map<KeyTuple, ResultContainer> containersMap = new HashMap<>(0);
    // LinkedHashMap to maintain a consistent iteration order of resulting pairs.
    private final Map<KeyTuple, OutTuple> resultMap = new LinkedHashMap<>(0);

    @Override
    public Runnable accumulate(InTuple input) {
        KeyTuple key = toKey(input);
        ResultContainer container = containersMap.computeIfAbsent(key, __ -> newContainer());
        Runnable undo = process(input, container);
        addTuple(key);
        return () -> {
            undo.run();
            long currentCount = removeTuple(key);
            if (currentCount == 0L) {
                containersMap.remove(key);
                resultMap.remove(key);
            }
        };
    }

    @Override
    public Collection<OutTuple> finish() {
        Set<KeyTuple> dirtyTupleSet = clearDirtyTupleSet();
        if (!dirtyTupleSet.isEmpty()) {
            for (KeyTuple tuple : dirtyTupleSet) {
                resultMap.put(tuple, toResult(tuple, containersMap.get(tuple)));
            }
        }
        return resultMap.values();
    }

    protected abstract KeyTuple toKey(InTuple tuple);

    protected abstract ResultContainer newContainer();

    protected abstract Runnable process(InTuple tuple, ResultContainer container);

    protected abstract OutTuple toResult(KeyTuple key, ResultContainer container);

}
