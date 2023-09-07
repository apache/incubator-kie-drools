/*
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

package org.optaplanner.constraint.streams.bavet.tri;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractFlattenLastNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;

final class FlattenLastTriNode<A, B, C, NewC>
        extends AbstractFlattenLastNode<TriTuple<A, B, C>, TriTuple<A, B, NewC>, C, NewC> {

    private final int outputStoreSize;

    FlattenLastTriNode(int flattenLastStoreIndex, Function<C, Iterable<NewC>> mappingFunction,
            TupleLifecycle<TriTuple<A, B, NewC>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(flattenLastStoreIndex, mappingFunction, nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected TriTuple<A, B, NewC> createTuple(TriTuple<A, B, C> originalTuple, NewC newC) {
        return new TriTupleImpl<>(originalTuple.getFactA(), originalTuple.getFactB(), newC, outputStoreSize);
    }

    @Override
    protected C getEffectiveFactIn(TriTuple<A, B, C> tuple) {
        return tuple.getFactC();
    }

    @Override
    protected NewC getEffectiveFactOut(TriTuple<A, B, NewC> outTuple) {
        return outTuple.getFactC();
    }
}
