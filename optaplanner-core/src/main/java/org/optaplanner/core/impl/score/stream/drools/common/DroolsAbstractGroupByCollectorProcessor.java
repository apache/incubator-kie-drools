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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

abstract class DroolsAbstractGroupByCollectorProcessor<InTuple, KeyTuple, OutTuple>
        implements GroupByCollectorProcessor<InTuple, OutTuple> {

    private final Map<KeyTuple, Long> tuplesInUseMap = new HashMap<>(0);
    private Set<KeyTuple> dirtyTupleSet;

    private static Long increment(Long count) {
        return count == null ? 1L : count + 1L;
    }

    private static Long decrement(Long count) {
        return count == 1L ? null : count - 1L;
    }

    protected void addTuple(KeyTuple tuple) {
        tuplesInUseMap.compute(tuple, (__, count) -> increment(count));
        markDirty(tuple);
    }

    protected long removeTuple(KeyTuple tuple) {
        Long useCount = tuplesInUseMap.compute(tuple, (__, count) -> decrement(count));
        if (useCount == null) {
            unmarkDirty(tuple);
            return 0L;
        } else {
            markDirty(tuple);
            return useCount;
        }
    }

    private void markDirty(KeyTuple tuple) {
        if (dirtyTupleSet == null) {
            dirtyTupleSet = new LinkedHashSet<>(1); // When trying a move, there will often only be 1 change.
        }
        dirtyTupleSet.add(tuple);
    }

    private void unmarkDirty(KeyTuple tuple) {
        if (dirtyTupleSet != null) {
            dirtyTupleSet.remove(tuple);
            if (dirtyTupleSet.isEmpty()) {
                clearDirtyTupleSet();
            }
        }
    }

    protected Set<KeyTuple> clearDirtyTupleSet() {
        if (dirtyTupleSet == null) {
            return Collections.emptySet();
        } else {
            Set<KeyTuple> currentDirtyTupleSet = dirtyTupleSet;
            dirtyTupleSet = null; // Faster than calling clear().
            return currentDirtyTupleSet;
        }
    }

}
