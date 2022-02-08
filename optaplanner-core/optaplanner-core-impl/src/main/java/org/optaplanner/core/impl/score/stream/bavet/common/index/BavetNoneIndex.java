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
import java.util.HashSet;
import java.util.Set;

import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeTuple;

public class BavetNoneIndex<Tuple_ extends BavetJoinBridgeTuple> extends BavetIndex<Tuple_> {

    private final Set<Tuple_> tupleSet = new HashSet<>();

    @Override
    public void remove(Tuple_ tuple) {
        boolean removed = tupleSet.remove(tuple);
        if (!removed) {
            throw new IllegalStateException("Impossible state: the fact (" + tuple.getFactsString()
                    + ")'s tuple cannot be removed in the index from the tupleSet (" + tupleSet + ").");
        }
        tuple.setIndexProperties(null);
    }

    @Override
    public void put(Object[] indexProperties, Tuple_ tuple) {
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
        return tupleSet;
    }

}
