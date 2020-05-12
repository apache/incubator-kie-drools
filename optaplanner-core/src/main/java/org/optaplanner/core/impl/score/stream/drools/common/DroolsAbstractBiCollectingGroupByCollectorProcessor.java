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

public abstract class DroolsAbstractBiCollectingGroupByCollectorProcessor<ResultContainer1, ResultContainer2, InTuple, KeyTuple, OutTuple>
        extends DroolsAbstractGroupByCollectorProcessor<InTuple, KeyTuple, OutTuple> {

    private final Map<KeyTuple, ResultContainer1> containersMap1 = new HashMap<>(0);
    private final Map<KeyTuple, ResultContainer2> containersMap2 = new HashMap<>(0);
    // LinkedHashMap to maintain a consistent iteration order of resulting pairs.
    private final Map<KeyTuple, OutTuple> resultMap = new LinkedHashMap<>(0);

    @Override
    public Runnable accumulate(InTuple input) {
        KeyTuple key = toKey(input);
        ResultContainer1 container1 = containersMap1.computeIfAbsent(key, __ -> newFirstContainer());
        ResultContainer2 container2 = containersMap2.computeIfAbsent(key, __ -> newSecondContainer());
        Runnable undo1 = processFirst(input, container1);
        Runnable undo2 = processSecond(input, container2);
        addTuple(key);
        return () -> {
            undo1.run();
            undo2.run();
            long currentCount = removeTuple(key);
            if (currentCount == 0L) {
                containersMap1.remove(key);
                containersMap2.remove(key);
                resultMap.remove(key);
            }
        };
    }

    @Override
    public Collection<OutTuple> finish() {
        Set<KeyTuple> dirtyTupleSet = clearDirtyTupleSet();
        if (!dirtyTupleSet.isEmpty()) {
            for (KeyTuple tuple : dirtyTupleSet) {
                resultMap.put(tuple, toResult(tuple, containersMap1.get(tuple), containersMap2.get(tuple)));
            }
        }
        return resultMap.values();
    }

    protected abstract KeyTuple toKey(InTuple tuple);

    protected abstract ResultContainer1 newFirstContainer();

    protected abstract ResultContainer2 newSecondContainer();

    protected abstract Runnable processFirst(InTuple tuple, ResultContainer1 container);

    protected abstract Runnable processSecond(InTuple tuple, ResultContainer2 container);

    protected abstract OutTuple toResult(KeyTuple key, ResultContainer1 container, ResultContainer2 container2);

}
