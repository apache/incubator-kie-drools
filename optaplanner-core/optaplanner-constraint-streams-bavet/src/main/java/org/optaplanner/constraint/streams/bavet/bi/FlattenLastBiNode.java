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

package org.optaplanner.constraint.streams.bavet.bi;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractFlattenLastNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;

final class FlattenLastBiNode<A, B, NewB> extends AbstractFlattenLastNode<BiTuple<A, B>, BiTuple<A, NewB>, B, NewB> {

    private final int outputStoreSize;

    FlattenLastBiNode(int flattenLastStoreIndex, Function<B, Iterable<NewB>> mappingFunction,
            TupleLifecycle<BiTuple<A, NewB>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(flattenLastStoreIndex, mappingFunction, nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected BiTuple<A, NewB> createTuple(BiTuple<A, B> originalTuple, NewB newB) {
        return new BiTupleImpl<>(originalTuple.getFactA(), newB, outputStoreSize);
    }

    @Override
    protected B getEffectiveFactIn(BiTuple<A, B> tuple) {
        return tuple.getFactB();
    }

    @Override
    protected NewB getEffectiveFactOut(BiTuple<A, NewB> outTuple) {
        return outTuple.getFactB();
    }
}
