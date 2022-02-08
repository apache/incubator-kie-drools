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

package org.optaplanner.core.impl.score.stream.bavet.common.index;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeTuple;

public class BavetEqualsIndex<Tuple_ extends BavetJoinBridgeTuple> extends BavetIndex<Tuple_> {

    private final Map<BavetIndexKey, Set<Tuple_>> map = new HashMap<>();

    @Override
    public void remove(Tuple_ tuple) {
        BavetIndexKey oldIndexKey = new BavetIndexKey(tuple.getIndexProperties());
        Set<Tuple_> tupleSet = map.get(oldIndexKey);
        boolean removed = tupleSet.remove(tuple);
        if (!removed) {
            throw new IllegalStateException("Impossible state: the fact (" + tuple.getFactsString()
                    + ")'s tuple cannot be removed in the index from the tupleSet (" + tupleSet + ").");
        }
        if (tupleSet.isEmpty()) {
            map.remove(oldIndexKey);
        }
        tuple.setIndexProperties(null);
    }

    @Override
    public void put(Object[] indexProperties, Tuple_ tuple) {
        Set<Tuple_> tupleSet = map.computeIfAbsent(new BavetIndexKey(indexProperties), k -> new LinkedHashSet<>());
        boolean added = tupleSet.add(tuple);
        if (!added) {
            throw new IllegalStateException("Impossible state: the fact (" + tuple.getFactsString()
                    + ") with indexProperties (" + Arrays.toString(indexProperties)
                    + ") was already added in the index to the tupleSet (" + tupleSet + ").");
        }
        tuple.setIndexProperties(indexProperties);
    }

    @Override
    public Set<Tuple_> get(Object[] indexProperties) {
        Set<Tuple_> tupleSet = map.get(new BavetIndexKey(indexProperties));
        if (tupleSet == null) {
            return Collections.emptySet();
        }
        return tupleSet;
    }

}
