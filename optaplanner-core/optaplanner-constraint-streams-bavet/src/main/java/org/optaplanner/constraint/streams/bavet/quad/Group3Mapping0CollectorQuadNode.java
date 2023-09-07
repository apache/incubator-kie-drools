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

package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.constraint.streams.bavet.tri.TriTupleImpl;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.util.Triple;

final class Group3Mapping0CollectorQuadNode<OldA, OldB, OldC, OldD, A, B, C>
        extends
        AbstractGroupQuadNode<OldA, OldB, OldC, OldD, TriTuple<A, B, C>, TriTupleImpl<A, B, C>, Triple<A, B, C>, Void, Void> {

    private final int outputStoreSize;

    public Group3Mapping0CollectorQuadNode(QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMappingA,
            QuadFunction<OldA, OldB, OldC, OldD, B> groupKeyMappingB, QuadFunction<OldA, OldB, OldC, OldD, C> groupKeyMappingC,
            int groupStoreIndex, TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle, int outputStoreSize,
            EnvironmentMode environmentMode) {
        super(groupStoreIndex, tuple -> createGroupKey(groupKeyMappingA, groupKeyMappingB, groupKeyMappingC, tuple),
                nextNodesTupleLifecycle, environmentMode);
        this.outputStoreSize = outputStoreSize;
    }

    static <A, B, C, OldA, OldB, OldC, OldD> Triple<A, B, C> createGroupKey(
            QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMappingA,
            QuadFunction<OldA, OldB, OldC, OldD, B> groupKeyMappingB,
            QuadFunction<OldA, OldB, OldC, OldD, C> groupKeyMappingC,
            QuadTuple<OldA, OldB, OldC, OldD> tuple) {
        OldA oldA = tuple.getFactA();
        OldB oldB = tuple.getFactB();
        OldC oldC = tuple.getFactC();
        OldD oldD = tuple.getFactD();
        A a = groupKeyMappingA.apply(oldA, oldB, oldC, oldD);
        B b = groupKeyMappingB.apply(oldA, oldB, oldC, oldD);
        C c = groupKeyMappingC.apply(oldA, oldB, oldC, oldD);
        return Triple.of(a, b, c);
    }

    @Override
    protected TriTupleImpl<A, B, C> createOutTuple(Triple<A, B, C> groupKey) {
        return new TriTupleImpl<>(groupKey.getA(), groupKey.getB(), groupKey.getC(), outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(TriTupleImpl<A, B, C> outTuple, Void unused) {
        throw new IllegalStateException("Impossible state: collector is null.");
    }

}
